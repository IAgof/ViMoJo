/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.videonasocialmedia.videonamediaframework.model.VMComposition;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.utils.ElementChangedListener;
import com.videonasocialmedia.videonamediaframework.playback.VMCompositionPlayer;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateComposition;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;

import com.videonasocialmedia.vimojo.presentation.mvp.views.DuplicateView;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.view.BackgroundExecutor;
import com.videonasocialmedia.vimojo.view.VimojoPresenter;

import javax.inject.Inject;
import javax.inject.Named;

import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_PLAYER_HEIGHT_VERTICAL_MODE;

/**
 * Created by vlf on 7/7/15.
 */
public class DuplicatePreviewPresenter extends VimojoPresenter implements ElementChangedListener {
    /**
     * LOG_TAG
     */
    private final String LOG_TAG = getClass().getSimpleName();
    private Context context;
    private DuplicateView duplicateView;
    private VMCompositionPlayer vmCompositionPlayerView;
    protected UserEventTracker userEventTracker;
    private AddVideoToProjectUseCase addVideoToProjectUseCase;
    private final ProjectInstanceCache projectInstanceCache;
    private Video videoToEdit;
    protected Project currentProject;
    private int videoIndexOnTrack;
    private UpdateComposition updateComposition;
    protected boolean amIAVerticalApp;

    /**
     * Get media list from project use case
     */
    @Inject
    public DuplicatePreviewPresenter(
        Context context, DuplicateView duplicateView,
        VMCompositionPlayer vmCompositionPlayerView,
        UserEventTracker userEventTracker, AddVideoToProjectUseCase addVideoToProjectUseCase,
        ProjectInstanceCache projectInstanceCache, UpdateComposition updateComposition,
        @Named("amIAVerticalApp") boolean amIAVerticalApp, BackgroundExecutor backgroundExecutor) {

        super(backgroundExecutor, userEventTracker);
        this.context = context;
        this.duplicateView = duplicateView;
        this.vmCompositionPlayerView = vmCompositionPlayerView;
        this.userEventTracker = userEventTracker;
        this.addVideoToProjectUseCase = addVideoToProjectUseCase;
        this.projectInstanceCache = projectInstanceCache;
        this.updateComposition = updateComposition;
        this.amIAVerticalApp = amIAVerticalApp;
    }

    public void updatePresenter(int videoIndexOnTrack) {
        this.videoIndexOnTrack = videoIndexOnTrack;
        this.currentProject = projectInstanceCache.getCurrentProject();
        currentProject.addListener(this);
        vmCompositionPlayerView.attachView(context);
        loadProjectVideo();
        if (amIAVerticalApp) {
            vmCompositionPlayerView.setAspectRatioVerticalVideos(DEFAULT_PLAYER_HEIGHT_VERTICAL_MODE);
        }
    }

    public void removePresenter() {
        vmCompositionPlayerView.detachView();
    }

    private void loadProjectVideo() {
        videoToEdit = (Video) currentProject.getVMComposition().getMediaTrack().getItems()
            .get(videoIndexOnTrack);
        VMComposition vmCompositionCopy = null;
        try {
            vmCompositionCopy = new VMComposition(currentProject.getVMComposition());
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            illegalItemOnTrack.printStackTrace();
            Crashlytics.log("Error getting copy VMComposition " + illegalItemOnTrack);
        }
        Video videoCopy = (Video) vmCompositionCopy.getMediaTrack().getItems().get(videoIndexOnTrack);
        vmCompositionPlayerView.initSingleClip(vmCompositionCopy, videoIndexOnTrack);
        duplicateView.initDuplicateView(videoCopy);
    }

    public void duplicateVideo(int numDuplicates) {
        for (int duplicates = 1; duplicates < numDuplicates; duplicates++) {
            addVideoToProjectUseCase.addVideoToProjectAtPosition(currentProject, getVideoCopy(),
                videoIndexOnTrack,
                new OnAddMediaFinishedListener() {
                    @Override
                    public void onAddMediaItemToTrackError() {
                        duplicateView.showError(String
                                .valueOf(R.string.addMediaItemToTrackError));
                    }

                    @Override
                    public void onAddMediaItemToTrackSuccess(Media media) {
                        executeUseCaseCall(() -> updateComposition.updateComposition(currentProject));
                    }
                });
        }
        userEventTracker.trackClipDuplicated(numDuplicates, currentProject);
        // TODO(jliarte): 18/07/18 deleteme
//        updateCompositionWithPlatform(currentProject);

    }

    @Override
    public void onObjectUpdated() {
        duplicateView.updateProject();
    }

    protected Video getVideoCopy() {
        return new Video(videoToEdit);
    }
}



