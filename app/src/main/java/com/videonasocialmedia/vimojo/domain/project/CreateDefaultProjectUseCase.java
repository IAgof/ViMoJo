package com.videonasocialmedia.vimojo.domain.project;

import android.graphics.drawable.Drawable;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraSettingsRepository;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettings;
import com.videonasocialmedia.vimojo.cameraSettings.model.FrameRateSetting;
import com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.track.TrackRepository;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.DateUtils;


import java.util.HashMap;

import javax.inject.Inject;

import static com.videonasocialmedia.vimojo.utils.Constants.CAMERA_PREF_FRAME_RATE_24_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.CAMERA_PREF_FRAME_RATE_25_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.CAMERA_PREF_FRAME_RATE_30_ID;


/**
 * Created by jliarte on 23/10/16.
 */
public class CreateDefaultProjectUseCase {

  public static final VideoQuality.Quality DEFAULT_VIDEO_QUALITY = VideoQuality.Quality.LOW;
  public static final VideoFrameRate.FrameRate DEFAULT_VIDEO_FRAME_RATE = VideoFrameRate.FrameRate.FPS30;
  private static final int MAX_NUM_FRAME_RATE_CHECKED = 3;
  protected CameraSettingsRepository cameraSettingsRepository;
  protected ProjectRepository projectRepository;
  protected TrackRepository trackRepository;
  private final Drawable drawableFadeTransitionVideo;

  /**
   * Default constructor with project repository argument.
   *
   * @param projectRepository the project repository.
   */
  @Inject public CreateDefaultProjectUseCase(ProjectRepository projectRepository, CameraSettingsRepository
          cameraSettingsRepository, TrackRepository trackRepository) {
    this.projectRepository = projectRepository;
    this.cameraSettingsRepository = cameraSettingsRepository;
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

   if(cameraSettingsRepository.getCameraPreferences() == null) {
      initCameraPrefs();
    }

    Project currentProject = Project.getInstance(projectTitle, rootPath, privatePath,
            getCurrentProfile(cameraSettingsRepository));
    currentProject.getVMComposition().setDrawableFadeTransitionVideo(drawableFadeTransitionVideo);
    if ((isProjectCreated && isWatermarkFeatured)) {
      currentProject.setWatermarkActivated(true);
    }
    projectRepository.update(currentProject);
  }

  private void initCameraPrefs() {
    ResolutionSetting resolutionSetting = new ResolutionSetting(
        Constants.DEFAULT_CAMERA_PREF_RESOLUTION, true, true, true,
        true, true, false);
    HashMap<Integer, Boolean> frameRateSupportedMap = new HashMap<>();
    frameRateSupportedMap.put(CAMERA_PREF_FRAME_RATE_24_ID, false);
    frameRateSupportedMap.put(CAMERA_PREF_FRAME_RATE_25_ID, false);
    frameRateSupportedMap.put(CAMERA_PREF_FRAME_RATE_30_ID, true);

    FrameRateSetting frameRateSetting = new FrameRateSetting(
        Constants.DEFAULT_CAMERA_PREF_FRAME_RATE, frameRateSupportedMap);
    String quality = Constants.DEFAULT_CAMERA_PREF_QUALITY;
    boolean interfaceProSelected = true;
    CameraSettings defaultCameraSettings = new CameraSettings(resolutionSetting,
            frameRateSetting, quality, interfaceProSelected);
    cameraSettingsRepository.createCameraPref(defaultCameraSettings);
  }

  public void createProject(String rootPath, String privatePath, boolean isWatermarkFeatured) {
    String projectTitle = DateUtils.getDateRightNow();
    Project currentProject = new Project(projectTitle, rootPath, privatePath,
            getCurrentProfile(cameraSettingsRepository));
    if (isWatermarkFeatured) {
      currentProject.setWatermarkActivated(true);
    }
    Project.INSTANCE = currentProject;
    projectRepository.update(currentProject);
  }

