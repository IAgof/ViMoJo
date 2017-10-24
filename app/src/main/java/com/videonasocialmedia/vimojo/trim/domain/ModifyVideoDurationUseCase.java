package com.videonasocialmedia.vimojo.trim.domain;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.common.base.Function;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelper;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptRepository;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.videonamediaframework.utils.TextToDrawable;
import com.videonasocialmedia.vimojo.utils.Constants;


import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

/**
 * Created by jca on 27/5/15.
 */
public class ModifyVideoDurationUseCase {
  private final String TAG = ModifyVideoDurationUseCase.class.getName();

  VideoRepository videoRepository;
  VideoToAdaptRepository videoToAdaptRepository;
  private final TextToDrawable drawableGenerator =
          new TextToDrawable(VimojoApplication.getAppContext());
  private final MediaTranscoder mediaTranscoder = MediaTranscoder.getInstance();
  protected TranscoderHelper transcoderHelper =
          new TranscoderHelper(drawableGenerator, mediaTranscoder);

  /**
   * Default constructor with video repository argument.
   *
   * @param videoRepository the video repository.
   * @param videoToAdaptRepository the video to adapt repository.
   */
  @Inject public ModifyVideoDurationUseCase(VideoRepository videoRepository,
                                            VideoToAdaptRepository videoToAdaptRepository) {
    this.videoRepository = videoRepository;
    this.videoToAdaptRepository = videoToAdaptRepository;
  }

  /**
   * Main method for video trimming use case.
   * @param videoToEdit video to trim
   * @param startTimeMs trim start time in milliseconds
   * @param finishTimeMs trim stop time in milliseconds
   * @param currentProject the project where the video belongs
   */
  public void trimVideo(final Video videoToEdit,
                        final int startTimeMs, final int finishTimeMs, Project currentProject) {
    setVideoTrimParams(videoToEdit, startTimeMs, finishTimeMs, currentProject);
    videoRepository.update(videoToEdit);

    if (videoIsBeingAdapted(videoToEdit)) {
      ListenableFuture<Video> videoAdaptTask = videoToEdit.getTranscodingTask();
      videoToEdit.setTranscodingTask(Futures.transform(videoAdaptTask,
              applyTrim(currentProject, videoToEdit, startTimeMs, finishTimeMs)));
    } else {
      // TODO(jliarte): 18/09/17 in this case, we don't want to wait for task to finish
      runTrimTranscodingTask(videoToEdit, currentProject);
    }
  }

  private void setVideoTrimParams(Video videoToEdit, int startTimeMs, int finishTimeMs,
                                  Project currentProject) {
    videoToEdit.setTempPath(currentProject.getProjectPathIntermediateFiles());
    videoToEdit.setStartTime(startTimeMs);
    videoToEdit.setStopTime(finishTimeMs);
    videoToEdit.setTrimmedVideo(true);
  }

  private Function<Video, Video> applyTrim(final Project currentProject, final Video videoToEdit,
                                           final int startTimeMs, final int finishTimeMs) {
    return new Function<Video, Video>() {
      @Nullable
      @Override
      public Video apply(Video input) {
        // (jliarte): 18/09/17 when this function is applied, start and stop times could have been
        // modified, as done after adapting video, so we have to store them as parameters here
        setVideoTrimParams(videoToEdit, startTimeMs, finishTimeMs, currentProject);
        videoRepository.update(videoToEdit);
        ListenableFuture<Video> task = runTrimTranscodingTask(videoToEdit, currentProject);
        // TODO(jliarte): 15/09/17 check this and error propagation
        try {
          return task.get();
        } catch (InterruptedException | ExecutionException ex) {
          // TODO(jliarte): 18/09/17 create an util class to log errors
          Log.e(TAG, "Caught exception while applying trim after adapting video");
          Crashlytics.log("Caught exception while applying trim after adapting video");
          Crashlytics.logException(ex);
          ex.printStackTrace();
          throw new RuntimeException(ex);
        }
      }
    };
  }

  private boolean videoIsBeingAdapted(Video videoToEdit) {
    return videoToAdaptRepository.getByMediaPath(videoToEdit.getMediaPath()) != null;
  }

  private ListenableFuture<Video> runTrimTranscodingTask(Video videoToEdit,
                                                         Project currentProject) {
    // (jliarte): 18/09/17 after adapting a video, tmpPath is reset calling video.resetTempPath(),
    // so we have to set it again before we call runTrimTranscoding in the futures chaining
    // TODO(jliarte): 18/09/17 I guess this method should work independent of the video object
    // state, so we move here the setting of tempPath right after transcoderHelper call
    videoToEdit.setTempPath(currentProject.getProjectPathIntermediateFiles());
    videoToEdit.setTranscodingTempFileFinished(false);
    ListenableFuture<Video> trimTask = null;
    try {
      trimTask = transcoderHelper.updateIntermediateFile(
              currentProject.getVMComposition().getDrawableFadeTransitionVideo(),
              currentProject.getVMComposition().isVideoFadeTransitionActivated(),
              currentProject.getVMComposition().isAudioFadeTransitionActivated(), videoToEdit,
              currentProject.getVMComposition().getVideoFormat(),
              currentProject.getProjectPathIntermediateFileAudioFade());
      Futures.addCallback(trimTask, new TrimTaskCallback(videoToEdit, currentProject));
    } catch (IOException ioError) {
      ioError.printStackTrace();
      handleTaskError(videoToEdit, ioError.getMessage(), currentProject);
    }
    return trimTask;
  }

  private void handleTaskSuccess(Video video) {
    Log.d(TAG, "onSuccessTranscoding after trim " + video.getTempPath());
    videoRepository.setSuccessTranscodingVideo(video);
  }

  private void handleTaskError(Video video, String message, Project currentProject) {
    Log.d(TAG, "onErrorTranscoding " + video.getTempPath() + " - " + message);
    if (video.getNumTriesToExportVideo() < Constants.MAX_NUM_TRIES_TO_EXPORT_VIDEO) {
      video.increaseNumTriesToExportVideo();
      runTrimTranscodingTask(video, currentProject);
    } else {
      //trimView.showError(message);
      video.setVideoError(Constants.ERROR_TRANSCODING_TEMP_FILE_TYPE.TRIM.name());
      video.setTranscodingTempFileFinished(true);
      videoRepository.update(video);
    }
  }

  private class TrimTaskCallback implements FutureCallback<Video> {
    private final Video video;
    private final Project currentProject;

    private TrimTaskCallback(Video video, Project currentProject) {
      this.video = video;
      this.currentProject = currentProject;
    }

    @Override
    public void onSuccess(Video result) {
      handleTaskSuccess(result);
    }

    @Override
    public void onFailure(@NonNull Throwable t) {
      handleTaskError(video, t.getMessage(), currentProject);
    }
  }
}

