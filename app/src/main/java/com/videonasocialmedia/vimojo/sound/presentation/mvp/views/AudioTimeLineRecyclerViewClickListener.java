package com.videonasocialmedia.vimojo.sound.presentation.mvp.views;

/**
 * Created by alvaro on 7/03/17.
 */

public interface AudioTimeLineRecyclerViewClickListener {
  void onAudioClipClicked(int position);
  void onMusicClipClicked(int position);
  void onVoiceOverClipClicked(int position);
}
