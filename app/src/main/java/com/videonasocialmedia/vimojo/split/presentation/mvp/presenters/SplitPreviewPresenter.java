/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.split.presentation.mvp.presenters;

import android.content.Context;


import com.videonasocialmedia.videonamediaframework.model.media.utils.ElementChangedListener;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;

import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.vimojo.split.domain.OnSplitVideoListener;
import com.videonasocialmedia.vimojo.split.presentation.mvp.views.SplitView;
import com.videonasocialmedia.vimojo.split.domain.SplitVideoUseCase;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by vlf on 7/7/15.
 */
public class SplitPreviewPresenter implements OnVideosRetrieved, OnSplitVideoListener,
        ElementChangedListener {

    /**
     * LOG_TAG
     */
    private final String LOG_TAG = getClass().getSimpleName();
    private final Context context;
    private final VideoRepository videoRepository;
    private SplitVideoUseCase splitVideoUseCase;

    private Video videoToEdit;

    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;

    private SplitView splitView;
    public UserEventTracker userEventTracker;
    public Project currentProject;

    private int maxSeekBarSplit;

    @Inject
    public SplitPreviewPresenter(SplitView splitView, UserEventTracker userEventTracker,
                                 Context context, VideoRepository videoRepository,
                                 SplitVideoUseCase splitVideoUseCase,
                                 GetMediaListFromProjectUseCase getMediaListFromProjectUseCase) {
        this.splitView = splitView;
        this.userEventTracker = userEventTracker;
        this.context = context;
        this.videoRepository = videoRepository;
        this.splitVideoUseCase = splitVideoUseCase;
        this.getMediaListFromProjectUseCase = getMediaListFromProjectUseCase;
        this.currentProject = loadCurrentProject();
        currentProject.addListener(this);
    }

    private Project loadCurrentProject() {
        return Project.getInstance(null, null, null, null);
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
        maxSeekBarSplit =  video.getStopTime() - video.getStartTime();
        splitView.initSplitView(video.getStartTime(), maxSeekBarSplit);
    }

    @Override
    public void onNoVideosRetrieved() {
        splitView.showError(R.string.onNoVideosRetrieved);
    }


    public void splitVideo(int positionInAdapter, int timeMs) {
        splitVideoUseCase.splitVideo(videoToEdit, positionInAdapter,timeMs, this);
        trackSplitVideo();
    }

    public void trackSplitVideo() {
        userEventTracker.trackClipSplitted(currentProject);
    }

    @Override
    public void showErrorSplittingVideo() {
        splitView.showError(R.string.addMediaItemToTrackError);
    }

    public void advanceBackwardStartSplitting(int advancePlayerPrecision,
                                              int currentSplitPosition) {
        int progress = 0;
        if(currentSplitPosition > advancePlayerPrecision) {
            progress = currentSplitPosition - advancePlayerPrecision;
        }
        splitView.updateSplitSeekbar(progress);
    }

    public void advanceForwardEndSplitting(int advancePlayerPrecision, int currentSplitPosition) {
        int progress = currentSplitPosition + advancePlayerPrecision;
        splitView.updateSplitSeekbar(Math.min(maxSeekBarSplit, progress));
    }

    @Override
    public void onObjectUpdated() {
        splitView.updateProject();
    }
}