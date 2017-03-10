package com.videonasocialmedia.vimojo.domain.editor;

import android.support.annotation.NonNull;

import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.track.MediaTrack;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnReorderMediaListener;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;

/**
 * Created by jliarte on 23/10/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class ReorderMediaItemUseCaseTest {
  @Mock ProjectRepository mockedProjectRepository;
  @InjectMocks ReorderMediaItemUseCase injectedUseCase;
  @Mock MediaTrack mockedMediaTrack;

  @Before
  public void injectDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testMoveMediaItemCallsUpdateProject() {
    Project currentProject = Project.getInstance(null, null, null);
    currentProject.setMediaTrack(mockedMediaTrack);
    Video video = new Video("media/path", 1f);
    OnReorderMediaListener listener = getOnReorderMediaListener();

    injectedUseCase.moveMediaItem(video, 1, listener);

    verify(mockedProjectRepository).update(currentProject);
  }

  @NonNull
  private OnReorderMediaListener getOnReorderMediaListener() {
    return new OnReorderMediaListener() {
      @Override
      public void onMediaReordered(Media media, int newPosition) {

      }

      @Override
      public void onErrorReorderingMedia() {

      }
    };
  }
}