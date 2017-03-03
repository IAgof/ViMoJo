package com.videonasocialmedia.vimojo.trim.domain;


import android.graphics.drawable.Drawable;

import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelper;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelperListener;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.videonamediaframework.utils.TextToDrawable;
import com.videonasocialmedia.vimojo.settings.domain.GetPreferencesTransitionFromProjectUseCase;


import javax.inject.Inject;

/**
 * Created by jca on 27/5/15.
 */
public class ModifyVideoDurationUseCase{

  private String TAG = "ModifyVideoDurationUseCase";

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
   * @param transcoderHelperListener
   */
  public void trimVideo(final Drawable drawableFadeTransition, final Video videoToEdit,
                        final VideonaFormat format,
                        final int startTimeMs, final int finishTimeMs,
                        final String intermediatesTempAudioFadeDirectory,
                        final TranscoderHelperListener
                        transcoderHelperListener){

      boolean isVideoFadeTransitionActivated =
          getPreferencesTransitionFromProjectUseCase.isVideoFadeTransitionActivated();
      boolean isAudioFadeTransitionActivated =
          getPreferencesTransitionFromProjectUseCase.isAudioFadeTransitionActivated();

      videoToEdit.setStartTime(startTimeMs);
      videoToEdit.setStopTime(finishTimeMs);
      videoToEdit.setTempPathFinished(false);
      Project project = Project.getInstance(null,null,null);
      videoToEdit.setTempPath(project.getProjectPathIntermediateFiles());
      videoToEdit.setTrimmedVideo(true);

      if (videoToEdit.hasText()) {
        transcoderHelper.generateOutputVideoWithOverlayImageAndTrimming(drawableFadeTransition,
            isVideoFadeTransitionActivated, isAudioFadeTransitionActivated, videoToEdit, format,
            intermediatesTempAudioFadeDirectory, transcoderHelperListener);

      } else {
        transcoderHelper.generateOutputVideoWithTrimming(drawableFadeTransition,
            isVideoFadeTransitionActivated, isAudioFadeTransitionActivated, videoToEdit, format,
            intermediatesTempAudioFadeDirectory, transcoderHelperListener);
      }
      videoRepository.update(videoToEdit);
  }
}
