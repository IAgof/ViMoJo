package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import com.videonasocialmedia.vimojo.domain.project.CreateDefaultProjectUseCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

/**
 * Created by jliarte on 22/10/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class InitAppPresenterTest {
  @Mock CreateDefaultProjectUseCase mockedUseCase;

  @InjectMocks InitAppPresenter injectedPresenter;

  @Before
  public void initDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void startLoadingProjectCallsLoadOrCreateProject() {
    injectedPresenter.startLoadingProject("root/path", "private/path");

    verify(mockedUseCase).loadOrCreateProject("root/path","private/path", false);
  }
}