package com.videonasocialmedia.vimojo.trim.domain;


import android.support.annotation.NonNull;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.common.base.Function;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelper;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.asset.repository.MediaRepository;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptDataSource;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.videonamediaframework.utils.TextToDrawable;
import com.videonasocialmedia.vimojo.utils.Constants;


import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;

import javax.inject.Inject;

import static com.videonasocialmedia.vimojo.utils.Constants.MIN_TRIM_OFFSET;
import static com.videonasocialmedia.vimojo.utils.Constants.MS_CORRECTION_FACTOR;

/**
 * Created by jca on 27/5/15.
 */
public class ModifyVideoDurationUseCase {
  static final int AUTOTRIM_MS_RANGE = 10;
  private final String TAG = ModifyVideoDurationUseCase.class.getName();

  VideoToAdaptDataSource videoToAdaptRepository;
  private final TextToDrawable drawableGenerator =
          new TextToDrawable(VimojoApplication.getAppContext());
  private final MediaTranscoder mediaTranscoder = MediaTranscoder.getInstance();
  protected TranscoderHelper transcoderHelper =
          new TranscoderHelper(drawableGenerator, mediaTranscoder);
  private MediaRepository mediaRepository; // TODO(jliarte): 12/09/18 explore how to decouple use case from repository calls

  /**
   * Default constructor with video repository argument.
   *
   * @param videoToAdaptRepository the video to adapt repository.
   * @param mediaRepository        the media repository.
   */
  @Inject public ModifyVideoDurationUseCase(VideoToAdaptDataSource videoToAdaptRepository,
                                    MediaRepository mediaRepository) {
    this.mediaRepository = mediaRepository;
    this.videoToAdaptRepository = videoToAdaptRepository;
  }

  /**
   * Main method for video trimming use case.
   * @param videoToEdit video to trim
   * @param startTimeMs trim start time in milliseconds
   * @param finishTimeMs trim stop time in milliseconds
   * @param currentProject the project where the video belongs
   */
  public ListenableFuture<Video> trimVideo(final Video videoToEdit, final int startTimeMs,
                                           final int finishTimeMs, Project currentProject) {

    setVideoTrimParams(videoToEdit, startTimeMs, finishTimeMs, currentProject);
    mediaRepository.update(videoToEdit);
    return transcodeVideo(videoToEdit, startTimeMs, finishTimeMs, currentProject);
  }

  private ListenableFuture<Video> transcodeVideo(Video videoToEdit, int startTimeMs, int finishTimeMs, Project currentProject) {
    if (videoIsBeingAdapted(videoToEdit)) {
      ListenableFuture<Video> videoAdaptTask = videoToEdit.getTranscodingTask();
      videoToEdit.setTranscodingTask(Futures.transform(videoAdaptTask,
              applyTrim(currentProject, videoToEdit, startTimeMs, finishTimeMs)));
      return videoToEdit.getTranscodingTask();
    } else {
      // (jliarte): 18/09/17 in this case, we don't want to wait for task to finish
      return runTrimTranscodingTask(videoToEdit, currentProject);
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
    return input -> {
      // (jliarte): 18/09/17 when this function is applied, start and stop times could have been
      // modified, as done after adapting video, so we have to store them as parameters here
      setVideoTrimParams(videoToEdit, startTimeMs, finishTimeMs, currentProject);
      mediaRepository.update(videoToEdit);
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
    };
  }

  private boolean videoIsBeingAdapted(Video videoToEdit) {
    return videoToAdaptRepository.getByMediaPath(videoToEdit.getMediaPath()) != null;
  }

  ListenableFuture<Video> runTrimTranscodingTask(Video videoToEdit,
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
    // TODO(jliarte): 10/09/18 should we move this code to listener?
    video.resetNumTriesToExportVideo();
    video.setTranscodingTempFileFinished(true);
    video.setVideoError(null);
    mediaRepository.update(video);
  }

  void handleTaskError(Video video, String message, Project currentProject) {
    Log.d(TAG, "onErrorTranscoding " + video.getTempPath() + " - " + message);
    if (video.getNumTriesToExportVideo() < Constants.MAX_NUM_TRIES_TO_EXPORT_VIDEO) {
      video.increaseNumTriesToExportVideo();
      // TODO(jliarte): 23/10/17 modify here trim times
      randomizeTrimTimes(video);
      mediaRepository.update(video);
      runTrimTranscodingTask(video, currentProject);
    } else {
      //trimView.showError(message);
      video.setVideoError(Constants.ERROR_TRANSCODING_TEMP_FILE_TYPE.TRIM.name());
      video.setTranscodingTempFileFinished(true);
      mediaRepository.update(video);
    }
  }

  private void randomizeTrimTimes(Video video) {
    int minTrimTime = (int) (MIN_TRIM_OFFSET * MS_CORRECTION_FACTOR);
    int startTime = video.getStartTime();
    int stopTime = video.getStopTime();
    int randomStart = startTime;
    while (randomStart == startTime) {
      int minStart = Math.max(0, startTime - AUTOTRIM_MS_RANGE);
      int maxStart = Math.min(startTime + AUTOTRIM_MS_RANGE, stopTime - minTrimTime);
      randomStart = ThreadLocalRandom.current().nextInt(minStart, maxStart + 1);
    }
    video.setStartTime(randomStart);

    int randomStop = stopTime;
    while (randomStop == stopTime) {
      int minStop = Math.max(stopTime - AUTOTRIM_MS_RANGE, randomStart + minTrimTime);
      int maxStop = Math.min(stopTime + AUTOTRIM_MS_RANGE, video.getFileDuration());
      randomStop = ThreadLocalRandom.current().nextInt(minStop, maxStop + 1);
    }
    video.setStopTime(randomStop);
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

