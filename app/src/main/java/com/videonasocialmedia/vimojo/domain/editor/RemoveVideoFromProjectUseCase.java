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
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalOrphanTransitionOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnRemoveMediaFinishedListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * This class is used to removed videos from the project.
 */
public class RemoveVideoFromProjectUseCase {
    /**
     * Default Constructor.
     */
    @Inject public RemoveVideoFromProjectUseCase() {
    }

    public void removeMediaItemsFromProject(Project currentProject, ArrayList<Media> mediaList,
                                            OnRemoveMediaFinishedListener listener) {
        boolean correct = false;
        Track mediaTrack = currentProject.getMediaTrack();
        ArrayList<Media> removedMedias = new ArrayList<>();
        for (Media media : mediaList) {
            correct = removeVideoItemFromTrack(media, mediaTrack);
            if (!correct) break;
            removedMedias.add(media);
        }
        notifyResultToListener(listener, correct, removedMedias);
    }

    public void removeMediaItemFromProject(Project currentProject, int positionVideoToRemove,
                                            OnRemoveMediaFinishedListener listener) {

        Track mediaTrack = currentProject.getMediaTrack();
        Media media = mediaTrack.getItems().get(positionVideoToRemove);
        boolean correct = removeVideoItemFromTrack(media, mediaTrack);
        if (!correct) return;
        notifyResultToListener(listener, correct, Collections.singletonList(media));
    }

    /**
     * Gets the path of the new video and insert it in the media track.
     *
     * @param video the video which wants to delete
     * @return bool if the item has been deleted from the track, return true. If it fails,
     *          return false.
     */
    private boolean removeVideoItemFromTrack(Media video, Track mediaTrack) {
        boolean result;
        try {
            mediaTrack.deleteItem(video);
            result = true;
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            result = false;
        } catch (IllegalOrphanTransitionOnTrack illegalOrphanTransitionOnTrack) {
            illegalOrphanTransitionOnTrack.printStackTrace();
            result = false;
        }
        return result;
    }

    private void notifyResultToListener(OnRemoveMediaFinishedListener listener, boolean correct,
                                        List<Media> removedMedias) {
        if (correct) {
            listener.onRemoveMediaItemFromTrackSuccess(removedMedias);
        } else {
            listener.onRemoveMediaItemFromTrackError();
        }
    }
}
