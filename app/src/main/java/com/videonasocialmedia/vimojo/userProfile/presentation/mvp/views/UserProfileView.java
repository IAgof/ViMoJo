package com.videonasocialmedia.vimojo.userProfile.presentation.mvp.views;


public interface UserProfileView {

  void showPreferenceUserName(String userNamePreference);

  void showPreferenceEmail(String emailPreference);

  void showLoading();

  void hideLoading();

  void showVideosRecorded(String videosRecorded);

  void showVideosEdited(String videosEdited);

  void setUserPropertyToMixpanel(String property, String value);

  void showError(int stringId);
}
