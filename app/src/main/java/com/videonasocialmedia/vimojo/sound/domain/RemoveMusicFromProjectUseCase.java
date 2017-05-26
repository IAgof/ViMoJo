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
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalOrphanTransitionOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.track.AudioTrack;
import com.videonasocialmedia.vimojo.repository.music.MusicRepository;

import javax.inject.Inject;

/**
 * This class is used to removed videos from the project.
 */
public class RemoveMusicFromProjectUseCase {
    private Project currentProject;
    protected MusicRepository musicRepository;

    /**
     * Default constructor with project repository argument.
     *
     */
    @Inject public RemoveMusicFromProjectUseCase(MusicRepository musicRepository) {
        this.musicRepository = musicRepository;
        currentProject = Project.getInstance(null, null, null);
    }

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
                    musicRepository.remove((Music) audio);
                } catch (IllegalItemOnTrack | IllegalOrphanTransitionOnTrack exception) {
                    //TODO treat exception properly
                }
            }
        }
    }

}
