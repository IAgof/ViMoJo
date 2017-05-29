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
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnLaunchAVTransitionTempFileListener;
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
    private Project currentProject;

    /**
     * Default Constructor with project repository argument.
     *
     * @param projectRepository the project repository
     */
    @Inject public AddVideoToProjectUseCase(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }


    public void addVideoToTrack(String videoPath, OnLaunchAVTransitionTempFileListener listener) {
        Video videoToAdd = new Video(videoPath, Video.DEFAULT_VOLUME);
        addVideoToTrack(videoToAdd);
        checkIfVideoNeedAVTransitionTempFile(videoToAdd, listener);
    }

    private void addVideoToTrack(Video video) {
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
    public void addVideoToTrack(Video video, OnAddMediaFinishedListener listener,
                                OnLaunchAVTransitionTempFileListener avtransitionsListener) {
        try {
            Project currentProject = Project.getInstance(null, null, null);
            MediaTrack mediaTrack = currentProject.getMediaTrack();
            mediaTrack.insertItem(video);
            projectRepository.update(currentProject);
            listener.onAddMediaItemToTrackSuccess(video);
            checkIfVideoNeedAVTransitionTempFile(video, avtransitionsListener);

            // TODO(jliarte): 22/10/16 should get rid of EventBus calls?
            EventBus.getDefault().post(new UpdateProjectDurationEvent(currentProject.getDuration()));
            EventBus.getDefault().post(new NumVideosChangedEvent(currentProject.getMediaTrack().getNumVideosInProject()));
            EventBus.getDefault().post(new VideoAddedToTrackEvent());
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            listener.onAddMediaItemToTrackError();
        }
    }

    public void addVideoToProjectAtPosition(Video video, int position,
                                            OnAddMediaFinishedListener listener) {
        try {
            Project currentProject = Project.getInstance(null, null, null);
            MediaTrack mediaTrack = currentProject.getMediaTrack();
            mediaTrack.insertItemAt(position, video);
            projectRepository.update(currentProject);
            listener.onAddMediaItemToTrackSuccess(video);
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            listener.onAddMediaItemToTrackError();
        }
    }

    public void addVideoListToTrack(List<Video> videoList, OnAddMediaFinishedListener listener,
                                    OnLaunchAVTransitionTempFileListener avtransitionsListener) {
        try {
            Project currentProject = Project.getInstance(null, null, null);
            MediaTrack mediaTrack = currentProject.getMediaTrack();
            for (Video video : videoList) {
                mediaTrack.insertItem(video);
                checkIfVideoNeedAVTransitionTempFile(video,avtransitionsListener);
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

    private void checkIfVideoNeedAVTransitionTempFile(Video videoToAdd,
                                                  OnLaunchAVTransitionTempFileListener listener) {
        currentProject = Project.getInstance(null,null,null);
        if(currentProject.isAudioFadeTransitionActivated()
            || currentProject.isVideoFadeTransitionActivated())
            listener.videoToLaunchAVTransitionTempFile(videoToAdd,
                currentProject.getProjectPathIntermediateFileAudioFade());
    }
}
