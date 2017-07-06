package com.videonasocialmedia.vimojo.importer.helpers;

import android.util.Log;

import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelperListener;
import com.videonasocialmedia.vimojo.record.domain.AdaptVideoRecordedToVideoFormatUseCase;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jliarte on 6/07/17.
 */
public class NewClipImporter implements TranscoderHelperListener {
  private static final String TAG = NewClipImporter.class.getCanonicalName();
  private final ProjectVideoAdder projectClipAdder;
  private AdaptVideoRecordedToVideoFormatUseCase adaptVideoRecordedToVideoFormatUseCase;
  private HashMap<String, VideoToAdapt> videoListToAdaptAndPosition = new HashMap<>();
  private final VideonaFormat videonaFormat;
  private static final int MAX_NUM_TRIES_ADAPTING_VIDEO = 3;
  private View view;

  public NewClipImporter(
          ProjectVideoAdder projectClipAdder, VideonaFormat videonaFormat,
          AdaptVideoRecordedToVideoFormatUseCase adaptVideoRecordedToVideoFormatUseCase,
          View view) {
    this.projectClipAdder = projectClipAdder;
    this.videonaFormat = videonaFormat;
    this.view = view;
    this.adaptVideoRecordedToVideoFormatUseCase = adaptVideoRecordedToVideoFormatUseCase;
  }

  public void adaptVideoToVideonaFormat(String origPath, int videoPosition, int cameraRotation, int retries) {
    Log.e(TAG, "Adapt video at position " + videoPosition);
    String destVideoRecorded = Constants.PATH_APP_MASTERS + File.separator
            + new File(origPath).getName();

    final Video video = new Video(origPath, Video.DEFAULT_VOLUME);
    VideoToAdapt videoToAdapt = new VideoToAdapt(video, videoPosition, cameraRotation, retries);
    videoListToAdaptAndPosition.put(video.getUuid(), videoToAdapt);

    // FIXME: 23/05/17 if rotation == 0, should be use getVideonaFormatToAdaptVideoRecordedAudio, more efficient.
    // Fix problems with profile MotoG, LG_pablo, ...
    // FIXME: 24/05/17 AdaptVideo not need fadeTransition or isTransitionActivated, refactor SDK
//      Drawable fadeTransition = projectClipAdder.getContext().getDrawable(R.drawable.alpha_transition_white);
    try {
      adaptVideoRecordedToVideoFormatUseCase.adaptVideo(video, videonaFormat,
              destVideoRecorded, cameraRotation, null, false, this);
    } catch (IOException e) {
      e.printStackTrace();
      onErrorTranscoding(video, "adaptVideoRecordedToVideoFormatUseCase");
      this.view.hideProgressAdaptingVideo();
    }
  }

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
    projectClipAdder.addVideoToProject(videoToAdapt);
//      } else {
//        // TODO(jliarte): 3/07/17 don't get the meaning of this case
//        Log.d(TAG, "onSuccessTranscoding " + video.getTempPath());
//        updateVideoRepositoryUseCase.succesTranscodingVideo(video);
//      }
  }

  @Override
  public void onErrorTranscoding(Video video, String message) {
//      if (newClipImporter.isAVideoAdaptedToFormat(video)) {
    Log.d(TAG, "onErrorTranscoding adapting video " + video.getMediaPath() + " - " + message);
    VideoToAdapt videoToAdapt = videoListToAdaptAndPosition.get(video.getUuid());
    if (videoToAdapt.numTriesAdaptingVideo < MAX_NUM_TRIES_ADAPTING_VIDEO) {
//          adaptVideoToVideonaFormat(video.getMediaPath(), videosRecorded, camera.getRotation());
      videoListToAdaptAndPosition.remove(video.getUuid());
      adaptVideoToVideonaFormat(video.getMediaPath(), videoToAdapt.getPosition(),
              videoToAdapt.getRotation(), ++videoToAdapt.numTriesAdaptingVideo);
    } else {
      // TODO:(alvaro.martinez) 24/05/17 How to manage this error adapting video ¿?
      projectClipAdder.addVideoToProject(videoListToAdaptAndPosition.remove(video.getUuid()));
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
    private int numTriesAdaptingVideo = 0;

    public VideoToAdapt(Video video, int position, int cameraRotation, int retries) {
      this.video = video;
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

  public interface ProjectVideoAdder {
    void addVideoToProject(VideoToAdapt videoToAdapt);
  }

  public interface View {
    void hideProgressAdaptingVideo();
  }
}
