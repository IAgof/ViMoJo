package com.videonasocialmedia.vimojo.sound.domain;

import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

/**
 * Created by alvaro on 9/02/17.
 */

public class UpdateMusicVolumeProjectUseCase {

  ProjectRepository projectRepository;

  public UpdateMusicVolumeProjectUseCase(ProjectRepository projectRepository){
    this.projectRepository = projectRepository;
  }

  public void setVolumeMusic(Project project, float volumeMusic){
    if(project.hasMusic()) {
      Music music = project.getMusic();
      music.setVolume(volumeMusic);
      projectRepository.update(project);
    }
  }
}
