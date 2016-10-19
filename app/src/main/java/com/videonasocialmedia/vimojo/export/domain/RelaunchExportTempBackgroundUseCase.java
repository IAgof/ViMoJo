package com.videonasocialmedia.vimojo.export.domain;

import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.MediaTranscoderListener;
import com.videonasocialmedia.transcoder.format.VideonaFormat;
import com.videonasocialmedia.vimojo.export.utils.TranscoderHelper;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.text.util.TextToDrawable;

import java.io.IOException;


/**
 * Created by alvaro on 28/09/16.
 */

public class RelaunchExportTempBackgroundUseCase {
  protected TextToDrawable drawableGenerator = new TextToDrawable();
  protected MediaTranscoder mediaTranscoder = MediaTranscoder.getInstance();
  protected TranscoderHelper transcoderHelper = new TranscoderHelper(drawableGenerator, mediaTranscoder);

  /**
   * Launch clip transcoding to generate intermediate video file for final export process.
   * @param videoToEdit video to apply operations with
   * @param listener listener for this use case
   * @param videonaFormat output format for the clip transcoding
   */
  public void relaunchExport(Video videoToEdit, MediaTranscoderListener listener,
                             VideonaFormat videonaFormat) {
    videoToEdit.increaseNumTriesToExportVideo();
    try {
      transcoderHelper.generateOutputVideoWithOverlayImageAndTrimming(videoToEdit, videonaFormat, listener);
    } catch (IOException exception) {
      exception.printStackTrace();
      listener.onTranscodeFailed(exception);
    }
  }
}
