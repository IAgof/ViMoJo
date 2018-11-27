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
import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.domain.usecase.SetCompositionResolution;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateComposition;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.ApplyAVTransitionsUseCase;
import com.videonasocialmedia.vimojo.importer.helpers.NewClipImporter;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.presentation.mvp.views.GalleryPagerView;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.view.BackgroundExecutor;
import com.videonasocialmedia.vimojo.view.VimojoPresenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

/**
 * This class is used for adding new videos to the project.
 */
public class GalleryPagerPresenter extends VimojoPresenter
//        implements OnRemoveMediaFinishedListener
{
    private final String LOG_TAG = "GalleryPagerPresenter";

    private final SharedPreferences preferences;
    private final AddVideoToProjectUseCase addVideoToProjectUseCase;
    private final GalleryPagerView galleryPagerView;
    protected Project currentProject;
    private final ArrayList<Integer> listErrorVideoIds = new ArrayList<>();
    private final Context context;
    private final ProjectInstanceCache projectInstanceCache;
    private boolean differentVideoFormat;

    private final ApplyAVTransitionsUseCase launchTranscoderAddAVTransitionUseCase;
    // TODO(jliarte): 3/05/17 init in constructor to inject it. Wrap android MMR with our own class
    MediaMetadataRetriever metadataRetriever;
    private final UpdateComposition updateComposition;
    private SetCompositionResolution setCompositionResolution;
    private NewClipImporter newClipImporter;

    /**
     * Constructor.
     */
    @Inject public GalleryPagerPresenter(
        GalleryPagerView galleryPagerView, Context context,
        AddVideoToProjectUseCase addVideoToProjectUseCase,
        ApplyAVTransitionsUseCase applyAVTransitionsUseCase, SharedPreferences preferences,
        ProjectInstanceCache projectInstanceCache, UpdateComposition updateComposition,
        SetCompositionResolution setCompositionResolution, NewClipImporter newClipImporter,
        BackgroundExecutor backgroundExecutor, UserEventTracker userEventTracker) {
        super(backgroundExecutor, userEventTracker);
        this.galleryPagerView = galleryPagerView;
        this.context = context;
        this.addVideoToProjectUseCase = addVideoToProjectUseCase;
        this.launchTranscoderAddAVTransitionUseCase = applyAVTransitionsUseCase;
        this.preferences = preferences;
        this.setCompositionResolution = setCompositionResolution;
        // TODO(jliarte): 23/04/18 inject this dependency? maybe abstracting from android with an interface
        metadataRetriever = new MediaMetadataRetriever();
        this.projectInstanceCache = projectInstanceCache;
        this.updateComposition = updateComposition;
        this.newClipImporter = newClipImporter;
    }

    public void updatePresenter() {
        this.currentProject = projectInstanceCache.getCurrentProject();
        galleryPagerView.showAdsView();
    }

    public void addVideoListToProject(List<Video> videoList) {
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
        // TODO(jliarte): 18/07/18 extract repo call outside this UC and replace in all other invocations!!
        addVideoToProjectUseCase.addVideoListToTrack(currentProject, checkedVideoList,
                new OnAddMediaFinishedListener() {
            @Override
            public void onAddMediaItemToTrackSuccess(Media video) {
                // TODO(jliarte): 18/07/18 check if this UC call should be inside if
                // TODO(jliarte): 18/07/18 should update project or add new media to project?
                executeUseCaseCall(() -> {
                    updateComposition.updateComposition(currentProject);
                    if (!differentVideoFormat) {
                        galleryPagerView.navigate();
                    }
                });
            }

            @Override
            public void onAddMediaItemToTrackError() {
                // TODO(jliarte): 18/07/18 handle this error - show message!!
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
                setCompositionResolution.setResolution(currentProject, resolutionForWidth);
                SharedPreferences.Editor preferencesEditor = preferences.edit();
                preferencesEditor.putString(ConfigPreferences.KEY_LIST_PREFERENCES_RESOLUTION,
                        getPreferenceResolutionForWidth(videoWidth));
                preferencesEditor.apply();
                executeUseCaseCall(() -> updateComposition.updateComposition(currentProject));
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

    public void importVideo(String realPathFromURI) {
        galleryPagerView.showImportingDialog();
        executeUseCaseCall(() -> {
            if (realPathFromURI.contains(Constants.PATH_APP_MASTERS)
                || realPathFromURI.contains(Constants.PATH_APP_EDITED)) {
                Log.e(LOG_TAG, "Video already in vimojo!!");
                loadVideoListToProject(Collections.singletonList(
                    new Video(realPathFromURI, Video.DEFAULT_VOLUME)));
            } else {
                final Video video = new Video(realPathFromURI, Video.DEFAULT_VOLUME);
                importingVideo(video);
            }
        });
    }

    private void importingVideo(Video video) {
        ListenableFuture<Video> importerJob
            = newClipImporter.adaptVideoToVideonaFormat(currentProject, video,
            currentProject.numberOfClips(), 0, 0);
        try {
            importerJob.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            galleryPagerView.showImportingError("Interrupted exception");
            return;
        } catch (ExecutionException e) {
            e.printStackTrace();
            galleryPagerView.showImportingError("Executed exception");
            return;
        }
        addVideoListToProject(Collections.singletonList(
            new Video(video.getMediaPath(), Video.DEFAULT_VOLUME)));
    }

    private void loadVideoListToProject(List<Video> videoList) {
        try {
            List<Video> checkedVideoList = filterVideosWithResolutionDifferentFromProjectResolution(videoList);
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
            Crashlytics.log("Error in GalleryPagerPresenter.filterVideosWithResolutionDifferentFromProjectResolution");
            Crashlytics.logException(errorLoadingVideoList);
        }
    }

//    @Override
//    public void onRemoveMediaItemFromTrackError() {
//    }
//
//  @Override
//  public void onTrackUpdated(Track track) {
//
//  }
//
//    @Override
//    public void onTrackRemoved(Track track) {
//
//    }
//
//    @Override
//    public void onRemoveMediaItemFromTrackSuccess(List<Media> removedMedias) {
//    }

}
