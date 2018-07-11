package com.videonasocialmedia.vimojo.domain.editor;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelper;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelperListener;
import com.videonasocialmedia.videonamediaframework.utils.TextToDrawable;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.cut.domain.model.Project;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.lang.ref.WeakReference;

/**
 * Created by alvaro on 22/03/17.
 */

public class ApplyAVTransitionsUseCase {
  private static final String TAG = ApplyAVTransitionsUseCase.class.getSimpleName();
  protected TextToDrawable drawableGenerator = new TextToDrawable(VimojoApplication.getAppContext());
  protected MediaTranscoder mediaTranscoder = MediaTranscoder.getInstance();
  protected TranscoderHelper transcoderHelper = new TranscoderHelper(drawableGenerator,
      mediaTranscoder);
  protected VideoRepository videoRepository;
  private Project currentProject;
  private WeakReference<AVTransitionsApplierListener> applierListener;

  public ApplyAVTransitionsUseCase(Project project,
                                   VideoRepository videoRepository) {
    this.currentProject = project;
    this.videoRepository = videoRepository;
  }


  public void applyAVTransitions(Drawable drawableFadeTransition, Video videoToEdit,
                                 VideonaFormat videoTranscoderFormat,
                                 String intermediatesTempAudioFadeDirectory,
                                 AVTransitionsApplierListener
                                 avTransitionsApplierListener) {
    this.applierListener = new WeakReference<>(avTransitionsApplierListener);
    boolean isVideoFadeTransitionActivated = currentProject.getVMComposition()
            .isVideoFadeTransitionActivated();
    boolean isAudioFadeTransitionActivated = currentProject.getVMComposition()
            .isAudioFadeTransitionActivated();
    videoToEdit.setTranscodingTempFileFinished(false);
    updateGeneratedVideo(drawableFadeTransition, isVideoFadeTransitionActivated,
        isAudioFadeTransitionActivated, videoToEdit, videoTranscoderFormat,
        intermediatesTempAudioFadeDirectory,
            new AVTransitionsListener(drawableFadeTransition, isVideoFadeTransitionActivated,
                    isAudioFadeTransitionActivated, videoTranscoderFormat,
                    intermediatesTempAudioFadeDirectory));
    videoRepository.update(videoToEdit);
  }

  private void updateGeneratedVideo(Drawable drawableFadeTransition,
                                    boolean isVideoFadeTransitionActivated,
                                    boolean isAudioFadeTransitionActivated,
                                    Video videoToEdit,
                                    VideonaFormat videoTranscoderFormat,
                                    String intermediatesTempAudioFadeDirectory,
                                    TranscoderHelperListener transcoderHelperListener) {
    if (isVideoFadeTransitionActivated) {
      transcoderHelper.generateOutputVideoWithAVTransitionsAsync(drawableFadeTransition,
          isVideoFadeTransitionActivated, isAudioFadeTransitionActivated, videoToEdit,
          videoTranscoderFormat, intermediatesTempAudioFadeDirectory, transcoderHelperListener);
    } else {
      if (isAudioFadeTransitionActivated) {
        transcoderHelper.generateOutputVideoWithAudioTransitionAsync(videoToEdit,
            intermediatesTempAudioFadeDirectory, transcoderHelperListener);
      }
    }
  }

  private class AVTransitionsListener implements TranscoderHelperListener {
    private Drawable drawableFadeTransition;
    private boolean isVideoFadeTransitionActivated;
    private boolean isAudioFadeTransitionActivated;
    private VideonaFormat videoTranscoderFormat;
    private String intermediatesTempAudioFadeDirectory;

    private AVTransitionsListener(Drawable drawableFadeTransition,
                                  boolean isVideoFadeTransitionActivated,
                                  boolean isAudioFadeTransitionActivated,
                                  VideonaFormat videoTranscoderFormat,
                                  String intermediatesTempAudioFadeDirectory) {
      this.drawableFadeTransition = drawableFadeTransition;
      this.isVideoFadeTransitionActivated = isVideoFadeTransitionActivated;
      this.isAudioFadeTransitionActivated = isAudioFadeTransitionActivated;
      this.videoTranscoderFormat = videoTranscoderFormat;
      this.intermediatesTempAudioFadeDirectory = intermediatesTempAudioFadeDirectory;
    }

    @Override
    public void onSuccessTranscoding(Video video) {
      Log.d(TAG, "onSuccessTranscoding " + video.getTempPath());
      videoRepository.setSuccessTranscodingVideo(video);
      AVTransitionsApplierListener listener = applierListener.get();
      if (listener != null) {
        listener.onSuccessApplyAVTransitions(video);
      }
    }

    @Override
    public void onErrorTranscoding(Video video, String message) {
      Log.d(TAG, "onErrorTranscoding " + video.getTempPath() + " - " + message);
      if (video.getNumTriesToExportVideo() < Constants.MAX_NUM_TRIES_TO_EXPORT_VIDEO) {
        video.increaseNumTriesToExportVideo();
//        launchTranscoderAddAVTransitionUseCase.launchExportTempFile(
//                currentProject.getVMComposition().getDrawableFadeTransitionVideo(), video,
//                videoFormat, currentProject.getProjectPathIntermediateFileAudioFade(),
//                this);
        updateGeneratedVideo(drawableFadeTransition, isVideoFadeTransitionActivated,
                isAudioFadeTransitionActivated, video, videoTranscoderFormat,
                intermediatesTempAudioFadeDirectory, this);
      } else {
        videoRepository.setErrorTranscodingVideo(video,
                Constants.ERROR_TRANSCODING_TEMP_FILE_TYPE.AVTRANSITION.name());
        AVTransitionsApplierListener listener = applierListener.get();
        if (listener != null) {
          listener.onErrorApplyAVTransitions(video, message);
        }
      }
    }
  }

  public interface AVTransitionsApplierListener {
    void onSuccessApplyAVTransitions(Video video);
    void onErrorApplyAVTransitions(Video video, String message);
  }
}
