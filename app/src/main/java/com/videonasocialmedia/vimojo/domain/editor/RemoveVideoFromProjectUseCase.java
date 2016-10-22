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
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.vimojo.model.entities.editor.exceptions.IllegalOrphanTransitionOnTrack;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Media;
import com.videonasocialmedia.vimojo.model.entities.editor.track.MediaTrack;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnRemoveMediaFinishedListener;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * This class is used to removed videos from the project.
 */
public class RemoveVideoFromProjectUseCase implements RemoveMediaFromProjectUseCase {
    ProjectRepository projectRepository = new ProjectRealmRepository();

    /**
     * Default empty Constructor.
     */
    public RemoveVideoFromProjectUseCase() {
    }

    @Override
    public void removeMediaItemsFromProject(ArrayList<Media> list, OnRemoveMediaFinishedListener listener) {
        boolean correct = false;
        Project currentProject = Project.getInstance(null, null, null);
        MediaTrack mediaTrack = currentProject.getMediaTrack();
        for (Media media : list) {
            correct = removeVideoItemFromTrack(media, mediaTrack);
            if (!correct) break;
        }
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
     * @return bool if the item has been deleted from the track, return true. If it fails, return false
     */
    private boolean removeVideoItemFromTrack(Media video, MediaTrack mediaTrack) {
        boolean result;
        try {
            mediaTrack.deleteItem(video);
            // TODO(jliarte): 23/10/16 get rid of EventBus?
            EventBus.getDefault().post(new UpdateProjectDurationEvent(Project.getInstance(null, null, null).getDuration()));
            EventBus.getDefault().post(new NumVideosChangedEvent(Project.getInstance(null, null, null).getMediaTrack().getNumVideosInProject()));
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
