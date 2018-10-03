package com.videonasocialmedia.vimojo.text.domain;

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
import com.videonasocialmedia.vimojo.export.domain.RelaunchTranscoderTempBackgroundUseCase;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptDataSource;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.videonamediaframework.utils.TextToDrawable;
import com.videonasocialmedia.vimojo.utils.Constants;


import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

/**
 * Created by alvaro on 1/09/16.
 */
public class ModifyVideoTextAndPositionUseCase {
  private static final String LOG_TAG = ModifyVideoTextAndPositionUseCase.class.getSimpleName();
  private final RelaunchTranscoderTempBackgroundUseCase relaunchTranscoderTempBackgroundUseCase;
  private final VideoToAdaptDataSource videoToAdaptRepository;
  // TODO:(alvaro.martinez) 23/11/16 Use Dagger for this injection
  private final TextToDrawable drawableGenerator =
      new TextToDrawable(VimojoApplication.getAppContext());
  private final MediaTranscoder mediaTranscoder = MediaTranscoder.getInstance();
  protected TranscoderHelper transcoderHelper =
      new TranscoderHelper(drawableGenerator, mediaTranscoder);
  private MediaRepository mediaRepository;

  /**
   * Default constructor with video repository.
   *
   * @param videoToAdaptRepository the video to adapt repository.
   * @param mediaRepository        the media repository.
   */
  @Inject
  public ModifyVideoTextAndPositionUseCase(
          RelaunchTranscoderTempBackgroundUseCase relaunchTranscoderTempBackgroundUseCase,
          VideoToAdaptDataSource videoToAdaptRepository, MediaRepository mediaRepository) {
    this.mediaRepository = mediaRepository;
    this.relaunchTranscoderTempBackgroundUseCase = relaunchTranscoderTempBackgroundUseCase;
    this.videoToAdaptRepository = videoToAdaptRepository;
  }

    public ListenableFuture<Video> addTextToVideo(Project currentProject, final Video videoToEdit,
                                                  String text,
                               String textPosition, boolean isShadowChecked) {
      setVideoTextParams(videoToEdit, text, textPosition, isShadowChecked, currentProject);
      mediaRepository.update(videoToEdit); // TODO(jliarte): 13/09/18 needed because of text settings change
      return transcodeVideo(currentProject, videoToEdit, text, textPosition);
    }

  private ListenableFuture<Video> transcodeVideo(Project currentProject, Video videoToEdit, String text, String textPosition) {
    if (videoIsBeingAdapted(videoToEdit)) {
      ListenableFuture<Video> videoAdaptTask = videoToEdit.getTranscodingTask();
      videoToEdit.setTranscodingTask(Futures.transform(videoAdaptTask,
              applyText(currentProject, videoToEdit, text, textPosition, isShadowChecked)));
      return videoToEdit.getTranscodingTask();
    } else {
      return runTextTranscodingTask(videoToEdit, currentProject);
    }
  }

  private Function<Video, Video> applyText(final Project currentProject, final Video videoToEdit,
                                           final String text, final String textPosition,
                                           boolean isShadowChecked) {
    return input -> {
      setVideoTextParams(videoToEdit, text, textPosition, isShadowChecked, currentProject);
      mediaRepository.update(videoToEdit); // TODO(jliarte): 13/09/18 needed because of text settings change
      ListenableFuture<Video> task = runTextTranscodingTask(videoToEdit, currentProject);
      // TODO(jliarte): 15/09/17 check this and error propagation
      try {
        return task.get();
      } catch (InterruptedException | ExecutionException ex) {
        // TODO(jliarte): 18/09/17 create an util class to log errors
        Log.e(LOG_TAG, "Caught exception while applying text after adapting video");
        Crashlytics.log("Caught exception while applying text after adapting video");
        Crashlytics.logException(ex);
        ex.printStackTrace();
        throw new RuntimeException(ex);
      }
    };
  }

  private boolean videoIsBeingAdapted(Video videoToEdit) {
    return videoToAdaptRepository.getByMediaPath(videoToEdit.getMediaPath()) != null;
  }

  private ListenableFuture<Video> runTextTranscodingTask(Video videoToEdit, Project project) {
    videoToEdit.setTempPath(project.getProjectPathIntermediateFiles());
    videoToEdit.setTranscodingTempFileFinished(false);
    mediaRepository.update(videoToEdit); // TODO(jliarte): 13/09/18 needed to set tempPath
    ListenableFuture<Video> transcoderTextTask = null;
    try {
      transcoderTextTask = transcoderHelper.updateIntermediateFile(
              project.getVMComposition().getDrawableFadeTransitionVideo(),
              project.getVMComposition().isVideoFadeTransitionActivated(),
              project.getVMComposition().isAudioFadeTransitionActivated(),
              videoToEdit, project.getVMComposition().getVideoFormat(),
              project.getProjectPathIntermediateFileAudioFade());
      Futures.addCallback(transcoderTextTask, new TextTaskCallback(project, videoToEdit));
    } catch (IOException ioError) {
      ioError.printStackTrace();
      handleTranscodingError(videoToEdit, ioError.getMessage(), project);
    }
    videoToEdit.setTranscodingTask(transcoderTextTask);
    return transcoderTextTask;
  }

  private void setVideoTextParams(Video videoToEdit, String text, String textPosition,
                                  boolean isShadowChecked, Project currentProject) {
    videoToEdit.setClipText(text);
    videoToEdit.setClipTextPosition(textPosition);
    videoToEdit.setTempPath(currentProject.getProjectPathIntermediateFiles());
    videoToEdit.setClipTextShadow(isShadowChecked);
  }

  private void handleTranscodingError(Video video, String message, Project currentProject) {
    Log.d(LOG_TAG, "onErrorTranscoding " + video.getTempPath() + " - " + message);
    if (video.getNumTriesToExportVideo() < Constants.MAX_NUM_TRIES_TO_EXPORT_VIDEO) {
      video.increaseNumTriesToExportVideo();
      relaunchTranscoderTempBackgroundUseCase.relaunchExport(video, currentProject);
    } else {
      // TODO(jliarte): 10/09/18 should we move this code to caller?
      video.setVideoError(Constants.ERROR_TRANSCODING_TEMP_FILE_TYPE.TEXT.name());
      video.setTranscodingTempFileFinished(true);
      mediaRepository.update(video); // TODO(jliarte): 13/09/18 needed to set transcoding error
    }
  }

  private void handleTranscodingSuccess(Video video) {
    Log.d(LOG_TAG, "onSuccessTranscoding " + video.getTempPath());
    // TODO(jliarte): 10/09/18 should we move this code to caller?
    video.resetNumTriesToExportVideo();
    video.setTranscodingTempFileFinished(true);
    video.setVideoError(null);
    mediaRepository.update(video); // TODO(jliarte): 13/09/18 needed to set transcoding success. check that is ran in background
  }


  private class TextTaskCallback implements FutureCallback<Video> {
    private final Project currentProject;
    private final Video video;

    private TextTaskCallback(Project currentProject, Video video) {
      this.currentProject = currentProject;
      this.video = video;
    }

    @Override
    public void onSuccess(Video result) {
      handleTranscodingSuccess(result);
    }

    @Override
    public void onFailure(@NonNull Throwable t) {
      handleTranscodingError(video, t.getMessage(), currentProject);
    }
  }
}

