package com.videonasocialmedia.vimojo.record.domain;

import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelper;
import com.videonasocialmedia.vimojo.export.domain.GetVideoFormatFromCurrentProjectUseCase;

import java.io.IOException;

/**
 * Created by alvaro on 3/02/17.
 */

public class AdaptVideoRecordedToTranscoderUseCase {

  private MediaTranscoder mediaTranscoder = MediaTranscoder.getInstance();
  protected TranscoderHelper transcoderHelper = new TranscoderHelper(mediaTranscoder);

  private VideonaFormat videoFormat;

  public AdaptVideoRecordedToTranscoderUseCase(){
    videoFormat = new VideonaFormat(192*1000,1);
  }


  public ListenableFuture adaptVideo(final String origVideoRecordedPath, final String destVideoPath) throws IOException {
    return transcoderHelper.adaptVideoToTranscoder(origVideoRecordedPath, videoFormat, destVideoPath);
  }
}
