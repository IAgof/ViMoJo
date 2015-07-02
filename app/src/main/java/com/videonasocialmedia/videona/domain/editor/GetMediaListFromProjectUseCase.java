/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.domain.editor;

import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.media.Media;
import com.videonasocialmedia.videona.model.entities.editor.track.Track;

import java.util.LinkedList;

public class GetMediaListFromProjectUseCase {

    public LinkedList<Media> getMediaListFromProject() {
        Project project=Project.getInstance(null, null, null);;
        Track track=project.getMediaTrack();
        return track.getItems();
    }
}
