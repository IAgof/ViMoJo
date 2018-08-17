package com.videonasocialmedia.vimojo.sound.domain;

import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.repository.ProjectRepository;

/**
 * Created by alvaro on 10/04/17.
 */

public class ModifyTrackUseCase {
  ProjectRepository projectRepository;
  public ModifyTrackUseCase(ProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
  }

  public void setTrackVolume(Project currentProject, Track track, float volume) {
    for(Media item: track.getItems()){
      item.setVolume(volume);
    }
    track.setVolume(volume);
    // TODO(jliarte): 17/08/18 change all mehtod calls
    //projectRepository.update(currentProject);
  }

  public void setTrackMute(Project currentProject, Track track, boolean isMute) {
    track.setMute(isMute);
    projectRepository.update(currentProject);
  }

}
