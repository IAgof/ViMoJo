package com.videonasocialmedia.vimojo.sound.domain;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnAddMediaFinishedListener;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.FileUtils;

/**
 * Created by alvaro on 7/12/16.
 */

public class AddVoiceOverToProjectUseCase {
  protected ProjectRepository projectRepository;
  private AddMusicToProjectUseCase addMusicToProjectUseCase;
  private RemoveMusicFromProjectUseCase removeMusicFromProjectUseCase;

  public AddVoiceOverToProjectUseCase(ProjectRepository projectRepository,
                                      AddMusicToProjectUseCase addMusicToProjectUseCase,
                                      RemoveMusicFromProjectUseCase removeMusicFromProjectUseCase) {
    this.projectRepository = projectRepository;
    this.addMusicToProjectUseCase = addMusicToProjectUseCase;
    this.removeMusicFromProjectUseCase = removeMusicFromProjectUseCase;
  }

  public void setVoiceOver(final Project project, String voiceOverPath, float volume) {
    Music voiceOver = new Music(voiceOverPath, volume, FileUtils.getDuration(voiceOverPath));
    voiceOver.setMusicTitle(Constants.MUSIC_AUDIO_VOICEOVER_TITLE);

    // if hasMusic, first removeFromTrack and then add VoiceOver as music
    if(project.getVMComposition().hasMusic()){
      removeMusicFromProjectUseCase.removeMusicFromProject(project.getMusic(),0);
    }

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
