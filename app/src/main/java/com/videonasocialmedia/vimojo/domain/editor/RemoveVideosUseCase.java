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

import com.videonasocialmedia.vimojo.eventbus.events.project.UpdateProjectDurationEvent;
import com.videonasocialmedia.vimojo.eventbus.events.video.NumVideosChangedEvent;
import com.videonasocialmedia.vimojo.eventbus.events.video.VideosRemovedFromProjectEvent;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalOrphanTransitionOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.track.MediaTrack;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * This class is used to removed videos from the project.
 */
public class RemoveVideosUseCase {

    private ProjectRealmRepository projectRealmRepository = new ProjectRealmRepository();
    /**
     * Constructor.
     */
    public RemoveVideosUseCase() {
    }

    public void removeMediaItemsFromProject() {
        boolean correct = false;
        MediaTrack mediaTrack = projectRealmRepository.getCurrentProject().getMediaTrack();
        List<Media> list = new ArrayList<Media>(mediaTrack.getItems());
        for (Media media : list) {
            correct = removeVideoItemFromTrack(media, mediaTrack);
            if (!correct) break;
            Utils.removeVideo(media.getMediaPath());
        }
        if(!mediaTrack.getItems().isEmpty()) {
            mediaTrack = new MediaTrack();
        }

        EventBus.getDefault().post(new VideosRemovedFromProjectEvent());
        EventBus.getDefault().post(new UpdateProjectDurationEvent(0));
        EventBus.getDefault().post(new NumVideosChangedEvent(0));
    }

    /**
     * Gets the path of the new video and remove it in the media track.
     *
     * @param video the video which wants to delete
     * @return bool if the item has been deleted from the track, return true. If it fails, return false
     */
    private boolean removeVideoItemFromTrack(Media video, MediaTrack mediaTrack) {
        boolean result;
        try {
            mediaTrack.deleteItem(video);
            EventBus.getDefault().post(new UpdateProjectDurationEvent(projectRealmRepository.getCurrentProject().getDuration()));
            EventBus.getDefault().post(new NumVideosChangedEvent(projectRealmRepository.getCurrentProject().getMediaTrack().getNumVideosInProject()));
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
