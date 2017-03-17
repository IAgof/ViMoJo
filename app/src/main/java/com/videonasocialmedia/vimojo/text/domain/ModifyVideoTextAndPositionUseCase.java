package com.videonasocialmedia.vimojo.text.domain;

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
 * Created by alvaro on 1/09/16.
 */
public class ModifyVideoTextAndPositionUseCase {

    // TODO:(alvaro.martinez) 23/11/16 Use Dagger for this injection
    protected TextToDrawable drawableGenerator =
        new TextToDrawable(VimojoApplication.getAppContext());
    private MediaTranscoder mediaTranscoder = MediaTranscoder.getInstance();
    protected TranscoderHelper transcoderHelper =
        new TranscoderHelper(drawableGenerator, mediaTranscoder);
    protected VideoRepository videoRepository;
    private GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase;

  /**
   * Default constructor with video repository.
   *
   * @param videoRepository the video repository.
   */
  @Inject public ModifyVideoTextAndPositionUseCase(VideoRepository videoRepository) {
    this.videoRepository = videoRepository;
    getPreferencesTransitionFromProjectUseCase =
            new GetPreferencesTransitionFromProjectUseCase();
    }

    public void addTextToVideo(Drawable drawableFadeTransition, Video videoToEdit,
                               VideonaFormat format, String text, String textPosition,
                               String intermediatesTempAudioFadeDirectory,
                               TranscoderHelperListener listener) {
      boolean isVideoFadeTransitionActivated =
              getPreferencesTransitionFromProjectUseCase.isVideoFadeTransitionActivated();
      boolean isAudioFadeTransitionActivated =
              getPreferencesTransitionFromProjectUseCase.isAudioFadeTransitionActivated();
      videoToEdit.setClipText(text);
      videoToEdit.setClipTextPosition(textPosition);
      videoToEdit.setTempPathFinished(false);
      Project project = Project.getInstance(null, null, null);
      videoToEdit.setTempPath(project.getProjectPathIntermediateFiles());
      updateGeneratedVideo(drawableFadeTransition, videoToEdit, format,
              intermediatesTempAudioFadeDirectory, listener, isVideoFadeTransitionActivated,
              isAudioFadeTransitionActivated);

      videoRepository.update(videoToEdit);
    }

  private void updateGeneratedVideo(Drawable drawableFadeTransition, Video videoToEdit,
                                    VideonaFormat format,
                                    String intermediatesTempAudioFadeDirectory,
                                    TranscoderHelperListener listener,
                                    boolean isVideoFadeTransitionActivated,
                                    boolean isAudioFadeTransitionActivated) {
    // TODO(jliarte): 19/10/16 move this logic to TranscoderHelper?
    if (videoToEdit.isTrimmedVideo()) {
      transcoderHelper.generateOutputVideoWithOverlayImageAndTrimming(
              drawableFadeTransition, isVideoFadeTransitionActivated,
              isAudioFadeTransitionActivated, videoToEdit, format,
              intermediatesTempAudioFadeDirectory, listener);
    } else {
      transcoderHelper.generateOutputVideoWithOverlayImage(drawableFadeTransition,
              isVideoFadeTransitionActivated, isAudioFadeTransitionActivated, videoToEdit,
              format, intermediatesTempAudioFadeDirectory, listener);
    }
  }
}

