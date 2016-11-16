package com.videonasocialmedia.vimojo.sound.domain;

import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.audio_mixer.listener.OnAudioEffectListener;
import com.videonasocialmedia.vimojo.export.utils.TranscoderHelper;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alvaro on 23/10/16.
 */

public class GetAudioFadeInFadeOutFromVideoUseCase implements OnAudioEffectListener {

  private MediaTranscoder mediaTranscoder = MediaTranscoder.getInstance();
  protected TranscoderHelper transcoderHelper = new TranscoderHelper(mediaTranscoder);
  private OnGetAudioFadeInFadeOutFromVideoListener listener;

  String tempFileAudio = Constants.PATH_APP_TEMP_AUDIO + File.separator + "AudioFadeInOut_" +
      new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".m4a";

  public GetAudioFadeInFadeOutFromVideoUseCase(OnGetAudioFadeInFadeOutFromVideoListener listener) {
    this.listener = listener;
  }

  public void getAudioFadeInFadeOutFromVideo(String videoToEditPath, int timeFadeInMs, int timeFadeOutMs)
      throws IOException {

    transcoderHelper.generateFileWithAudioFadeInFadeOut(videoToEditPath, timeFadeInMs,
        timeFadeOutMs, Constants.PATH_APP_TEMP_AUDIO, tempFileAudio, this);
  }

  @Override
  public void onAudioEffectSuccess(String outputFile) {
    listener.onGetAudioFadeInFadeOutFromVideoSuccess(outputFile);
  }

  @Override
  public void onAudioEffectProgress(String progress) {

  }

  @Override
  public void onAudioEffectError(String error) {
    listener.onGetAudioFadeInFadeOutFromVideoError(error);
  }

  @Override
  public void onAudioEffectCanceled() {
    listener.onGetAudioFadeInFadeOutFromVideoError("canceled");

  }

}
