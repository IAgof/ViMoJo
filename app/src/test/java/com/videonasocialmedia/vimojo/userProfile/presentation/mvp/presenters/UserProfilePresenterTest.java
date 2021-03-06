package com.videonasocialmedia.vimojo.userProfile.presentation.mvp.presenters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.auth0.UserAuth0Helper;
import com.videonasocialmedia.vimojo.auth0.accountmanager.GetAccount;
import com.videonasocialmedia.vimojo.domain.ObtainLocalVideosUseCase;
import com.videonasocialmedia.vimojo.featuresToggles.domain.usecase.FetchUserFeatures;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.repository.upload.UploadDataSource;
import com.videonasocialmedia.vimojo.userProfile.presentation.mvp.views.UserProfileView;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.view.BackgroundExecutor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
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
  @Mock Activity mockedActivity;
  @Mock UserAuth0Helper mockedUserAuth0Helper;
  @Mock FetchUserFeatures mockedFetchUserFeatures;
  @Mock GetAccount mockedGetAccount;
  @Mock UploadDataSource mockedUploadDataSource;
  private boolean vimojoPlatformAvailable;
  @Mock BackgroundExecutor mockedBackgroundExecutor;
  @Mock UserEventTracker mockedUserEventTracker;

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
    UserProfilePresenter presenterSpy = Mockito.spy(getUserProfilePresenter());
    presenterSpy.vimojoPlatformAvailable = true;

    presenterSpy.onClickEmail(mockedActivity, true);

    verify(presenterSpy).performLoginAndSaveAccount(mockedActivity);
  }

  @Test
  public void clickUserNameCallsUserAuthIfEmptyField(){
    UserProfilePresenter presenterSpy = Mockito.spy(getUserProfilePresenter());
    presenterSpy.vimojoPlatformAvailable = true;

    presenterSpy.onClickUsername(mockedActivity, true);

    verify(presenterSpy).performLoginAndSaveAccount(mockedActivity);
  }

  @NonNull
  private UserProfilePresenter getUserProfilePresenter() {
    return new UserProfilePresenter(mockedContext, mockedUserProfileView, mockedSharedPreferences,
            mockedObtainLocalVideosUseCase, mockedUserAuth0Helper, mockedFetchUserFeatures,
            mockedGetAccount, mockedUploadDataSource, vimojoPlatformAvailable,
            mockedBackgroundExecutor, mockedUserEventTracker);
  }
}
