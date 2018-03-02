package com.videonasocialmedia.vimojo.userProfile.presentation.mvp.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
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
import com.videonasocialmedia.vimojo.vimojoapiclient.model.AuthToken;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.User;

import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.Nullable;
import javax.inject.Inject;

/**
 * TODO: document this class, whats the objective and responsibility for this class?
 */
public class UserProfilePresenter extends VimojoPresenter {
  private final SharedPreferences sharedPreferences;
  private final UserProfileView userProfileView;
  private final ObtainLocalVideosUseCase obtainLocalVideosUseCase;
  private final GetAuthToken getAuthToken;
  private final Context context;
  private final UserApiClient userApiClient;

  @Inject
  public UserProfilePresenter(Context context, UserProfileView view,
                              SharedPreferences sharedPreferences, ObtainLocalVideosUseCase
                              obtainLocalVideosUseCase, GetAuthToken getAuthToken,
                              UserApiClient userApiClient) {
    this.context = context;
    this.userProfileView =view;
    this.sharedPreferences = sharedPreferences;
    this.obtainLocalVideosUseCase = obtainLocalVideosUseCase;
    this.getAuthToken = getAuthToken;
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
    ListenableFuture<AuthToken> authTokenFuture = executeUseCaseCall(new Callable<AuthToken>() {
      @Override
      public AuthToken call() throws Exception {
        return getAuthToken.getAuthToken(context);
      }
    });
    Futures.addCallback(authTokenFuture, new FutureCallback<AuthToken>() {
      @Override
      public void onSuccess(AuthToken authToken) {
        callUserServiceGetUser(authToken.getToken(), authToken.getId());
      }
      @Override
      public void onFailure(@NonNull Throwable errorGettingToken) {
      }
    });
  }

  private void callUserServiceGetUser(String token, String id) {
    ListenableFuture<User> userFuture = executeUseCaseCall(new Callable<User>() {
      @Override
      public User call() throws Exception {
        return userApiClient.getUser(token, id);
      }
    });
    Futures.addCallback(userFuture, new FutureCallback<User>() {
      @Override
      public void onSuccess(@Nullable User result) {
        userProfileView.showPreferenceUserName(result.getUsername());
        userProfileView.showPreferenceEmail(result.getEmail());
      }

      @Override
      public void onFailure(Throwable t) {
        userProfileView.showError(R.string.error);
      }
    });
  }

  public void onClickUsername(boolean emptyField) {
    if (emptyField && BuildConfig.FEATURE_VIMOJO_PLATFORM) {
      userProfileView.navigateToUserAuth();
    }
  }

  public void onClickEmail(boolean emptyField) {
    if (emptyField && BuildConfig.FEATURE_VIMOJO_PLATFORM) {
      userProfileView.navigateToUserAuth();
    }
  }
}
