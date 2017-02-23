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

import javax.inject.Inject;


/**
 * Created by alvaro on 28/09/16.
 */

public class RelaunchTranscoderTempBackgroundUseCase{
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
                             VideonaFormat videonaFormat,String intermediatesTempAudioFadeDirectory,
                             MediaTranscoderListener mediaTranscoderListener) {

    boolean isVideoFadeTransitionActivated =
        getPreferencesTransitionFromProjectUseCase.isVideoFadeTransitionActivated();
    boolean isAudioFadeTransitionActivated =
        getPreferencesTransitionFromProjectUseCase.isAudioFadeTransitionActivated();
      if (videoToEdit.hasText()) {
        transcoderHelper.generateOutputVideoWithOverlayImageAndTrimming(drawableFadeTransition,
            isVideoFadeTransitionActivated,isAudioFadeTransitionActivated, videoToEdit,
            videonaFormat,intermediatesTempAudioFadeDirectory, mediaTranscoderListener);
      } else {
        transcoderHelper.generateOutputVideoWithTrimming(drawableFadeTransition,
            isVideoFadeTransitionActivated, isAudioFadeTransitionActivated, videoToEdit,
            videonaFormat, intermediatesTempAudioFadeDirectory, mediaTranscoderListener);
      }
  }

}
