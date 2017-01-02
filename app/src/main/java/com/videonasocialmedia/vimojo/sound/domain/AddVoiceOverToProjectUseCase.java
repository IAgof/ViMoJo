package com.videonasocialmedia.vimojo.sound.domain;

import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnAddMediaFinishedListener;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.utils.Constants;

/**
 * Created by alvaro on 7/12/16.
 */

public class AddVoiceOverToProjectUseCase {
  protected ProjectRepository projectRepository;
  private AddMusicToProjectUseCase addMusicToProjectUseCase;

  public AddVoiceOverToProjectUseCase(ProjectRepository projectRepository,
                                      AddMusicToProjectUseCase addMusicToProjectUseCase) {
    this.projectRepository = projectRepository;
    this.addMusicToProjectUseCase = addMusicToProjectUseCase;
  }

  public void setVoiceOver(Project project, String voiceOverPath, float volume) {
    Music voiceOver = new Music(voiceOverPath, volume);
    voiceOver.setMusicTitle(Constants.MUSIC_AUDIO_MIXED_TITLE);
    // TODO(jliarte): 23/12/16 maybe use a different track for voice over? a different
    //                VMComposition method or use case?
    addMusicToProjectUseCase.addMusicToTrack(voiceOver, 0, new OnAddMediaFinishedListener() {
      @Override
      public void onAddMediaItemToTrackError() {
        // TODO(jliarte): 23/12/16 handle errors and send back through a listener?
      }

      @Override
      public void onAddMediaItemToTrackSuccess(Media media) {

      }
    });
    projectRepository.update(project);
  }
}
