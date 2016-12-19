/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.split.presentation.mvp.presenters;

import android.content.Context;
import android.content.Intent;

import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.export.ExportTempBackgroundService;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;

import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.split.domain.OnSplitVideoListener;
import com.videonasocialmedia.vimojo.split.presentation.mvp.views.SplitView;
import com.videonasocialmedia.vimojo.split.domain.SplitVideoUseCase;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.IntentConstants;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vlf on 7/7/15.
 */
public class SplitPreviewPresenter implements OnVideosRetrieved, OnSplitVideoListener {

    /**
     * LOG_TAG
     */
    private final String LOG_TAG = getClass().getSimpleName();
    private SplitVideoUseCase splitVideoUseCase;

    private Video videoToEdit;

    /**
     * Get media list from project use case
     */
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;

    private SplitView splitView;
    public UserEventTracker userEventTracker;
    public Project currentProject;
    private ProjectRepository projectRepository = new ProjectRealmRepository();

    public SplitPreviewPresenter(SplitView splitView, UserEventTracker userEventTracker) {
        this.splitView = splitView;
        getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        splitVideoUseCase= new SplitVideoUseCase();
        this.currentProject = loadCurrentProject();
        this.userEventTracker = userEventTracker;
    }

    private Project loadCurrentProject() {
        return projectRepository.getCurrentProject();
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
        Context appContext = VimojoApplication.getAppContext();
        Intent trimServiceIntent = new Intent(appContext, ExportTempBackgroundService.class);
        trimServiceIntent.putExtra(IntentConstants.VIDEO_ID, video.getUuid());
        trimServiceIntent.putExtra(IntentConstants.IS_VIDEO_TRIMMED, true);
        trimServiceIntent.putExtra(IntentConstants.START_TIME_MS, startTimeMs);
        trimServiceIntent.putExtra(IntentConstants.FINISH_TIME_MS, finishTimeMs);
        // TODO:(alvaro.martinez) 22/11/16 use project tmp path
        trimServiceIntent.putExtra(IntentConstants.VIDEO_TEMP_DIRECTORY,
            currentProject.getProjectPath() + Constants.FOLDER_INTERMEDIATE_FILES);
        appContext.startService(trimServiceIntent);
    }
}



