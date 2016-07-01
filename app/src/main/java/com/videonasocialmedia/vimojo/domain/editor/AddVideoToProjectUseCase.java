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
import com.videonasocialmedia.vimojo.model.entities.editor.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.model.entities.editor.track.MediaTrack;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnAddMediaFinishedListener;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * This class is used to add a new videos to the project.
 */
public class AddVideoToProjectUseCase {

    /**
     * Constructor.
     */
    public AddVideoToProjectUseCase() {
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
            MediaTrack mediaTrack = Project.getInstance(null, null, null).getMediaTrack();
            mediaTrack.insertItem(video);
            EventBus.getDefault().post(new AddMediaItemToTrackSuccessEvent(video));
            EventBus.getDefault().post(new UpdateProjectDurationEvent(Project.getInstance(null, null, null).getDuration()));
            EventBus.getDefault().post(new NumVideosChangedEvent(Project.getInstance(null, null, null).getMediaTrack().getNumVideosInProject()));
            EventBus.getDefault().post(new VideoAddedToTrackEvent());
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            //TODO manejar error
        }
    }

    /**
     * @param videoPath
     * @param listener
     * @deprecated use the one parameter version instead
     */
    public void addVideoToTrack(String videoPath, OnAddMediaFinishedListener listener) {
        Video videoToAdd = new Video(videoPath);
        addVideoToTrack(videoToAdd, listener);
    }

    /**
     * @param video
     * @param listener
     * @deprecated use the one parameter version instead
     */
    public void addVideoToTrack(Video video, OnAddMediaFinishedListener listener) {
        try {
            MediaTrack mediaTrack = Project.getInstance(null, null, null).getMediaTrack();
            mediaTrack.insertItem(video);
            listener.onAddMediaItemToTrackSuccess(video);
            EventBus.getDefault().post(new UpdateProjectDurationEvent(Project.getInstance(null, null, null).getDuration()));
            EventBus.getDefault().post(new NumVideosChangedEvent(Project.getInstance(null, null, null).getMediaTrack().getNumVideosInProject()));
            EventBus.getDefault().post(new VideoAddedToTrackEvent());
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            listener.onAddMediaItemToTrackError();
        }
    }

    public void addVideoToProjectAtPosition(Video video, int position) {
        try {
            MediaTrack mediaTrack = Project.getInstance(null, null, null).getMediaTrack();
            mediaTrack.insertItemAt(position, video);
        } catch (IllegalItemOnTrack illegalItemOnTrack) {

        }
    }

    public void addVideoListToTrack(List<Video> videoList, OnAddMediaFinishedListener listener) {
        try {
            MediaTrack mediaTrack = Project.getInstance(null, null, null).getMediaTrack();
            for (Video video : videoList) {
                mediaTrack.insertItem(video);
            }
            listener.onAddMediaItemToTrackSuccess(null);
            EventBus.getDefault().post(new UpdateProjectDurationEvent(Project.getInstance(null, null, null).getDuration()));
            EventBus.getDefault().post(new NumVideosChangedEvent(Project.getInstance(null, null, null).getMediaTrack().getNumVideosInProject()));
            EventBus.getDefault().post(new VideoAddedToTrackEvent());
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            listener.onAddMediaItemToTrackError();
        }
    }


}
