/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.domain.editor;

import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnRemoveMediaFinishedListener;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class CheckIfVideoFilesExistUseCase implements OnVideosRetrieved,
        OnRemoveMediaFinishedListener {
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    private RemoveVideoFromProjectUseCase removeVideoFromProjectUseCase;
    private ProjectRepository projectRepository;
    private Project currentProject;

    @Inject
    public CheckIfVideoFilesExistUseCase(
        Project currentProject,
        GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
        RemoveVideoFromProjectUseCase removeVideoFromProjectUseCase) {
        this.getMediaListFromProjectUseCase = getMediaListFromProjectUseCase;
        this.removeVideoFromProjectUseCase = removeVideoFromProjectUseCase;
        this.currentProject = currentProject;
    }

    public void check() {
        getMediaListFromProjectUseCase.getMediaListFromProject(currentProject,
            this);
    }

    @Override
    public void onVideosRetrieved(List<Video> videoList) {
        ArrayList<Media> mediasToDeleteFromProject = new ArrayList<>();
        for(Video video : videoList) {
            String path = video.getMediaPath();
            if(!fileExists(path)) {
                mediasToDeleteFromProject.add(video);
            }
        }
        if(mediasToDeleteFromProject.size() > 0) {
            removeVideoFromProjectUseCase.removeMediaItemsFromProject(currentProject,
                mediasToDeleteFromProject,
                    this);
        }
    }

    private boolean fileExists(String path) {
        boolean result;
        File f = new File(path);
        if(f.exists() && !f.isDirectory()) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public void onNoVideosRetrieved() {

    }

    @Override
    public void onRemoveMediaItemFromTrackError() {

    }

    @Override
    public void onRemoveMediaItemFromTrackSuccess() {

    }
}
