/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.domain.editor;

import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnRemoveMediaFinishedListener;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * This class is not used anywhere
 * // TODO(jliarte): 19/07/18 deleteme
 */
@Deprecated
public class CheckIfVideoFilesExistUseCase {
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    private RemoveVideoFromProjectUseCase removeVideoFromProjectUseCase;
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
                new OnVideosRetrieved() {
                    @Override
                    public void onVideosRetrieved(List<Video> videoList) {
                        ArrayList<Media> mediasToDeleteFromProject = new ArrayList<>();
                        for (Video video : videoList) {
                            String path = video.getMediaPath();
                            if (!fileExists(path)) {
                                mediasToDeleteFromProject.add(video);
                            }
                        }
                        if (mediasToDeleteFromProject.size() > 0) {
                            removeVideoFromProjectUseCase.removeMediaItemsFromProject(currentProject,
                                    mediasToDeleteFromProject, new OnRemoveMediaFinishedListener() {
                                        @Override
                                        public void onRemoveMediaItemFromTrackSuccess(List<Media> removedMedias) {

                                        }

                                        @Override
                                        public void onRemoveMediaItemFromTrackError() {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onNoVideosRetrieved() {

                    }
                });
    }


    private boolean fileExists(String path) {
        boolean result;
        File f = new File(path);
        if (f.exists() && !f.isDirectory()) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

}
