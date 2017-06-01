package com.videonasocialmedia.vimojo.sound.domain;

import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.AudioTrack;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnAddMediaFinishedListener;
import com.videonasocialmedia.vimojo.repository.music.MusicRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.track.TrackRepository;

import javax.inject.Inject;

/**
 * Created by alvaro on 1/06/17.
 */

public class AddAudioUseCase {

  private Project currentProject;
  private ProjectRepository projectRepository;
  private TrackRepository trackRepository;
  private MusicRepository musicRepository;
  private final int SECOND_POSITION = 2;
  private final int FIRST_POSITION = 1;

  @Inject
  public AddAudioUseCase(ProjectRepository projectRepository, TrackRepository trackRepository,
                         MusicRepository musicRepository) {
    this.projectRepository = projectRepository;
    this.trackRepository = trackRepository;
    this.musicRepository = musicRepository;
    currentProject = Project.getInstance(null,null,null);
  }

  public void addMusic(Music music, int trackIndex, OnAddMediaFinishedListener listener) {
    AudioTrack audioTrack = currentProject.getAudioTracks().get(trackIndex);
    updateTrack(audioTrack, trackIndex, music);
    addMusicToTrack(music, listener, audioTrack);
    updateProject();
  }

  private void updateTrack(AudioTrack audioTrack, int trackIndex, Music music) {
    audioTrack.setPosition(getTrackPositionByUserInteraction(audioTrack, trackIndex));
    audioTrack.setVolume(music.getVolume());
    trackRepository.update(audioTrack);
  }

  private void addMusicToTrack(Music music, OnAddMediaFinishedListener listener, AudioTrack audioTrack) {
    try {
      audioTrack.insertItemAt(0,music);
      musicRepository.update(music);
      listener.onAddMediaItemToTrackSuccess(music);
    } catch (IndexOutOfBoundsException | IllegalItemOnTrack exception) {
      listener.onAddMediaItemToTrackError();
    }
  }

  private void updateProject() {
    projectRepository.update(currentProject);
  }

  private int getTrackPositionByUserInteraction(AudioTrack audioTrack, int trackIndex) {
    if(audioTrack.getPosition()!=0){
      return audioTrack.getPosition();
    }
    switch (trackIndex){
      case Constants.INDEX_AUDIO_TRACK_MUSIC:
        if(currentProject.hasVoiceOver()){
          return SECOND_POSITION;
        } else {
          return FIRST_POSITION;
        }
      case Constants.INDEX_AUDIO_TRACK_VOICE_OVER:
        if(currentProject.hasMusic()){
          return SECOND_POSITION;
        } else {
          return FIRST_POSITION;
        }
      default:
        return 0;
    }
  }
}
