package com.videonasocialmedia.vimojo.record.domain;

import android.graphics.drawable.Drawable;

import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelper;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelperListener;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;

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
  int rotation = 0;
  Drawable fadeTransition;
  boolean isFadeActivated;
  @Mock VideonaFormat mockedVideoFormat;

  @Before
  public void injectDoubles() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void adaptVideoRecordedCallsTranscoderAdaptVideoToFormat() throws IOException {
    String tempDirectory = Project.getInstance(null, null, null)
            .getProjectPathIntermediateAudioMixedFiles();
    injectedAdaptVideoRecordedToVideoFormatUseCase.transcoderHelper = mockedTranscoderHelper;

    injectedAdaptVideoRecordedToVideoFormatUseCase.adaptVideo(mockedVideo, mockedVideoFormat,
        destVideoPath, rotation, fadeTransition, isFadeActivated, mockedTranscoderHelperListener);

    verify(mockedTranscoderHelper).adaptVideoWithRotationToDefaultFormatAsync(mockedVideo,
        mockedVideoFormat, destVideoPath, rotation, fadeTransition, isFadeActivated,
        mockedTranscoderHelperListener, tempDirectory);
  }
}