  private Profile getCurrentProfile(CameraSettingsRepository cameraSettingsRepository) {
    VideoResolution.Resolution resolution = getResolutionFromPreferencesSetting(cameraSettingsRepository);
    VideoQuality.Quality quality = getQualityFromPreferenceSettings(cameraSettingsRepository);
    VideoFrameRate.FrameRate frameRate = getFrameRateFromPreferenceSettings(cameraSettingsRepository);

    Profile currentProfileInstance = Profile.getInstance(resolution, quality, frameRate);
    currentProfileInstance.setResolution(resolution);
    currentProfileInstance.setQuality(quality);
    currentProfileInstance.setFrameRate(frameRate);
    return currentProfileInstance;
  }

  private VideoResolution.Resolution getResolutionFromPreferencesSetting(CameraSettingsRepository cameraSettingsRepository) {
    ResolutionSetting resolutionSetting = cameraSettingsRepository.getCameraPreferences()
            .getResolutionSetting();
    String resolution = resolutionSetting.getResolution();
    if(resolutionSetting.isResolutionBack720pSupported())
      if (resolution.compareTo(Constants.CAMERA_PREF_RESOLUTION_720) == 0) {
        return VideoResolution.Resolution.HD720;
      }
    if(resolutionSetting.isResolutionBack1080pSupported()) {
      if (resolution.compareTo(Constants.CAMERA_PREF_RESOLUTION_1080) == 0) {
        return VideoResolution.Resolution.HD1080;
      }
    }
    if(resolutionSetting.isResolutionBack2160pSupported()) {
      if (resolution.compareTo(Constants.CAMERA_PREF_RESOLUTION_2160) == 0) {
        return VideoResolution.Resolution.HD4K;
      }
    }
    // default 1080p. We suppose that 720p is the minimum supported, 1080p not is always presented if all phones,ex Videona MotoG.
    if (resolutionSetting.isResolutionBack1080pSupported()) {
      return VideoResolution.Resolution.HD1080;
    } else {
      return VideoResolution.Resolution.HD720;
    }
  }

  private VideoQuality.Quality getQualityFromPreferenceSettings(CameraSettingsRepository cameraSettingsRepository) {
    CameraSettings cameraSettings = cameraSettingsRepository.getCameraPreferences();
    String quality = cameraSettings.getQuality();
    if (quality.equals(Constants.CAMERA_PREF_QUALITY_16)) {
      return VideoQuality.Quality.LOW;
    }
    if (quality.equals(Constants.CAMERA_PREF_QUALITY_32)) {
      return VideoQuality.Quality.GOOD;
    }
    if (quality.equals(Constants.CAMERA_PREF_QUALITY_50)) {
      return VideoQuality.Quality.HIGH;
    }
    // default
    return DEFAULT_VIDEO_QUALITY;
  }

  private VideoFrameRate.FrameRate getFrameRateFromPreferenceSettings(CameraSettingsRepository cameraSettingsRepository) {
    FrameRateSetting frameRateSetting = cameraSettingsRepository.getCameraPreferences()
            .getFrameRateSetting();
    HashMap<Integer, Boolean> frameRateMap = frameRateSetting.getFrameRatesSupportedMap();
    String frameRate = frameRateSetting.getFrameRate();
    if (frameRateMap.get(CAMERA_PREF_FRAME_RATE_24_ID)) {
      if (frameRate.compareTo(Constants.CAMERA_PREF_FRAME_RATE_24) == 0) {
        return VideoFrameRate.FrameRate.FPS24;
      }
    }
    if (frameRateMap.get(CAMERA_PREF_FRAME_RATE_25_ID)) {
      if (frameRate.compareTo(Constants.CAMERA_PREF_FRAME_RATE_25) == 0) {
        return VideoFrameRate.FrameRate.FPS25;
      }
    }
    if (frameRateMap.get(CAMERA_PREF_FRAME_RATE_30_ID)) {
      if (frameRate.compareTo(Constants.CAMERA_PREF_FRAME_RATE_30) == 0) {
        return VideoFrameRate.FrameRate.FPS30;
      }
    }
    // default 30 fps, standard
    return DEFAULT_VIDEO_FRAME_RATE;
  }
}
