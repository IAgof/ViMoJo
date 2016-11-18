package com.videonasocialmedia.vimojo.trim.domain;


import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.MediaTranscoderListener;
import com.videonasocialmedia.transcoder.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelper;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.repository.video.VideoRealmRepository;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.vimojo.text.util.TextToDrawable;

import java.io.IOException;

/**
 * Created by jca on 27/5/15.
 */
public class ModifyVideoDurationUseCase {

  private TextToDrawable drawableGenerator = new TextToDrawable();
  private MediaTranscoder mediaTranscoder = MediaTranscoder.getInstance();
  protected TranscoderHelper transcoderHelper = new TranscoderHelper(drawableGenerator, mediaTranscoder);
  protected VideoRepository videoRepository = new VideoRealmRepository();

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
      videoRepository.update(videoToEdit);
    } catch (IOException exception) {
      exception.printStackTrace();
      listener.onTranscodeFailed(exception);
    }
  }
}
