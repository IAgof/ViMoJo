/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
*
 * Authors:
 * Veronica Lago Fominaya
 */

package com.videonasocialmedia.vimojo.sound.domain;


import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.vimojo.model.entities.editor.exceptions.IllegalOrphanTransitionOnTrack;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Media;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Music;
import com.videonasocialmedia.vimojo.model.entities.editor.track.AudioTrack;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

/**
 * This class is used to removed videos from the project.
 */
public class RemoveMusicFromProjectUseCase {
    private final Project currentProject = Project.getInstance(null, null, null);
    protected ProjectRepository projectRepository = new ProjectRealmRepository();

    /**
     * @param music      the music object to be removed
     * @param trackIndex the index of the track where de music is placed
     */
    public void removeMusicFromProject(Music music, int trackIndex) {
        AudioTrack audioTrack = currentProject.getAudioTracks().get(trackIndex);
        for (int musicIndex = 0; musicIndex < audioTrack.getItems().size(); musicIndex++) {
            Media audio = audioTrack.getItems().get(musicIndex);
            if (audio.equals(music)) {
                try {
                    audioTrack.deleteItem(audio);
                    currentProject.setMusicOnProject(false);
                    projectRepository.update(currentProject);
                } catch (IllegalItemOnTrack | IllegalOrphanTransitionOnTrack exception) {
                    //TODO treat exception properly
                }
            }
        }
    }

}
