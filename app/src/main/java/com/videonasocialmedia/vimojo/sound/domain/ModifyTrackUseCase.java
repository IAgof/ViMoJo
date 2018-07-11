package com.videonasocialmedia.vimojo.sound.domain;

import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.cut.domain.model.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectDataSource;

/**
 * Created by alvaro on 10/04/17.
 */

public class ModifyTrackUseCase {
  ProjectDataSource projectRepository;
  public ModifyTrackUseCase(ProjectDataSource projectRepository) {
    this.projectRepository = projectRepository;
  }

  public void setTrackVolume(Project currentProject, Track track, float volume) {
    for(Media item: track.getItems()){
      item.setVolume(volume);
    }
    track.setVolume(volume);
    projectRepository.update(currentProject);
  }

  public void setTrackMute(Project currentProject, Track track, boolean isMute) {
    track.setMute(isMute);
    projectRepository.update(currentProject);
  }

}
