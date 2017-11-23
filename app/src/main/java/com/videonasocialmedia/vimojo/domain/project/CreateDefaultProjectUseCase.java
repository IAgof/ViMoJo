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
import static com.videonasocialmedia.vimojo.utils.Constants.CAMERA_PREF_RESOLUTION_1080_BACK_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.CAMERA_PREF_RESOLUTION_1080_FRONT_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.CAMERA_PREF_RESOLUTION_2160_BACK_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.CAMERA_PREF_RESOLUTION_2160_FRONT_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.CAMERA_PREF_RESOLUTION_720_BACK_ID;
import static com.videonasocialmedia.vimojo.utils.Constants.CAMERA_PREF_RESOLUTION_720_FRONT_ID;


/**
 * Created by jliarte on 23/10/16.
 */
public class CreateDefaultProjectUseCase {

  public static final VideoQuality.Quality DEFAULT_VIDEO_QUALITY = VideoQuality.Quality.LOW;
  public static final VideoFrameRate.FrameRate DEFAULT_VIDEO_FRAME_RATE = VideoFrameRate
      .FrameRate.FPS30;
  protected CameraSettingsRepository cameraSettingsRepository;
  protected ProjectRepository projectRepository;
  protected TrackRepository trackRepository;
  private final Drawable drawableFadeTransitionVideo;

  /**
   * Default constructor with project repository argument.
   *
   * @param projectRepository the project repository.
   */
  @Inject public CreateDefaultProjectUseCase(ProjectRepository projectRepository,
                                             CameraSettingsRepository
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

   if(cameraSettingsRepository.getCameraSettings() == null) {
      initCameraSettings();
    }

    Project currentProject = Project.getInstance(projectTitle, rootPath, privatePath,
            getCurrentProfile(cameraSettingsRepository));
    currentProject.getVMComposition().setDrawableFadeTransitionVideo(drawableFadeTransitionVideo);
    if ((isProjectCreated && isWatermarkFeatured)) {
      currentProject.setWatermarkActivated(true);
    }
    projectRepository.update(currentProject);
  }

  private void initCameraSettings() {
    HashMap<Integer, Boolean> resolutionsSupportedMap = new HashMap<>();
    resolutionsSupportedMap.put(CAMERA_PREF_RESOLUTION_720_BACK_ID, true);
    resolutionsSupportedMap.put(CAMERA_PREF_RESOLUTION_1080_BACK_ID, true);
    resolutionsSupportedMap.put(CAMERA_PREF_RESOLUTION_2160_BACK_ID, false);
    resolutionsSupportedMap.put(CAMERA_PREF_RESOLUTION_720_FRONT_ID, true);
    resolutionsSupportedMap.put(CAMERA_PREF_RESOLUTION_1080_FRONT_ID, true);
    resolutionsSupportedMap.put(CAMERA_PREF_RESOLUTION_2160_FRONT_ID, false);
    ResolutionSetting resolutionSetting = new ResolutionSetting(
        Constants.DEFAULT_CAMERA_PREF_RESOLUTION, resolutionsSupportedMap);

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
    VideoResolution.Resolution resolution =
        getResolutionFromPreferencesSetting(cameraSettingsRepository);
    VideoQuality.Quality quality = getQualityFromPreferenceSettings(cameraSettingsRepository);
    VideoFrameRate.FrameRate frameRate =
        getFrameRateFromPreferenceSettings(cameraSettingsRepository);

    Profile currentProfileInstance = Profile.getInstance(resolution, quality, frameRate);
    currentProfileInstance.setResolution(resolution);
    currentProfileInstance.setQuality(quality);
    currentProfileInstance.setFrameRate(frameRate);
    return currentProfileInstance;
  }

  private VideoResolution.Resolution getResolutionFromPreferencesSetting(CameraSettingsRepository
                                                                         cameraSettingsRepository) {
    ResolutionSetting resolutionSetting = cameraSettingsRepository.getCameraSettings()
            .getResolutionSetting();
    HashMap<Integer, Boolean> resolutionSupportedMap = resolutionSetting
        .getResolutionsSupportedMap();
    String resolution = resolutionSetting.getResolution();
    if(resolutionSupportedMap.get(CAMERA_PREF_RESOLUTION_720_BACK_ID))
      if (resolution.equals(Constants.CAMERA_PREF_RESOLUTION_720)) {
        return VideoResolution.Resolution.HD720;
      }
    if(resolutionSupportedMap.get(CAMERA_PREF_RESOLUTION_1080_BACK_ID)) {
      if (resolution.equals(Constants.CAMERA_PREF_RESOLUTION_1080)) {
        return VideoResolution.Resolution.HD1080;
      }
    }
    if(resolutionSupportedMap.get(CAMERA_PREF_RESOLUTION_2160_BACK_ID)) {
      if (resolution.equals(Constants.CAMERA_PREF_RESOLUTION_2160)) {
        return VideoResolution.Resolution.HD4K;
      }
    }
    // default 1080p. We suppose that 720p is the minimum supported, 1080p not is always presented if all phones,ex Videona MotoG.
    if (resolutionSupportedMap.get(CAMERA_PREF_RESOLUTION_1080_BACK_ID)) {
      return VideoResolution.Resolution.HD1080;
    } else {
      return VideoResolution.Resolution.HD720;
    }
  }

  private VideoQuality.Quality getQualityFromPreferenceSettings(CameraSettingsRepository
                                                                    cameraSettingsRepository) {
    CameraSettings cameraSettings = cameraSettingsRepository.getCameraSettings();
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

  private VideoFrameRate.FrameRate getFrameRateFromPreferenceSettings(CameraSettingsRepository
                                                                        cameraSettingsRepository) {
    FrameRateSetting frameRateSetting = cameraSettingsRepository.getCameraSettings()
            .getFrameRateSetting();
    HashMap<Integer, Boolean> frameRateResolutionMap = frameRateSetting.getFrameRatesSupportedMap();
    String frameRate = frameRateSetting.getFrameRate();
    if (frameRateResolutionMap.get(CAMERA_PREF_FRAME_RATE_24_ID)) {
      if (frameRate.equals(Constants.CAMERA_PREF_FRAME_RATE_24)) {
        return VideoFrameRate.FrameRate.FPS24;
      }
    }
    if (frameRateResolutionMap.get(CAMERA_PREF_FRAME_RATE_25_ID)) {
      if (frameRate.equals(Constants.CAMERA_PREF_FRAME_RATE_25)) {
        return VideoFrameRate.FrameRate.FPS25;
      }
    }
    if (frameRateResolutionMap.get(CAMERA_PREF_FRAME_RATE_30_ID)) {
      if (frameRate.equals(Constants.CAMERA_PREF_FRAME_RATE_30)) {
        return VideoFrameRate.FrameRate.FPS30;
      }
    }
    // default 30 fps, standard
    return DEFAULT_VIDEO_FRAME_RATE;
  }
}
