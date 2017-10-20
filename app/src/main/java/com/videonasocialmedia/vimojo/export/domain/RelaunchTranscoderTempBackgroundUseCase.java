package com.videonasocialmedia.vimojo.export.domain;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelper;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.utils.TextToDrawable;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.io.IOException;

/**
 * Created by alvaro on 28/09/16.
 */

public class RelaunchTranscoderTempBackgroundUseCase {
  private static final String LOG_TAG = RelaunchTranscoderTempBackgroundUseCase.class
          .getSimpleName();
  private final VideoRepository videoRepository;
  private final TextToDrawable drawableGenerator =
          new TextToDrawable(VimojoApplication.getAppContext());
  private final MediaTranscoder mediaTranscoder = MediaTranscoder.getInstance();
  protected TranscoderHelper transcoderHelper = new TranscoderHelper(drawableGenerator,
          mediaTranscoder);

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
   * @param currentProject the project where the video belongs
   */
  public void relaunchExport(Video videoToEdit, Project currentProject) {
    // TODO(jliarte): 19/09/17 should we also wait for adapting jobs here?
    try {
      runTranscodingTask(videoToEdit, currentProject);
    } catch (IOException ioError) {
      ioError.printStackTrace();
      handleTranscodingError(videoToEdit, ioError.getMessage());
    }
  }

  private void runTranscodingTask(Video videoToEdit, Project project) throws IOException {
    videoToEdit.setTempPath(project.getProjectPathIntermediateFiles());
    videoToEdit.setTranscodingTempFileFinished(false);
    videoRepository.update(videoToEdit);
    // TODO(jliarte): 28/07/17 wait for adapt video tasks to end
    ListenableFuture<Video> transcodingTask = transcoderHelper.updateIntermediateFile(
            project.getVMComposition().getDrawableFadeTransitionVideo(),
            project.getVMComposition().isVideoFadeTransitionActivated(),
            project.getVMComposition().isAudioFadeTransitionActivated(),
            videoToEdit, project.getVMComposition().getVideoFormat(),
            project.getProjectPathIntermediateFileAudioFade());
    Futures.addCallback(transcodingTask, new TranscodingTaskCallback(videoToEdit));
  }

  private void handleTranscodingSuccess(Video video) {
    Log.d(LOG_TAG, "onSuccessTranscoding " + video.getTempPath());
    videoRepository.setSuccessTranscodingVideo(video);
  }

  private void handleTranscodingError(Video video, String message) {
    Log.d(LOG_TAG, "onErrorTranscoding " + video.getTempPath() + " - " + message);
    if (video.getNumTriesToExportVideo() < Constants.MAX_NUM_TRIES_TO_EXPORT_VIDEO) {
      video.increaseNumTriesToExportVideo();
      relaunchExport(video, currentProject);
    } else {
      videoRepository.setErrorTranscodingVideo(video,
              Constants.ERROR_TRANSCODING_TEMP_FILE_TYPE.TRIM.name());
    }
  }

  private class TranscodingTaskCallback implements FutureCallback<Video> {
    private final Video video;

    private TranscodingTaskCallback(Video video) {
      this.video = video;
    }

    @Override
    public void onSuccess(Video result) {
      handleTranscodingSuccess(result);
    }

    @Override
    public void onFailure(@NonNull Throwable t) {
      handleTranscodingError(video, t.getMessage());
    }
  }
}
