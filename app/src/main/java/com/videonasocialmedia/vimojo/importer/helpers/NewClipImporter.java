package com.videonasocialmedia.vimojo.importer.helpers;

import android.util.Log;

import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelperListener;
import com.videonasocialmedia.vimojo.export.domain.GetVideoFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.RelaunchTranscoderTempBackgroundUseCase;
import com.videonasocialmedia.vimojo.importer.model.entities.VideoToAdapt;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptRepository;
import com.videonasocialmedia.vimojo.record.domain.AdaptVideoRecordedToVideoFormatUseCase;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by jliarte on 6/07/17.
 */
public class NewClipImporter implements TranscoderHelperListener {
  private static final String TAG = NewClipImporter.class.getCanonicalName();
  private final VideoRepository videoRepository;
  private final VideoToAdaptRepository videoToAdaptRepository;
  private AdaptVideoRecordedToVideoFormatUseCase adaptVideoRecordedToVideoFormatUseCase;
  private static final int MAX_NUM_TRIES_ADAPTING_VIDEO = 3;
  private GetVideoFormatFromCurrentProjectUseCase getVideoFormatFromCurrentProjectUseCase;
  private RelaunchTranscoderTempBackgroundUseCase relaunchTranscoderTempBackgroundUseCase;

  public NewClipImporter(
          GetVideoFormatFromCurrentProjectUseCase getVideoFormatFromCurrentProjectUseCase,
          AdaptVideoRecordedToVideoFormatUseCase adaptVideoRecordedToVideoFormatUseCase,
          RelaunchTranscoderTempBackgroundUseCase relaunchTranscoderTempBackgroundUseCase,
          VideoRepository videoRepository, VideoToAdaptRepository videoToAdaptRepository) {
    this.getVideoFormatFromCurrentProjectUseCase = getVideoFormatFromCurrentProjectUseCase;
    this.adaptVideoRecordedToVideoFormatUseCase = adaptVideoRecordedToVideoFormatUseCase;
    this.relaunchTranscoderTempBackgroundUseCase = relaunchTranscoderTempBackgroundUseCase;
    this.videoRepository = videoRepository;
    this.videoToAdaptRepository = videoToAdaptRepository;
  }

  public void adaptVideoToVideonaFormat(Video video, int videoPosition, int cameraRotation,
                                        int retries) {
    Log.d(TAG, "Adapt video at position " + videoPosition);
    String destVideoRecorded = Constants.PATH_APP_MASTERS + File.separator
            + new File(video.getMediaPath()).getName();
    saveVideoToAdapt(video, destVideoRecorded, videoPosition, cameraRotation, retries);
    VideonaFormat videoFormat = getVideoFormat(cameraRotation);
    try {
      adaptVideoRecordedToVideoFormatUseCase.adaptVideo(video, videoFormat,
              destVideoRecorded, cameraRotation, this);
    } catch (IOException e) {
      e.printStackTrace();
      onErrorTranscoding(video, "adaptVideoRecordedToVideoFormatUseCase");
    }
  }

  private VideonaFormat getVideoFormat(int rotation) {
    // FIXME: 23/05/17 if rotation == 0, should be use getVideonaFormatToAdaptVideoRecordedAudio, more efficient.
    // Fix problems with profile MotoG, LG_pablo, ...
    return getVideoFormatFromCurrentProjectUseCase
              .getVideonaFormatToAdaptVideoRecordedAudioAndVideo();
  }

  private void saveVideoToAdapt(Video video, String destVideoPath, int videoPosition,
                                int cameraRotation, int retries) {
    // TODO(jliarte): 18/07/17 move this to a realm repo
    VideoToAdapt videoToAdapt = new VideoToAdapt(video, destVideoPath, videoPosition,
            cameraRotation, retries);
    videoToAdaptRepository.update(videoToAdapt);
  }

//    private boolean isAVideoAdaptedToFormat(Video video) {
//      String videoFolderPath = new File(video.getMediaPath()).getParent();
//      return videoFolderPath.equals(Constants.PATH_APP_TEMP);
//    }

