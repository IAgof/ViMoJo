package com.videonasocialmedia.vimojo.sound.domain;

import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

/**
 * Created by alvaro on 10/04/17.
 */

public class ModifyTrackUseCase {
  ProjectRepository projectRepository;
  Project currentProject;

  public ModifyTrackUseCase(ProjectRepository projectRepository){
    this.projectRepository = projectRepository;
  }

  public void setTrackVolume(Track track, float volume){
    currentProject = Project.getInstance(null, null, null);
    for(Media item: track.getItems()){
      item.setVolume(volume);
    }
    track.setVolume(volume);
    updateProject();
  }

  public void setTrackMute(Track track, boolean isMute){
    currentProject = Project.getInstance(null, null, null);
    track.setMute(isMute);
    updateProject();
  }

  private void updateProject() {
    projectRepository.update(currentProject);
  }

}
