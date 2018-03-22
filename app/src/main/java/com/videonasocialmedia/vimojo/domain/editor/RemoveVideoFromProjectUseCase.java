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

import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.eventbus.events.project.UpdateProjectDurationEvent;
import com.videonasocialmedia.vimojo.eventbus.events.video.NumVideosChangedEvent;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalOrphanTransitionOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnRemoveMediaFinishedListener;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;

import java.util.ArrayList;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * This class is used to removed videos from the project.
 */
public class RemoveVideoFromProjectUseCase implements RemoveMediaFromProjectListener {
    protected ProjectRepository projectRepository;
    protected VideoRepository videoRepository;

    /**
     * Default Constructor.
     *
     * @param projectRepository the project repository.
     * @param videoRepository the video repository.
     */
    @Inject public RemoveVideoFromProjectUseCase(ProjectRepository projectRepository,
                                                 VideoRepository videoRepository) {
        this.projectRepository = projectRepository;
        this.videoRepository = videoRepository;
    }

    @Override
    public void removeMediaItemsFromProject(Project currentProject, ArrayList<Media> mediaList,
                                            OnRemoveMediaFinishedListener listener) {
        boolean correct = false;
        Track mediaTrack = currentProject.getMediaTrack();
        for (Media media : mediaList) {
            correct = removeVideoItemFromTrack(currentProject, media, mediaTrack);
            if (!correct) break;
            //video repository remove media, remove videos from other projects also.
            videoRepository.remove((Video) media);
        }
        if (correct) {
            projectRepository.update(currentProject);
            listener.onRemoveMediaItemFromTrackSuccess();
        } else {
            listener.onRemoveMediaItemFromTrackError();
        }
    }

    @Override
    public void removeMediaItemFromProject(Project currentProject, int positionVideoToRemove,
                                            OnRemoveMediaFinishedListener listener) {

        Track mediaTrack = currentProject.getMediaTrack();
        Media media = mediaTrack.getItems().get(positionVideoToRemove);
        boolean correct = removeVideoItemFromTrack(currentProject, media, mediaTrack);
        if (!correct) return;
        //video repository remove media, remove videos from other projects also.
        videoRepository.remove((Video) media);
        if (correct) {
            projectRepository.update(currentProject);
            listener.onRemoveMediaItemFromTrackSuccess();
        } else {
            listener.onRemoveMediaItemFromTrackError();
        }
    }

    /**
     * Gets the path of the new video and insert it in the media track.
     *
     * @param video the video which wants to delete
     * @return bool if the item has been deleted from the track, return true. If it fails,
     *          return false.
     */
    private boolean removeVideoItemFromTrack(Project currentProject, Media video, Track mediaTrack) {
        boolean result;
        try {
            mediaTrack.deleteItem(video);
            // TODO(jliarte): 23/10/16 get rid of EventBus?
            EventBus.getDefault().post(
                    new UpdateProjectDurationEvent(currentProject.getDuration()));
            EventBus.getDefault().post(
                    new NumVideosChangedEvent(currentProject.getMediaTrack()
                            .getNumItemsInTrack()));
            result = true;
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            result = false;
        } catch (IllegalOrphanTransitionOnTrack illegalOrphanTransitionOnTrack) {
            illegalOrphanTransitionOnTrack.printStackTrace();
            result = false;
        }
        return result;
    }
}
