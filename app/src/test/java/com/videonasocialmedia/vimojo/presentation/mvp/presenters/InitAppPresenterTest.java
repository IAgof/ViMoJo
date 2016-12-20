package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import com.videonasocialmedia.vimojo.domain.project.CreateDefaultProjectUseCase;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

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
  @Mock ProjectRepository mockedProjectRepo;
  @Mock CreateDefaultProjectUseCase mockedUseCase;

  @InjectMocks InitAppPresenter injectedPresenter;

  @Before
  public void initDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void startLoadingProjectCallsLoadOrCreateProject() {
    Profile profile = new Profile(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
        VideoFrameRate.FrameRate.FPS25);
    injectedPresenter.startLoadingProject("root/path", profile);

    verify(mockedUseCase).loadOrCreateProject("root/path", profile);
  }
}