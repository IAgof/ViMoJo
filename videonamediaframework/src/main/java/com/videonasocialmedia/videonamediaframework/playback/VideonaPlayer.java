package com.videonasocialmedia.videonamediaframework.playback;

import android.content.Context;

import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;

import java.util.List;

/**
 * Created by jliarte on 13/05/16.
 */
public interface VideonaPlayer {

  void onShown(Context context);

  void onDestroy();

  void onPause();

  void setListener(VideonaPlayerExo.VideonaPlayerListener videonaPlayerListener);

  void initPreview(int instantTime);

  void initPreviewLists(List<Video> videoList);

  void bindVideoList(List<Video> videoList);

  void updatePreviewTimeLists();

  void playPreview();

  void pausePreview();

  void seekTo(int timeInMsec);

  void seekClipTo(int seekTimeInMsec);

  void seekToClip(int position);

  void setMusic(Music music);

  void setVolumen(float volume);

  int getCurrentPosition();

  void setSeekBarProgress(int progress);

  void setSeekBarLayoutEnabled(boolean seekBarEnabled);

  void resetPreview();

  /**
   * Created by jliarte on 13/05/16.
   */
  interface VideonaPlayerListener {
    void newClipPlayed(int currentClipIndex);
  }
}
