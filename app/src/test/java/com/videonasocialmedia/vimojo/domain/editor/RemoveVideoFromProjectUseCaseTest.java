package com.videonasocialmedia.vimojo.domain.editor;

import android.support.annotation.NonNull;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Media;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.model.entities.editor.track.MediaTrack;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnRemoveMediaFinishedListener;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;

/**
 * Created by jliarte on 23/10/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(EventBus.class)
public class RemoveVideoFromProjectUseCaseTest {
  @Mock ProjectRepository mockedProjectRepository;
  @Mock VideoRepository mockedVideoRepository;
  @InjectMocks RemoveVideoFromProjectUseCase injectedUseCase;
  @Mock MediaTrack mockedMediaTrack;
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
  public void testRemoveMediaItemsFromProjectCallsUpdateProject() {
    Project currentProject = Project.getInstance(null, null, null);
    currentProject.setMediaTrack(mockedMediaTrack);
    Video video = new Video("media/path");
    ArrayList<Media> videos = new ArrayList<Media>();
    videos.add(video);
    OnRemoveMediaFinishedListener listener = getOnRemoveMediaFinishedListener();

    injectedUseCase.removeMediaItemsFromProject(videos, listener);

    verify(mockedProjectRepository).update(currentProject);
  }

  @Test
  public void testRemoveMediaItemsFromProjectCallsRemoveRealmVideos() {
    Project currentProject = Project.getInstance(null, null, null);
    currentProject.setMediaTrack(mockedMediaTrack);
    Video video = new Video("media/path");
    ArrayList<Media> videos = new ArrayList<Media>();
    videos.add(video);
    OnRemoveMediaFinishedListener listener = getOnRemoveMediaFinishedListener();

    injectedUseCase.removeMediaItemsFromProject(videos, listener);

    verify(mockedVideoRepository).remove(video);
  }

  @NonNull
  private OnRemoveMediaFinishedListener getOnRemoveMediaFinishedListener() {
    return new OnRemoveMediaFinishedListener() {
      @Override
      public void onRemoveMediaItemFromTrackError() {

      }

      @Override
      public void onRemoveMediaItemFromTrackSuccess() {

      }
    };
  }
}