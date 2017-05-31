package com.videonasocialmedia.vimojo.sound.domain;


import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnAddMediaFinishedListener;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.FileUtils;

import javax.inject.Inject;

import static com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACK_VOICE_OVER;

/**
 * Created by alvaro on 7/12/16.
 */

public class AddVoiceOverToProjectUseCase {

  private AddMusicToProjectUseCase addMusicToProjectUseCase;

  @Inject
  public AddVoiceOverToProjectUseCase(AddMusicToProjectUseCase addMusicToProjectUseCase) {
    this.addMusicToProjectUseCase = addMusicToProjectUseCase;
  }

  public void setVoiceOver(String voiceOverPath, float volume) {
    Music voiceOver = new Music(voiceOverPath, volume, FileUtils.getDuration(voiceOverPath));
    voiceOver.setMusicTitle(Constants.MUSIC_AUDIO_VOICEOVER_TITLE);
    voiceOver.setIconResourceId(R.drawable.activity_edit_audio_voice_over_icon);
    addMusicToProjectUseCase.addMusicToTrack(voiceOver, INDEX_AUDIO_TRACK_VOICE_OVER,
        new OnAddMediaFinishedListener() {
          @Override
          public void onAddMediaItemToTrackError() {
            // TODO(jliarte): 23/12/16 handle errors and send back through a listener?

          }

          @Override
          public void onAddMediaItemToTrackSuccess(Media media) {
          }
    });
  }
}
