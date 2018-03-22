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

import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnRemoveMediaFinishedListener;

import java.util.ArrayList;

/**
 * This interface is used to delete an existing media items from the project.
 */
public interface RemoveMediaFromProjectListener {
    /**
     * This method is used to remove media items from the project.
     *
     * @param list     the list of the media items which wants to remove
     * @param listener the listener which monitoring when this items have been deleted from the project
     */
    void removeMediaItemsFromProject(Project currentProject, ArrayList<Media> list,
                                     OnRemoveMediaFinishedListener listener);

    void removeMediaItemFromProject(Project currentProject, int positionVideoToRemove,
                               OnRemoveMediaFinishedListener listener);
}
