package com.videonasocialmedia.vimojo.export.domain;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelper;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelperListener;
import com.videonasocialmedia.videonamediaframework.utils.TextToDrawable;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.vimojo.utils.Constants;

/**
 * Created by alvaro on 28/09/16.
 */

public class RelaunchTranscoderTempBackgroundUseCase implements TranscoderHelperListener {
  private static final String LOG_TAG = RelaunchTranscoderTempBackgroundUseCase.class
          .getSimpleName();
  protected TextToDrawable drawableGenerator =
          new TextToDrawable(VimojoApplication.getAppContext());
  protected MediaTranscoder mediaTranscoder = MediaTranscoder.getInstance();
  protected TranscoderHelper transcoderHelper = new TranscoderHelper(drawableGenerator,
          mediaTranscoder);
  protected VideoRepository videoRepository;

  private final Project currentProject;

  public RelaunchTranscoderTempBackgroundUseCase(VideoRepository videoRepository) {
    this.videoRepository = videoRepository;
    this.currentProject = getCurrentProject();
  }

  private Project getCurrentProject() {
    return Project.getInstance(null, null, null, null);
  }

  /**
   * Launch clip transcoding to generate intermediate video file for final export process.
   * @param videoToEdit video to apply operations with
   * @param videonaFormat output format for the clip transcoding
   * @param intermediatesTempAudioFadeDirectory
   */
  public void relaunchExport(
          Drawable drawableFadeTransition, Video videoToEdit, VideonaFormat videonaFormat,
          String intermediatesTempAudioFadeDirectory) {
    boolean isVideoFadeTransitionActivated = currentProject.getVMComposition()
            .isVideoFadeTransitionActivated();
    boolean isAudioFadeTransitionActivated = currentProject.getVMComposition()
            .isAudioFadeTransitionActivated();
    videoToEdit.setTranscodingTempFileFinished(false);
    videoRepository.update(videoToEdit);
    // TODO(jliarte): 28/07/17 wait for adapt video tasks to end
    ListenableFuture<Video> transcodingTask = transcoderHelper.updateIntermediateFile(
            drawableFadeTransition, isVideoFadeTransitionActivated, isAudioFadeTransitionActivated,
            videoToEdit, videonaFormat, intermediatesTempAudioFadeDirectory);
    Futures.addCallback(transcodingTask, new TranscodingTaskCallback(videoToEdit));
  }

  @Override
  public void onSuccessTranscoding(Video video) {
    handleTranscodingSuccess(video);
  }

  private void handleTranscodingSuccess(Video video) {
    Log.d(LOG_TAG, "onSuccessTranscoding " + video.getTempPath());
    videoRepository.setSuccessTranscodingVideo(video);
  }

  @Override
  public void onErrorTranscoding(Video video, String message) {
    handleTranscodingError(video, message);
  }

  private void handleTranscodingError(Video video, String message) {
    Log.d(LOG_TAG, "onErrorTranscoding " + video.getTempPath() + " - " + message);
    if (video.getNumTriesToExportVideo() < Constants.MAX_NUM_TRIES_TO_EXPORT_VIDEO) {
      video.increaseNumTriesToExportVideo();
      Drawable drawableVideoFadeTransition = currentProject.getVMComposition()
              .getDrawableFadeTransitionVideo();
      relaunchExport(drawableVideoFadeTransition, video,
              currentProject.getVMComposition().getVideoFormat(),
              currentProject.getProjectPathIntermediateFileAudioFade());
    } else {
      videoRepository.setErrorTranscodingVideo(video,
              Constants.ERROR_TRANSCODING_TEMP_FILE_TYPE.TRIM.name());
    }
  }

  private class TranscodingTaskCallback implements FutureCallback<Video> {
    private Video video;

    private TranscodingTaskCallback(Video video) {
      this.video = video;
    }

    @Override
    public void onSuccess(Video result) {
      handleTranscodingSuccess(result);
    }

    @Override
    public void onFailure(Throwable t) {
      handleTranscodingError(video, t.getMessage());
    }
  }
}
