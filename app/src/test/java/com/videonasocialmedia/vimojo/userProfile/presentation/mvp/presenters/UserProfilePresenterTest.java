package com.videonasocialmedia.vimojo.userProfile.presentation.mvp.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.auth.domain.usecase.GetAuthToken;
import com.videonasocialmedia.vimojo.domain.ObtainLocalVideosUseCase;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.userProfile.presentation.mvp.views.UserProfileView;
import com.videonasocialmedia.vimojo.vimojoapiclient.UserApiClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UserProfilePresenterTest {
  @Mock UserProfileView mockedUserProfileView;
  @Mock Context mockedContext;
  @Mock SharedPreferences mockedSharedPreferences;
  @Mock ObtainLocalVideosUseCase mockedObtainLocalVideosUseCase;
  @Mock GetAuthToken mockedGetAuthToken;
  @Mock UserApiClient mockedUserApiClient;

  @Before
  public void injectMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void getInfoVideosRecordedEditedSharedCallsShowLoading() {
    UserProfilePresenter presenter = getUserProfilePresenter();

    presenter.getInfoVideosRecordedEditedShared();

    verify(mockedUserProfileView).showLoading();
    verify(mockedUserProfileView).showVideosRecorded(anyString());
    verify(mockedUserProfileView).showVideosShared(anyString());
  }

  @Test
  public void obtainLocalVideosUseCaseObtainEditedVideosShowVideosEdited() {
    UserProfilePresenter presenter = getUserProfilePresenter();
    final List<Video> videoList = new ArrayList<>();
    Video video = new Video("mediaPath", 0.5f);
    videoList.add(video);
    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        ((OnVideosRetrieved)invocation.getArguments()[0]).onVideosRetrieved(videoList);
        return null;
      }
    }).when(mockedObtainLocalVideosUseCase).obtainEditedVideos(any(OnVideosRetrieved.class));

    presenter.getInfoVideosRecordedEditedShared();

    verify(mockedUserProfileView).showVideosEdited(anyString());
    verify(mockedUserProfileView).hideLoading();
  }

  @Test
  public void clickUserEmailCallsUserAuthIfEmptyField(){
    UserProfilePresenter presenter = getUserProfilePresenter();

    presenter.onClickEmail(true);

    if (BuildConfig.FEATURE_VIMOJO_PLATFORM)
      verify(mockedUserProfileView).navigateToUserAuth();
  }

  @Test
  public void clickUserNameCallsUserAuthIfEmptyField(){
    UserProfilePresenter presenter = getUserProfilePresenter();

    presenter.onClickUsername(true);

    if (BuildConfig.FEATURE_VIMOJO_PLATFORM)
      verify(mockedUserProfileView).navigateToUserAuth();
  }

  @NonNull
  private UserProfilePresenter getUserProfilePresenter() {
    return new UserProfilePresenter(mockedContext, mockedUserProfileView, mockedSharedPreferences,
            mockedObtainLocalVideosUseCase, mockedGetAuthToken, mockedUserApiClient);
  }
}
