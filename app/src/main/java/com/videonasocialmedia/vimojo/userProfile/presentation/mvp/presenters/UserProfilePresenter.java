package com.videonasocialmedia.vimojo.userProfile.presentation.mvp.presenters;

import android.content.Context;
import android.content.SharedPreferences;

import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.domain.ObtainLocalVideosUseCase;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.userProfile.presentation.views.UserProfileActivityView;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by ruth on 13/10/17.
 */

public class UserProfilePresenter {

  private SharedPreferences sharedPreferences;
  private SharedPreferences.Editor preferencesEditor;
  private UserProfileActivityView userProfileActivityView;
  private Context context;
  private ObtainLocalVideosUseCase obtainLocalVideosUseCase;
  private int videosRecorded = -1;
  private int videosEdited = -1;
  private boolean isVideosRecordedRetrieved = false;
  private boolean isVideosEditedRetrieved = false;

  @Inject
  public UserProfilePresenter(UserProfileActivityView view, Context context,
                              SharedPreferences sharedPreferences, ObtainLocalVideosUseCase
                              obtainLocalVideosUseCase){
    this.context=context;
    this.userProfileActivityView=view;
    this.sharedPreferences = sharedPreferences;
    this.obtainLocalVideosUseCase = obtainLocalVideosUseCase;
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

  public void getInfoVideosRecordedEdited() {
    if(videosRecorded != -1 && videosEdited != -1) {
      return;
    }

    userProfileActivityView.showLoading();

    obtainLocalVideosUseCase.obtainRawVideos(new OnVideosRetrieved() {
      @Override
      public void onVideosRetrieved(List<Video> videoList) {
        videosRecorded = videoList.size();
        userProfileActivityView.showVideosRecorded(Integer.toString(videosRecorded));
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
        userProfileActivityView.showVideosEdited(Integer.toString(videosEdited));
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
      userProfileActivityView.hideLoading();
    }
  }

}
