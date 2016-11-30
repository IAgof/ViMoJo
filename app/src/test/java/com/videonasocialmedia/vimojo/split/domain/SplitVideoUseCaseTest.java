package com.videonasocialmedia.vimojo.split.domain;

import android.support.annotation.NonNull;

import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Created by jliarte on 23/10/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class SplitVideoUseCaseTest {
  @Mock ProjectRepository mockedProjectRepository;
  @Mock AddVideoToProjectUseCase mockedAddVideoToProjectUseCase;
  @InjectMocks SplitVideoUseCase injectedUseCase;

  @Before
  public void injectDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void splitVideoCallsProjectRepositoryUpdateAsItCallsAddVideoToProjectAtPosition() {
    Video video = new Video("media/path");
    OnSplitVideoListener listener = getOnSplitVideoListener();

    injectedUseCase.splitVideo(video, 0, 10, listener);

    ArgumentCaptor<Video> videoCaptor = ArgumentCaptor.forClass(Video.class);
    verify(mockedAddVideoToProjectUseCase).addVideoToProjectAtPosition(videoCaptor.capture(),
            eq(1));
    assertThat(videoCaptor.getValue().getMediaPath(), is(video.getMediaPath()));
  }

  @NonNull
  private OnSplitVideoListener getOnSplitVideoListener() {
    return new OnSplitVideoListener() {
      @Override
      public void trimVideo(Video video, int startTimeMs, int finishTimeMs) {

      }
    };
  }
}