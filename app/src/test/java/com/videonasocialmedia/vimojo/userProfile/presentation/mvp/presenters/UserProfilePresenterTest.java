package com.videonasocialmedia.vimojo.userProfile.presentation.mvp.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.domain.ObtainLocalVideosUseCase;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.userProfile.presentation.mvp.views.UserProfileView;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by alvaro on 11/01/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class UserProfilePresenterTest {

  @Mock UserProfileView mockedUserProfileView;
  @Mock Context mockedContext;
  @Mock SharedPreferences mockedSharedPreferences;
  @Mock SharedPreferences.Editor mockedEditor;
  @Mock ObtainLocalVideosUseCase mockedObtainLocalVideosUseCase;
  @Mock OnVideosRetrieved mockedOnVideosRetrieved;
  @Mock UserEventTracker mockedUserEventTracker;

  @Before
  public void injectMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void getUserNameFromPreferencesCallsShowPreferenceUserName() {
    UserProfilePresenter presenter = getUserProfilePresenter();
    String userName = "John Doe";
    when(mockedSharedPreferences.getString(ConfigPreferences.USERNAME, null)).thenReturn(userName);

    presenter.getUserNameFromPreferences();

    verify(mockedUserProfileView).showPreferenceUserName(userName);
  }

  @Test
  public void getEmailFromPreferencesCallsShowPreferencesEmail() {
    UserProfilePresenter presenter = getUserProfilePresenter();
    String email = "a@a.a";
    when(mockedSharedPreferences.getString(ConfigPreferences.EMAIL, null)).thenReturn(email);

    presenter.getEmailFromPreferences();

    verify(mockedUserProfileView).showPreferenceEmail(email);
  }

  @Test
  public void updateUserEmailPreferenceShowErrorIfInvalidMail() {
    UserProfilePresenter presenter = getUserProfilePresenter();
    String email = "a@a";
    presenter = Mockito.spy(presenter);
    doReturn(false).when(presenter).isValidEmail(email);

    presenter.updateUserEmailPreference(email);

    verify(mockedUserProfileView).showError(anyInt());
  }

  @Test
  public void updateUserEmailPreferenceCallsShowPreferenceEmailIfValidMail() {
    UserProfilePresenter presenter = getUserProfilePresenter();
    String email = "a@a.com";
    presenter = Mockito.spy(presenter);
    doReturn(true).when(presenter).isValidEmail(email);
    when(mockedSharedPreferences.edit()).thenReturn(mockedEditor);
    when(mockedSharedPreferences.getString(ConfigPreferences.EMAIL, null)).thenReturn(email);

    presenter.updateUserEmailPreference(email);

    verify(mockedUserProfileView).showPreferenceEmail(email);
    verify(mockedUserEventTracker).trackUpdateUserEmail(email);
  }

  @Test
  public void updateUserNamePreferenceCallsShowPreferenceUserName() {
    UserProfilePresenter presenter = getUserProfilePresenter();
    String userName = "John Doe";
    when(mockedSharedPreferences.edit()).thenReturn(mockedEditor);
    when(mockedSharedPreferences.getString(ConfigPreferences.USERNAME, null)).thenReturn(userName);

    presenter.updateUserNamePreference(userName);

    verify(mockedUserProfileView).showPreferenceUserName(userName);
    verify(mockedUserEventTracker).trackUpdateUserName(userName);
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

  @NonNull
  private UserProfilePresenter getUserProfilePresenter() {
    return new UserProfilePresenter(mockedUserProfileView, mockedUserEventTracker,
        mockedSharedPreferences, mockedObtainLocalVideosUseCase);
  }
}
