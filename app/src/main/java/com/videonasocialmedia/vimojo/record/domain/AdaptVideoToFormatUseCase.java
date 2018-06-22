package com.videonasocialmedia.vimojo.record.domain;

import android.util.Log;

import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelper;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelperListener;
import com.videonasocialmedia.vimojo.importer.model.entities.VideoToAdapt;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptRepository;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.vimojo.utils.FileUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Created by alvaro on 3/02/17.
 */

public class AdaptVideoToFormatUseCase {
  private static final String TAG = AdaptVideoListener.class.getSimpleName();
  private static final int MAX_NUM_TRIES_ADAPTING_VIDEO = 3;
  private MediaTranscoder mediaTranscoder = MediaTranscoder.getInstance();
  protected TranscoderHelper transcoderHelper = new TranscoderHelper(mediaTranscoder);
  private VideoToAdaptRepository videoToAdaptRepository;
  private VideoRepository videoRepository;
  private WeakReference<AdaptListener> adaptListener;

  public AdaptVideoToFormatUseCase(VideoToAdaptRepository videoToAdaptRepository,
                                   VideoRepository videoRepository) {
    this.videoToAdaptRepository = videoToAdaptRepository;
    this.videoRepository = videoRepository;
  }

  public void adaptVideo(Project currentProject, final VideoToAdapt videoToAdapt, final VideonaFormat videoFormat,
                         AdaptListener listener) throws IOException {
    this.adaptListener = new WeakReference<>(listener);
    videoToAdaptRepository.update(videoToAdapt);
    videoToAdapt.getVideo().setTempPath(currentProject.getProjectPathIntermediateFiles());
    transcoderHelper.adaptVideoWithRotationToDefaultFormatAsync(videoToAdapt.getVideo(),
            videoFormat, videoToAdapt.getDestVideoPath(), videoToAdapt.getRotation(),
            new AdaptVideoListener(videoFormat, videoToAdapt.getDestVideoPath(),
                    videoToAdapt.getRotation(), currentProject),
            currentProject.getProjectPathIntermediateAudioMixedFiles());
  }


  class AdaptVideoListener implements TranscoderHelperListener {
    private final VideonaFormat videonaFormat;
    private final String destVideoPath;
    private final int rotation;
    private Project currentProject;

    private AdaptVideoListener(VideonaFormat videonaFormat, String destVideoPath, int rotation,
                               Project currentProject) {
      this.videonaFormat = videonaFormat;
      this.destVideoPath = destVideoPath;
      this.rotation = rotation;
      this.currentProject = currentProject;
    }

    @Override
    public void onSuccessTranscoding(Video video) {
      Log.d(TAG, "onSuccessTranscoding adapting video " + video.getMediaPath());
      VideoToAdapt videoToAdapt = videoToAdaptRepository.remove(video.getMediaPath());
      if (videoToAdapt != null) {
        FileUtils.removeFile(video.getMediaPath());
        Log.d(TAG, "deleting " + video.getMediaPath());
        video.setMediaPath(videoToAdapt.getDestVideoPath());
      } else {
        Log.e(TAG, "Null video in retrieved video to adapt!! for video " + video.getMediaPath());
      }
      video.setVolume(Video.DEFAULT_VOLUME);
      video.setStopTime(FileUtils.getDuration(video.getMediaPath()));
      video.setTranscodingTask(null);
      video.resetTempPath();
      video.notifyChanges();
      // (jliarte): 18/07/17 now we should move the file, notify changes, and launch AV transitions
      videoRepository.update(video);
      notifySuccess(video);
    }
    private void notifySuccess(Video video) {
      AdaptListener listener = adaptListener.get();
      if (listener != null) {
        listener.onSuccessAdapting(video);
      }
    }

    @Override
    public void onErrorTranscoding(Video video, String message) {
      Log.d(TAG, "onErrorTranscoding adapting video " + video.getMediaPath() + " - " + message);
      VideoToAdapt videoToAdapt = videoToAdaptRepository.getByMediaPath(video.getMediaPath());
      if (videoToAdapt == null) {
        return;
      }
      if (videoToAdapt.numTriesAdaptingVideo < MAX_NUM_TRIES_ADAPTING_VIDEO) {
        videoToAdaptRepository.remove(video.getMediaPath());
        // TODO(jliarte): 18/07/17 check if video still has volume
        try {
          transcoderHelper.adaptVideoWithRotationToDefaultFormatAsync(video, videonaFormat,
                  destVideoPath, rotation, this,
                  currentProject.getProjectPathIntermediateAudioMixedFiles());
        } catch (IOException e) {
          e.printStackTrace();
          AdaptListener listener = adaptListener.get();
          if (listener != null) {
            listener.onErrorAdapting(video, e.getMessage());
          }
        }
      } else {
        // TODO:(alvaro.martinez) 24/05/17 How to manage this error adapting video ¿?
      }
    }
  }

  public interface AdaptListener {
    void onSuccessAdapting(Video video);
    void onErrorAdapting(Video video, String message);
  }

}
