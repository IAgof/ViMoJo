/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import com.videonasocialmedia.videonamediaframework.model.media.utils.ElementChangedListener;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;

import com.videonasocialmedia.vimojo.presentation.mvp.views.DuplicateView;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

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

    private Video videoToEdit;

    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase =
            new GetMediaListFromProjectUseCase();
    private AddVideoToProjectUseCase addVideoToProjectUseCase;

    private DuplicateView duplicateView;
    protected UserEventTracker userEventTracker;
    protected Project currentProject;

    /**
     * Get media list from project use case
     */
    @Inject public DuplicatePreviewPresenter(DuplicateView duplicateView,
                                             UserEventTracker userEventTracker,
                                             ProjectRepository projectRepository,
                                             AddVideoToProjectUseCase addVideoToProjectUseCase) {
        this.duplicateView = duplicateView;
        this.userEventTracker = userEventTracker;
        this.addVideoToProjectUseCase = addVideoToProjectUseCase;
        this.currentProject = projectRepository.getCurrentProject();
        currentProject.addListener(this);
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
    }

    @Override
    public void onObjectUpdated() {
        duplicateView.updateProject();
    }
    public Video getVideoCopy() {
        return new Video(videoToEdit);
    }
}



