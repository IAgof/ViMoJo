package com.videonasocialmedia.vimojo.export.domain;

import android.graphics.drawable.Drawable;

import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.MediaTranscoderListener;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelper;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.utils.TextToDrawable;
import com.videonasocialmedia.vimojo.repository.video.VideoRealmRepository;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.vimojo.settings.domain.GetPreferencesTransitionFromProjectUseCase;

import java.io.IOException;

import javax.inject.Inject;


/**
 * Created by alvaro on 28/09/16.
 */

public class RelaunchTranscoderTempBackgroundUseCase implements MediaTranscoderListener {
  //protected TextToDrawable drawableGenerator = new TextToDrawable();
  @Inject
  protected TextToDrawable drawableGenerator;
  protected MediaTranscoder mediaTranscoder = MediaTranscoder.getInstance();
  protected TranscoderHelper transcoderHelper = new TranscoderHelper(drawableGenerator,
          mediaTranscoder);
  protected VideoRepository videoRepository;

  private GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase;

  public RelaunchTranscoderTempBackgroundUseCase(){
      getPreferencesTransitionFromProjectUseCase = new GetPreferencesTransitionFromProjectUseCase();
      videoRepository = new VideoRealmRepository();
  }

  /**
   * Launch clip transcoding to generate intermediate video file for final export process.
   * @param videoToEdit video to apply operations with
   * @param videonaFormat output format for the clip transcoding
   * @param intermediatesTempAudioFadeDirectory
   */
  public void relaunchExport(Drawable drawableFadeTransition, Video videoToEdit,
                             VideonaFormat videonaFormat, String intermediatesTempAudioFadeDirectory) {
    videoToEdit.increaseNumTriesToExportVideo();
    boolean isVideoFadeTransitionActivated =
        getPreferencesTransitionFromProjectUseCase.isVideoFadeTransitionActivated();
    boolean isAudioFadeTransitionActivated =
        getPreferencesTransitionFromProjectUseCase.isAudioFadeTransitionActivated();
    try {
      if (videoToEdit.hasText()) {
        transcoderHelper.generateOutputVideoWithOverlayImageAndTrimming(drawableFadeTransition,
            isVideoFadeTransitionActivated,isAudioFadeTransitionActivated, videoToEdit,
            videonaFormat,intermediatesTempAudioFadeDirectory, this);
      } else {
        transcoderHelper.generateOutputVideoWithTrimming(drawableFadeTransition,
            isVideoFadeTransitionActivated, isAudioFadeTransitionActivated, videoToEdit,
            videonaFormat, intermediatesTempAudioFadeDirectory, this);
      }
    } catch (IOException exception) {
      exception.printStackTrace();
      onErrorTranscoding(videoToEdit, exception.getMessage());
    }
  }

  @Override
  public void onSuccessTranscoding(Video video) {
    videoRepository.update(video);
  }

  @Override
  public void onErrorTranscoding(Video video, String message) {
// TODO:(alvaro.martinez) 15/02/17 Manage this error

  }
}