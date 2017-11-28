package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.content.SharedPreferences;

import com.videonasocialmedia.vimojo.domain.project.CreateDefaultProjectUseCase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

/**
 * Created by jliarte on 22/10/16.
 */
public class InitAppPresenterTest {

  @Mock CreateDefaultProjectUseCase mockedUseCase;
  @Mock SharedPreferences mockedSharedPreferences;

  @Before
  public void initDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void startLoadingProjectCallsLoadOrCreateProject() {
    InitAppPresenter initAppPresenter = getInitAppPresenter();

    initAppPresenter.startLoadingProject("root/path", "private/path");

    // TODO:(alvaro.martinez) 28/11/17 Learn how to mock BuildConfig values and check values in verify method, not anyString, anyString, anyBoolean
    verify(mockedUseCase).loadOrCreateProject(anyString(),anyString(), anyBoolean());
  }

  private InitAppPresenter getInitAppPresenter() {
    return new InitAppPresenter(mockedSharedPreferences, mockedUseCase);
  }
}