package com.videonasocialmedia.vimojo.domain.editor;

import android.support.annotation.NonNull;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnAddMediaFinishedListener;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;
import java.util.List;

import de.greenrobot.event.EventBus;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;

/**
 * Created by jliarte on 22/10/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(EventBus.class)
public class AddVideoToProjectUseCaseTest {
  @Mock ProjectRealmRepository mockedProjectRepository;

  @InjectMocks AddVideoToProjectUseCase injectedUseCase;
  private EventBus mockedEventBus;

  @Before
  public void injectDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @Before
  public void setupTestEventBus() {
    PowerMockito.mockStatic(EventBus.class);
    EventBus mockedEventBus = PowerMockito.mock(EventBus.class);
    PowerMockito.when(EventBus.getDefault()).thenReturn(mockedEventBus);
    this.mockedEventBus = mockedEventBus;
  }

  @Test
  public void testAddVideoToTrackCallsUpdateProject() {
    Project currentProject = Project.getInstance(null, null, null);
    Video video = new Video("media/path", 1f);

    injectedUseCase.addVideoToTrack(video);

    verify(mockedProjectRepository).update(currentProject);
  }

  @Test
  public void testAddVideoToTrackWithListenerCallsUpdateProject() {
    Project currentProject = Project.getInstance(null, null, null);
    Video video = new Video("media/path", 1f);
    OnAddMediaFinishedListener listener = getOnAddMediaFinishedListener();

    injectedUseCase.addVideoToTrack(video, listener);

    verify(mockedProjectRepository).update(currentProject);
  }

  @Test
  public void testAddVideoToProjectAtPositionCallsUpdateProject() {
    Project currentProject = Project.getInstance(null, null, null);
    Video video = new Video("media/path", 1f);

    injectedUseCase.addVideoToProjectAtPosition(video, 0);

    verify(mockedProjectRepository).update(currentProject);
  }

  @Test
  public void testAddVideoListToTrackCallsUpdateProject() {
    Project currentProject = Project.getInstance(null, null, null);
    Video video = new Video("media/path", 1f);
    List<Video> videoList = Collections.singletonList(video);
    OnAddMediaFinishedListener listener = getOnAddMediaFinishedListener();

    injectedUseCase.addVideoListToTrack(videoList, listener);

    verify(mockedProjectRepository).update(currentProject);
  }

  @NonNull
  private OnAddMediaFinishedListener getOnAddMediaFinishedListener() {
    return new OnAddMediaFinishedListener() {
      @Override
      public void onAddMediaItemToTrackError() {

      }

      @Override
      public void onAddMediaItemToTrackSuccess(Media media) {

      }
    };
  }
}