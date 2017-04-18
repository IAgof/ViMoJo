package com.videonasocialmedia.vimojo.record.domain;

import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelper;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelperListener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.mockito.Mockito.verify;

/**
 * Created by alvaro on 5/04/17.
 */

public class AdaptVideoRecordedToVideoFormatUseCaseTest {

  @InjectMocks AdaptVideoRecordedToVideoFormatUseCase injectedAdaptVideoRecordedToVideoFormatUseCase;
  @Mock TranscoderHelper mockedTranscoderHelper;
  @Mock MediaTranscoder mockedMediaTranscoder;
  @Mock TranscoderHelperListener mockedTranscoderHelperListener;
  @Mock Video mockedVideo;
  String destVideoPath = "dcim/vimojo/masters";
  @Mock VideonaFormat mockedVideoFormat;

  @Before
  public void injectDoubles() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void adaptVideoRecordedCallsTranscodeAdaptVideoToFormat() throws IOException {

    injectedAdaptVideoRecordedToVideoFormatUseCase.transcoderHelper = mockedTranscoderHelper;

    injectedAdaptVideoRecordedToVideoFormatUseCase.adaptVideo(mockedVideo, mockedVideoFormat,
        destVideoPath, mockedTranscoderHelperListener);

    verify(mockedTranscoderHelper).adaptVideoToDefaultFormat(mockedVideo, mockedVideoFormat,
        destVideoPath, mockedTranscoderHelperListener);
  }
}
