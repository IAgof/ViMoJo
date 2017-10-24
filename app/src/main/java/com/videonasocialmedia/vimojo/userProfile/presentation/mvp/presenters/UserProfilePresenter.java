package com.videonasocialmedia.vimojo.userProfile.presentation.mvp.presenters;

import android.content.Context;
import android.content.SharedPreferences;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.userProfile.presentation.views.UserProfileActivityView;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;

import javax.inject.Inject;

/**
 * Created by ruth on 13/10/17.
 */

public class UserProfilePresenter {

  private SharedPreferences sharedPreferences;
  private SharedPreferences.Editor preferencesEditor;
  private UserProfileActivityView userProfileActivityView;
  private Context context;

  @Inject
  public UserProfilePresenter(UserProfileActivityView view, Context context,
                              SharedPreferences sharedPreferences){
    this.context=context;
    this.userProfileActivityView=view;
    this.sharedPreferences = sharedPreferences;
  }

  public void getUserNameFromPreferences() {
    String userNamePreference = sharedPreferences.getString(ConfigPreferences.USERNAME, null);
    if(userNamePreference!=null && !userNamePreference.isEmpty())
      userProfileActivityView.showPreferenceUserName(userNamePreference);
    else {
      userProfileActivityView.showPreferenceUserName(context.getResources().getString(R.string.username));
    }
  }

  public void getEmailFromPreferences() {
    String emailPreference=sharedPreferences.getString(ConfigPreferences.EMAIL,null);
    if(emailPreference!=null && !emailPreference.isEmpty()) {
      userProfileActivityView.showPreferenceEmail(emailPreference);
    }else {
      userProfileActivityView.showPreferenceEmail(context.getResources().getString(R.string.emailPreference));
    }
  }

  public void updateUserNamePreference(String userNamePreference) {
    preferencesEditor = sharedPreferences.edit();
    preferencesEditor.putString(ConfigPreferences.USERNAME, userNamePreference);
    preferencesEditor.apply();
    userProfileActivityView.showPreferenceUserName(userNamePreference);
  }
}
