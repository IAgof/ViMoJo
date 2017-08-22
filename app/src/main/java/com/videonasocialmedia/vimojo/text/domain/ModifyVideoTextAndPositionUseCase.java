package com.videonasocialmedia.vimojo.text.domain;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelper;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelperListener;
import com.videonasocialmedia.vimojo.export.domain.RelaunchTranscoderTempBackgroundUseCase;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.videonamediaframework.utils.TextToDrawable;
import com.videonasocialmedia.vimojo.utils.Constants;


import javax.inject.Inject;

/**
 * Created by alvaro on 1/09/16.
 */
public class ModifyVideoTextAndPositionUseCase {
  private static final String LOG_TAG = ModifyVideoTextAndPositionUseCase.class.getSimpleName();
  private final RelaunchTranscoderTempBackgroundUseCase relaunchTranscoderTempBackgroundUseCase;
  // TODO:(alvaro.martinez) 23/11/16 Use Dagger for this injection
    protected TextToDrawable drawableGenerator =
        new TextToDrawable(VimojoApplication.getAppContext());
    private MediaTranscoder mediaTranscoder = MediaTranscoder.getInstance();
    protected TranscoderHelper transcoderHelper =
        new TranscoderHelper(drawableGenerator, mediaTranscoder);
    protected VideoRepository videoRepository;

  /**
   * Default constructor with video repository.
   *
   * @param videoRepository the video repository.
   */
  @Inject public ModifyVideoTextAndPositionUseCase(
          VideoRepository videoRepository,
          RelaunchTranscoderTempBackgroundUseCase relaunchTranscoderTempBackgroundUseCase) {
    this.videoRepository = videoRepository;
    this.relaunchTranscoderTempBackgroundUseCase = relaunchTranscoderTempBackgroundUseCase;
    }

    public void addTextToVideo(Drawable drawableFadeTransition, final Video videoToEdit,
                               VideonaFormat format, String text, String textPosition,
                               String intermediatesTempAudioFadeDirectory) {
      final Project currentProject = getCurrentProject();
      boolean isVideoFadeTransitionActivated = currentProject.getVMComposition()
              .isVideoFadeTransitionActivated();
      boolean isAudioFadeTransitionActivated = currentProject.getVMComposition()
              .isAudioFadeTransitionActivated();
      videoToEdit.setClipText(text);
      videoToEdit.setClipTextPosition(textPosition);
      videoToEdit.setTempPath(currentProject.getProjectPathIntermediateFiles());
      TranscoderHelperListener listener = new TranscoderHelperListener() {
        @Override
        public void onSuccessTranscoding(Video video) {
          Log.d(LOG_TAG, "onSuccessTranscoding " + video.getTempPath());
          videoRepository.setSuccessTranscodingVideo(video);
        }

        @Override
        public void onErrorTranscoding(Video video, String message) {
          Log.d(LOG_TAG, "onErrorTranscoding " + video.getTempPath() + " - " + message);
          if (video.getNumTriesToExportVideo() < Constants.MAX_NUM_TRIES_TO_EXPORT_VIDEO) {
            videoToEdit.increaseNumTriesToExportVideo();
            VideonaFormat videoFormat = currentProject.getVMComposition().getVideoFormat();
            Drawable drawableFadeTransitionVideo = currentProject.getVMComposition()
                    .getDrawableFadeTransitionVideo();

            relaunchTranscoderTempBackgroundUseCase.relaunchExport(drawableFadeTransitionVideo,
                    video, videoFormat, currentProject.getProjectPathIntermediateFileAudioFade()
            );
          } else {
            videoRepository.setErrorTranscodingVideo(video,
                    Constants.ERROR_TRANSCODING_TEMP_FILE_TYPE.TEXT.name());
          }
        }
      };
      updateGeneratedVideo(drawableFadeTransition, videoToEdit, format,
              intermediatesTempAudioFadeDirectory, listener, isVideoFadeTransitionActivated,
              isAudioFadeTransitionActivated);

      videoRepository.update(videoToEdit);
    }

  private Project getCurrentProject() {
    return Project.getInstance(null, null, null, null);
  }

  private void updateGeneratedVideo(
          Drawable drawableFadeTransition, Video videoToEdit, VideonaFormat format,
          String intermediatesTempAudioFadeDirectory, TranscoderHelperListener listener,
          boolean isVideoFadeTransitionActivated, boolean isAudioFadeTransitionActivated) {
    // TODO(jliarte): 19/10/16 move this logic to TranscoderHelper?
    if (videoToEdit.isTrimmedVideo()) {
      transcoderHelper.generateOutputVideoWithOverlayImageAndTrimmingAsync(
              drawableFadeTransition, isVideoFadeTransitionActivated,
              isAudioFadeTransitionActivated, videoToEdit, format,
              intermediatesTempAudioFadeDirectory, listener);
    } else {
      transcoderHelper.generateOutputVideoWithOverlayImageAsync(drawableFadeTransition,
              isVideoFadeTransitionActivated, isAudioFadeTransitionActivated, videoToEdit,
              format, intermediatesTempAudioFadeDirectory, listener);
    }
  }
}

