package com.videonasocialmedia.vimojo.trim.domain;


import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.MediaTranscoderListener;
import com.videonasocialmedia.transcoder.format.VideonaFormat;
import com.videonasocialmedia.vimojo.export.utils.TranscoderHelper;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.text.util.TextToDrawable;

import java.io.IOException;

/**
 * Created by jca on 27/5/15.
 */
public class ModifyVideoDurationUseCase {

  private TextToDrawable drawableGenerator = new TextToDrawable();
  private MediaTranscoder mediaTranscoder = MediaTranscoder.getInstance();
  protected TranscoderHelper transcoderHelper = new TranscoderHelper(drawableGenerator, mediaTranscoder);

  /**
   * Main method for video trimming use case.
   * @param videoToEdit video to trim
   * @param format
   * @param startTimeMs
   * @param finishTimeMs
   * @param listener
   */
  public void trimVideo(Video videoToEdit, VideonaFormat format, final int startTimeMs,
                        final int finishTimeMs, MediaTranscoderListener listener) {
    try {
      videoToEdit.setStartTime(startTimeMs);
      videoToEdit.setStopTime(finishTimeMs);
      videoToEdit.setTempPathFinished(false);
      videoToEdit.setTempPath();
      videoToEdit.setTrimmedVideo(true);

      if (videoToEdit.hasText()) {
        transcoderHelper.generateOutputVideoWithOverlayImageAndTrimming(videoToEdit, format,
                listener);
      } else {
        transcoderHelper.generateOutputVideoWithTrimming(videoToEdit, format, listener);
      }
    } catch (IOException exception) {
      exception.printStackTrace();
      listener.onTranscodeFailed(exception);
    }
  }
}
