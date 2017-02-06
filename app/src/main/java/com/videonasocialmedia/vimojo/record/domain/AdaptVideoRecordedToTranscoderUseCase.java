package com.videonasocialmedia.vimojo.record.domain;

import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.MediaTranscoderListener;
import com.videonasocialmedia.transcoder.video.format.VideoTranscoderFormat;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelper;
import com.videonasocialmedia.vimojo.export.domain.GetVideoFormatFromCurrentProjectUseCase;

import java.io.IOException;

/**
 * Created by alvaro on 3/02/17.
 */

public class AdaptVideoRecordedToTranscoderUseCase {

  private MediaTranscoder mediaTranscoder = MediaTranscoder.getInstance();
  protected TranscoderHelper transcoderHelper = new TranscoderHelper(mediaTranscoder);

  GetVideoFormatFromCurrentProjectUseCase getVideoFormatFromCurrentProjectUseCase;
  private VideoTranscoderFormat videoFormat;

  public AdaptVideoRecordedToTranscoderUseCase(){
    getVideoFormatFromCurrentProjectUseCase = new GetVideoFormatFromCurrentProjectUseCase();
    //videoFormat = getVideoFormatFromCurrentProjectUseCase.getVideoTranscodedFormatFromCurrentProject();
    videoFormat = new VideoTranscoderFormat(192*1000,1);
  }


  public void adaptVideo(String origVideoRecordedPath, MediaTranscoderListener listener,
                         String destVideoPath) throws IOException {

    transcoderHelper.adaptVideoToTranscoder(origVideoRecordedPath, videoFormat, listener, destVideoPath);

  }
}
