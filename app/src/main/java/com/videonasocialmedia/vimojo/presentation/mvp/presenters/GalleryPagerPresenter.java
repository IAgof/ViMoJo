/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Veronica Lago Fominaya
 */

package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.ApplyAVTransitionsUseCase;
import com.videonasocialmedia.vimojo.export.domain.GetVideoFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;

import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.presentation.mvp.views.GalleryPagerView;
import com.videonasocialmedia.vimojo.composition.repository.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.video.VideoDataSource;
import com.videonasocialmedia.vimojo.sync.AssetUploadQueue;
import com.videonasocialmedia.vimojo.sync.helper.RunSyncAdapterHelper;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.view.VimojoPresenter;
import com.videonasocialmedia.vimojo.vimojoapiclient.CompositionApiClient;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.AssetUpload;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.Nullable;
import javax.inject.Inject;

/**
 * This class is used for adding new videos to the project.
 */
public class GalleryPagerPresenter extends VimojoPresenter implements OnAddMediaFinishedListener,
    OnRemoveMediaFinishedListener
//        , OnLaunchAVTransitionTempFileListener
//        , TranscoderHelperListener
{
    private final String LOG_TAG = "GalleryPagerPresenter";

    private final SharedPreferences preferences;
    private final GetVideoFormatFromCurrentProjectUseCase getVideonaFormatFromCurrentProjectUseCase;
    private final AddVideoToProjectUseCase addVideoToProjectUseCase;
    private final GalleryPagerView galleryPagerView;
    protected Project currentProject;
    private final ArrayList<Integer> listErrorVideoIds = new ArrayList<>();
    private final Context context;
    private final VideoDataSource videoRepository;
    private final ProjectInstanceCache projectInstanceCache;
    private boolean differentVideoFormat;

    private VideonaFormat videoFormat;
    private final ApplyAVTransitionsUseCase launchTranscoderAddAVTransitionUseCase;
    // TODO(jliarte): 3/05/17 init in constructor to inject it. Wrap android MMR with our own class
    MediaMetadataRetriever metadataRetriever;
    private final ProjectRepository projectRepository;
    private final AssetUploadQueue assetUploadQueue;
    private final RunSyncAdapterHelper runSyncAdapterHelper;
    private final CompositionApiClient compositionApiClient;

    /**
     * Constructor.
     */
    @Inject public GalleryPagerPresenter(
            GalleryPagerView galleryPagerView, Context context,
            AddVideoToProjectUseCase addVideoToProjectUseCase,
            GetVideoFormatFromCurrentProjectUseCase getVideonaFormatFromCurrentProjectUseCase,
            ApplyAVTransitionsUseCase applyAVTransitionsUseCase,
            ProjectRepository projectRepository,
            VideoDataSource videoRepository, SharedPreferences preferences,
            ProjectInstanceCache projectInstanceCache, AssetUploadQueue assetUploadQueue,
            RunSyncAdapterHelper runSyncAdapterHelper, CompositionApiClient compositionApiClient) {
        this.galleryPagerView = galleryPagerView;
        this.context = context;
        this.addVideoToProjectUseCase = addVideoToProjectUseCase;
        this.getVideonaFormatFromCurrentProjectUseCase = getVideonaFormatFromCurrentProjectUseCase;
        this.launchTranscoderAddAVTransitionUseCase = applyAVTransitionsUseCase;
        this.projectRepository = projectRepository;
        this.videoRepository = videoRepository;
        this.preferences = preferences;
        // TODO(jliarte): 23/04/18 inject this dependency? maybe abstracting from android with an interface
        metadataRetriever = new MediaMetadataRetriever();
        this.projectInstanceCache = projectInstanceCache;
        this.assetUploadQueue = assetUploadQueue;
        this.runSyncAdapterHelper = runSyncAdapterHelper;
        this.compositionApiClient = compositionApiClient;
    }

    public void updatePresenter() {
        this.currentProject = projectInstanceCache.getCurrentProject();
    }

    public void loadVideoListToProject(List<Video> videoList) {
        try {
            List<Video> checkedVideoList =
                    filterVideosWithResolutionDifferentFromProjectResolution(videoList);
            if (listErrorVideoIds.size() > 0) {
                galleryPagerView.showDialogVideosNotAddedFromGallery(listErrorVideoIds);
                differentVideoFormat = true;
            }
            addVideoToProject(checkedVideoList);
        } catch (Exception errorLoadingVideoList) {
            // TODO(jliarte): 13/06/17 I'm unable to find the error that is generating these bugs:
            // https://fabric.io/rtve4/android/apps/com.videonasocialmedia.vimojo.rtve/issues/59391686be077a4dccd740b9
            // https://fabric.io/vimojo-rtve/android/apps/com.videonasocialmedia.vimojo.main/issues/5920d048be077a4dcc014b98
            // so this is a workarround while discovering the origin of the bug
            errorLoadingVideoList.printStackTrace();
            Log.e(LOG_TAG, "Error while loading videos from gallery", errorLoadingVideoList);
            Crashlytics.log("Error in GalleryPagerPresenter." +
                    "filterVideosWithResolutionDifferentFromProjectResolution");
            Crashlytics.logException(errorLoadingVideoList);
        }
    }

    private void addVideoToProject(List<Video> checkedVideoList) {
        addVideoToProjectUseCase.addVideoListToTrack(currentProject, checkedVideoList, this);
        for (Video video: checkedVideoList) {
          // TODO: 21/6/18 Get projectId, currentCompositin.getProjectId()
            AssetUpload assetUpload = new AssetUpload("ElConfiHack", video);
            executeUseCaseCall((Callable<Void>) () -> {
                try {
                    assetUploadQueue.addAssetToUpload(assetUpload);
                    runSyncAdapterHelper.runNowSyncAdapter();
                    Log.d(LOG_TAG, "addAsset " + assetUpload.getName());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    Log.d(LOG_TAG, ioException.getMessage());
                    Crashlytics.log("Error adding video to upload");
                    Crashlytics.logException(ioException);
                }
                return null;
            });
        }
        updateCompositionWithPlatform(currentProject);
    }

    private void updateCompositionWithPlatform(Project currentProject) {
        ListenableFuture<Project> compositionFuture = executeUseCaseCall(new Callable<Project>() {
            @Override
            public Project call() throws Exception {
                return compositionApiClient.updateComposition(currentProject);
            }
        });
        Futures.addCallback(compositionFuture, new FutureCallback<Project>() {
            @Override
            public void onSuccess(@Nullable Project result) {
                Log.d(LOG_TAG, "Success uploading composition to server ");
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(LOG_TAG, "Error uploading composition to server " + t.getMessage());
            }
        });
    }

    private List<Video> filterVideosWithResolutionDifferentFromProjectResolution(
            List<Video> videoList) {
        List<Video> filteredVideoList = new ArrayList<>();
        updateProfileForEmptyProject(currentProject, videoList);
        VideoResolution projectProfileVideoResolution
                = currentProject.getProfile().getVideoResolution();
        for (int index = 0; index < videoList.size(); index++) {
            Video video = videoList.get(index);
            try {
                // TODO(jliarte): 13/06/17 this is not the responsibility stated in the name of
                // the method!
                setVideoDurationFromMediaMetadata(video);

                String videoWidth = getVideoWidth(video);
                if (videoWidth.compareTo(String
                        .valueOf(projectProfileVideoResolution.getWidth())) != 0) {
                    listErrorVideoIds.add(index + 1);
                } else {
                    filteredVideoList.add(video);
                }
            } catch (Exception e) {
                // FIXME(jliarte): 13/06/17 if exception is triggered comparing videoWidths video
                // duration should not be set to 0
                video.setDuration(0);
                e.printStackTrace();
                Crashlytics.log("Error in GalleryPagerPresenter." +
                        "filterVideosWithResolutionDifferentFromProjectResolution");
                Crashlytics.logException(e);
            }
        }
        return filteredVideoList;
    }

    private String getVideoWidth(Video video) {
        // TODO(jliarte): 11/07/18 capture error getting info from MMR
        metadataRetriever.setDataSource(video.getMediaPath());
        return metadataRetriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
    }

    private void setVideoDurationFromMediaMetadata(Video video) {
        metadataRetriever.setDataSource(video.getMediaPath());
        String duration = metadataRetriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_DURATION);

        int durationInt = Integer.parseInt(duration);
        video.setDuration(durationInt);
        video.setStopTime(durationInt);
    }

    protected void updateProfileForEmptyProject(Project currentProject, List<Video> videoList) {
        if ((currentProject.getVMComposition().getMediaTrack().getItems().size() == 0)
                && (videoList.size() > 0)) {
            Video firstVideo = videoList.get(0);
            String videoWidth = getVideoWidth(firstVideo);
            VideoResolution.Resolution resolutionForWidth = getResolutionForWidth(videoWidth);

            if (resolutionForWidth != null) {
                // TODO(jliarte): 11/07/18 this is a use case!
                projectRepository.updateResolution(currentProject, resolutionForWidth);
                SharedPreferences.Editor preferencesEditor = preferences.edit();
                preferencesEditor.putString(ConfigPreferences.KEY_LIST_PREFERENCES_RESOLUTION,
                        getPreferenceResolutionForWidth(videoWidth));
                preferencesEditor.commit();
            }
        }
    }

    private String getPreferenceResolutionForWidth(String videoWidth) {
        // TODO(jliarte): 3/05/17 move to a use case or somewhere else?
        switch (videoWidth) {
            case "4096":
            case "3840": // TODO(jliarte): 3/05/17 BQx5 4K resolution!!!
                return context.getString(R.string.high_resolution_name);
            case "1920":
                return context.getString(R.string.good_resolution_name);
            case "1280":
                return context.getString(R.string.low_resolution_name);
            default:
                return null;
        }
    }

    private VideoResolution.Resolution getResolutionForWidth(String videoWidth) {
        // TODO(jliarte): 3/05/17 move this logic to Resolution class in SDK?
        switch (videoWidth) {
            case "4096":
            case "3840": // TODO(jliarte): 3/05/17 BQx5 4K resolution!!!
                return VideoResolution.Resolution.HD4K;
            case "1920":
                return VideoResolution.Resolution.HD1080;
            case "1280":
                return VideoResolution.Resolution.HD720;
            default:
                return null;
        }
    }

    @Override
    public void onRemoveMediaItemFromTrackError() {
    }

    @Override
    public void onRemoveMediaItemFromTrackSuccess() {
    }

    @Override
    public void onAddMediaItemToTrackError() {
    }

    /*

    ÑAPA

     */

    @Override
    public void onAddMediaItemToTrackSuccess(Media video) {
        if (!differentVideoFormat) {
            galleryPagerView.navigate();
        }
    }

}
