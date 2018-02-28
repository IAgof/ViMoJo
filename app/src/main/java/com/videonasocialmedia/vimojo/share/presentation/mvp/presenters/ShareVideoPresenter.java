package com.videonasocialmedia.vimojo.share.presentation.mvp.presenters;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.auth.domain.usecase.GetAuthToken;
import com.videonasocialmedia.vimojo.domain.editor.AddLastVideoExportedToProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.ExportProjectUseCase;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.domain.project.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.share.domain.ObtainNetworksToShareUseCase;
import com.videonasocialmedia.vimojo.share.domain.GetFtpListUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.share.model.entities.FtpNetwork;
import com.videonasocialmedia.vimojo.share.model.entities.SocialNetwork;
import com.videonasocialmedia.vimojo.share.model.entities.VimojoNetwork;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnExportFinishedListener;
import com.videonasocialmedia.vimojo.share.presentation.mvp.views.ShareVideoView;
import com.videonasocialmedia.vimojo.share.presentation.views.utils.LoggedValidator;
import com.videonasocialmedia.vimojo.sync.UploadToPlatformQueue;
import com.videonasocialmedia.vimojo.sync.helper.RunSyncAdapterHelper;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.DateUtils;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.utils.Utils;
import com.videonasocialmedia.vimojo.view.VimojoPresenter;
import com.videonasocialmedia.vimojo.sync.model.VideoUpload;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import static android.content.Context.*;

/**
 * Created by jca on 11/12/15.
 */
public class ShareVideoPresenter extends VimojoPresenter {
    private String LOG_TAG = ShareVideoPresenter.class.getCanonicalName();

    private Context context;
    private ObtainNetworksToShareUseCase obtainNetworksToShareUseCase;
    private GetFtpListUseCase getFtpListUseCase;
    private CreateDefaultProjectUseCase createDefaultProjectUseCase;
    private WeakReference<ShareVideoView> shareVideoViewReference;
    protected Project currentProject;
    protected UserEventTracker userEventTracker;
    private SharedPreferences sharedPreferences;
    private List<FtpNetwork> ftpList;
    private List<SocialNetwork> socialNetworkList;
    private VimojoNetwork vimojoNetwork;
    private List optionToShareList;
    private SharedPreferences.Editor preferencesEditor;

    private AddLastVideoExportedToProjectUseCase addLastVideoExportedProjectUseCase;
    private ExportProjectUseCase exportUseCase;
    private final GetAuthToken getAuthToken;
    private UploadToPlatformQueue uploadToPlatformQueue;
    private final LoggedValidator loggedValidator;
    private final RunSyncAdapterHelper runSyncAdapterHelper;

    @Inject
    public ShareVideoPresenter(
            Context context, ShareVideoView shareVideoView, UserEventTracker userEventTracker,
            SharedPreferences sharedPreferences,
            CreateDefaultProjectUseCase createDefaultProjectUseCase,
            AddLastVideoExportedToProjectUseCase addLastVideoExportedProjectUseCase,
            ExportProjectUseCase exportProjectUseCase,
            ObtainNetworksToShareUseCase obtainNetworksToShareUseCase,
            GetFtpListUseCase getFtpListUseCase, GetAuthToken getAuthToken,
            UploadToPlatformQueue uploadToPlatformQueue, LoggedValidator loggedValidator,
            RunSyncAdapterHelper runSyncAdapterHelper) {
        this.context = context;
        this.shareVideoViewReference = new WeakReference<>(shareVideoView);
        this.userEventTracker = userEventTracker;
        this.sharedPreferences = sharedPreferences;
        this.createDefaultProjectUseCase = createDefaultProjectUseCase;
        this.addLastVideoExportedProjectUseCase = addLastVideoExportedProjectUseCase;
        this.exportUseCase = exportProjectUseCase;
        this.obtainNetworksToShareUseCase = obtainNetworksToShareUseCase;
        this.getFtpListUseCase = getFtpListUseCase;
        this.getAuthToken = getAuthToken;
        this.uploadToPlatformQueue = uploadToPlatformQueue;
        this.loggedValidator = loggedValidator;
        this.runSyncAdapterHelper = runSyncAdapterHelper;
        currentProject = loadCurrentProject();
    }

    // TODO(jliarte): 27/02/18 why is the project get from instance?!?!?!
    private Project loadCurrentProject() {
        return Project.getInstance(null, null, null, null);
    }

    public void onResume() {
        obtainNetworksToShare();
        obtainListFtp();
        setupVimojoNetwork();
        obtainListOptionsToShare(vimojoNetwork, ftpList, socialNetworkList);
        if (shareVideoViewReference != null) {
            shareVideoViewReference.get().showOptionsShareList(optionToShareList);
            shareVideoViewReference.get().startVideoExport();
        }
    }

    private void setupVimojoNetwork() {
        vimojoNetwork = new VimojoNetwork(ConfigPreferences.VIMOJO_NETWORK,
                context.getString(R.string.upload_to_server),
                R.drawable.activity_share_icon_vimojo_network);
    }

