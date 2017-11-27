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
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.ApplyAVTransitionsUseCase;
import com.videonasocialmedia.vimojo.export.domain.GetVideoFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;

import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.presentation.mvp.views.GalleryPagerView;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * This class is used for adding new videos to the project.
 */
public class GalleryPagerPresenter implements OnAddMediaFinishedListener,
    OnRemoveMediaFinishedListener
//        , OnLaunchAVTransitionTempFileListener
//        , TranscoderHelperListener
{
    private final String LOG_TAG = "GalleryPagerPresenter";

    private final SharedPreferences preferences;
    private final GetVideoFormatFromCurrentProjectUseCase getVideonaFormatFromCurrentProjectUseCase;
    private final AddVideoToProjectUseCase addVideoToProjectUseCase;
    private final GalleryPagerView galleryPagerView;
    protected final Project currentProject;
    private final ArrayList<Integer> listErrorVideoIds = new ArrayList<>();
    private final Context context;
    private final VideoRepository videoRepository;
    private boolean differentVideoFormat;

    private VideonaFormat videoFormat;
    private final ApplyAVTransitionsUseCase launchTranscoderAddAVTransitionUseCase;
    // TODO(jliarte): 3/05/17 init in constructor to inject it. Wrap android MMR with our own class
    MediaMetadataRetriever metadataRetriever;
    private final ProjectRepository projectRepository;

    /**
     * Constructor.
     */
    @Inject public GalleryPagerPresenter(
            GalleryPagerView galleryPagerView, Context context,
            AddVideoToProjectUseCase addVideoToProjectUseCase,
            GetVideoFormatFromCurrentProjectUseCase getVideonaFormatFromCurrentProjectUseCase,
            ApplyAVTransitionsUseCase applyAVTransitionsUseCase,
            ProjectRepository projectRepository,
            VideoRepository videoRepository, SharedPreferences preferences) {
        this.galleryPagerView = galleryPagerView;
        this.context = context;
        this.addVideoToProjectUseCase = addVideoToProjectUseCase;
        this.getVideonaFormatFromCurrentProjectUseCase = getVideonaFormatFromCurrentProjectUseCase;
        this.launchTranscoderAddAVTransitionUseCase = applyAVTransitionsUseCase;
        this.currentProject = loadCurrentProject();
        this.projectRepository = projectRepository;
        this.videoRepository = videoRepository;
        this.preferences = preferences;
        metadataRetriever = new MediaMetadataRetriever();
    }

    private Project loadCurrentProject() {
        // TODO(jliarte): this should make use of a repository or use case to load the Project
        return Project.getInstance(null, null, null, null);
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
        addVideoToProjectUseCase.addVideoListToTrack(checkedVideoList, this);
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

//    @Override
//    public void onSuccessTranscoding(Video video) {
//        Log.d(LOG_TAG, "onSuccessTranscoding " + video.getTempPath());
//        videoRepository.setSuccessTranscodingVideo(video);
//    }
//
//    @Override
//    public void onErrorTranscoding(Video video, String message) {
//        Log.d(LOG_TAG, "onErrorTranscoding " + video.getTempPath() + " - " + message);
//        if(video.getNumTriesToExportVideo() < Constants.MAX_NUM_TRIES_TO_EXPORT_VIDEO){
//            video.increaseNumTriesToExportVideo();
//            Project currentProject = Project.getInstance(null, null, null, null);
//            launchTranscoderAddAVTransitionUseCase.applyAVTransitions(context
//                    .getDrawable(R.drawable.alpha_transition_white), video,
//                getVideonaFormatFromCurrentProjectUseCase.getVideonaFormatFromCurrentProject(),
//                currentProject.getProjectPathIntermediateFileAudioFade(), this);
//        } else {
//            videoRepository.setErrorTranscodingVideo(video,
//                    Constants.ERROR_TRANSCODING_TEMP_FILE_TYPE.AVTRANSITION.name());
//        }
//    }

//    @Override
//    public void videoToLaunchAVTransitionTempFile(
//            Video video, String intermediatesTempAudioFadeDirectory) {
//        video.setTempPath(currentProject.getProjectPathIntermediateFiles());
//
//        videoFormat = currentProject.getVMComposition().getVideoFormat();
//        Drawable drawableFadeTransitionVideo = currentProject.getVMComposition()
//                .getDrawableFadeTransitionVideo();
//
//        launchTranscoderAddAVTransitionUseCase
//                .applyAVTransitions(drawableFadeTransitionVideo, video, videoFormat,
//            intermediatesTempAudioFadeDirectory, this);
//    }
}
