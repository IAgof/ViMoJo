package com.videonasocialmedia.vimojo.presentation.mvp.views;

import android.content.Context;

import com.videonasocialmedia.videonamediaframework.model.VMComposition;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer;

/**
 * Created by ruth on 23/11/16.
 */

public interface EditorActivityView {
  void showError(int causeTextResource);
  void showMessage(int stringToast);
  void restartActivity(Class clas);
  void itemDarkThemePurchased();
  void showWatermarkSwitch(boolean watermarkIsSelected);
  void hideWatermarkSwitch();
  void setDefaultIconsForStoreItems();
  void setLockIconsForStoreItems();
  void hideVimojoStoreViews();
  void deactivateDarkTheme();
  void activateWatermark();
  void setHeaderViewCurrentProject(String pathThumbProject, String projectName, String projectDate);
  void goToRecordOrGalleryScreen();
  void hideLinkToVimojoPlatform();
  void hideTutorialViews();

  // Player views
  void attachView(Context context);
  void detachView();
  void setAspectRatioVerticalVideos(int height);
  void initSingleVideo(Video video);
  void init(VMComposition vmComposition);
  void setVideonaPlayerListener(VideonaPlayer.VideonaPlayerListener videonaPlayerListener);
  void playPreview();
  void pausePreview();
  void seekToClip(int position);
  void setVideoVolume(float volume);
  void setVoiceOverVolume(float volume);
  void setMusicVolume(float volume);
}
