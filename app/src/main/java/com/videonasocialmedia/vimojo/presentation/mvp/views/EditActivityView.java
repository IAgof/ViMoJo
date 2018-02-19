/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.presentation.mvp.views;

import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;

import java.util.List;

/**
 * @author Juan Javier Cabanas Abascal
 */
public interface EditActivityView {
  void goToShare(String videoToSharePath);

  void showProgressDialog();

  void hideProgressDialog();

  void showError(int causeTextResource);

  void showMessage(int stringToast);

  void updateVideoList(List<Video> movieList);

  void updateProject();

  void enableEditActions();

  void disableEditActions();

  void enableBottomBar();

  void disableBottomBar();

  void changeAlphaBottomBar(float alpha);

  void expandFabMenu();

  void showDialogMediasNotFound();

  void enableFabText(boolean isEnable);

  void updateViewResetProject();
}
