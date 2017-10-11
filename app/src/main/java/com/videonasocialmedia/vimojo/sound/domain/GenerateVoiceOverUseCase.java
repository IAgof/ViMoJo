package com.videonasocialmedia.vimojo.sound.domain;

import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelper;
import com.videonasocialmedia.videonamediaframework.utils.TextToDrawable;
import com.videonasocialmedia.vimojo.main.VimojoApplication;

import javax.inject.Inject;

/**
 * Created by alvaro on 10/10/17.
 */

public class GenerateVoiceOverUseCase {

  private final TextToDrawable drawableGenerator =
      new TextToDrawable(VimojoApplication.getAppContext());
  private final MediaTranscoder mediaTranscoder = MediaTranscoder.getInstance();
  protected TranscoderHelper transcoderHelper =
      new TranscoderHelper(drawableGenerator, mediaTranscoder);

  @Inject
  public GenerateVoiceOverUseCase() {
  }

  public void generateVoiceOver(String originFile, String destFile){
    transcoderHelper.generateOutputAudioVoiceOver(originFile, destFile);
  }
}
