package com.videonasocialmedia.vimojo.sound.domain;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.sound.model.VoiceOver;

/**
 * Created by alvaro on 7/12/16.
 */

public class AddVoiceOverToProjectUseCase {

  protected ProjectRepository projectRepository = new ProjectRealmRepository();

  public AddVoiceOverToProjectUseCase(){

  }

  public void setVoiceOver(String voiceOverPath, float volume){

    VoiceOver voiceOver = new VoiceOver(voiceOverPath, volume);
    Project currentProject = Project.getInstance(null, null, null);
    currentProject.setVoiceOver(voiceOver);
    projectRepository.update(currentProject);

  }

}
