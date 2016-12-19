/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.domain.editor;

import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.video.VideoRealmRepository;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmQuery;

public class GetMediaListFromProjectUseCase {

    private ProjectRepository projectRepository = new ProjectRealmRepository();
    private VideoRepository videoRepository = new VideoRealmRepository();

    /**
     * @return
     */
    public List<Media> getMediaListFromProject() {
        Project project = projectRepository.getCurrentProject();
        Track track=project.getMediaTrack();
        return track.getItems();
    }

    public List<Video> getVideoListFromProject() {
        return videoRepository.getListVideos();
    }


    public void getMediaListFromProject(OnVideosRetrieved listener){
        Project project = projectRepository.getCurrentProject();
        Track track=project.getMediaTrack();
        List items= track.getItems();
        if (items.size()>0)
            listener.onVideosRetrieved(items);
        else
            listener.onNoVideosRetrieved();
    }
}
