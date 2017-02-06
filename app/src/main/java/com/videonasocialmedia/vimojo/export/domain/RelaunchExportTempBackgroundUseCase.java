package com.videonasocialmedia.vimojo.export.domain;

import android.graphics.drawable.Drawable;

import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.MediaTranscoderListener;
import com.videonasocialmedia.transcoder.video.format.VideoTranscoderFormat;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelper;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.utils.TextToDrawable;
import com.videonasocialmedia.vimojo.settings.domain.GetPreferencesTransitionFromProjectUseCase;

import java.io.IOException;

import javax.inject.Inject;


/**
 * Created by alvaro on 28/09/16.
 */

public class RelaunchExportTempBackgroundUseCase {
  //protected TextToDrawable drawableGenerator = new TextToDrawable();
  @Inject
  protected TextToDrawable drawableGenerator;
  protected MediaTranscoder mediaTranscoder = MediaTranscoder.getInstance();
  protected TranscoderHelper transcoderHelper = new TranscoderHelper(drawableGenerator,
          mediaTranscoder);

  private GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase;

  public RelaunchExportTempBackgroundUseCase(){
      getPreferencesTransitionFromProjectUseCase = new GetPreferencesTransitionFromProjectUseCase();
  }

  /**
   * Launch clip transcoding to generate intermediate video file for final export process.
   * @param videoToEdit video to apply operations with
   * @param listener listener for this use case
   * @param videoTranscoderFormat output format for the clip transcoding
   */
  public void relaunchExport(Drawable drawableFadeTransition, Video videoToEdit,
                             MediaTranscoderListener listener, VideoTranscoderFormat videoTranscoderFormat) {
    videoToEdit.increaseNumTriesToExportVideo();
    boolean isVideoFadeTransitionActivated = getPreferencesTransitionFromProjectUseCase.isVideoFadeTransitionActivated();
    try {
      if (videoToEdit.hasText()) {
        transcoderHelper.generateOutputVideoWithOverlayImageAndTrimming(drawableFadeTransition,
            isVideoFadeTransitionActivated, videoToEdit, videoTranscoderFormat, listener);
      } else {
        transcoderHelper.generateOutputVideoWithTrimming(drawableFadeTransition,
            isVideoFadeTransitionActivated, videoToEdit, videoTranscoderFormat, listener);
      }
    } catch (IOException exception) {
      exception.printStackTrace();
      listener.onTranscodeFailed(exception);
    }
  }
}