    private void obtainListFtp() {
        ftpList = getFtpListUseCase.getFtpList();
    }

    public void obtainNetworksToShare() {
        socialNetworkList = obtainNetworksToShareUseCase.obtainMainNetworks();
    }

    private void obtainListOptionsToShare(VimojoNetwork vimojoNetwork, List<FtpNetwork> ftpList,
                                          List<SocialNetwork> socialNetworkList) {
        optionToShareList = new ArrayList();
        if (BuildConfig.FEATURE_VIMOJO_PLATFORM) {
            optionToShareList.add(vimojoNetwork);
        }
        if (BuildConfig.FEATURE_FTP) {
            optionToShareList.addAll(ftpList);
        }
        optionToShareList.addAll(socialNetworkList);
    }

    public void shareVideo(String videoPath, SocialNetwork appToShareWith, Context ctx) {
        final ComponentName name = new ComponentName(appToShareWith.getAndroidPackageName(),
                appToShareWith.getAndroidActivityName());

        Uri uri = Utils.obtainUriToShare(ctx, videoPath);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("video/*");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                VimojoApplication.getAppContext().getResources().getString(R.string.sharedWithVideona));
        intent.putExtra(Intent.EXTRA_TEXT,
                VimojoApplication.getAppContext().getResources().getString(R.string.videonaTags));
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addCategory(Intent.CATEGORY_DEFAULT);

        intent.setComponent(name);

