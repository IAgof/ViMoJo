package com.videonasocialmedia.vimojo.record.domain;

import android.graphics.drawable.Drawable;

import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelper;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelperListener;

import java.io.IOException;

/**
 * Created by alvaro on 3/02/17.
 */

public class AdaptVideoRecordedToVideoFormatUseCase {

  private MediaTranscoder mediaTranscoder = MediaTranscoder.getInstance();
  protected TranscoderHelper transcoderHelper = new TranscoderHelper(mediaTranscoder);

  public AdaptVideoRecordedToVideoFormatUseCase(){
  }

  public void adaptVideo(final Video videoToAdapt, final VideonaFormat videoFormat,
                         final String destVideoPath, int rotation, Drawable fadeTransition,
                         boolean isFadeActivated, TranscoderHelperListener listener)
      throws IOException {
    transcoderHelper.adaptVideoWithRotationToDefaultFormat(videoToAdapt, videoFormat, destVideoPath,
        rotation, fadeTransition, isFadeActivated, listener);
  }
}
