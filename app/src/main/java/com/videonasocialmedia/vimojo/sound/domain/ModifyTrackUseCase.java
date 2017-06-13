package com.videonasocialmedia.vimojo.sound.domain;

import com.videonasocialmedia.videonamediaframework.model.media.track.MediaTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.track.TrackRepository;

/**
 * Created by alvaro on 10/04/17.
 */

public class ModifyTrackUseCase {
  TrackRepository trackRepository;
  ProjectRepository projectRepository;
  Project currentProject;

  public ModifyTrackUseCase(ProjectRepository projectRepository, TrackRepository trackRepository){
    this.projectRepository = projectRepository;
    this.trackRepository = trackRepository;
  }

  public void setTrackVolume(Track track, float volume){
    currentProject = Project.getInstance(null, null, null, null);
    track.setVolume(volume);
    //trackRepository.update(track);
    updateProject();
  }

  public void setTrackMute(Track track, boolean isMute){
    currentProject = Project.getInstance(null, null, null, null);
    track.setMute(isMute);
    //trackRepository.update(track);
    updateProject();
  }

  private void updateProject() {
    projectRepository.update(currentProject);
  }

}