        ctx.startActivity(intent);
    }

    // TODO(jliarte): 15/12/16 safe delete this method - old way to show networks?
    public void obtainExtraAppsToShare() {
        List networks = obtainNetworksToShareUseCase.obtainSecondaryNetworks();
        if (shareVideoViewReference.get() != null) {
            shareVideoViewReference.get().hideShareNetworks();
            shareVideoViewReference.get().showMoreNetworks(networks);
        }
    }

    public void updateNumTotalVideosShared() {
        int totalVideosShared = sharedPreferences.getInt(ConfigPreferences.TOTAL_VIDEOS_SHARED, 0);
        preferencesEditor = sharedPreferences.edit();
        preferencesEditor.putInt(ConfigPreferences.TOTAL_VIDEOS_SHARED, ++totalVideosShared);
        preferencesEditor.commit();
    }

    public int getNumTotalVideosShared() {
        return sharedPreferences.getInt(ConfigPreferences.TOTAL_VIDEOS_SHARED, 0);
    }

    public String getResolution() {
        VideoResolution videoResolution = currentProject.getProfile().getVideoResolution();
        return videoResolution.getWidth() + "x" + videoResolution.getHeight();
    }

    public void trackVideoShared(String socialNetwork) {
        userEventTracker.trackVideoSharedSuperProperties();
        userEventTracker.trackVideoShared(socialNetwork, currentProject, getNumTotalVideosShared());
        userEventTracker.trackVideoSharedUserTraits();
    }

    public void newDefaultProject(String rootPath, String privatePath) {
        clearProjectDataFromSharedPreferences();
        createDefaultProjectUseCase.createProject(rootPath, privatePath, isWatermarkActivated());
    }

    private boolean isWatermarkActivated() {
        if (BuildConfig.FEATURE_FORCE_WATERMARK) {
            return true;
        }
        return sharedPreferences.getBoolean(ConfigPreferences.WATERMARK, false);
    }

    // TODO(jliarte): 23/10/16 should this be moved to activity or other outer layer? maybe a repo?
    // TODO:(alvaro.martinez) 4/01/17 these data will no be saved in SharedPreferences,
    // rewrite mixpanel tracking and delete.
    private void clearProjectDataFromSharedPreferences() {
        sharedPreferences = VimojoApplication.getAppContext().getSharedPreferences(
                ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME,
                MODE_PRIVATE);
        preferencesEditor = sharedPreferences.edit();
        preferencesEditor.putLong(ConfigPreferences.VIDEO_DURATION, 0);
        preferencesEditor.putInt(ConfigPreferences.NUMBER_OF_CLIPS, 0);
    }

    public void addVideoExportedToProject(String videoPath) {
        addLastVideoExportedProjectUseCase.addLastVideoExportedToProject(videoPath,
                DateUtils.getDateRightNow());
    }

    public void startExport() {
        exportUseCase.export(Constants.PATH_WATERMARK, new OnExportFinishedListener() {
            @Override
            public void onExportError(String error) {
                Crashlytics.log("Error exporting: " + error);
                // TODO(jliarte): 28/04/17 pass the string?
                // known strings
                switch (error) {
                    case "No space left on device":
                        if (shareVideoViewReference.get() != null) {
                            shareVideoViewReference.get()
                                    .showVideoExportError(Constants.EXPORT_ERROR_NO_SPACE_LEFT);
                        }
                        break;
                    default:
                        if (shareVideoViewReference.get() != null) {
                            shareVideoViewReference.get()
                                    .showVideoExportError(Constants.EXPORT_ERROR_UNKNOWN);
                        }
                }
            }

            @Override
            public void onExportSuccess(final Video video) {
                if (shareVideoViewReference.get() != null) {
                    shareVideoViewReference.get().loadExportedVideoPreview(video.getMediaPath());
                }
            }

            @Override
            public void onExportProgress(String progressMsg, int exportStage) {
                if (shareVideoViewReference.get() != null) {
                    shareVideoViewReference.get().showExportProgress(progressMsg);
                }
            }
        });
    }

    public void clickUploadToPlatform(boolean isWifiConnected,
                                      boolean isAcceptedUploadWithMobileNetwork,
                                      boolean isMobileNetworkConnected,
                                      String videoPath) {

        if (isNeededAskPermissionForMobileUpload(isWifiConnected, isMobileNetworkConnected,
            isAcceptedUploadWithMobileNetwork)) {
            shareVideoViewReference.get().showDialogUploadVideoWithMobileNetwork();
            return;
        }
        if (!isUserLogged()) {
            // TODO: 8/2/18 Should I ask confirmation from user that he is going to navigate to User Authentication screen.
            shareVideoViewReference.get().showDialogNeedToRegisterLoginToUploadVideo();
            return;
        }
        if (!isThereFreeStorageOnPlatform(videoPath)) {
            // TODO:(alvaro.martinez) 26/01/18 Get user free storage from platform
            //shareVideoViewReference.get().showError("Don´t have enough storage to upload video");
            return;
        }

        if (!areThereProjectFieldsCompleted(currentProject)) {
            shareVideoViewReference.get().showDialogNeedToCompleteDetailProjectFields();
            return;
        }

        ProjectInfo projectInfo = currentProject.getProjectInfo();
        if (!isDeviceConnectedToUpload(isWifiConnected, isMobileNetworkConnected,
            isAcceptedUploadWithMobileNetwork)) {
            shareVideoViewReference.get().showDialogNotNetworkUploadVideoOnConnection();
        } else {
            shareVideoViewReference.get().showMessage(R.string.uploading_video);
        }
        uploadVideo(videoPath, projectInfo.getTitle(), projectInfo.getDescription(),
            projectInfo.getProductTypeList(), isAcceptedUploadWithMobileNetwork);
    }

    private boolean areThereProjectFieldsCompleted(Project currentProject) {
        ProjectInfo projectInfo = currentProject.getProjectInfo();
        return (!projectInfo.getTitle().isEmpty()) && (!projectInfo.getDescription().isEmpty()) &&
            (projectInfo.getProductTypeList().size() > 0);
    }

    private boolean isDeviceConnectedToUpload(boolean isWifiConnected,
                                              boolean isMobileNetworkConnected,
                                              boolean isAcceptedUploadMobileNetwork) {
        return isWifiConnected || (isMobileNetworkConnected &&  isAcceptedUploadMobileNetwork);
    }

    private boolean isNeededAskPermissionForMobileUpload(boolean isWifiConnected,
                                                         boolean isMobileNetworConnected,
                                                         boolean acceptUploadVideoMobileNetwork) {
        return !isWifiConnected && isMobileNetworConnected && !acceptUploadVideoMobileNetwork;
    }

    protected boolean isUserLogged() {
        shareVideoViewReference.get().showProgressDialogCheckingUserAuth();
        String authToken = "";
        try {
            authToken = executeUseCaseCall(() -> getAuthToken.getAuthToken(context).getToken())
                    .get();
        } catch (InterruptedException | ExecutionException errorGettingToken) {
            if (BuildConfig.DEBUG) {
                errorGettingToken.printStackTrace();
            }
            Crashlytics.log("Error getting info from user e");
            Crashlytics.logException(errorGettingToken);
        }
        shareVideoViewReference.get().hideProgressDialogCheckingUserAuth();
        return loggedValidator.loggedValidate(authToken);
    }

//    protected ListenableFuture<String> getAuthTokenFuture() {
//        ListenableFuture<String> authTokenFuture =
//                executeUseCaseCall(() -> getAuthToken.getAuthToken(context).getToken());
//        return authTokenFuture;
//    }

    protected void uploadVideo(String mediaPath, String title, String description,
                               List<String> productTypeList, boolean isAcceptedUploadMobileNetwork) {
        // Convert productTypeList to string. VideoApiClient not support RequestBody with List<String>
        String productTypeListToString = TextUtils.join(", ", productTypeList);
        VideoUpload videoUpload = new VideoUpload(mediaPath, title, description,
            productTypeListToString, isAcceptedUploadMobileNetwork);
        executeUseCaseCall((Callable<Void>) () -> {
            try {
                uploadToPlatformQueue.addVideoToUpload(videoUpload);
            } catch (IOException ioException) {
                ioException.printStackTrace();
                Log.d(LOG_TAG, ioException.getMessage());
                Crashlytics.log("Error adding video to upload");
                Crashlytics.logException(ioException);
            }
            return null;
        });
        runSyncAdapterHelper.runNowSyncAdapter();
    }

    private boolean isThereFreeStorageOnPlatform(String mediaPath) {
        long videoToUploadLength = new File(mediaPath).length();
        // return (freeStorage > videoToUploadLenght)
        return true;
    }
}