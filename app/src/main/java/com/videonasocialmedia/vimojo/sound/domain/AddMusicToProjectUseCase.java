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
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.track.AudioTrack;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnAddMediaFinishedListener;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

/**
 * This class is used to add a new videos to the project.
 */
public class AddMusicToProjectUseCase {


    private ProjectRepository projectRepository = new ProjectRealmRepository();
    private Project currentProject = projectRepository.getCurrentProject();

    private AudioTrack obtainAudioTrack(int trackIndex) {
        return currentProject.getAudioTracks().get(trackIndex);
    }

    public void addMusicToTrack(Music music, int trackIndex, OnAddMediaFinishedListener listener) {
        AudioTrack audioTrack = null;
        try {
            audioTrack = obtainAudioTrack(trackIndex);
            audioTrack.insertItemAt(0,music);
            currentProject.setMusicOnProject(true);
            projectRepository.update(currentProject);
            listener.onAddMediaItemToTrackSuccess(music);
        } catch (IndexOutOfBoundsException | IllegalItemOnTrack exception) {
//            exception.printStackTrace();
            listener.onAddMediaItemToTrackError();
        }
    }

}
