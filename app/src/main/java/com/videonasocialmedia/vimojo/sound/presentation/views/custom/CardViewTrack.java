package com.videonasocialmedia.vimojo.sound.presentation.views.custom;

import android.support.v7.widget.RecyclerView;

/**
 * Created by alvaro on 10/04/17.
 */

interface CardViewTrack {
  void setSeekBar(int progress);
  void setSwitchMuteMedia(boolean state);
  void setTitleTrack(String title);
  void setImageTrack(int resourceId);
  boolean isShowedAudioTrackOptions();
}
