package com.videonasocialmedia.vimojo.domain.editor;

import android.support.annotation.NonNull;

import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.track.MediaTrack;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnReorderMediaListener;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;

/**
 * Created by jliarte on 23/10/16.
 */
@RunWith(RobolectricTestRunner.class)
public class ReorderMediaItemUseCaseTest {
  @Mock ProjectRepository mockedProjectRepository;
  @InjectMocks ReorderMediaItemUseCase injectedUseCase;
  @Mock MediaTrack mockedMediaTrack;
  private Project currentProject;

  @Before
  public void injectDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @Before
  public void setProject() {
    currentProject = Project.getInstance(null, null, null, null);
  }

  @After
  public void clearProject() {
    currentProject.clear();
  }

  @Test
  public void testMoveMediaItemCallsUpdateProject() throws IllegalItemOnTrack {
    Video video0 = new Video("video/0", Video.DEFAULT_VOLUME);
    Video video1 = new Video("video/1", Video.DEFAULT_VOLUME);
    currentProject.getVMComposition().getMediaTrack().insertItemAt(0, video0);
    currentProject.getVMComposition().getMediaTrack().insertItemAt(1, video1);
    OnReorderMediaListener onReorderMediaListener = getOnReorderMediaListener();

    injectedUseCase.moveMediaItem(1, 0, onReorderMediaListener);

    verify(mockedProjectRepository).update(currentProject);
  }

  @Test
  public void testSwap2Items() throws IllegalItemOnTrack {
    Video video0 = new Video("video/0", Video.DEFAULT_VOLUME);
    video0.setPosition(0);
    Video video1 = new Video("video/1", Video.DEFAULT_VOLUME);
    video1.setPosition(1);
    currentProject.getVMComposition().getMediaTrack().insertItemAt(0, video0);
    currentProject.getVMComposition().getMediaTrack().insertItemAt(1, video1);
    OnReorderMediaListener onReorderMediaListener = getOnReorderMediaListener();

    injectedUseCase.moveMediaItem(1, 0, onReorderMediaListener);

    assertThat(currentProject.getMediaTrack().getItems().indexOf(video0), is(1));
    assertThat(video0.getPosition(), is(1));
    assertThat(currentProject.getMediaTrack().getItems().indexOf(video1), is(0));
    assertThat(video1.getPosition(), is(0));
  }

  @Test
  public void testReorder4Items() throws IllegalItemOnTrack {
    Video video0 = new Video("video/0", Video.DEFAULT_VOLUME);
    Video video1 = new Video("video/1", Video.DEFAULT_VOLUME);
    Video video2 = new Video("video/2", Video.DEFAULT_VOLUME);
    Video video3 = new Video("video/3", Video.DEFAULT_VOLUME);
    currentProject.getVMComposition().getMediaTrack().insertItemAt(0, video0);
    currentProject.getVMComposition().getMediaTrack().insertItemAt(1, video1);
    currentProject.getVMComposition().getMediaTrack().insertItemAt(2, video2);
    currentProject.getVMComposition().getMediaTrack().insertItemAt(3, video3);
    OnReorderMediaListener onReorderMediaListener = getOnReorderMediaListener();

    injectedUseCase.moveMediaItem(3, 1, onReorderMediaListener);

    assertThat(currentProject.getMediaTrack().getItems().indexOf(video0), is(0));
    assertThat(video0.getPosition(), is(0));
    assertThat(currentProject.getMediaTrack().getItems().indexOf(video3), is(1));
    assertThat(video3.getPosition(), is(1));
    assertThat(currentProject.getMediaTrack().getItems().indexOf(video1), is(2));
    assertThat(video1.getPosition(), is(2));
    assertThat(currentProject.getMediaTrack().getItems().indexOf(video2), is(3));
    assertThat(video2.getPosition(), is(3));

    injectedUseCase.moveMediaItem(1, 0, onReorderMediaListener);

    assertThat(currentProject.getMediaTrack().getItems().indexOf(video3), is(0));
    assertThat(video3.getPosition(), is(0));
    assertThat(currentProject.getMediaTrack().getItems().indexOf(video0), is(1));
    assertThat(video0.getPosition(), is(1));
    assertThat(currentProject.getMediaTrack().getItems().indexOf(video1), is(2));
    assertThat(video1.getPosition(), is(2));
    assertThat(currentProject.getMediaTrack().getItems().indexOf(video2), is(3));
    assertThat(video2.getPosition(), is(3));
  }

  @Test
  public void testReorder4ItemsTwice() throws IllegalItemOnTrack {
    Video video0 = new Video("video/0", Video.DEFAULT_VOLUME);
    Video video1 = new Video("video/1", Video.DEFAULT_VOLUME);
    Video video2 = new Video("video/2", Video.DEFAULT_VOLUME);
    Video video3 = new Video("video/3", Video.DEFAULT_VOLUME);
    currentProject.getVMComposition().getMediaTrack().insertItemAt(0, video0);
    currentProject.getVMComposition().getMediaTrack().insertItemAt(1, video1);
    currentProject.getVMComposition().getMediaTrack().insertItemAt(2, video2);
    currentProject.getVMComposition().getMediaTrack().insertItemAt(3, video3);
    OnReorderMediaListener onReorderMediaListener = getOnReorderMediaListener();

    injectedUseCase.moveMediaItem(2, 1, onReorderMediaListener);

    assertThat(currentProject.getMediaTrack().getItems().indexOf(video0), is(0));
    assertThat(video0.getPosition(), is(0));
    assertThat(currentProject.getMediaTrack().getItems().indexOf(video2), is(1));
    assertThat(video2.getPosition(), is(1));
    assertThat(currentProject.getMediaTrack().getItems().indexOf(video1), is(2));
    assertThat(video1.getPosition(), is(2));
    assertThat(currentProject.getMediaTrack().getItems().indexOf(video3), is(3));
    assertThat(video3.getPosition(), is(3));

    injectedUseCase.moveMediaItem(2, 1, onReorderMediaListener);

    assertThat(currentProject.getMediaTrack().getItems().indexOf(video0), is(0));
    assertThat(video0.getPosition(), is(0));
    assertThat(currentProject.getMediaTrack().getItems().indexOf(video1), is(1));
    assertThat(video1.getPosition(), is(1));
    assertThat(currentProject.getMediaTrack().getItems().indexOf(video2), is(2));
    assertThat(video2.getPosition(), is(2));
    assertThat(currentProject.getMediaTrack().getItems().indexOf(video3), is(3));
    assertThat(video3.getPosition(), is(3));
  }

  @NonNull
  private OnReorderMediaListener getOnReorderMediaListener() {
    return new OnReorderMediaListener() {
      @Override
      public void onSuccessMediaReordered() {

      }

      @Override
      public void onErrorReorderingMedia() {

      }
    };
  }
}