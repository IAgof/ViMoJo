package com.videonasocialmedia.vimojo.importer.helpers;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.domain.editor.ApplyAVTransitionsUseCase;
import com.videonasocialmedia.vimojo.export.domain.GetVideoFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.importer.model.entities.VideoToAdapt;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptDataSource;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.record.domain.AdaptVideoToFormatUseCase;
import com.videonasocialmedia.vimojo.asset.repository.datasource.VideoDataSource;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

/**
 * Created by jliarte on 6/07/17.
 */
public class NewClipImporter {
  private static final String TAG = NewClipImporter.class.getCanonicalName();
  private final VideoDataSource videoRepository;
  protected VideoToAdaptDataSource videoToAdaptRepository;
  private AdaptVideoToFormatUseCase adaptVideoToFormatUseCase;
  private GetVideoFormatFromCurrentProjectUseCase getVideoFormatFromCurrentProjectUseCase;
  private ApplyAVTransitionsUseCase launchTranscoderAddAVTransitionUseCase;
  private static final int N_THREADS = 5;
  private final ListeningExecutorService executorPool;

  public NewClipImporter(
          GetVideoFormatFromCurrentProjectUseCase getVideoFormatFromCurrentProjectUseCase,
          AdaptVideoToFormatUseCase adaptVideoToFormatUseCase,
          ApplyAVTransitionsUseCase launchTranscoderAddAVTransitionUseCase,
          VideoDataSource videoRepository,
          VideoToAdaptDataSource videoToAdaptRepository) {
    this.getVideoFormatFromCurrentProjectUseCase = getVideoFormatFromCurrentProjectUseCase;
    this.adaptVideoToFormatUseCase = adaptVideoToFormatUseCase;
    this.launchTranscoderAddAVTransitionUseCase = launchTranscoderAddAVTransitionUseCase;
    this.videoRepository = videoRepository;
    this.videoToAdaptRepository = videoToAdaptRepository;
    executorPool = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(N_THREADS));
  }

  public void adaptVideoToVideonaFormat(Project currentProject, Video video, int videoPosition,
                                        int cameraRotation, int retries) {
    Log.d(TAG, "Adapt video at position " + videoPosition);
    String destVideoRecorded = Constants.PATH_APP_MASTERS + File.separator
            + new File(video.getMediaPath()).getName();
    VideonaFormat videoFormat = getVideoFormat(currentProject, cameraRotation);
    AdaptVideoToFormatUseCase.AdaptListener adaptListener =
            new AdaptVideoToFormatUseCase.AdaptListener() {
      @Override
      public void onSuccessAdapting(Video video) {
        videoRepository.update(video); // (jliarte): 18/07/17 now we should move the file, notify changes, and launch AV transitions
        // TODO(jliarte): 18/07/18 done in AdaptVideoToFormatUseCase.AdaptListener, but change to videoRepository (and execute in background?)
//        addAssetToUpload(video);
//        updateCompositionWithPlatform(currentProject);
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
      adaptVideoToFormatUseCase.adaptVideo(currentProject, videoToAdapt, videoFormat, adaptListener);
      // TODO(jliarte): 18/07/18 should chain with adapt job
      applyAVTransitions(video, currentProject);
    } catch (IOException e) {
      e.printStackTrace();
      adaptListener.onErrorAdapting(video, "adaptVideoToFormatUseCase");
    }
  }

  protected final <T> ListenableFuture<T> executeUseCaseCall(Callable<T> callable) {
    return executorPool.submit(callable);
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
    return input -> {
      video.setTempPath(currentProject.getProjectPathIntermediateFiles());
      VideonaFormat videoFormat = currentProject.getVMComposition().getVideoFormat();
      Drawable drawableFadeTransitionVideo = currentProject.getVMComposition()
              .getDrawableFadeTransitionVideo();
      launchTranscoderAddAVTransitionUseCase.applyAVTransitions(drawableFadeTransitionVideo,
              video, videoFormat, currentProject.getProjectPathIntermediateFileAudioFade(),
              new ApplyAVTransitionsUseCase.AVTransitionsApplierListener() {
                @Override
                public void onSuccessApplyAVTransitions(Video video1) {
                  video1.setTranscodingTask(null);
                  VideoToAdapt adaptedVideo = videoToAdaptRepository
                          .getByMediaPath(video1.getMediaPath());
                  videoToAdaptRepository.remove(adaptedVideo);
                }

                @Override
                public void onErrorApplyAVTransitions(Video video1, String message) {
                  // TODO(jliarte): 31/08/17 implement this method
                }
              });
      // TODO(jliarte): 29/08/17 wait for finish
      return null;
    };
  }

  protected VideonaFormat getVideoFormat(Project currentProject, int rotation) {
    // FIXME: 23/05/17 if rotation == 0, should be use getVideonaFormatToAdaptVideoRecordedAudio, more efficient.
    // Fix problems with profile MotoG, LG_pablo, ...
    return getVideoFormatFromCurrentProjectUseCase
              .getVideonaFormatToAdaptVideoRecordedAudioAndVideo(currentProject);
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
          // Now find which videoRepository video correspond with videoToAdaptRepository by uuid
          Video videoToRelaunch = getVideoToRelaunch(currentProject, videoToAdapt);
          if (videoToRelaunch != null) {
            adaptVideoToVideonaFormat(currentProject, videoToRelaunch,
                    videoToAdapt.getPosition(), videoToAdapt.getRotation(),
                    ++videoToAdapt.numTriesAdaptingVideo);
            videoToRelaunch.addListener(currentProject);
          } else {
            Log.e(TAG, "Video to relaunch not found " + videoToAdapt);
            videoToAdaptRepository.remove(videoToAdapt);
          }

        }
      }
    }
  }

  protected Video getVideoToRelaunch(Project currentProject, VideoToAdapt videoToAdapt) {
    for(Media video: currentProject.getMediaTrack().getItems()){
      Video videoToRelaunch = (Video) video;
      if (videoToRelaunch.getUuid().compareTo(videoToAdapt.getVideo().getUuid()) == 0) {
        return videoToRelaunch;
      }
    }
    return null;
  }

}
