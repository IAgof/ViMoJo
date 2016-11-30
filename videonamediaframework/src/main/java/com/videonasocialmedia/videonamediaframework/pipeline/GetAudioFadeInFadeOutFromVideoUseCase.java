package com.videonasocialmedia.videonamediaframework.pipeline;


import com.videonasocialmedia.videonamediaframework.model.Constants;

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

  String tempFileAudio;
  String tempDirectoryFilesAudio;

  public GetAudioFadeInFadeOutFromVideoUseCase(OnGetAudioFadeInFadeOutFromVideoListener listener,
                                               String tempDirectory) {
    this.tempDirectoryFilesAudio = tempDirectory + File.separator
        + Constants.DIRECTORY_NAME_TEMP_AUDIO_FILES;
    this.tempFileAudio = tempDirectoryFilesAudio + File.separator + "AudioFadeInOut_"
        + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".m4a";
    this.listener = listener;
  }

  public void getAudioFadeInFadeOutFromVideo(String videoToEditPath, int timeFadeInMs, int timeFadeOutMs)
      throws IOException {

    transcoderHelper.generateFileWithAudioFadeInFadeOut(videoToEditPath, timeFadeInMs,
        timeFadeOutMs, tempDirectoryFilesAudio, tempFileAudio, this);
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

  /**
   * Created by alvaro on 23/10/16.
   */
  public static interface OnGetAudioFadeInFadeOutFromVideoListener {
      void onGetAudioFadeInFadeOutFromVideoSuccess(String audioFile);
      void onGetAudioFadeInFadeOutFromVideoError(String message);
  }
}
