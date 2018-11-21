package com.videonasocialmedia.vimojo.userProfile.presentation.mvp.views;


public interface UserProfileView {

  void showPreferenceUserName(String userNamePreference);

  void showPreferenceEmail(String emailPreference);

  void showLoading();

  void hideLoading();

  void showVideosRecorded(String videosRecorded);

  void showVideosEdited(String videosEdited);

  void showVideosShared(String videosShared);

  void showError(int stringId);

  void showPreferenceUserPic(String pictureURL);

  void navigateToInitRegisterLogin();
}
