package com.videonasocialmedia.vimojo.sound.presentation.views.custom;

/**
 * Created by alvaro on 10/04/17.
 */

public interface CardViewAudioTrackListener {
  void setSeekBarProgress(int progress, int id);
  void setSwitchSoloAudio(boolean isChecked, int id);
  void setSwitchMuteAudio(boolean isChecked, int id);
}
