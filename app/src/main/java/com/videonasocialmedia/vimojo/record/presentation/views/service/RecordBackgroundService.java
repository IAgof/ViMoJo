package com.videonasocialmedia.vimojo.record.presentation.views.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.videonasocialmedia.transcoder.MediaTranscoderListener;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.record.domain.AdaptVideoRecordedToTranscoderUseCase;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.utils.IntentConstants;
import com.videonasocialmedia.vimojo.utils.Utils;

import java.io.IOException;

/**
 * Created by alvaro on 2/02/17.
 */

public class RecordBackgroundService extends Service {

  AddVideoToProjectUseCase addVideoToProjectUseCase;
  AdaptVideoRecordedToTranscoderUseCase adaptVideoRecordedToTranscoderUseCase;

  public RecordBackgroundService(){
    addVideoToProjectUseCase = new AddVideoToProjectUseCase(new ProjectRealmRepository());
    adaptVideoRecordedToTranscoderUseCase = new AdaptVideoRecordedToTranscoderUseCase();
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public int onStartCommand(final Intent intent, int flags, int startId) {
    new Thread(new Runnable() {
      @Override
      public void run() {

        final String origVideoRecorded = intent.getStringExtra(IntentConstants.VIDEO_RECORDED_ORIG);
        final String destVideoRecorded = intent.getStringExtra(IntentConstants.VIDEO_RECORDED_DEST);


        MediaTranscoderListener useCaseListener = new MediaTranscoderListener() {
          @Override
          public void onTranscodeProgress(double v) {
          }

          @Override
          public void onTranscodeCompleted() {
            addVideoToProjectUseCase.addVideoToTrack(destVideoRecorded);
            Utils.removeVideo(origVideoRecorded);
          }

          @Override
          public void onTranscodeCanceled() {

          }

          @Override
          public void onTranscodeFailed(Exception e) {

          }

        };

        try {
          adaptVideo(origVideoRecorded, useCaseListener, destVideoRecorded);
        } catch (IOException e) {
          e.printStackTrace();
        }

      }
    }).start();

    return START_NOT_STICKY;
  }

  private void adaptVideo(String videoRecorded, MediaTranscoderListener useCaseListener,
                         String destVideoRecorded) throws IOException {

    adaptVideoRecordedToTranscoderUseCase.adaptVideo(videoRecorded, useCaseListener,
        destVideoRecorded);

  }
}
