package com.videonasocialmedia.vimojo.userProfile.presentation.views;


public interface UserProfileActivityView {

  void showPreferenceUserName(String userNamePreference);

  void showPreferenceEmail(String emailPreference);

  void showLoading();

  void hideLoading();

  void showVideosRecorded(String videosRecorded);

  void showVideosEdited(String videosEdited);
}
