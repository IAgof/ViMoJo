package com.videonasocialmedia.vimojo.export;

import com.videonasocialmedia.vimojo.export.domain.ExportSwapAudioToVideoUseCase;
import com.videonasocialmedia.vimojo.export.domain.OnExportEndedListener;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.repository.video.VideoRealmRepository;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.vimojo.sound.domain.GetAudioFadeInFadeOutFromVideoUseCase;
import com.videonasocialmedia.vimojo.sound.domain.OnGetAudioFadeInFadeOutFromVideoListener;

import java.io.IOException;

/**
 * Created by alvaro on 23/10/16.
 */

public class ApplyAudioFadeInFadeOutToVideo implements OnExportEndedListener,
    OnGetAudioFadeInFadeOutFromVideoListener {

  GetAudioFadeInFadeOutFromVideoUseCase getAudioFadeInFadeOutFromVideoUseCase;
  ExportSwapAudioToVideoUseCase exportSwapAudioToVideoUseCase;
  Video videoToEdit;
  OnApplyAudioFadeInFadeOutToVideoListener listener;
  private final VideoRepository videoRepository = new VideoRealmRepository();

  public ApplyAudioFadeInFadeOutToVideo() {

    getAudioFadeInFadeOutFromVideoUseCase = new GetAudioFadeInFadeOutFromVideoUseCase(this);
    exportSwapAudioToVideoUseCase = new ExportSwapAudioToVideoUseCase(this);

  }

  public void applyAudioFadeToVideo(Video videoToEdit, int timeFadeInMs, int timeFadeOutMs)
      throws IOException {

    getAudioFadeInFadeOutFromVideoUseCase.getAudioFadeInFadeOutFromVideo(videoToEdit,
        timeFadeInMs, timeFadeOutMs);
  }


  @Override
  public void onExportError(String error) {

  }

  @Override
  public void onExportSuccess(Video video) {
    // update video export finished

    videoRepository.update(video);
  }

  @Override
  public void onGetAudioFadeInFadeOutFromVideoSuccess(String audioFile) {
    videoToEdit.setTempPath();
    exportSwapAudioToVideoUseCase.export(videoToEdit.getMediaPath(), audioFile,
        videoToEdit.getTempPath());
  }

  @Override
  public void onGetAudioFadeInFadeOutFromVideoError(String message) {

  }
}
