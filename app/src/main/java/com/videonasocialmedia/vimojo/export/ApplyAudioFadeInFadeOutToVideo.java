package com.videonasocialmedia.vimojo.export;

import com.videonasocialmedia.vimojo.export.domain.ExportSwapAudioToVideoUseCase;
import com.videonasocialmedia.vimojo.export.domain.OnExportEndedSwapAudioListener;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.sound.domain.GetAudioFadeInFadeOutFromVideoUseCase;
import com.videonasocialmedia.vimojo.sound.domain.OnGetAudioFadeInFadeOutFromVideoListener;

import java.io.IOException;

/**
 * Created by alvaro on 23/10/16.
 */

public class ApplyAudioFadeInFadeOutToVideo implements OnExportEndedSwapAudioListener,
    OnGetAudioFadeInFadeOutFromVideoListener {

  private GetAudioFadeInFadeOutFromVideoUseCase getAudioFadeInFadeOutFromVideoUseCase;
  private ExportSwapAudioToVideoUseCase exportSwapAudioToVideoUseCase;
  private Video videoToEdit;
  private OnApplyAudioFadeInFadeOutToVideoListener listener;
  private String tempPreviousPath;

  public ApplyAudioFadeInFadeOutToVideo(OnApplyAudioFadeInFadeOutToVideoListener listener) {

    getAudioFadeInFadeOutFromVideoUseCase = new GetAudioFadeInFadeOutFromVideoUseCase(this);
    exportSwapAudioToVideoUseCase = new ExportSwapAudioToVideoUseCase(this);
    this.listener = listener;
  }

  public void applyAudioFadeToVideo(Video videoToEdit, int timeFadeInMs, int timeFadeOutMs)
      throws IOException {

    this.videoToEdit = videoToEdit;
    tempPreviousPath = videoToEdit.getTempPath();
    getAudioFadeInFadeOutFromVideoUseCase.getAudioFadeInFadeOutFromVideo(videoToEdit.getTempPath(),
        timeFadeInMs, timeFadeOutMs);
  }


  @Override
  public void onExportError(String error) {
    listener.OnGetAudioFadeInFadeOutError(error,videoToEdit);
  }

  @Override
  public void onExportSuccess() {
    listener.OnGetAudioFadeInFadeOutSuccess(videoToEdit);
  }

  @Override
  public void onGetAudioFadeInFadeOutFromVideoSuccess(String audioFile) {
    videoToEdit.setTempPath();
    exportSwapAudioToVideoUseCase.export(tempPreviousPath, audioFile,
        videoToEdit.getTempPath());
  }

  @Override
  public void onGetAudioFadeInFadeOutFromVideoError(String message) {
    listener.OnGetAudioFadeInFadeOutError(message, videoToEdit);

  }
}
