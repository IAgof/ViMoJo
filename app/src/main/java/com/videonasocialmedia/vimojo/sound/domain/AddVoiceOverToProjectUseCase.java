package com.videonasocialmedia.vimojo.sound.domain;


import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnAddMediaFinishedListener;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.FileUtils;

import javax.inject.Inject;

import static com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACKS_VOICE_OVER;

/**
 * Created by alvaro on 7/12/16.
 */

public class AddVoiceOverToProjectUseCase {
  protected ProjectRepository projectRepository;
  private AddMusicToProjectUseCase addMusicToProjectUseCase;

  @Inject
  public AddVoiceOverToProjectUseCase(ProjectRepository projectRepository,
                                      AddMusicToProjectUseCase addMusicToProjectUseCase,
                                      RemoveMusicFromProjectUseCase removeMusicFromProjectUseCase) {
    this.projectRepository = projectRepository;
    this.addMusicToProjectUseCase = addMusicToProjectUseCase;
  }

  public void setVoiceOver(final Project project, String voiceOverPath, float volume) {
    Music voiceOver = new Music(voiceOverPath, volume, FileUtils.getDuration(voiceOverPath));
    voiceOver.setMusicTitle(Constants.MUSIC_AUDIO_VOICEOVER_TITLE);
    addMusicToProjectUseCase.addMusicToTrack(voiceOver, INDEX_AUDIO_TRACKS_VOICE_OVER,
        new OnAddMediaFinishedListener() {
          @Override
          public void onAddMediaItemToTrackError() {
            // TODO(jliarte): 23/12/16 handle errors and send back through a listener?
            projectRepository.update(project);
          }

          @Override
          public void onAddMediaItemToTrackSuccess(Media media) {

          }
    });
  }
}
