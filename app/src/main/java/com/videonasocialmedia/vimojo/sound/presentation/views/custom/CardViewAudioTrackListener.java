package com.videonasocialmedia.vimojo.sound.presentation.views.custom;

/**
 * Created by alvaro on 10/04/17.
 */

public interface CardViewAudioTrackListener {
  void setSeekBarProgress(int id, int progress);
  void setSwitchMuteAudio(int id, boolean isChecked);
  void onClickExpandInfoTrack(int positionInTrack);
  void onClickMediaClip(int position, int trackId);
  void onClickDeleteAudio(int id);
}
