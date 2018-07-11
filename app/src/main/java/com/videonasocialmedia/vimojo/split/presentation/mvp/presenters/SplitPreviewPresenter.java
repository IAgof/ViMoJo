/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.split.presentation.mvp.presenters;

import android.util.Log;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import com.videonasocialmedia.videonamediaframework.model.media.utils.ElementChangedListener;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.cut.domain.model.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;

import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.split.domain.OnSplitVideoListener;
import com.videonasocialmedia.vimojo.split.presentation.mvp.views.SplitView;
import com.videonasocialmedia.vimojo.split.domain.SplitVideoUseCase;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.view.VimojoPresenter;
import com.videonasocialmedia.vimojo.vimojoapiclient.CompositionApiClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.Nullable;
import javax.inject.Inject;

/**
 * Created by vlf on 7/7/15.
 */
public class SplitPreviewPresenter extends VimojoPresenter implements OnVideosRetrieved,
    OnSplitVideoListener, ElementChangedListener {
    /**
     * LOG_TAG
     */
    private final String LOG_TAG = getClass().getSimpleName();
    private final ProjectInstanceCache projectInstanceCache;
    private SplitVideoUseCase splitVideoUseCase;

    private Video videoToEdit;

    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;

    private SplitView splitView;
    protected UserEventTracker userEventTracker;
    protected Project currentProject;

    private int maxSeekBarSplit;
    private int videoIndexOnTrack;
    private CompositionApiClient compositionApiClient;

    @Inject
    public SplitPreviewPresenter(
            SplitView splitView, UserEventTracker userEventTracker,
            SplitVideoUseCase splitVideoUseCase,
            GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
            ProjectInstanceCache projectInstanceCache, CompositionApiClient compositionApiClient) {
        this.splitView = splitView;
        this.userEventTracker = userEventTracker;
        this.splitVideoUseCase = splitVideoUseCase;
        this.getMediaListFromProjectUseCase = getMediaListFromProjectUseCase;
        this.projectInstanceCache = projectInstanceCache;
        this.compositionApiClient = compositionApiClient;
    }

    public void init(int videoIndexOnTrack) {
        this.videoIndexOnTrack = videoIndexOnTrack;
    }

    public void updatePresenter() {
        currentProject = projectInstanceCache.getCurrentProject();
        currentProject.addListener(this);
        loadProjectVideo(this.videoIndexOnTrack);
    }

    public void loadProjectVideo(int videoToTrimIndex) {
        List<Media> videoList = getMediaListFromProjectUseCase.getMediaListFromProject(currentProject);
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
        splitVideoUseCase.splitVideo(currentProject, videoToEdit, positionInAdapter,timeMs, this);
        trackSplitVideo();
        updateCompositionWithPlatform(currentProject);
    }

    public void trackSplitVideo() {
        userEventTracker.trackClipSplitted(currentProject);
    }

    private void updateCompositionWithPlatform(Project currentProject) {
        ListenableFuture<Project> compositionFuture = executeUseCaseCall(new Callable<Project>() {
            @Override
            public Project call() throws Exception {
                return compositionApiClient.addComposition(currentProject);
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