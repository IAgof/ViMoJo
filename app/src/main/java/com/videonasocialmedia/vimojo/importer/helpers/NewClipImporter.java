package com.videonasocialmedia.vimojo.importer.helpers;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.domain.editor.ApplyAVTransitionsUseCase;
import com.videonasocialmedia.vimojo.export.domain.GetVideoFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.RelaunchTranscoderTempBackgroundUseCase;
import com.videonasocialmedia.vimojo.importer.model.entities.VideoToAdapt;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptRepository;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.record.domain.AdaptVideoToFormatUseCase;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by jliarte on 6/07/17.
 */
public class NewClipImporter {
  private static final String TAG = NewClipImporter.class.getCanonicalName();
  private final VideoRepository videoRepository;
  private final VideoToAdaptRepository videoToAdaptRepository;
  private AdaptVideoToFormatUseCase adaptVideoToFormatUseCase;
  private GetVideoFormatFromCurrentProjectUseCase getVideoFormatFromCurrentProjectUseCase;
  private RelaunchTranscoderTempBackgroundUseCase relaunchTranscoderTempBackgroundUseCase;
  private ApplyAVTransitionsUseCase launchTranscoderAddAVTransitionUseCase;

  public NewClipImporter(
          GetVideoFormatFromCurrentProjectUseCase getVideoFormatFromCurrentProjectUseCase,
          AdaptVideoToFormatUseCase adaptVideoToFormatUseCase,
          ApplyAVTransitionsUseCase launchTranscoderAddAVTransitionUseCase,
          RelaunchTranscoderTempBackgroundUseCase relaunchTranscoderTempBackgroundUseCase,
          VideoRepository videoRepository, VideoToAdaptRepository videoToAdaptRepository) {
    this.getVideoFormatFromCurrentProjectUseCase = getVideoFormatFromCurrentProjectUseCase;
    this.adaptVideoToFormatUseCase = adaptVideoToFormatUseCase;
    this.launchTranscoderAddAVTransitionUseCase = launchTranscoderAddAVTransitionUseCase;
    this.relaunchTranscoderTempBackgroundUseCase = relaunchTranscoderTempBackgroundUseCase;
    this.videoRepository = videoRepository;
    this.videoToAdaptRepository = videoToAdaptRepository;
  }

  public void adaptVideoToVideonaFormat(Project currentProject, Video video, int videoPosition,
                                        int cameraRotation, int retries) {
    Log.d(TAG, "Adapt video at position " + videoPosition);
    String destVideoRecorded = Constants.PATH_APP_MASTERS + File.separator
            + new File(video.getMediaPath()).getName();
    VideonaFormat videoFormat = getVideoFormat(cameraRotation);
    AdaptVideoToFormatUseCase.AdaptListener adaptListener =
            new AdaptVideoToFormatUseCase.AdaptListener() {
      @Override
      public void onSuccessAdapting(Video video) {
        // TODO(jliarte): 31/08/17 implement this method
      }

      @Override
      public void onErrorAdapting(Video video, String message) {
        // TODO(jliarte): 31/08/17 implement this method
      }
    };
    // TODO(jliarte): 11/09/17 check if video is retrieved on error
//    saveVideoToAdapt(video, destVideoRecorded, videoPosition, cameraRotation, retries);
    VideoToAdapt videoToAdapt = new VideoToAdapt(video, destVideoRecorded, videoPosition,
            cameraRotation, retries);
    try {
      adaptVideoToFormatUseCase.adaptVideo(videoToAdapt, videoFormat, adaptListener);
      applyAVTransitions(video, currentProject);
    } catch (IOException e) {
      e.printStackTrace();
      adaptListener.onErrorAdapting(video, "adaptVideoToFormatUseCase");
    }
  }

  private void applyAVTransitions(Video video, Project currentProject) {
    if (currentProject.getVMComposition().isAudioFadeTransitionActivated()
            || currentProject.getVMComposition().isVideoFadeTransitionActivated()) {
      video.setTranscodingTask(Futures.transform(video.getTranscodingTask(),
              getAVTransitionApplierFunction(video, currentProject)));
    }
  }

  private Function<Video, Video> getAVTransitionApplierFunction(
          final Video video, final Project currentProject) {
    return new Function<Video, Video>() {
      @Override
      public Video apply(Video input) {
        video.setTempPath(currentProject.getProjectPathIntermediateFiles());
        VideonaFormat videoFormat = currentProject.getVMComposition().getVideoFormat();
        Drawable drawableFadeTransitionVideo = currentProject.getVMComposition()
                .getDrawableFadeTransitionVideo();
        launchTranscoderAddAVTransitionUseCase.applyAVTransitions(drawableFadeTransitionVideo,
                video, videoFormat, currentProject.getProjectPathIntermediateFileAudioFade(),
                new ApplyAVTransitionsUseCase.AVTransitionsApplierListener() {
                  @Override
                  public void onSuccessApplyAVTransitions(Video video) {
                    video.setTranscodingTask(null);
                    VideoToAdapt adaptedVideo = videoToAdaptRepository
                            .getByMediaPath(video.getMediaPath());
                    videoToAdaptRepository.remove(adaptedVideo);
                  }

                  @Override
                  public void onErrorApplyAVTransitions(Video video, String message) {
                    // TODO(jliarte): 31/08/17 implement this method
                  }
                });
        // TODO(jliarte): 29/08/17 wait for finish
        return null;
      }
    };
  }

  private VideonaFormat getVideoFormat(int rotation) {
    // FIXME: 23/05/17 if rotation == 0, should be use getVideonaFormatToAdaptVideoRecordedAudio, more efficient.
    // Fix problems with profile MotoG, LG_pablo, ...
    return getVideoFormatFromCurrentProjectUseCase
              .getVideonaFormatToAdaptVideoRecordedAudioAndVideo();
  }

  public void relaunchUnfinishedAdaptTasks(Project currentProject) {
    List<VideoToAdapt> videosToAdapt = videoToAdaptRepository.getAllVideos();
    // (jliarte): 24/08/17 videos are reconstructed from repository, so they are not the same
    // instance that where previously stored. Thus some fields differs or are missing, such as
    // queried transcodingTask!!! Thus, we're included a in memory cache in the repo
    Log.d(TAG, "There are " + videosToAdapt.size() + " videos to adapt in repository");
    for (VideoToAdapt videoToAdapt : videosToAdapt) {
      if (videoToAdapt.getVideo() == null) {
        Log.e(TAG, "Orphan video to adapt " + videoToAdapt);
        videoToAdaptRepository.remove(videoToAdapt);
      } else {
        if (videoToAdapt.getVideo().getTranscodingTask() == null) {
          Log.d(TAG, "Relaunching video adapt task for video "
                  + videoToAdapt.getVideo().getMediaPath());
          adaptVideoToVideonaFormat(currentProject, videoToAdapt.getVideo(),
                  videoToAdapt.getPosition(), videoToAdapt.getRotation(),
                  ++videoToAdapt.numTriesAdaptingVideo);
          videoToAdapt.getVideo().addListener(currentProject);
        }
      }
    }
  }

}
