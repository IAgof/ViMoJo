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
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import java.util.List;

public class GetMediaListFromProjectUseCase {

    protected ProjectRepository projectRepository = new ProjectRealmRepository();

    /**
     * @return
     */
    public List<Media> getMediaListFromProject() {
        Project project = loadCurrentProject();
        Track track=project.getMediaTrack();
        return track.getItems();
    }

    public void getMediaListFromProject(OnVideosRetrieved listener){
        Project project = loadCurrentProject();
        Track track=project.getMediaTrack();
        List items= track.getItems();
        if (items.size()>0)
            listener.onVideosRetrieved(items);
        else
            listener.onNoVideosRetrieved();
    }

    public Project loadCurrentProject() {
        Project project = projectRepository.getCurrentProject();

        if(project == null){
            return Project.getInstance(null, null, null);
        }
        return projectRepository.getCurrentProject();
    }
}
