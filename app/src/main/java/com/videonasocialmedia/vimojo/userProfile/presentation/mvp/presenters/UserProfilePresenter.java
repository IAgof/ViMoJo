package com.videonasocialmedia.vimojo.userProfile.presentation.mvp.presenters;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.authentication.storage.CredentialsManagerException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.result.Credentials;
import com.auth0.android.result.UserProfile;
import com.crashlytics.android.Crashlytics;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.auth0.UserAuth0Helper;
import com.videonasocialmedia.vimojo.domain.ObtainLocalVideosUseCase;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.userProfile.presentation.mvp.views.UserProfileView;
import com.videonasocialmedia.vimojo.featuresToggles.domain.usecase.FetchUserFeatures;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.view.VimojoPresenter;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * TODO: document this class, whats the objective and responsibility for this class?
 */
public class UserProfilePresenter extends VimojoPresenter {
  private String LOG_TAG = UserProfilePresenter.class.getCanonicalName();
  private final SharedPreferences sharedPreferences;
  private final UserProfileView userProfileView;
  private final ObtainLocalVideosUseCase obtainLocalVideosUseCase;
  protected final UserAuth0Helper userAuth0Helper;
  private FetchUserFeatures fetchUserFeatures;
  private boolean vimojoPlatformAvailable;

  @Inject
  public UserProfilePresenter(
          UserProfileView view, SharedPreferences sharedPreferences,
          ObtainLocalVideosUseCase obtainLocalVideosUseCase, UserAuth0Helper userAuth0Helper,
          FetchUserFeatures fetchUserFeatures,
          @Named("vimojoPlatformAvailable") boolean vimojoPlatformAvailable) {
    this.userProfileView = view;
    this.sharedPreferences = sharedPreferences;
    this.obtainLocalVideosUseCase = obtainLocalVideosUseCase;
    this.userAuth0Helper = userAuth0Helper;
    this.fetchUserFeatures = fetchUserFeatures;
    this.vimojoPlatformAvailable = vimojoPlatformAvailable;
  }

  public void init() {
    getInfoVideosRecordedEditedShared();
    setupUserInfo();
  }

  protected void getInfoVideosRecordedEditedShared() {
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

  private void setupUserInfo() {
    if (!userAuth0Helper.isLogged()) {
      return;
    }
    // Get token, needed for get user info.
    userAuth0Helper.getAccessToken(new BaseCallback<Credentials, CredentialsManagerException>() {
      @Override
      public void onFailure(CredentialsManagerException error) {
        Log.d(LOG_TAG, "Error getAccessToken CredentialsManagerException "
            + error.getMessage());
        Crashlytics.log("Error getAccessToken CredentialsManagerException: " + error);
        // Show error
        userProfileView.showError(R.string.error);
      }

      @Override
      public void onSuccess(Credentials credentials) {
        getUserProfile(credentials.getAccessToken());
      }
    });

  }

  public void onClickUsername(Activity activity, boolean emptyField) {
    if (emptyField && vimojoPlatformAvailable) {
      performLoginAndSaveAccount(activity);
    }
  }

  public void onClickEmail(Activity activity, boolean emptyField) {
    if (emptyField && vimojoPlatformAvailable) {
      performLoginAndSaveAccount(activity);
    }
  }

  protected void performLoginAndSaveAccount(Activity activity) {
    userAuth0Helper.performLogin(activity, new UserAuth0Helper.AuthCallback() {
      @Override
      public void onFailure(AuthenticationException exception) {
        userProfileView.showError(R.string.auth0_error_authentication);
      }

      @Override
      public void onSuccess(@NonNull Credentials credentials) {
        fetchUserFeatures();
        setupUserInfo();
      }
    });
  }

  private void fetchUserFeatures() {
    fetchUserFeatures.fetch();
  }

  private void getUserProfile(String accessToken) {
    userAuth0Helper.getUserProfile(accessToken,
        new BaseCallback<UserProfile, AuthenticationException>() {
          @Override
          public void onFailure(AuthenticationException error) {
            Log.d(LOG_TAG, "Error getting user profile info " + error.getMessage());
            Crashlytics.log("Error getUserProfile AuthenticationException: " + error);
          }

          @Override
          public void onSuccess(UserProfile userProfile) {
            // Display the user profile
            Log.d(LOG_TAG, " onSuccess getUserProfile accessToken " + accessToken);
            userProfileView.showPreferenceUserName(userProfile.getName());
            userProfileView.showPreferenceEmail(userProfile.getEmail());
            userProfileView.showPreferenceUserPic(userProfile.getPictureURL());
          }
        });
  }
}
