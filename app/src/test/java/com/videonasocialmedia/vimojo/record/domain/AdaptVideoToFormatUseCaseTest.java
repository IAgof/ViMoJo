package com.videonasocialmedia.vimojo.record.domain;

import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelper;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelperListener;
import com.videonasocialmedia.vimojo.importer.model.entities.VideoToAdapt;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptRepository;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Created by alvaro on 5/04/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class AdaptVideoToFormatUseCaseTest {
  @Mock VideoToAdaptRepository videoToAdaptRepository;
  @Mock TranscoderHelper mockedTranscoderHelper;
  @Mock private AdaptVideoToFormatUseCase.AdaptListener mockedAdaptListener;

  @InjectMocks AdaptVideoToFormatUseCase injectedAdaptVideoToFormatUseCase;

  @Before
  public void injectTestDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void adaptVideoRecordedCallsTranscoderAdaptVideoToFormat() throws IOException {
    String tempDirectory = Project.getInstance(null, null, null, null)
            .getProjectPathIntermediateAudioMixedFiles();
    injectedAdaptVideoToFormatUseCase.transcoderHelper = mockedTranscoderHelper;
    Video video = new Video("media/path", Video.DEFAULT_VOLUME);
    String destVideoPath = "dcim/vimojo/masters";
    VideoToAdapt videoToAdapt = new VideoToAdapt(video, destVideoPath, 1, 0, 0);
    VideonaFormat videoFormat = new VideonaFormat();
    int rotation = 0;

    injectedAdaptVideoToFormatUseCase.adaptVideo(videoToAdapt, videoFormat,
            mockedAdaptListener);

    verify(mockedTranscoderHelper).adaptVideoWithRotationToDefaultFormatAsync(eq(video),
        eq(videoFormat), eq(destVideoPath), eq(rotation),
            any(AdaptVideoToFormatUseCase.AdaptVideoListener.class), eq(tempDirectory));
  }
}
