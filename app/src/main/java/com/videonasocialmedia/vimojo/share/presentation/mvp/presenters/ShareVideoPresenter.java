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
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.domain.project.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.presentation.mvp.views.OptionsToShareList;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
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
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import static android.content.Context.*;

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
            SharedPreferences sharedPreferences, ProjectRepository projectRepository,
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
        this.currentProject = projectRepository.getCurrentProject();
    }

    public void init(boolean hasBeenProjectExported, String videoExportedPath,
                     boolean isAppExportingProject) {
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

    public void newDefaultProject(String rootPath, String privatePath) {
        clearProjectDataFromSharedPreferences();
        createDefaultProjectUseCase.createProject(rootPath, privatePath, isWatermarkActivated());
    }

    private boolean isWatermarkActivated() {
        return BuildConfig.FEATURE_FORCE_WATERMARK ||
            sharedPreferences.getBoolean(ConfigPreferences.WATERMARK, false);
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
        preferencesEditor.apply();
    }

    public void addVideoExportedToProject(String videoPath) {
        addLastVideoExportedProjectUseCase.addLastVideoExportedToProject(videoPath,
                DateUtils.getDateRightNow());
    }

    protected void startExport(int typeNetworkSelected) {
        shareVideoViewReference.get().startVideoExport();
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
                    videoPath = video.getMediaPath();
                    shareVideoViewReference.get().loadExportedVideoPreview(videoPath);
                    processNetworkClicked(typeNetworkSelected, videoPath);
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

    protected void clickUploadToPlatform(boolean isWifiConnected,
                               boolean acceptUploadVideoMobileNetwork,
                               boolean isMobileNetworkConnected,
                               String videoPath) {
        if (isNeededAskPermissionForMobileUpload(isWifiConnected, isMobileNetworkConnected,
            acceptUploadVideoMobileNetwork)) {
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

        boolean connectedToNetwork = true;
        ProjectInfo projectInfo = currentProject.getProjectInfo();
        if (!isWifiOrMobileNetworkConnected(isWifiConnected, isMobileNetworkConnected)) {
            shareVideoViewReference.get().showDialogNotNetworkUploadVideoOnConnection();
            connectedToNetwork = false;
        } else {
            shareVideoViewReference.get().showMessage(R.string.uploading_video);
        }
        uploadVideo(videoPath, projectInfo.getTitle(), projectInfo.getDescription(),
            projectInfo.getProductTypeList(), connectedToNetwork);
    }

    private boolean areThereProjectFieldsCompleted(Project currentProject) {
        ProjectInfo projectInfo = currentProject.getProjectInfo();
        return (!projectInfo.getTitle().isEmpty()) && (!projectInfo.getDescription().isEmpty()) &&
            (projectInfo.getProductTypeList().size() > 0);
    }

    private boolean isWifiOrMobileNetworkConnected(boolean isWifiConnected,
                                                   boolean isMobileNetworkConnected) {
        return isWifiConnected || isMobileNetworkConnected;
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
                             List<String> productTypeList, boolean connectedToNetwork) {
        // Convert productTypeList to string. VideoApiClient not support RequestBody with List<String>
        String productTypeListToString = TextUtils.join(", ", productTypeList);
        VideoUpload videoUpload = new VideoUpload(mediaPath, title, description,
            productTypeListToString);
        executeUseCaseCall((Callable<Void>) () -> {
            try {
                uploadToPlatformQueue.addVideoToUpload(videoUpload, connectedToNetwork);
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

}