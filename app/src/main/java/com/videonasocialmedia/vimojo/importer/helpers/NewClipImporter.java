package com.videonasocialmedia.vimojo.importer.helpers;

import android.util.Log;

import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelperListener;
import com.videonasocialmedia.vimojo.domain.video.UpdateVideoRepositoryUseCase;
import com.videonasocialmedia.vimojo.export.domain.GetVideoFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.record.domain.AdaptVideoRecordedToVideoFormatUseCase;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jliarte on 6/07/17.
 */
public class NewClipImporter implements TranscoderHelperListener {
  private static final String TAG = NewClipImporter.class.getCanonicalName();
  private final UpdateVideoRepositoryUseCase updateVideoRepositoryUseCase;
  private AdaptVideoRecordedToVideoFormatUseCase adaptVideoRecordedToVideoFormatUseCase;
  private HashMap<String, VideoToAdapt> videoListToAdaptAndPosition = new HashMap<>();
  private static final int MAX_NUM_TRIES_ADAPTING_VIDEO = 3;
//  private final WeakReference<ProjectVideoAdder> projectClipAdder;
//  private WeakReference<View> view;
  private GetVideoFormatFromCurrentProjectUseCase getVideoFormatFromCurrentProjectUseCase;

  public NewClipImporter(
//          ProjectVideoAdder projectClipAdder,
          GetVideoFormatFromCurrentProjectUseCase getVideoFormatFromCurrentProjectUseCase,
          AdaptVideoRecordedToVideoFormatUseCase adaptVideoRecordedToVideoFormatUseCase,
          UpdateVideoRepositoryUseCase updateVideoRepositoryUseCase) {
//    this.projectClipAdder = new WeakReference<>(projectClipAdder);
    this.getVideoFormatFromCurrentProjectUseCase = getVideoFormatFromCurrentProjectUseCase;
    this.adaptVideoRecordedToVideoFormatUseCase = adaptVideoRecordedToVideoFormatUseCase;
    this.updateVideoRepositoryUseCase = updateVideoRepositoryUseCase;
//    this.view = new WeakReference<>(view);
  }

  public void adaptVideoToVideonaFormat(Video video, int videoPosition, int cameraRotation, int retries) {
    Log.e(TAG, "Adapt video at position " + videoPosition);
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
//      handleViewCall();
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
    VideoToAdapt videoToAdapt = new VideoToAdapt(video, destVideoPath, videoPosition,
            cameraRotation, retries);
    videoListToAdaptAndPosition.put(video.getUuid(), videoToAdapt);
  }

//  private void handleViewCall() {
//    View activity = view.get();
//    if (activity != null) {
//      activity.hideProgressAdaptingVideo();
//    }
//  }

  public boolean areTherePendingTranscodingTask() {
    if (videoListToAdaptAndPosition.size() == 0) {
      return false;
    }
    for (Map.Entry<String, VideoToAdapt> videoToAdaptHashMap :
            videoListToAdaptAndPosition.entrySet()) {
      VideoToAdapt video = videoToAdaptHashMap.getValue();
      if ((video.getVideo().getTranscodingTask() == null)
              || (!video.getVideo().getTranscodingTask().isDone())) {
        return true;
      }
    }
    return false;
  }

//    private boolean isAVideoAdaptedToFormat(Video video) {
//      String videoFolderPath = new File(video.getMediaPath()).getParent();
//      return videoFolderPath.equals(Constants.PATH_APP_TEMP);
//    }

  @Override
  public void onSuccessTranscoding(Video video) {
//      if (isAVideoAdaptedToFormat(video)) {
    Log.d(TAG, "onSuccessTranscoding adapting video " + video.getMediaPath());
    VideoToAdapt videoToAdapt = videoListToAdaptAndPosition.remove(video.getUuid());
    FileUtils.removeFile(video.getMediaPath());
    Log.e(TAG, "deleting " + video.getMediaPath());
    video.setMediaPath(videoToAdapt.destVideoPath);
    video.setVolume(Video.DEFAULT_VOLUME);
    video.setStopTime(FileUtils.getDuration(video.getMediaPath()));
    video.resetTempPath();
    updateVideoRepositoryUseCase.updateVideo(video);
    // (jliarte): 18/07/17 now we should move the file, notify changes, and launch AV transitions

//    hadleClipAdderCall(videoToAdapt);


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
    VideoToAdapt videoToAdapt = videoListToAdaptAndPosition.get(video.getUuid());
    if (videoToAdapt.numTriesAdaptingVideo < MAX_NUM_TRIES_ADAPTING_VIDEO) {
      videoListToAdaptAndPosition.remove(video.getUuid());
      // TODO(jliarte): 18/07/17 check if video still has volume
      adaptVideoToVideonaFormat(video, videoToAdapt.getPosition(),
              videoToAdapt.getRotation(), ++videoToAdapt.numTriesAdaptingVideo);
    } else {
      // TODO:(alvaro.martinez) 24/05/17 How to manage this error adapting video ¿?
//      hadleClipAdderCall(videoListToAdaptAndPosition.remove(video.getUuid()));
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

  public class VideoToAdapt {
    private final int position;
    private final Video video;
    private final int rotation;
    private final String destVideoPath;
    private int numTriesAdaptingVideo = 0;

    public VideoToAdapt(Video video, String destVideoPath, int position, int cameraRotation, int retries) {
      this.video = video;
      this.destVideoPath = destVideoPath;
      this.position = position;
      this.rotation = cameraRotation;
      this.numTriesAdaptingVideo = retries;
    }

    public int getPosition() {
      return position;
    }

    public Video getVideo() {
      return video;
    }

    public int getRotation() {
      return rotation;
    }
  }

//  public interface ProjectVideoAdder {
//    void addVideoToProject(VideoToAdapt videoToAdapt);
//  }

//  public interface View {
//    void hideProgressAdaptingVideo();
//  }
}
