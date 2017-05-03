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
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelperListener;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.UpdateVideoResolutionToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.video.UpdateVideoRepositoryUseCase;
import com.videonasocialmedia.vimojo.export.domain.GetVideonaFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.LaunchTranscoderAddAVTransitionsUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;

import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.presentation.mvp.views.GalleryPagerView;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * This class is used for adding new videos to the project.
 */
public class GalleryPagerPresenter implements OnAddMediaFinishedListener,
    OnRemoveMediaFinishedListener, OnLaunchAVTransitionTempFileListener, TranscoderHelperListener {
    private String LOG_TAG = "GalleryPagerPresenter";

    private Context context;
    private final SharedPreferences preferences;
    private GetVideonaFormatFromCurrentProjectUseCase getVideonaFormatFromCurrentProjectUseCase;
    private AddVideoToProjectUseCase addVideoToProjectUseCase;
    GalleryPagerView galleryPagerView;
    protected Project currentProject;
    ArrayList<Integer> listErrorVideoIds = new ArrayList<>();
    private boolean differentVideoFormat;

    private Drawable drawableFadeTransitionVideo;
    private VideonaFormat videoFormat;
    private UpdateVideoRepositoryUseCase updateVideoRepositoryUseCase;
    private LaunchTranscoderAddAVTransitionsUseCase launchTranscoderAddAVTransitionUseCase;
    // TODO(jliarte): 3/05/17 init in constructor to inject it. Wrap android MMR with our own class
    MediaMetadataRetriever metadataRetriever;
    private final UpdateVideoResolutionToProjectUseCase updateVideoResolutionToProjectUseCase;

    /**
     * Constructor.
     */
    @Inject public GalleryPagerPresenter(
            GalleryPagerView galleryPagerView, Context context,
            AddVideoToProjectUseCase addVideoToProjectUseCase,
            UpdateVideoRepositoryUseCase updateVideoRepositoryUseCase,
            GetVideonaFormatFromCurrentProjectUseCase getVideonaFormatFromCurrentProjectUseCase,
            LaunchTranscoderAddAVTransitionsUseCase launchTranscoderAddAVTransitionsUseCase,
            UpdateVideoResolutionToProjectUseCase updateVideoResolutionToProjectUseCase,
            SharedPreferences preferences) {
        this.galleryPagerView = galleryPagerView;
        this.addVideoToProjectUseCase = addVideoToProjectUseCase;
        this.updateVideoRepositoryUseCase = updateVideoRepositoryUseCase;
        this.getVideonaFormatFromCurrentProjectUseCase = getVideonaFormatFromCurrentProjectUseCase;
        this.launchTranscoderAddAVTransitionUseCase = launchTranscoderAddAVTransitionsUseCase;
        this.currentProject = loadCurrentProject();
        this.context = context;
        this.updateVideoResolutionToProjectUseCase = updateVideoResolutionToProjectUseCase;
        this.preferences = preferences;
        metadataRetriever = new MediaMetadataRetriever();
    }

    public Project loadCurrentProject() {
        // TODO(jliarte): this should make use of a repository or use case to load the Project
        return Project.getInstance(null, null, null);
    }

    public void loadVideoListToProject(List<Video> videoList) {
        List<Video> checkedVideoList = checkFormatVideoSelected(videoList);

        if(listErrorVideoIds.size() > 0){
            galleryPagerView.showDialogVideosNotAddedFromGallery(listErrorVideoIds);
            differentVideoFormat = true;
        }
        addVideoToProject(checkedVideoList);
    }

    private void addVideoToProject(List<Video> checkedVideoList) {
        addVideoToProjectUseCase.addVideoListToTrack(checkedVideoList, this, this);
    }

    public List<Video> checkFormatVideoSelected(List<Video> videoList) {
        List<Video> checkedFortmatVideoList = new ArrayList<>();
        updateProfileForEmptyProject(currentProject, videoList);
        VideoResolution projectProfileVideoResolution
                = currentProject.getProfile().getVideoResolution();
        for (int index = 0; index < videoList.size(); index++) {
            Video video = videoList.get(index);
            try {
                metadataRetriever.setDataSource(video.getMediaPath());
                String duration = metadataRetriever.extractMetadata(
                        MediaMetadataRetriever.METADATA_KEY_DURATION);

                int durationInt = Integer.parseInt(duration);
                video.setDuration(durationInt);
                video.setStopTime(durationInt);

                String width = metadataRetriever.extractMetadata(
                        MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                if (width.compareTo(String
                        .valueOf(projectProfileVideoResolution.getWidth())) != 0) {
                    listErrorVideoIds.add(index + 1);
                } else {
                    checkedFortmatVideoList.add(video);
                }
            } catch (Exception e) {
                video.setDuration(0);
                e.printStackTrace();
            }
        }
        return checkedFortmatVideoList;
    }

    protected void updateProfileForEmptyProject(Project currentProject, List<Video> videoList) {
        if ((currentProject.getVMComposition().getMediaTrack().getItems().size() == 0)
                && (videoList.size() > 0)) {
            Video firstVideo = videoList.get(0);
            metadataRetriever.setDataSource(firstVideo.getMediaPath());
            String videoWidth =
                    metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            VideoResolution.Resolution resolutionForWidth = getResolutionForWidth(videoWidth);

            updateVideoResolutionToProjectUseCase.updateResolution(resolutionForWidth);
            SharedPreferences.Editor preferencesEditor = preferences.edit();
            preferencesEditor.putString(ConfigPreferences.KEY_LIST_PREFERENCES_RESOLUTION,
                    getPreferenceResolutionForWidth(videoWidth));
            preferencesEditor.commit();
        }
    }

    private String getPreferenceResolutionForWidth(String videoWidth) {
        // TODO(jliarte): 3/05/17 move to a use case or somewhere else?
        switch (videoWidth) {
            case "4096":
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
        if(!differentVideoFormat)
            galleryPagerView.navigate();
    }

    @Override
    public void onSuccessTranscoding(Video video) {
        Log.d(LOG_TAG, "onSuccessTranscoding " + video.getTempPath());
        updateVideoRepositoryUseCase.succesTranscodingVideo(video);
    }

    @Override
    public void onErrorTranscoding(Video video, String message) {
        Log.d(LOG_TAG, "onErrorTranscoding " + video.getTempPath() + " - " + message);
        if(video.getNumTriesToExportVideo() < Constants.MAX_NUM_TRIES_TO_EXPORT_VIDEO){
            video.increaseNumTriesToExportVideo();
            Project currentProject = Project.getInstance(null, null, null);
            launchTranscoderAddAVTransitionUseCase.launchExportTempFile(context
                    .getDrawable(R.drawable.alpha_transition_white), video,
                getVideonaFormatFromCurrentProjectUseCase.getVideonaFormatFromCurrentProject(),
                currentProject.getProjectPathIntermediateFileAudioFade(), this);
        } else {
            updateVideoRepositoryUseCase.errorTranscodingVideo(video,
                Constants.ERROR_TRANSCODING_TEMP_FILE_TYPE.AVTRANSITION.name());
        }
    }

    @Override
    public void videoToLaunchAVTransitionTempFile(Video video,
                                                  String intermediatesTempAudioFadeDirectory) {

        video.setTempPath(currentProject.getProjectPathIntermediateFiles());

        videoFormat = getVideonaFormatFromCurrentProjectUseCase.getVideonaFormatFromCurrentProject();
        drawableFadeTransitionVideo = context.getDrawable(R.drawable.alpha_transition_white);

        launchTranscoderAddAVTransitionUseCase.launchExportTempFile(drawableFadeTransitionVideo, video, videoFormat,
            intermediatesTempAudioFadeDirectory, this);
    }
}
