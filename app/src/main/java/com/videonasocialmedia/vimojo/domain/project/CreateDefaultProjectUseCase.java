package com.videonasocialmedia.vimojo.domain.project;

import android.graphics.drawable.Drawable;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.cameraSettings.domain.UpdateCameraPreferencesUseCase;
import com.videonasocialmedia.vimojo.cameraSettings.model.CameraPreferences;
import com.videonasocialmedia.vimojo.cameraSettings.model.FrameRatePreference;
import com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionPreference;
import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraPrefRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.track.TrackRepository;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.DateUtils;


import javax.inject.Inject;


/**
 * Created by jliarte on 23/10/16.
 */
public class CreateDefaultProjectUseCase {

  public static final VideoQuality.Quality DEFAULT_VIDEO_QUALITY = VideoQuality.Quality.LOW;
  public static final VideoFrameRate.FrameRate DEFAULT_VIDEO_FRAME_RATE = VideoFrameRate.FrameRate.FPS30;
  protected CameraPrefRepository cameraPrefRepository;
  protected ProjectRepository projectRepository;
  protected TrackRepository trackRepository;
  private final Drawable drawableFadeTransitionVideo;

  /**
   * Default constructor with project repository argument.
   *
   * @param projectRepository the project repository.
   */
  @Inject public CreateDefaultProjectUseCase(ProjectRepository projectRepository, CameraPrefRepository
          cameraPrefRepository, TrackRepository trackRepository) {
    this.projectRepository = projectRepository;
    this.cameraPrefRepository = cameraPrefRepository;
    this.trackRepository = trackRepository;
    drawableFadeTransitionVideo = VimojoApplication.getAppContext()
            .getDrawable(R.drawable.alpha_transition_white);
  }

  public void loadOrCreateProject(String rootPath, String privatePath,
                                  boolean isWatermarkFeatured) {
    // By default project title,
    String projectTitle = DateUtils.getDateRightNow();
    // TODO(jliarte): 22/10/16 we should store current project in other place than Project instance.
    //                This is done for convenience only as we should get rid of all
    //                Project.getInstance calls
    boolean isProjectCreated = false;
    if (Project.INSTANCE == null) {
      Project.INSTANCE = projectRepository.getCurrentProject();
      isProjectCreated = true;
    }

   if(cameraPrefRepository.getCameraPreferences() == null) {
      initCameraPrefs();
    }

    Project currentProject = Project.getInstance(projectTitle, rootPath, privatePath,
            getCurrentProfile(cameraPrefRepository));
    currentProject.getVMComposition().setDrawableFadeTransitionVideo(drawableFadeTransitionVideo);
    if ((isProjectCreated && isWatermarkFeatured)) {
      currentProject.setWatermarkActivated(true);
    }
    projectRepository.update(currentProject);
  }

  private void initCameraPrefs() {
    UpdateCameraPreferencesUseCase updateCameraPreferencesUseCase = new
        UpdateCameraPreferencesUseCase(cameraPrefRepository);
    ResolutionPreference resolutionPreference = new ResolutionPreference(
        Constants.DEFAULT_CAMERA_PREF_RESOLUTION, true, true, true,
        true, true, false);
    FrameRatePreference frameRatePreference = new FrameRatePreference(
        Constants.DEFAULT_CAMERA_PREF_FRAME_RATE, false, false, true);
    String quality = Constants.DEFAULT_CAMERA_PREF_QUALITY;
    boolean interfaceProSelected = true;
    CameraPreferences defaultCameraPreferences = new CameraPreferences(resolutionPreference,
        frameRatePreference, quality, interfaceProSelected);
    updateCameraPreferencesUseCase.createCameraPref(defaultCameraPreferences);
  }

  public void createProject(String rootPath, String privatePath, boolean isWatermarkFeatured) {
    String projectTitle = DateUtils.getDateRightNow();
    Project currentProject = new Project(projectTitle, rootPath, privatePath,
            getCurrentProfile(cameraPrefRepository));
    if (isWatermarkFeatured) {
      currentProject.setWatermarkActivated(true);
    }
    Project.INSTANCE = currentProject;
    projectRepository.update(currentProject);
  }

