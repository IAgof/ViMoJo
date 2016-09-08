/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.trim.presentation.mvp.presenters;

import android.content.Context;
import android.content.Intent;

import com.videonasocialmedia.vimojo.VimojoApplication;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Media;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.trim.presentation.mvp.views.TrimView;
import com.videonasocialmedia.vimojo.utils.ExportIntentConstants;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.export.ExportTempBackgroundService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vlf on 7/7/15.
 */
public class TrimPreviewPresenter implements OnVideosRetrieved {

    /**
     * LOG_TAG
     */
    private final String LOG_TAG = getClass().getSimpleName();

    private Video videoToEdit;

    /**
     * Get media list from project use case
     */
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;


    private TrimView trimView;
    public UserEventTracker userEventTracker;
    public Project currentProject;

    public TrimPreviewPresenter(TrimView trimView, UserEventTracker userEventTracker) {
        this.trimView = trimView;
        getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        this.currentProject = loadCurrentProject();
        this.userEventTracker = userEventTracker;
    }

    private Project loadCurrentProject() {
        // TODO(jliarte): this should make use of a repository or use case to load the Project
        return Project.getInstance(null, null, null);
    }

    public void init(int videoToTrimIndex) {
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
        trimView.showPreview(videoList);
        Video video = videoList.get(0);
        //showTimeTags(video);
        if(video.isTextToVideoAdded())
           // trimView.showText(video.getTextToVideo(),video.getTextPositionToVideo());
        trimView.showTrimBar(video.getStartTime(), video.getStopTime(), video.getFileDuration());
    }

    private void showTimeTags(Video video) {
        trimView.refreshDurationTag(video.getDuration());
        trimView.refreshStartTimeTag(video.getStartTime());
        trimView.refreshStopTimeTag(video.getStopTime());
    }

    @Override
    public void onNoVideosRetrieved() {
        trimView.showError("No videos");
    }


    public void setTrim(int startTimeMs, int finishTimeMs) {
        Context appContext = VimojoApplication.getAppContext();
        Intent trimServiceIntent = new Intent(appContext, ExportTempBackgroundService.class);
        trimServiceIntent.putExtra(ExportIntentConstants.VIDEO_ID, videoToEdit.getIdentifier());
        trimServiceIntent.putExtra(ExportIntentConstants.IS_VIDEO_TRIMMED, true);
        trimServiceIntent.putExtra(ExportIntentConstants.START_TIME_MS, startTimeMs);
        trimServiceIntent.putExtra(ExportIntentConstants.FINISH_TIME_MS, finishTimeMs);
        appContext.startService(trimServiceIntent);
        trackVideoTrimmed();
    }

    public void trackVideoTrimmed() {
        userEventTracker.trackClipTrimmed(currentProject);
    }

    public void updateVideoTrim(int videoIndexOnTrack, int startTimeMs, int finishTimeMs) {
        List<Media> videoList = getMediaListFromProjectUseCase.getMediaListFromProject();
        if (videoList != null) {
            ArrayList<Video> v = new ArrayList<>();
            videoToEdit = (Video) videoList.get(videoIndexOnTrack);
            v.add(videoToEdit);
            onVideosRetrieved(v);
            trimView.showPreview(v);
            Video video = v.get(0);
            trimView.showTrimBar(startTimeMs, finishTimeMs, video.getDuration());
        }

    }
}



