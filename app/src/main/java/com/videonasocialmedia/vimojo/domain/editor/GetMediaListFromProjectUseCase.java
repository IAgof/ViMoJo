/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.domain.editor;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;

import java.util.List;

import javax.inject.Inject;

public class GetMediaListFromProjectUseCase {

    @Inject
    public GetMediaListFromProjectUseCase() {

    }

    /**
     * @return
     */
    public List<Media> getMediaListFromProject(Project currentProject) {
        Track track = currentProject.getMediaTrack();
        return track.getItems();
    }

    public void getMediaListFromProject(Project currentProject, OnVideosRetrieved listener) {
        Track track = currentProject.getMediaTrack();
        List items = track.getItems();
        if ( items.size() > 0 ) {
            listener.onVideosRetrieved(items);
        } else {
            listener.onNoVideosRetrieved();
        }
    }

}
