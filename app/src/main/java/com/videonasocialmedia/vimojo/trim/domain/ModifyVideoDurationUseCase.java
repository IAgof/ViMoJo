package com.videonasocialmedia.vimojo.trim.domain;


import android.graphics.drawable.Drawable;

import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.MediaTranscoderListener;
import com.videonasocialmedia.transcoder.video.format.VideoTranscoderFormat;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelper;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.videonamediaframework.utils.TextToDrawable;
import com.videonasocialmedia.vimojo.settings.domain.GetPreferencesTransitionFromProjectUseCase;

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

  private GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase;

  /**
   * Default constructor with video repository argument.
   *
   * @param videoRepository the video repository.
   */
  @Inject public ModifyVideoDurationUseCase(VideoRepository videoRepository) {
    this.videoRepository = videoRepository;
    getPreferencesTransitionFromProjectUseCase = new GetPreferencesTransitionFromProjectUseCase();
  }

  /**
   * Main method for video trimming use case.
   * @param videoToEdit video to trim
   * @param format
   * @param startTimeMs
   * @param finishTimeMs
   * @param listener
   */
  public void trimVideo(Drawable drawableFadeTransition, Video videoToEdit, VideoTranscoderFormat format,
                        final int startTimeMs, final int finishTimeMs,
                        MediaTranscoderListener listener) {
    try {

      boolean isVideoFadeTransitionActivated =
          getPreferencesTransitionFromProjectUseCase.isVideoFadeTransitionActivated();

      videoToEdit.setStartTime(startTimeMs);
      videoToEdit.setStopTime(finishTimeMs);
      videoToEdit.setTempPathFinished(false);
      Project project = Project.getInstance(null,null,null);
      videoToEdit.setTempPath(project.getProjectPathIntermediateFiles());
      videoToEdit.setTrimmedVideo(true);

      if (videoToEdit.hasText()) {
        transcoderHelper.generateOutputVideoWithOverlayImageAndTrimming(drawableFadeTransition,
            isVideoFadeTransitionActivated, videoToEdit, format,
                listener);
      } else {
        transcoderHelper.generateOutputVideoWithTrimming(drawableFadeTransition,
            isVideoFadeTransitionActivated, videoToEdit, format, listener);
      }
      videoRepository.update(videoToEdit);
    } catch (IOException exception) {
      exception.printStackTrace();
      listener.onTranscodeFailed(exception);
    }
  }
}
