package com.videonasocialmedia.vimojo.text.domain;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.common.base.Function;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelper;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.export.domain.RelaunchTranscoderTempBackgroundUseCase;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptRepository;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.videonamediaframework.utils.TextToDrawable;
import com.videonasocialmedia.vimojo.utils.Constants;


import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

/**
 * Created by alvaro on 1/09/16.
 */
public class ModifyVideoTextAndPositionUseCase {
  private static final String LOG_TAG = ModifyVideoTextAndPositionUseCase.class.getSimpleName();
  private final VideoRepository videoRepository;
  private final RelaunchTranscoderTempBackgroundUseCase relaunchTranscoderTempBackgroundUseCase;
  private final VideoToAdaptRepository videoToAdaptRepository;
  // TODO:(alvaro.martinez) 23/11/16 Use Dagger for this injection
  private final TextToDrawable drawableGenerator =
      new TextToDrawable(VimojoApplication.getAppContext());
  private final MediaTranscoder mediaTranscoder = MediaTranscoder.getInstance();
  protected TranscoderHelper transcoderHelper =
      new TranscoderHelper(drawableGenerator, mediaTranscoder);

  /**
   * Default constructor with video repository.
   *  @param videoRepository the video repository.
   * @param videoToAdaptRepository the video to adapt repository
   */
  @Inject public ModifyVideoTextAndPositionUseCase(
          VideoRepository videoRepository,
          RelaunchTranscoderTempBackgroundUseCase relaunchTranscoderTempBackgroundUseCase,
          VideoToAdaptRepository videoToAdaptRepository) {
    this.videoRepository = videoRepository;
    this.relaunchTranscoderTempBackgroundUseCase = relaunchTranscoderTempBackgroundUseCase;
    this.videoToAdaptRepository = videoToAdaptRepository;
  }

    public void addTextToVideo(final Video videoToEdit, String text, String textPosition,
                               Project project) {
      final Project currentProject = getCurrentProject();
      setVideoTextParams(videoToEdit, text, textPosition, currentProject);
      videoRepository.update(videoToEdit);

      if (videoIsBeingAdapted(videoToEdit)) {
        ListenableFuture<Video> videoAdaptTask = videoToEdit.getTranscodingTask();
        videoToEdit.setTranscodingTask(Futures.transform(videoAdaptTask,
                applyText(currentProject, videoToEdit, text, textPosition)));
      } else {
        runTextTranscodingTask(videoToEdit, project);
      }
    }

  private Function<Video, Video> applyText(final Project currentProject, final Video videoToEdit,
                                           final String text, final String textPosition) {
    return new Function<Video, Video>() {
      @Nullable
      @Override
      public Video apply(Video input) {
        setVideoTextParams(videoToEdit, text, textPosition, currentProject);
        videoRepository.update(videoToEdit);
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
      }
    };
  }

  private boolean videoIsBeingAdapted(Video videoToEdit) {
    return videoToAdaptRepository.getByMediaPath(videoToEdit.getMediaPath()) != null;
  }

  private ListenableFuture<Video> runTextTranscodingTask(Video videoToEdit, Project project) {
    videoToEdit.setTempPath(project.getProjectPathIntermediateFiles());
    videoToEdit.setTranscodingTempFileFinished(false);
    videoRepository.update(videoToEdit);
    ListenableFuture<Video> transcoderTextTask = transcoderHelper.updateIntermediateFile(
            project.getVMComposition().getDrawableFadeTransitionVideo(),
            project.getVMComposition().isVideoFadeTransitionActivated(),
            project.getVMComposition().isAudioFadeTransitionActivated(),
            videoToEdit, project.getVMComposition().getVideoFormat(),
            project.getProjectPathIntermediateFileAudioFade());
    Futures.addCallback(transcoderTextTask, new TextTaskCallback(project, videoToEdit));
    return transcoderTextTask;
  }

  private void setVideoTextParams(Video videoToEdit, String text, String textPosition,
                                  Project currentProject) {
    videoToEdit.setClipText(text);
    videoToEdit.setClipTextPosition(textPosition);
    videoToEdit.setTempPath(currentProject.getProjectPathIntermediateFiles());
  }

  private void handleTranscodingError(Video video, String message, Project currentProject) {
    Log.d(LOG_TAG, "onErrorTranscoding " + video.getTempPath() + " - " + message);
    if (video.getNumTriesToExportVideo() < Constants.MAX_NUM_TRIES_TO_EXPORT_VIDEO) {
      video.increaseNumTriesToExportVideo();
      VideonaFormat videoFormat = currentProject.getVMComposition().getVideoFormat();
      Drawable drawableFadeTransitionVideo = currentProject.getVMComposition()
              .getDrawableFadeTransitionVideo();

      relaunchTranscoderTempBackgroundUseCase.relaunchExport(
              video,
              currentProject);
    } else {
      videoRepository.setErrorTranscodingVideo(video,
              Constants.ERROR_TRANSCODING_TEMP_FILE_TYPE.TEXT.name());
    }
  }

  private void handleTranscodingSuccess(Video video) {
    Log.d(LOG_TAG, "onSuccessTranscoding " + video.getTempPath());
    videoRepository.setSuccessTranscodingVideo(video);
  }

  private Project getCurrentProject() {
    return Project.getInstance(null, null, null, null);
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

