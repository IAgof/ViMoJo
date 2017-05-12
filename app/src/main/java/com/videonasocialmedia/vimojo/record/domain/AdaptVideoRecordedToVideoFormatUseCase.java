package com.videonasocialmedia.vimojo.record.domain;

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
                         final String destVideoPath, TranscoderHelperListener listener) throws IOException {
    transcoderHelper.adaptVideoToDefaultFormat(videoToAdapt, videoFormat, destVideoPath, listener);
  }
}
