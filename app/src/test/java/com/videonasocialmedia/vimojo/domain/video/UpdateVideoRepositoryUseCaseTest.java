package com.videonasocialmedia.vimojo.domain.video;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;

/**
 * Created by alvaro on 26/04/17.
 */

public class UpdateVideoRepositoryUseCaseTest {

  @Mock
  VideoRepository mockedVideoRepository;

  UpdateVideoRepositoryUseCase updateVideoRepositoryUseCase;

  @Before
  public void injectDoubles(){
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void succesTranscodingVideoResetNumTriesVideoToExport(){

    Video video= new Video("media/path", Video.DEFAULT_VOLUME);
    video.increaseNumTriesToExportVideo();
    assertThat(video.getNumTriesToExportVideo(), is(1));

    updateVideoRepositoryUseCase = new UpdateVideoRepositoryUseCase(mockedVideoRepository);
    updateVideoRepositoryUseCase.succesTranscodingVideo(video);

    assertThat(video.getNumTriesToExportVideo(), is(0));
  }

  @Test
  public void succesTranscodingVideoSetTrueIsTranscodingTempfileFinished(){
    Video video= new Video("media/path", Video.DEFAULT_VOLUME);
    video.setTranscodingTempFileFinished(false);
    assertThat(video.isTranscodingTempFileFinished(), is(false));

    updateVideoRepositoryUseCase = new UpdateVideoRepositoryUseCase(mockedVideoRepository);
    updateVideoRepositoryUseCase.succesTranscodingVideo(video);

    assertThat(video.isTranscodingTempFileFinished(), is(true));
  }

  @Test
  public void errorTranscodingVideoSetTrueIsTranscodingTempfileFinished(){
    Video video= new Video("media/path", Video.DEFAULT_VOLUME);
    video.setTranscodingTempFileFinished(false);
    assertThat(video.isTranscodingTempFileFinished(), is(false));

    updateVideoRepositoryUseCase = new UpdateVideoRepositoryUseCase(mockedVideoRepository);
    updateVideoRepositoryUseCase.errorTranscodingVideo(video, anyString());

    assertThat(video.isTranscodingTempFileFinished(), is(true));
  }
}
