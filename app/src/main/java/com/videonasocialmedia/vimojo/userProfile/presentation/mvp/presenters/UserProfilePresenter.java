package com.videonasocialmedia.vimojo.userProfile.presentation.mvp.presenters;

import android.content.Context;
import android.content.SharedPreferences;

import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.domain.ObtainLocalVideosUseCase;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.userProfile.presentation.mvp.views.UserProfileView;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by ruth on 13/10/17.
 */

public class UserProfilePresenter {

  public static final String MIXPANEL_EMAIL_ID = "$account_email";
  public static final String MIXPANEL_USERNAME_ID = "$username";
  private SharedPreferences sharedPreferences;
  private SharedPreferences.Editor preferencesEditor;
  private UserProfileView userProfileView;
  private Context context;
  private ObtainLocalVideosUseCase obtainLocalVideosUseCase;
  private int videosRecorded = -1;
  private int videosEdited = -1;
  private boolean isVideosRecordedRetrieved = false;
  private boolean isVideosEditedRetrieved = false;

  @Inject
  public UserProfilePresenter(UserProfileView view, Context context,
                              SharedPreferences sharedPreferences, ObtainLocalVideosUseCase
                              obtainLocalVideosUseCase){
    this.context=context;
    this.userProfileView =view;
    this.sharedPreferences = sharedPreferences;
    this.obtainLocalVideosUseCase = obtainLocalVideosUseCase;
  }

  public void getUserNameFromPreferences() {
    String userNamePreference = sharedPreferences.getString(ConfigPreferences.USERNAME, null);
    if(userNamePreference!=null && !userNamePreference.isEmpty())
      userProfileView.showPreferenceUserName(userNamePreference);
    else {
      userProfileView.showPreferenceUserName(context.getResources().getString(R.string.username));
    }
  }

  public void getEmailFromPreferences() {
    // TODO:(alvaro.martinez) 17/01/18 Get email from user register
    String emailPreference=sharedPreferences.getString(ConfigPreferences.EMAIL,null);
    if(emailPreference!=null && !emailPreference.isEmpty()) {
      userProfileView.showPreferenceEmail(emailPreference);
    }else {
      userProfileView.showPreferenceEmail(context.getResources().getString(R.string.emailPreference));
    }
  }

  public void updateUserNamePreference(String userNamePreference) {
    preferencesEditor = sharedPreferences.edit();
    preferencesEditor.putString(ConfigPreferences.USERNAME, userNamePreference);
    preferencesEditor.apply();
    userProfileView.showPreferenceUserName(userNamePreference);
    String data = sharedPreferences.getString(ConfigPreferences.USERNAME, null);
    if (data != null && !data.isEmpty()) {
      userProfileView.setUserPropertyToMixpanel(MIXPANEL_USERNAME_ID, data);
    }
  }

  public void updateUserEmailPreference(String userEmailPreference) {
    if(!isValidEmail(userEmailPreference)) {
      userProfileView.showError(R.string.invalid_email);
      return;
    }
    preferencesEditor = sharedPreferences.edit();
    preferencesEditor.putString(ConfigPreferences.EMAIL, userEmailPreference);
    preferencesEditor.apply();
    userProfileView.showPreferenceEmail(userEmailPreference);
    String data = sharedPreferences.getString(ConfigPreferences.EMAIL, null);
    if (data != null && !data.isEmpty()) {
      userProfileView.setUserPropertyToMixpanel(MIXPANEL_EMAIL_ID, data);
    }
  }

  public void getInfoVideosRecordedEdited() {
    if(videosRecorded != -1 && videosEdited != -1) {
      return;
    }

    userProfileView.showLoading();

    obtainLocalVideosUseCase.obtainRawVideos(new OnVideosRetrieved() {
      @Override
      public void onVideosRetrieved(List<Video> videoList) {
        videosRecorded = videoList.size();
        userProfileView.showVideosRecorded(Integer.toString(videosRecorded));
        isVideosRecordedRetrieved = true;
        checkHideLoading();
      }

      @Override
      public void onNoVideosRetrieved() {
        videosRecorded = 0;
        isVideosRecordedRetrieved = true;
        checkHideLoading();
      }
    });

    obtainLocalVideosUseCase.obtainEditedVideos(new OnVideosRetrieved() {
      @Override
      public void onVideosRetrieved(List<Video> videoList) {
        videosEdited = videoList.size();
        userProfileView.showVideosEdited(Integer.toString(videosEdited));
        isVideosEditedRetrieved = true;
        checkHideLoading();
      }

      @Override
      public void onNoVideosRetrieved() {
        videosEdited = 0;
        isVideosEditedRetrieved = true;
        checkHideLoading();
      }
    });
  }

  private void checkHideLoading() {
    if(isVideosRecordedRetrieved && isVideosEditedRetrieved){
      userProfileView.hideLoading();
    }
  }

  protected boolean isValidEmail(String email) {
    return android.util.Patterns.EMAIL_ADDRESS
        .matcher(email).matches();
  }

}
