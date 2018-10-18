package com.videonasocialmedia.vimojo.domain.editor;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelper;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelperListener;
import com.videonasocialmedia.videonamediaframework.utils.TextToDrawable;
import com.videonasocialmedia.vimojo.asset.repository.MediaRepository;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.asset.repository.datasource.VideoDataSource;
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
  private Project currentProject;
  private WeakReference<AVTransitionsApplierListener> applierListener;
  private MediaRepository mediaRepository; // TODO(jliarte): 12/09/18 explore how to decouple use case from repository calls

  public ApplyAVTransitionsUseCase(Project project, MediaRepository mediaRepository) {
    this.currentProject = project;
    this.mediaRepository = mediaRepository;
  }


  public void applyAVTransitions(
          Drawable drawableFadeTransition, Video videoToEdit, VideonaFormat videoTranscoderFormat,
          String intermediatesTempAudioFadeDirectory,
          AVTransitionsApplierListener avTransitionsApplierListener) {
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
    mediaRepository.update(videoToEdit); // (jliarte): 12/09/18 this update is needed cause videoToEdit.setTranscodingTempFileFinished, is it really needed to persist this field?
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
      // TODO(jliarte): 10/09/18 should move this code to listener?
      video.resetNumTriesToExportVideo();
      video.setTranscodingTempFileFinished(true);
      video.setVideoError(null);
      mediaRepository.update(video); // TODO(jliarte): 12/09/18 needed mainly to set video error, could be moved to listener

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
        // TODO(jliarte): 10/09/18 should we move this code to caller?
        video.setVideoError(Constants.ERROR_TRANSCODING_TEMP_FILE_TYPE.AVTRANSITION.name());
        video.setTranscodingTempFileFinished(true);
        mediaRepository.update(video); // TODO(jliarte): 12/09/18 needed mainly to set video error, could be moved to listener
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
