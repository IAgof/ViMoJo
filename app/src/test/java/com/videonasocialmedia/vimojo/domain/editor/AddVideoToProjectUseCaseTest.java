package com.videonasocialmedia.vimojo.domain.editor;

import android.support.annotation.NonNull;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnAddMediaFinishedListener;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnLaunchAVTransitionTempFileListener;
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
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by jliarte on 22/10/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(EventBus.class)
public class AddVideoToProjectUseCaseTest {
  @Mock ProjectRealmRepository mockedProjectRepository;
  @Mock OnLaunchAVTransitionTempFileListener mockedLaunchAVTransitionTempFileListener;
  @Mock OnAddMediaFinishedListener mockedOnAddMediaFinishedListener;
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
  public void testAddVideoToProjectAtPositionCallsUpdateProject() {
    Project currentProject = Project.getInstance(null, null, null);
    Video video = new Video("media/path", 1f);

    injectedUseCase.addVideoToProjectAtPosition(video, 0, mockedOnAddMediaFinishedListener);

    verify(mockedProjectRepository).update(currentProject);
  }

  @Test
  public void testAddVideoListToTrackCallsUpdateProject() {
    Project currentProject = Project.getInstance(null, null, null);
    Video video = new Video("media/path", 1f);
    List<Video> videoList = Collections.singletonList(video);
    OnAddMediaFinishedListener listener = getOnAddMediaFinishedListener();
    OnLaunchAVTransitionTempFileListener avTransitionTempFileListener =
        getOnLaunchAVTransitionTempFileListener();

    injectedUseCase.addVideoListToTrack(videoList, listener, avTransitionTempFileListener);

    verify(mockedProjectRepository).update(currentProject);
  }

  @Test
  public void ifAudioTransitionActivatedAddVideoToProjectCallVideoToLaunchAVTransitionTempFile() {
    Project project = getAProject();
    project.setAudioFadeTransitionActivated(true);
    assertThat("Audio transition is activated ", project.isAudioFadeTransitionActivated(), is(true));

    Video video = new Video("media/path", 1f);
    List<Video> videoList = Collections.singletonList(video);
    OnAddMediaFinishedListener listener = getOnAddMediaFinishedListener();

    injectedUseCase.addVideoListToTrack(videoList, listener, mockedLaunchAVTransitionTempFileListener);

    verify(mockedLaunchAVTransitionTempFileListener).videoToLaunchAVTransitionTempFile(video,
        project.getProjectPathIntermediateFileAudioFade());
  }

  @Test
  public void ifVideoTransitionActivatedAddVideoToProjectCallVideoToLaunchAVTransitionTempFile() {
    Project project = getAProject();
    project.setVideoFadeTransitionActivated(true);
    assertThat("Video transition is activated ", project.isVideoFadeTransitionActivated(), is(true));

    Video video = new Video("media/path", 1f);
    List<Video> videoList = Collections.singletonList(video);
    OnAddMediaFinishedListener listener = getOnAddMediaFinishedListener();

    injectedUseCase.addVideoListToTrack(videoList, listener, mockedLaunchAVTransitionTempFileListener);

    verify(mockedLaunchAVTransitionTempFileListener).videoToLaunchAVTransitionTempFile(video,
        project.getProjectPathIntermediateFileAudioFade());
  }

  @Test
  public void ifAVTransitionNotActivatedAddVideoToProjectNotCallVideoToLaunchAVTransitionTempFile() {
    getAProject().clear();
    Project project = getAProject();
    assertThat("Audio transition is not activated ", project.isAudioFadeTransitionActivated(), is(false));
    assertThat("Video transition is not activated ", project.isVideoFadeTransitionActivated(), is(false));

    Video video = new Video("media/path", 1f);
    List<Video> videoList = Collections.singletonList(video);
    OnAddMediaFinishedListener listener = getOnAddMediaFinishedListener();

    injectedUseCase.addVideoListToTrack(videoList, listener, mockedLaunchAVTransitionTempFileListener);

    verify(mockedLaunchAVTransitionTempFileListener, never()).videoToLaunchAVTransitionTempFile(video,
        project.getProjectPathIntermediateFileAudioFade());
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

  @NonNull
  private OnLaunchAVTransitionTempFileListener getOnLaunchAVTransitionTempFileListener() {
    return new OnLaunchAVTransitionTempFileListener() {
      @Override
      public void videoToLaunchAVTransitionTempFile(Video video,
                                                    String intermediatesTempAudioFadeDirectory) {

      }
    };
  }

  public Project getAProject() {
    return Project.getInstance("title", "/path", Profile.getInstance(VideoResolution.Resolution.HD720,
        VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25));
  }
}