package com.videonasocialmedia.vimojo.userProfile.presentation.mvp.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.authentication.storage.CredentialsManagerException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.result.Credentials;
import com.auth0.android.result.UserProfile;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.auth.domain.usecase.GetAuthToken;
import com.videonasocialmedia.vimojo.domain.ObtainLocalVideosUseCase;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.userProfile.presentation.mvp.views.UserProfileView;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.view.VimojoPresenter;
import com.videonasocialmedia.vimojo.vimojoapiclient.UserApiClient;

import java.util.List;

import javax.inject.Inject;

/**
 * TODO: document this class, whats the objective and responsibility for this class?
 */
public class UserProfilePresenter extends VimojoPresenter {
  private String LOG_TAG = UserProfilePresenter.class.getCanonicalName();
  private final SharedPreferences sharedPreferences;
  private final UserProfileView userProfileView;
  private final ObtainLocalVideosUseCase obtainLocalVideosUseCase;
  private final Context context;
  private final UserApiClient userApiClient;

  @Inject
  public UserProfilePresenter(Context context, UserProfileView view,
                              SharedPreferences sharedPreferences, ObtainLocalVideosUseCase
                              obtainLocalVideosUseCase, UserApiClient userApiClient) {
    this.context = context;
    this.userProfileView =view;
    this.sharedPreferences = sharedPreferences;
    this.obtainLocalVideosUseCase = obtainLocalVideosUseCase;
    this.userApiClient = userApiClient;
  }

  public void getInfoVideosRecordedEditedShared() {
    userProfileView.showLoading();

    int videosRecorded = sharedPreferences
        .getInt(ConfigPreferences.TOTAL_VIDEOS_RECORDED, 0);
    userProfileView.showVideosRecorded(Integer.toString(videosRecorded));

    obtainLocalVideosUseCase.obtainEditedVideos(new OnVideosRetrieved() {
      @Override
      public void onVideosRetrieved(List<Video> videoList) {
        userProfileView.showVideosEdited(Integer.toString(videoList.size()));
        userProfileView.hideLoading();
      }

      @Override
      public void onNoVideosRetrieved() {
        userProfileView.hideLoading();
      }
    });

    int videosShared = sharedPreferences.getInt(ConfigPreferences.TOTAL_VIDEOS_SHARED, 0);
    userProfileView.showVideosShared(Integer.toString(videosShared));

  }

  public void setupUserInfo() {
    if (!BuildConfig.FEATURE_VIMOJO_PLATFORM) {
      return;
    }

    if (!userApiClient.isLogged()) {
      return;
    }
    // Get token, needed for get user info.
    final String[] accesToken = new String[1];
    userApiClient.getManager().getCredentials(new BaseCallback<Credentials,
        CredentialsManagerException>() {
      @Override
      public void onSuccess(Credentials credentials) {
        //Use credentials
        accesToken[0] = credentials.getAccessToken();
        // Get User Info
        getUserInfo(accesToken[0]);
      }

      @Override
      public void onFailure(CredentialsManagerException error) {
        //No credentials were previously saved or they couldn't be refreshed
        return;
      }
    });

  }

  private void getUserInfo(String accessToken) {
    userApiClient.getAuthenticator().userInfo(accessToken)
        .start(new BaseCallback<UserProfile, AuthenticationException>() {
          @Override
          public void onSuccess(UserProfile userinfo) {
            // Display the user profile
            Log.d(LOG_TAG, " onSuccess userInfo id " + userinfo.getId());
            userProfileView.showPreferenceUserName(userinfo.getName());
            userProfileView.showPreferenceEmail(userinfo.getEmail());
          }

          @Override
          public void onFailure(AuthenticationException error) {
            // Show error
            userProfileView.showError(R.string.error);
          }
        });
  }

  public void onClickUsername(boolean emptyField) {
    if (emptyField && BuildConfig.FEATURE_VIMOJO_PLATFORM) {
      userProfileView.navigateToUserAuth0();
    }
  }

  public void onClickEmail(boolean emptyField) {
    if (emptyField && BuildConfig.FEATURE_VIMOJO_PLATFORM) {
      userProfileView.navigateToUserAuth0();
    }
  }
}
