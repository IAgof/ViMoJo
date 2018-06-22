/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.util.Log;

import com.videonasocialmedia.videonamediaframework.model.media.utils.ElementChangedListener;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;

import com.videonasocialmedia.vimojo.presentation.mvp.views.DuplicateView;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.vimojoapiclient.CompositionApiClient;
import com.videonasocialmedia.vimojo.vimojoapiclient.VimojoApiException;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by vlf on 7/7/15.
 */
public class DuplicatePreviewPresenter implements OnVideosRetrieved, ElementChangedListener {
    /**
     * LOG_TAG
     */
    private final String LOG_TAG = getClass().getSimpleName();
    private DuplicateView duplicateView;
    protected UserEventTracker userEventTracker;
    private AddVideoToProjectUseCase addVideoToProjectUseCase;
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    private final ProjectInstanceCache projectInstanceCache;

    private Video videoToEdit;
    protected Project currentProject;
    private int videoIndexOnTrack;
    private CompositionApiClient compositionApiClient;

    /**
     * Get media list from project use case
     */
    @Inject public DuplicatePreviewPresenter(
            DuplicateView duplicateView, UserEventTracker userEventTracker,
            AddVideoToProjectUseCase addVideoToProjectUseCase,
            GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
            ProjectInstanceCache projectInstanceCache, CompositionApiClient compositionApiClient) {
        this.duplicateView = duplicateView;
        this.userEventTracker = userEventTracker;
        this.addVideoToProjectUseCase = addVideoToProjectUseCase;
        this.getMediaListFromProjectUseCase = getMediaListFromProjectUseCase;
        this.projectInstanceCache = projectInstanceCache;
        this.compositionApiClient = compositionApiClient;
    }

    public void init(int videoIndexOnTrack) {
        this.videoIndexOnTrack = videoIndexOnTrack;
    }

    public void updatePresenter() {
        this.currentProject = projectInstanceCache.getCurrentProject();
        currentProject.addListener(this);
        loadProjectVideo(videoIndexOnTrack);
    }

    public void loadProjectVideo(int videoIndex) {
        List<Media> videoList = getMediaListFromProjectUseCase.getMediaListFromProject(currentProject);
        if (videoList != null) {
            ArrayList<Video> v = new ArrayList<>();
            videoToEdit = (Video) videoList.get(videoIndex);
            v.add(videoToEdit);
            onVideosRetrieved(v);
        }
    }

    @Override
    public void onVideosRetrieved(List<Video> videoList) {
        duplicateView.showPreview(videoList);
        duplicateView.initDuplicateView(videoList.get(0).getMediaPath());
    }

    @Override
    public void onNoVideosRetrieved() {
        duplicateView.showError("No videos");
    }

    public void duplicateVideo(int positionInAdapter, int numDuplicates) {
        for (int duplicates = 1; duplicates < numDuplicates; duplicates++) {
            //Video copyVideo = new Video(getVideoCopy());
            addVideoToProjectUseCase.addVideoToProjectAtPosition(currentProject, getVideoCopy(),
                positionInAdapter,
                new OnAddMediaFinishedListener() {
                    @Override
                    public void onAddMediaItemToTrackError() {
                        duplicateView.showError(String
                                .valueOf(R.string.addMediaItemToTrackError));
                    }

                    @Override
                    public void onAddMediaItemToTrackSuccess(Media media) {

                    }
                });
        }
        userEventTracker.trackClipDuplicated(numDuplicates, currentProject);
        updateCompositionWithPlatform(currentProject);

    }
    @Override
    public void onObjectUpdated() {
        duplicateView.updateProject();
    }

    public Video getVideoCopy() {
        return new Video(videoToEdit);
    }

    private void updateCompositionWithPlatform(Project currentProject) {
        try {
            compositionApiClient.uploadComposition(currentProject);
        } catch (VimojoApiException e) {
            Log.d(LOG_TAG, "Error uploading composition with server " + e.getApiErrorCode());
            e.printStackTrace();
        }
    }
}



