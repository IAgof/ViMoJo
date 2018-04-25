package com.videonasocialmedia.vimojo.share.presentation.mvp.presenters;

/**
 * Created by jca on 11/12/15.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.auth.domain.usecase.GetAuthToken;
import com.videonasocialmedia.vimojo.domain.editor.AddLastVideoExportedToProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.ExportProjectUseCase;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.domain.project.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.presentation.mvp.views.OptionsToShareList;
import com.videonasocialmedia.vimojo.share.domain.ObtainNetworksToShareUseCase;
import com.videonasocialmedia.vimojo.share.domain.GetFtpListUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
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
import com.videonasocialmedia.vimojo.view.VimojoPresenter;
import com.videonasocialmedia.vimojo.sync.model.VideoUpload;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

/**
 * Presenter class for {@link com.videonasocialmedia.vimojo.share.presentation.views.activity.ShareActivity}
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
    private final ProjectInstanceCache projectInstanceCache;
    private String videoPath = "";
    private SocialNetwork socialNetworkSelected;
    private boolean isWifiConnected;
    private boolean acceptUploadVideoMobileNetwork;
    private boolean isMobileNetworkConnected;
    private FtpNetwork ftpNetworkSelected;
    private boolean hasBeenProjectExported;

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
            RunSyncAdapterHelper runSyncAdapterHelper, ProjectInstanceCache projectInstanceCache) {
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
        this.projectInstanceCache = projectInstanceCache;
    }

    public void updatePresenter(boolean hasBeenProjectExported, String videoExportedPath,
                                boolean isAppExportingProject) {
        this.currentProject = projectInstanceCache.getCurrentProject();
        obtainNetworksToShare();
        obtainListFtp();
        setupVimojoNetwork();
        obtainListOptionsToShare(vimojoNetwork, ftpList, socialNetworkList);
        if (shareVideoViewReference != null) {
            shareVideoViewReference.get().showOptionsShareList(optionToShareList);
        }
        this.hasBeenProjectExported = hasBeenProjectExported;
        this.videoPath = videoExportedPath;
        if (isAppExportingProject) {
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

    private void updateNumTotalVideosShared() {
        int totalVideosShared = sharedPreferences.getInt(ConfigPreferences.TOTAL_VIDEOS_SHARED, 0);
        preferencesEditor = sharedPreferences.edit();
        preferencesEditor.putInt(ConfigPreferences.TOTAL_VIDEOS_SHARED, ++totalVideosShared);
        preferencesEditor.apply();
    }

    private int getNumTotalVideosShared() {
        return sharedPreferences.getInt(ConfigPreferences.TOTAL_VIDEOS_SHARED, 0);
    }

    protected void trackVideoShared(String socialNetwork) {
        userEventTracker.trackVideoSharedSuperProperties();
        userEventTracker.trackVideoShared(socialNetwork, currentProject, getNumTotalVideosShared());
        userEventTracker.trackVideoSharedUserTraits();
    }

    public void addVideoExportedToProject(String videoPath) {
        addLastVideoExportedProjectUseCase.addLastVideoExportedToProject(currentProject, videoPath,
                DateUtils.getDateRightNow());
    }

    protected void startExport(int typeNetworkSelected) {
        shareVideoViewReference.get().startVideoExport();
        exportUseCase.export(currentProject, Constants.PATH_WATERMARK, new OnExportFinishedListener() {
            @Override
            public void onExportError(int error, Exception exception) {
                Crashlytics.log("Error exporting: " + error);
                if (shareVideoViewReference.get() != null) {
                    shareVideoViewReference.get().showVideoExportError(error, exception);
                }
            }

            @Override
            public void onExportSuccess(final Video video) {
                if (shareVideoViewReference.get() != null) {
                    videoPath = video.getMediaPath();
                    shareVideoViewReference.get().loadExportedVideoPreview(videoPath);
                    processNetworkClicked(typeNetworkSelected, videoPath);
                }
            }

            @Override
            public void onExportProgress(int exportStage) {
                if (shareVideoViewReference.get() != null) {
                    shareVideoViewReference.get().showExportProgress(exportStage);
                }
            }

            @Override
            public void onExportCanceled() {
                if (shareVideoViewReference.get() != null) {
                    shareVideoViewReference.get().showExportCanceled();
                }
            }
        });
    }

    protected void clickUploadToPlatform(boolean isWifiConnected,
                               boolean isAcceptedUploadWithMobileNetwork,
                               boolean isMobileNetworkConnected,
                               String videoPath) {
        if (isNeededAskPermissionForMobileUpload(isWifiConnected, isMobileNetworkConnected,
            isAcceptedUploadWithMobileNetwork)) {
            shareVideoViewReference.get().showDialogUploadVideoWithMobileNetwork();
            return;
        }
        if (!isUserLogged()) {
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

    protected void uploadVideo(String mediaPath, String title, String description,
                               List<String> productTypeList, boolean isAcceptedUploadMobileNetwork) {
        // Convert productTypeList to string. VideoApiClient not support RequestBody with List<String>
        String productTypeListToString = TextUtils.join(", ", productTypeList);
        int id = (int) (new Date().getTime()/1000);
        VideoUpload videoUpload = new VideoUpload(id, mediaPath, title, description,
            productTypeListToString, isAcceptedUploadMobileNetwork);
        executeUseCaseCall((Callable<Void>) () -> {
            try {
                uploadToPlatformQueue.addVideoToUpload(videoUpload);
                runSyncAdapterHelper.runNowSyncAdapter();
            } catch (IOException ioException) {
                ioException.printStackTrace();
                Log.d(LOG_TAG, ioException.getMessage());
                Crashlytics.log("Error adding video to upload");
                Crashlytics.logException(ioException);
            }
            return null;
        });
    }

    private boolean isThereFreeStorageOnPlatform(String mediaPath) {
        long videoToUploadLength = new File(mediaPath).length();
        // return (freeStorage > videoToUploadLenght)
        return true;
    }

    public void onSocialNetworkClicked(SocialNetwork socialNetwork) {
        shareVideoViewReference.get().pauseVideoPlayerPreview();
        socialNetworkSelected = socialNetwork;
        exportOrProcessNetwork(OptionsToShareList.typeSocialNetwork);
    }

    public void onVimojoPlatformClicked(boolean isWifiConnected,
                                        boolean acceptUploadVideoMobileNetwork,
                                        boolean isMobileNetworkConnected) {
        this.isWifiConnected = isWifiConnected;
        this.acceptUploadVideoMobileNetwork = acceptUploadVideoMobileNetwork;
        this.isMobileNetworkConnected = isMobileNetworkConnected;
        shareVideoViewReference.get().pauseVideoPlayerPreview();
        exportOrProcessNetwork(OptionsToShareList.typeVimojoNetwork);
    }

    public void onFtpClicked(FtpNetwork ftp) {
        shareVideoViewReference.get().pauseVideoPlayerPreview();
        ftpNetworkSelected = ftp;
        exportOrProcessNetwork(OptionsToShareList.typeFtp);
    }

    public void onMoreSocialNetworkClicked() {
        shareVideoViewReference.get().pauseVideoPlayerPreview();
        exportOrProcessNetwork(OptionsToShareList.typeMoreSocialNetwork);
    }

    protected void exportOrProcessNetwork(int typeNetworkSelected) {
        if (!hasBeenProjectExported()) {
            startExport(typeNetworkSelected);
        } else {
            processNetworkClicked(typeNetworkSelected, videoPath);
        }
    }

    protected boolean hasBeenProjectExported() {
        return hasBeenProjectExported;
    }

    protected void processNetworkClicked(int typeNetworkSelected, String videoPath) {
        switch (typeNetworkSelected) {
            case OptionsToShareList.typeVimojoNetwork:
                clickUploadToPlatform(isWifiConnected, acceptUploadVideoMobileNetwork,
                    isMobileNetworkConnected, videoPath);
                break;
            case OptionsToShareList.typeFtp:
                shareVideoViewReference.get().createDialogToInsertNameProject(ftpNetworkSelected,
                    videoPath);
                break;
            case OptionsToShareList.typeSocialNetwork:
                trackVideoShared(getSocialNetworkSelected().getIdSocialNetwork());
                if (getSocialNetworkSelected().getName().equals(context.getString(R.string.save_to_gallery))) {
                    shareVideoViewReference.get().showMessage(R.string.video_saved);
                    return;
                }
                updateNumTotalVideosShared();
                shareVideoViewReference.get().shareVideo(videoPath, getSocialNetworkSelected());
                break;
            case OptionsToShareList.typeMoreSocialNetwork:
                trackVideoShared("Other network");
                updateNumTotalVideosShared();
                shareVideoViewReference.get().showIntentOtherNetwork(videoPath);
                break;
        }
    }

    public void updateHasBeenProjectExported(boolean hasBeenProjectExported) {
        this.hasBeenProjectExported = hasBeenProjectExported;
    }

    public SocialNetwork getSocialNetworkSelected() {
        return socialNetworkSelected;
    }

    public void cancelExportation() {
        exportUseCase.cancelExport();
    }
}