  private Profile getCurrentProfile(CameraPrefRepository cameraPrefRepository) {
    VideoResolution.Resolution resolution = getResolutionFromPreferencesSetting(cameraPrefRepository);
    VideoQuality.Quality quality = getQualityFromPreferenceSettings(cameraPrefRepository);
    VideoFrameRate.FrameRate frameRate = getFrameRateFromPreferenceSettings(cameraPrefRepository);

    Profile currentProfileInstance = Profile.getInstance(resolution, quality, frameRate);
    currentProfileInstance.setResolution(resolution);
    currentProfileInstance.setQuality(quality);
    currentProfileInstance.setFrameRate(frameRate);
    return currentProfileInstance;
  }

  private VideoResolution.Resolution getResolutionFromPreferencesSetting(CameraPrefRepository cameraPrefRepository) {
    ResolutionPreference resolutionPreference = cameraPrefRepository.getCameraPreferences()
            .getResolutionPreference();
    String resolution = resolutionPreference.getResolution();
    if(resolutionPreference.isResolutionBack720pSupported())
      if (resolution.compareTo(Constants.CAMERA_PREF_RESOLUTION_720) == 0) {
        return VideoResolution.Resolution.HD720;
      }
    if(resolutionPreference.isResolutionBack1080pSupported()) {
      if (resolution.compareTo(Constants.CAMERA_PREF_RESOLUTION_1080) == 0) {
        return VideoResolution.Resolution.HD1080;
      }
    }
    if(resolutionPreference.isResolutionBack2160pSupported()) {
      if (resolution.compareTo(Constants.CAMERA_PREF_RESOLUTION_2160) == 0) {
        return VideoResolution.Resolution.HD4K;
      }
    }
    // default 1080p. We suppose that 720p is the minimum supported, 1080p not is always presented if all phones,ex Videona MotoG.
    if (resolutionPreference.isResolutionBack1080pSupported()) {
      return VideoResolution.Resolution.HD1080;
    } else {
      return VideoResolution.Resolution.HD720;
    }
  }

  private VideoQuality.Quality getQualityFromPreferenceSettings(CameraPrefRepository cameraPrefRepository) {
    CameraPreferences cameraPreferences = cameraPrefRepository.getCameraPreferences();
    String quality = cameraPreferences.getQuality();
    if (quality.compareTo(Constants.CAMERA_PREF_QUALITY_16) == 0) {
      return VideoQuality.Quality.LOW;
    }
    if (quality.compareTo(Constants.CAMERA_PREF_QUALITY_32) == 0) {
      return VideoQuality.Quality.GOOD;
    }
    if (quality.compareTo((Constants.CAMERA_PREF_QUALITY_50)) == 0) {
      return VideoQuality.Quality.HIGH;
    }
    // default
    return DEFAULT_VIDEO_QUALITY;
  }

  private VideoFrameRate.FrameRate getFrameRateFromPreferenceSettings(CameraPrefRepository cameraPrefRepository) {
    FrameRatePreference frameRatePreference = cameraPrefRepository.getCameraPreferences()
            .getFrameRatePreference();
    String frameRate = frameRatePreference.getFrameRate();
    if (frameRatePreference.isFrameRate24FpsSupported()) {
      if (frameRate.compareTo(Constants.CAMERA_PREF_FRAME_RATE_24) == 0) {
        return VideoFrameRate.FrameRate.FPS24;
      }
    }
    if (frameRatePreference.isFrameRate25FpsSupported()) {
      if (frameRate.compareTo(Constants.CAMERA_PREF_FRAME_RATE_25) == 0) {
        return VideoFrameRate.FrameRate.FPS25;
      }
    }
    if (frameRatePreference.isFrameRate30FpsSupported()) {
      if (frameRate.compareTo(Constants.CAMERA_PREF_FRAME_RATE_30) == 0) {
        return VideoFrameRate.FrameRate.FPS30;
      }
    }
    // default 30 fps, standard
    return DEFAULT_VIDEO_FRAME_RATE;
  }
}