  @Override
  public void onSuccessTranscoding(Video video) {
//      if (isAVideoAdaptedToFormat(video)) {
    Log.d(TAG, "onSuccessTranscoding adapting video " + video.getMediaPath());
    VideoToAdapt videoToAdapt = videoToAdaptRepository.remove(video.getMediaPath());
    if (videoToAdapt != null) {
      FileUtils.removeFile(video.getMediaPath());
      Log.d(TAG, "deleting " + video.getMediaPath());
      video.setMediaPath(videoToAdapt.getDestVideoPath());
    }
    video.setVolume(Video.DEFAULT_VOLUME);
    video.setStopTime(FileUtils.getDuration(video.getMediaPath()));
    video.resetTempPath();
    video.notifyChanges();

    videoRepository.update(video);
    // (jliarte): 18/07/17 now we should move the file, notify changes, and launch AV transitions

//      } else {
//        // TODO(jliarte): 3/07/17 don't get the meaning of this case
//        Log.d(TAG, "onSuccessTranscoding " + video.getTempPath());
//        updateVideoRepositoryUseCase.succesTranscodingVideo(video);
//      }
  }

//  private void hadleClipAdderCall(VideoToAdapt videoToAdapt) {
//    ProjectVideoAdder adder = projectClipAdder.get();
//    if (adder != null) {
//      adder.addVideoToProject(videoToAdapt);
//    }
//  }

  @Override
  public void onErrorTranscoding(Video video, String message) {
//      if (newClipImporter.isAVideoAdaptedToFormat(video)) {
    Log.d(TAG, "onErrorTranscoding adapting video " + video.getMediaPath() + " - " + message);
    VideoToAdapt videoToAdapt = videoToAdaptRepository.getByMediaPath(video.getMediaPath());
    if (videoToAdapt == null) {
      return;
    }
    if (videoToAdapt.numTriesAdaptingVideo < MAX_NUM_TRIES_ADAPTING_VIDEO) {
      videoToAdaptRepository.remove(video.getMediaPath());
      // TODO(jliarte): 18/07/17 check if video still has volume
      adaptVideoToVideonaFormat(video, videoToAdapt.getPosition(),
              videoToAdapt.getRotation(), ++videoToAdapt.numTriesAdaptingVideo);
    } else {
      // TODO:(alvaro.martinez) 24/05/17 How to manage this error adapting video ¿?
    }
//      } else {
//        Log.d(TAG, "onErrorTranscoding " + video.getTempPath() + " - " + message);
//        if (video.getNumTriesToExportVideo() < Constants.MAX_NUM_TRIES_TO_EXPORT_VIDEO) {
//          video.increaseNumTriesToExportVideo();
//          Project currentProject = Project.getInstance(null, null, null);
//          launchTranscoderAddAVTransitionUseCase.launchExportTempFile(context
//                          .getDrawable(R.drawable.alpha_transition_white), video,
//                  getVideoFormatFromCurrentProjectUseCase.getVideonaFormatFromCurrentProject(),
//                  currentProject.getProjectPathIntermediateFileAudioFade(), this);
//        } else {
//          updateVideoRepositoryUseCase.errorTranscodingVideo(video,
//                  Constants.ERROR_TRANSCODING_TEMP_FILE_TYPE.AVTRANSITION.name());
//        }
//      }
  }

  public void relaunchUnfinishedAdaptTasks() {
    List<VideoToAdapt> videosToAdapt = videoToAdaptRepository.getAllVideos();
    // (jliarte): 24/08/17 videos are reconstructed from repository, so they are not the same
    // instance that where previously stored. Thus some fields differs or are missing, such as
    // queried transcodingTask!!! Thus, we're included a in memory cache in the repo
    Log.d(TAG, "There are " + videosToAdapt.size() + " videos to adapt in repository");
    for (VideoToAdapt videoToAdapt : videosToAdapt) {
      if (videoToAdapt.getVideo() == null) {
        Log.e(TAG, "Orphan video to adapt " + videoToAdapt);
//        videoToAdaptRepository.remove(videoToAdapt);
      } else {
        if (videoToAdapt.getVideo().getTranscodingTask() == null) {
          Log.d(TAG, "Relaunching video adapt task for video "
                  + videoToAdapt.getVideo().getMediaPath());
          adaptVideoToVideonaFormat(videoToAdapt.getVideo(), videoToAdapt.getPosition(),
                  videoToAdapt.getRotation(), ++videoToAdapt.numTriesAdaptingVideo);
        }
      }
    }
  }

}
