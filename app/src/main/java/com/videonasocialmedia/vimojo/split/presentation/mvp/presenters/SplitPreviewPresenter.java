/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.split.presentation.mvp.presenters;

import com.videonasocialmedia.videonamediaframework.model.media.utils.ElementChangedListener;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateComposition;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;

import com.videonasocialmedia.vimojo.split.domain.OnSplitVideoListener;
import com.videonasocialmedia.vimojo.split.presentation.mvp.views.SplitView;
import com.videonasocialmedia.vimojo.split.domain.SplitVideoUseCase;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.view.VimojoPresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by vlf on 7/7/15.
 */
public class SplitPreviewPresenter extends VimojoPresenter implements ElementChangedListener {
    private final String LOG_TAG = SplitPreviewPresenter.class.getSimpleName();
    private final ProjectInstanceCache projectInstanceCache;
    private SplitVideoUseCase splitVideoUseCase;

    private Video videoToEdit;

    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;

    private SplitView splitView;
    protected UserEventTracker userEventTracker;
    protected Project currentProject;

    private int maxSeekBarSplit;
    private int videoIndexOnTrack;
    private UpdateComposition updateComposition;
    private boolean amIAVerticalApp;

    @Inject
    public SplitPreviewPresenter(
        SplitView splitView, UserEventTracker userEventTracker,
        SplitVideoUseCase splitVideoUseCase,
        GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
        ProjectInstanceCache projectInstanceCache, UpdateComposition updateComposition,
        @Named("amIAVerticalApp") boolean amIAVerticalApp) {
        this.splitView = splitView;
        this.userEventTracker = userEventTracker;
        this.splitVideoUseCase = splitVideoUseCase;
        this.getMediaListFromProjectUseCase = getMediaListFromProjectUseCase;
        this.projectInstanceCache = projectInstanceCache;
        this.updateComposition = updateComposition;
        this.amIAVerticalApp = amIAVerticalApp;
    }

    public void init(int videoIndexOnTrack) {
        this.videoIndexOnTrack = videoIndexOnTrack;
    }

    public void updatePresenter() {
        currentProject = projectInstanceCache.getCurrentProject();
        currentProject.addListener(this);
        loadProjectVideo(this.videoIndexOnTrack);
        if (amIAVerticalApp) {
            splitView.setAspectRatioVerticalVideos();
        }
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

    public void onVideosRetrieved(List<Video> videoList) {
        splitView.showPreview(videoList);
        Video video = videoList.get(0);
        if(video.hasText()) {
            VideoResolution videoResolution = currentProject.getProfile().getVideoResolution();
            splitView.showText(video.getClipText(), video.getClipTextPosition(),
                videoResolution.getWidth(), videoResolution.getHeight());
        }
        maxSeekBarSplit =  video.getStopTime() - video.getStartTime();
        splitView.initSplitView(video.getStartTime(), maxSeekBarSplit);
    }

    public void splitVideo(int positionInAdapter, int timeMs) {
        splitVideoUseCase.splitVideo(currentProject, videoToEdit, positionInAdapter, timeMs,
                new OnSplitVideoListener() {
            @Override
            public void onSuccessSplittingVideo() {
                // TODO(jliarte): 18/07/18 deal with this case for updating project and videos
                executeUseCaseCall(() -> updateComposition.updateComposition(currentProject));
            }

            @Override
            public void showErrorSplittingVideo() {
                splitView.showError(R.string.addMediaItemToTrackError);
            }
        });
        trackSplitVideo();
    }

    public void trackSplitVideo() {
        userEventTracker.trackClipSplitted(currentProject);
    }

    public void advanceBackwardStartSplitting(int advancePlayerPrecision,
                                              int currentSplitPosition) {
        int progress = 0;
        if (currentSplitPosition > advancePlayerPrecision) {
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
