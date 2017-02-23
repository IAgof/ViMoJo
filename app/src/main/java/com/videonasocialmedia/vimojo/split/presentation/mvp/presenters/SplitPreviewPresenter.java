/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.split.presentation.mvp.presenters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.videonasocialmedia.transcoder.MediaTranscoderListener;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.domain.video.UpdateVideoRepositoryUseCase;
import com.videonasocialmedia.vimojo.export.domain.GetVideonaFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;

import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.split.domain.OnSplitVideoListener;
import com.videonasocialmedia.vimojo.split.presentation.mvp.views.SplitView;
import com.videonasocialmedia.vimojo.split.domain.SplitVideoUseCase;
import com.videonasocialmedia.vimojo.trim.domain.ModifyVideoDurationUseCase;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by vlf on 7/7/15.
 */
public class SplitPreviewPresenter implements OnVideosRetrieved, OnSplitVideoListener,
    MediaTranscoderListener {

    /**
     * LOG_TAG
     */
    private final String LOG_TAG = getClass().getSimpleName();
    private final Context context;
    private SplitVideoUseCase splitVideoUseCase;

    private Video videoToEdit;

    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    private ModifyVideoDurationUseCase modifyVideoDurationUseCase;
    private GetVideonaFormatFromCurrentProjectUseCase getVideonaFormatFromCurrentProjectUseCase;
    private UpdateVideoRepositoryUseCase updateVideoRepositoryUseCase;

    private SplitView splitView;
    public UserEventTracker userEventTracker;
    public Project currentProject;

    @Inject
    public SplitPreviewPresenter(SplitView splitView, UserEventTracker userEventTracker,
                                 Context context, SplitVideoUseCase splitVideoUseCase,
                                 GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
                                 ModifyVideoDurationUseCase modifyVideoDurationUseCase,
                                 GetVideonaFormatFromCurrentProjectUseCase
                                         getVideonaFormatFromCurrentProjectUseCase,
                                 UpdateVideoRepositoryUseCase updateVideoRepositoryUseCase) {
        this.splitView = splitView;
        this.userEventTracker = userEventTracker;
        this.context = context;
        this.splitVideoUseCase = splitVideoUseCase;
        this.getMediaListFromProjectUseCase = getMediaListFromProjectUseCase;
        this.modifyVideoDurationUseCase = modifyVideoDurationUseCase;
        this.getVideonaFormatFromCurrentProjectUseCase = getVideonaFormatFromCurrentProjectUseCase;
        this.updateVideoRepositoryUseCase = updateVideoRepositoryUseCase;
        this.currentProject = loadCurrentProject();

    }

    private Project loadCurrentProject() {
        return Project.getInstance(null, null, null);
    }

    public void loadProjectVideo(int videoToTrimIndex) {
        List<Media> videoList = getMediaListFromProjectUseCase.getMediaListFromProject();
        if (videoList != null) {
            ArrayList<Video> v = new ArrayList<>();
            videoToEdit = (Video) videoList.get(videoToTrimIndex);
            v.add(videoToEdit);
            onVideosRetrieved(v);
        }

    }

    @Override
    public void onVideosRetrieved(List<Video> videoList) {
        splitView.showPreview(videoList);
        Video video = videoList.get(0);
        if(video.hasText())
            splitView.showText(video.getClipText(), video.getClipTextPosition());
        splitView.initSplitView(video.getStartTime(), video.getStopTime() - video.getStartTime());
    }

    @Override
    public void onNoVideosRetrieved() {
        splitView.showError("No videos");
    }


    public void splitVideo(Video video, int positionInAdapter, int timeMs) {
        splitVideoUseCase.splitVideo(video, positionInAdapter,timeMs, this);
        trackSplitVideo();
    }

    public void trackSplitVideo() {
        userEventTracker.trackClipSplitted(currentProject);
    }

    @Override
    public void trimVideo(Video video, int startTimeMs, int finishTimeMs) {
        VideonaFormat videoFormat =
            getVideonaFormatFromCurrentProjectUseCase.getVideonaFormatFromCurrentProject();
        // TODO:(alvaro.martinez) 22/02/17 This drawable saved in app or sdk?
        Drawable drawableFadeTransitionVideo =
            ContextCompat.getDrawable(context, R.drawable.alpha_transition_black);

        modifyVideoDurationUseCase.trimVideo(drawableFadeTransitionVideo, videoToEdit, videoFormat,
            startTimeMs, finishTimeMs, currentProject.getProjectPathIntermediateFileAudioFade(),
            this);
    }

    @Override
    public void onSuccessTranscoding(Video video) {
        // update videoRepository
        updateVideoRepositoryUseCase.updateVideo(video);
    }

    @Override
    public void onErrorTranscoding(Video video, String message) {
        //splitView.showError(message);
    }
}



