package com.videonasocialmedia.vimojo.trim.domain;


import android.graphics.drawable.Drawable;
import android.util.Log;

import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelper;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelperListener;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptRepository;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.videonamediaframework.utils.TextToDrawable;
import com.videonasocialmedia.vimojo.utils.Constants;


import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

/**
 * Created by jca on 27/5/15.
 */
public class ModifyVideoDurationUseCase {
  private String TAG = ModifyVideoDurationUseCase.class.getName();

  private TextToDrawable drawableGenerator = new TextToDrawable(VimojoApplication.getAppContext());
  private MediaTranscoder mediaTranscoder = MediaTranscoder.getInstance();
  protected TranscoderHelper transcoderHelper =
          new TranscoderHelper(drawableGenerator, mediaTranscoder);
  protected VideoRepository videoRepository;

  VideoToAdaptRepository videoToAdaptRepository;
  private Drawable drawableFadeTransition;
  private VideonaFormat videoFormat;
  private String intermediatesTempAudioFadeDirectory;

  /**
   * Default constructor with video repository argument.
   *
   * @param videoRepository the video repository.
   * @param videoToAdaptRepository
   */
  @Inject public ModifyVideoDurationUseCase(VideoRepository videoRepository,
                                            VideoToAdaptRepository videoToAdaptRepository) {
    this.videoRepository = videoRepository;
    // TODO(jliarte): 27/07/17 inject this field
    this.videoToAdaptRepository = videoToAdaptRepository;
  }

  /**
   * Main method for video trimming use case.
   * @param videoToEdit video to trim
   * @param format
   * @param startTimeMs
   * @param finishTimeMs
   */
  public void trimVideo(final Drawable drawableFadeTransition, final Video videoToEdit,
                        final VideonaFormat format,
                        final int startTimeMs, final int finishTimeMs,
                        final String intermediatesTempAudioFadeDirectory) {
    final Project currentProject = getCurrentProject();
    videoToEdit.setStartTime(startTimeMs);
    videoToEdit.setStopTime(finishTimeMs);
    videoToEdit.setTempPath(currentProject.getProjectPathIntermediateFiles());
    videoToEdit.setTrimmedVideo(true);
    videoRepository.update(videoToEdit);

    this.drawableFadeTransition = drawableFadeTransition;
    this.videoFormat = format;
    this.intermediatesTempAudioFadeDirectory = intermediatesTempAudioFadeDirectory;

    runTrimTranscoding(videoToEdit, currentProject);
  }

  private void runTrimTranscoding(final Video videoToEdit, final Project currentProject) {
    Runnable useCaseRunnable = new Runnable() {
      @Override
      public void run() {
        boolean isVideoFadeTransitionActivated = currentProject.getVMComposition()
                .isVideoFadeTransitionActivated();
        boolean isAudioFadeTransitionActivated = currentProject.getVMComposition()
                .isAudioFadeTransitionActivated();
        if (videoToAdaptRepository.getByMediaPath(videoToEdit.getMediaPath()) != null) {
          try {
            videoToEdit.getTranscodingTask().get();
          } catch (InterruptedException | ExecutionException e) {
            // TODO(jliarte): 27/07/17 handle error
            Log.e(TAG, "Error waiting for adapt job to finish");
            e.printStackTrace();
          }
        }
        videoToEdit.setTranscodingTempFileFinished(false);
        TranscoderHelperListener transcoderHelperListener = new TranscoderHelperListener() {
          @Override
          public void onSuccessTranscoding(Video video) {
            Log.d(TAG, "onSuccessTranscoding after trim " + video.getTempPath());
            videoRepository.setSuccessTranscodingVideo(video);
          }

          @Override
          public void onErrorTranscoding(Video video, String message) {
            Log.d(TAG, "onErrorTranscoding " + video.getTempPath() + " - " + message);
            if (video.getNumTriesToExportVideo() < Constants.MAX_NUM_TRIES_TO_EXPORT_VIDEO) {
              video.increaseNumTriesToExportVideo();
              runTrimTranscoding(videoToEdit, currentProject);
//              setTrim(video.getStartTime(), video.getStopTime());
            } else {
              //trimView.showError(message);
              video.setVideoError(Constants.ERROR_TRANSCODING_TEMP_FILE_TYPE.TRIM.name());
              video.setTranscodingTempFileFinished(true);
              videoRepository.update(video);
            }
          }

        };
        updateGeneratedVideo(videoToEdit, transcoderHelperListener,
                isVideoFadeTransitionActivated, isAudioFadeTransitionActivated);
      }
    };
    new Thread(useCaseRunnable, "trimVideo Use Case").start();
  }

  private Project getCurrentProject() {
    return Project.getInstance(null, null, null, null);
  }

  private void updateGeneratedVideo(Video videoToEdit,
                                    TranscoderHelperListener transcoderHelperListener,
                                    boolean isVideoFadeTransitionActivated,
                                    boolean isAudioFadeTransitionActivated) {
    // TODO(jliarte): 17/03/17 move this logic to TranscoderHelper?
    if (videoToEdit.hasText()) {
      transcoderHelper.generateOutputVideoWithOverlayImageAndTrimmingAsync(drawableFadeTransition,
          isVideoFadeTransitionActivated, isAudioFadeTransitionActivated, videoToEdit, videoFormat,
          intermediatesTempAudioFadeDirectory, transcoderHelperListener);
    } else {
      transcoderHelper.generateOutputVideoWithTrimmingAsync(drawableFadeTransition,
          isVideoFadeTransitionActivated, isAudioFadeTransitionActivated, videoToEdit, videoFormat,
          intermediatesTempAudioFadeDirectory, transcoderHelperListener);
    }
  }
}
