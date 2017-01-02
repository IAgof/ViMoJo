/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Veronica Lago Fominaya
 */

package com.videonasocialmedia.vimojo.domain.editor;

import com.videonasocialmedia.vimojo.eventbus.events.AddMediaItemToTrackSuccessEvent;
import com.videonasocialmedia.vimojo.eventbus.events.project.UpdateProjectDurationEvent;
import com.videonasocialmedia.vimojo.eventbus.events.video.NumVideosChangedEvent;
import com.videonasocialmedia.vimojo.eventbus.events.video.VideoAddedToTrackEvent;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.track.MediaTrack;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnAddMediaFinishedListener;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * This class is used to add a new videos to the project.
 */
// TODO(jliarte): 22/10/16 refactor this class to have a unique insert point. Get rid of event bus
public class AddVideoToProjectUseCase {
    protected ProjectRepository projectRepository;

    /**
     * Default Constructor with project repository argument.
     *
     * @param projectRepository the project repository
     */
    @Inject public AddVideoToProjectUseCase(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    /**
     * @param videoPath
     */
    public void addVideoToTrack(String videoPath) {
        Video videoToAdd = new Video(videoPath);
        addVideoToTrack(videoToAdd);
    }

    public void addVideoToTrack(Video video) {
        try {
            Project currentProject = Project.getInstance(null, null, null);
            MediaTrack mediaTrack = currentProject.getMediaTrack();
            mediaTrack.insertItem(video);
            projectRepository.update(currentProject);
            // TODO(jliarte): 22/10/16 should get rid of EventBus calls?
            EventBus.getDefault().post(new AddMediaItemToTrackSuccessEvent(video));
            EventBus.getDefault().post(new UpdateProjectDurationEvent(currentProject.getDuration()));
            EventBus.getDefault().post(new NumVideosChangedEvent(currentProject.getMediaTrack().getNumVideosInProject()));
            EventBus.getDefault().post(new VideoAddedToTrackEvent());
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            //TODO manejar error
        }
    }

    /**
     * @param video
     * @param listener
     * @deprecated use the one parameter version instead
     */
    public void addVideoToTrack(Video video, OnAddMediaFinishedListener listener) {
        try {
            Project currentProject = Project.getInstance(null, null, null);
            MediaTrack mediaTrack = currentProject.getMediaTrack();
            mediaTrack.insertItem(video);
            projectRepository.update(currentProject);
            listener.onAddMediaItemToTrackSuccess(video);
            // TODO(jliarte): 22/10/16 should get rid of EventBus calls?
            EventBus.getDefault().post(new UpdateProjectDurationEvent(currentProject.getDuration()));
            EventBus.getDefault().post(new NumVideosChangedEvent(currentProject.getMediaTrack().getNumVideosInProject()));
            EventBus.getDefault().post(new VideoAddedToTrackEvent());
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            listener.onAddMediaItemToTrackError();
        }
    }

    public void addVideoToProjectAtPosition(Video video, int position) {
        try {
            Project currentProject = Project.getInstance(null, null, null);
            MediaTrack mediaTrack = currentProject.getMediaTrack();
            mediaTrack.insertItemAt(position, video);
            projectRepository.update(currentProject);
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            // TODO(jliarte): 22/10/16 error management?
        }
    }

    public void addVideoListToTrack(List<Video> videoList, OnAddMediaFinishedListener listener) {
        try {
            Project currentProject = Project.getInstance(null, null, null);
            MediaTrack mediaTrack = currentProject.getMediaTrack();
            for (Video video : videoList) {
                mediaTrack.insertItem(video);
            }
            projectRepository.update(currentProject);
            listener.onAddMediaItemToTrackSuccess(null);
            // TODO(jliarte): 22/10/16 should get rid of EventBus calls?
            EventBus.getDefault().post(new UpdateProjectDurationEvent(currentProject.getDuration()));
            EventBus.getDefault().post(new NumVideosChangedEvent(currentProject.getMediaTrack().getNumVideosInProject()));
            EventBus.getDefault().post(new VideoAddedToTrackEvent());
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            listener.onAddMediaItemToTrackError();
        }
    }
}
