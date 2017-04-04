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
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;

import com.videonasocialmedia.transcoder.video.format.VideoTranscoderFormat;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelperListener;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.video.UpdateVideoRepositoryUseCase;
import com.videonasocialmedia.vimojo.export.domain.GetVideoFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.LaunchTranscoderAddAVTransitionsUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;

import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.presentation.mvp.views.GalleryPagerView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * This class is used for adding new videos to the project.
 */
public class GalleryPagerPresenter implements OnAddMediaFinishedListener,
    OnRemoveMediaFinishedListener, OnLaunchAVTransitionTempFileListener, TranscoderHelperListener {

    private Context context;
    private AddVideoToProjectUseCase addVideoToProjectUseCase;
    GalleryPagerView galleryPagerView;
    protected Project currentProject;
    ArrayList<Integer> listErrorVideoIds = new ArrayList<>();
    private boolean differentVideoFormat;

    private Drawable drawableFadeTransitionVideo;
    private VideoTranscoderFormat videoFormat;
    private UpdateVideoRepositoryUseCase updateVideoRepositoryUseCase;
    private LaunchTranscoderAddAVTransitionsUseCase launchTranscoderAddAVTransitionUseCase;

    /**
     * Constructor.
     */
    @Inject public GalleryPagerPresenter(GalleryPagerView galleryPagerView,
                                 AddVideoToProjectUseCase addVideoToProjectUseCase,
                                 UpdateVideoRepositoryUseCase updateVideoRepositoryUseCase,
                                 LaunchTranscoderAddAVTransitionsUseCase
                                             launchTranscoderAddAVTransitionsUseCase,
                                 Context context) {
        this.galleryPagerView = galleryPagerView;
        this.addVideoToProjectUseCase = addVideoToProjectUseCase;
        this.updateVideoRepositoryUseCase = updateVideoRepositoryUseCase;
        this.launchTranscoderAddAVTransitionUseCase = launchTranscoderAddAVTransitionsUseCase;
        this.currentProject = loadCurrentProject();
        this.context = context;
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
        VideoResolution videoResolution = currentProject.getProfile().getVideoResolution();
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        for (int index = 0; index < videoList.size(); index++) {
            Video video = videoList.get(index);
            try {
                retriever.setDataSource(video.getMediaPath());
                String duration = retriever.extractMetadata(
                        MediaMetadataRetriever.METADATA_KEY_DURATION);

                int durationInt = Integer.parseInt(duration);
                video.setDuration(durationInt);
                video.setStopTime(durationInt);

                String width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                if(width.compareTo(String.valueOf(videoResolution.getWidth())) != 0){
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
        updateVideoRepositoryUseCase.updateVideo(video);
    }

    @Override
    public void onErrorTranscoding(Video video, String message) {

    }

    @Override
    public void videoToLaunchAVTransitionTempFile(Video video,
                                                  String intermediatesTempAudioFadeDirectory) {

        video.setTempPath(currentProject.getProjectPathIntermediateFiles());

        videoFormat = currentProject.getVMComposition().getVideoFormat();
        drawableFadeTransitionVideo = context.getDrawable(R.drawable.alpha_transition_white);

        launchTranscoderAddAVTransitionUseCase.launchExportTempFile(drawableFadeTransitionVideo, video, videoFormat,
            intermediatesTempAudioFadeDirectory, this);
    }
}
