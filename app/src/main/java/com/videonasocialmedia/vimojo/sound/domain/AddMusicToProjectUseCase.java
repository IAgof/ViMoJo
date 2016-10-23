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
import com.videonasocialmedia.vimojo.model.entities.editor.media.Music;
import com.videonasocialmedia.vimojo.model.entities.editor.track.AudioTrack;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnAddMediaFinishedListener;

/**
 * This class is used to add a new videos to the project.
 */
public class AddMusicToProjectUseCase {


    private AudioTrack obtainAudioTrack(int trackIndex) {

        return Project.getInstance(null, null, null).getAudioTracks().get(trackIndex);
    }

    public void addMusicToTrack(Music music, int trackIndex, OnAddMediaFinishedListener listener) {
        AudioTrack audioTrack = null;
        try {
            audioTrack = obtainAudioTrack(trackIndex);
            audioTrack.insertItemAt(0,music);
            listener.onAddMediaItemToTrackSuccess(music);

        } catch (IndexOutOfBoundsException | IllegalItemOnTrack exception) {
            exception.printStackTrace();
            listener.onAddMediaItemToTrackError();
        }
    }

}
