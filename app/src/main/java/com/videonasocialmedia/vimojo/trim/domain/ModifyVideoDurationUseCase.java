package com.videonasocialmedia.vimojo.trim.domain;


import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.MediaTranscoderListener;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelper;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.videonamediaframework.utils.TextToDrawable;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.io.IOException;

import javax.inject.Inject;

/**
 * Created by jca on 27/5/15.
 */
public class ModifyVideoDurationUseCase {

  private TextToDrawable drawableGenerator = new TextToDrawable(VimojoApplication.getAppContext());
  private MediaTranscoder mediaTranscoder = MediaTranscoder.getInstance();
  protected TranscoderHelper transcoderHelper = new TranscoderHelper(drawableGenerator, mediaTranscoder);
  protected VideoRepository videoRepository;

  /**
   * Default constructor with video repository argument.
   *
   * @param videoRepository the video repository.
   */
  @Inject public ModifyVideoDurationUseCase(VideoRepository videoRepository) {
    this.videoRepository = videoRepository;
  }

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
   /* try {
      videoToEdit.setStartTime(startTimeMs);
      videoToEdit.setStopTime(finishTimeMs);
      videoToEdit.setTempPathFinished(false);
      // TODO:(alvaro.martinez) 22/11/16 use project tmp path
      videoToEdit.setTempPath(Constants.PATH_APP_TEMP_INTERMEDIATE_FILES);
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
    } */
  }
}
