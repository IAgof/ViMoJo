package com.videonasocialmedia.vimojo.utils;

/*
 * Copyright (C) 2015 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Juan Javier Cabanas
 * Álvaro Martínez Marco
 */

import android.os.Environment;

import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.cameraSettings.model.CameraSettings;
import com.videonasocialmedia.vimojo.cameraSettings.model.FrameRateSetting;
import com.videonasocialmedia.vimojo.cameraSettings.model.ResolutionSetting;

import java.io.File;

public class Constants {

    //TODO cambiar el endpoint a la dirección de producción
    public static final String API_ENDPOINT = "http://192.168.0.22/Videona/web/app_dev.php/api";
    //OAuth
    public static final String OAUTH_CLIENT_ID
            = "4_6c1bbez44j0okk8sckcssk4wocsgks044wsw0sogkw4gwc8gg0";
    public static final String OAUTH_CLIENT_SECRET
            = "64a2br3oixwk0kkw4wwscoocssss0cwg0og8g0ssggcs80owww";


    // Folders
    final public static String FOLDER_NAME_VIMOJO_MASTERS =
        Utils.capitalizeFirstLetter(BuildConfig.FLAVOR) + "_Masters";
    final public static String FOLDER_NAME_VIMOJO = Utils.capitalizeFirstLetter(BuildConfig.FLAVOR);
    final public static String FOLDER_NAME_VIMOJO_TEMP = ".temporal";
    final public static String FOLDER_NAME_VIMOJO_PROJECTS = ".projects";

    final public static String RESOURCE_WATERMARK_NAME = "watermark.png";
//    final public static String FOLDER_NAME_VIMOJO_TEMP_AUDIO = ".tempAudio";

    final public static String PATH_APP_ANDROID = Environment.getExternalStorageDirectory()
        .getPath() + "/Android/data/" + BuildConfig.APPLICATION_ID;

    final public static String PATH_WATERMARK = PATH_APP_ANDROID + File.separator +
        Constants.RESOURCE_WATERMARK_NAME;

    final public static String PATH_APP = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DCIM) + File.separator + FOLDER_NAME_VIMOJO;
    final public static String PATH_APP_EDITED = PATH_APP;
    final public static String PATH_APP_MASTERS = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DCIM) + File.separator + FOLDER_NAME_VIMOJO_MASTERS;

    final public static String PATH_APP_TEMP = PATH_APP + File.separator + FOLDER_NAME_VIMOJO_TEMP;
    final public static String PATH_APP_PROJECTS = PATH_APP + File.separator
        + FOLDER_NAME_VIMOJO_PROJECTS;

    final public static String VIDEO_TEMP_RECORD_FILENAME = "VID_temp.mp4";
    final public static String AUDIO_TEMP_RECORD_VOICE_OVER_FILENAME = "AudioVoiceOver.mp4";
    final public static String AUDIO_TEMP_RECORD_VOICE_OVER_RAW_FILE_NAME = "AudioVoiceOver.pcm";
    final public static String MUSIC_AUDIO_VOICEOVER_TITLE = "audio_voice_over_music";

    final public static String AUDIO_MUSIC_FILE_EXTENSION = ".m4a";

    // Project
    //TODO define this values
    final public static String PROJECT_TITLE = "model";
    final public static String FOLDER_VIDEONA_PRIVATE_MODEL = "model";

    // EXTRAS INTENT EDIT
    final public static String CURRENT_VIDEO_INDEX = "current_video_index";
    final public static String VIDEO_TO_SHARE_PATH = "video_to_share_path";
    final public static String MUSIC_SELECTED_TITLE = "music_selected";
    final public static String OUTPUT_FILE_MIXED_AUDIO = PATH_APP_TEMP + File.separator
            + com.videonasocialmedia.videonamediaframework.model.Constants.MIXED_AUDIO_FILE_NAME;


    public static final String NOTIFICATION_EXPORT_SERVICES_RECEIVER =
            "com.videonasocialmedia.vimojo";

    public static final int DEFAULT_VIMOJO_WIDTH = 1280;
    public static final int DEFAULT_VIMOJO_HEIGHT = 720;
    public static final int DEFAULT_VIMOJO_AUDIO_BITRATE = 192 * 1000;
    public static final int DEFAULT_VIMOJO_AUDIO_CHANNELS = 1;
    public static final float ALPHA_DISABLED_BOTTOM_BAR= 0.3f;
    public static final int DEFAULT_VIMOJO_BITRATE = 10*1000*1000;
    public static final int DEFAULT_VIMOJO_FRAME_RATE = 25;

    final public static String FLAVOR_VIMOJO = "vimojo";

    public static final String USER_THUMB = "userThumb.jpg";
    public static final String USER_PROFILE_THUMB = "userProfileThumb.jpg";

    public static final int MAX_NUM_TRIES_TO_EXPORT_VIDEO = 4;
    public static final int EXPORT_ERROR_UNKNOWN = 10;
    public static final int EXPORT_ERROR_NO_SPACE_LEFT = 11;
  public enum ERROR_TRANSCODING_TEMP_FILE_TYPE {SPLIT, TRIM, TEXT, AVTRANSITION, APP_CRASH;}

    public enum BATTERY_STATUS {CHARGING, CRITICAL, LOW, MEDIUM, FULL, UNKNOW;}

    public enum MEMORY_STATUS {CRITICAL, MEDIUM, OKAY;}

    public static final float MIN_TRIM_OFFSET = 0.35f; //350ms

  public static final float MS_CORRECTION_FACTOR = 1000f;
  public static final int ADVANCE_PLAYER_PRECISION_LOW = 300;
  public static final int ADVANCE_PLAYER_PRECISION_MEDIUM = 600;
  public static final int ADVANCE_PLAYER_PRECISION_HIGH = 1200;
    public static final String IN_APP_BILLING_ITEM_WATERMARK = "inappitem_watermark";

  public static final String IN_APP_BILLING_ITEM_DARK_THEME = "inappitem_darktheme";
    public static final boolean DEFAULT_THEME_DARK_STATE = false;

  public static final boolean DEFAULT_WATERMARK_STATE = true;
  // TODO: 30/7/18 Delete BuildConfig dependency and default values from Constants.java
  public static final String DEFAULT_CAMERA_SETTING_RESOLUTION =
      BuildConfig.FEATURE_VERTICAL_VIDEOS ? ResolutionSetting.CAMERA_SETTING_RESOLUTION_V_720
          : ResolutionSetting.CAMERA_SETTING_RESOLUTION_H_720;
  public static final VideoResolution.Resolution DEFAULT_CAMERA_SETTING_VIDEO_RESOLUTION =
      BuildConfig.FEATURE_VERTICAL_VIDEOS ? VideoResolution.Resolution.V_720P
          : VideoResolution.Resolution.HD720;
  public static final String DEFAULT_CAMERA_SETTING_QUALITY = CameraSettings.CAMERA_SETTING_QUALITY_16;
  public static final VideoQuality.Quality DEFAULT_CAMERA_SETTING_VIDEO_QUALITY =
          VideoQuality.Quality.LOW;
  public static final String DEFAULT_CAMERA_SETTING_FRAME_RATE = FrameRateSetting.CAMERA_SETTING_FRAME_RATE_30;
  public static final VideoFrameRate.FrameRate DEFAULT_CAMERA_SETTING_VIDEO_FRAME_RATE =
          VideoFrameRate.FrameRate.FPS30;
  public static final int BACK_CAMERA_ID = 0;
  public static final int FRONT_CAMERA_ID = 1;

  public static final int CAMERA_SETTING_INTERFACE_PRO_ID = 1;

  public static final String CAMERA_SETTING_INTERFACE_PRO = "Camera pro";
  public static final int CAMERA_SETTING_INTERFACE_BASIC_ID = 2;

  public static final String CAMERA_SETTING_INTERFACE_BASIC = "Camera basic";
  public static final String DEFAULT_CAMERA_SETTING_INTERFACE_SELECTED = CAMERA_SETTING_INTERFACE_PRO;
  public static final int DEFAULT_CAMERA_SETTINGS_CAMERA_ID_SELECTED = BACK_CAMERA_ID;
  public static final int DEFAULT_CAMERA_SETTINGS_CAMERA_ID_SELECTED_VERTICAL_APP = FRONT_CAMERA_ID;
  public static final String BASE_PACKAGE_NAME = "com.videonasocialmedia.vimojo";
  public static final int DEFAULT_PLAYER_HEIGHT_VERTICAL_MODE = 260;
  /**
   * Feature toggles names - These names are local feature toggle ids for our repo
   */
  public static final String USER_FEATURE_FORCE_WATERMARK = "user-feature-force-watermark";
  public static final String USER_FEATURE_WATERMARK = "user-feature-watermark";
  public static final String FEATURE_VIMOJO_STORE = "feature-vimojo-store";
  public static final String FEATURE_VIMOJO_PLATFORM = "feature-vimojo-platform";
  public static final String USER_FEATURE_FTP_PUBLISHING = "user-feature-ftp-publishing";
  public static final String FEATURE_ADS_ENABLED = "feature-ads-enabled";
  public static final String USER_FEATURE_VOICE_OVER = "user-feature-voice-over";
  public static final String FEATURE_AVTRANSITIONS = "feature-avtransitions";
  public static final String USER_FEATURE_CAMERA_PRO = "user-feature-camera-pro";
  public static final String USER_FEATURE_SELECT_FRAME_RATE = "user-feature-select-frame-rate";
  public static final String USER_FEATURE_SELECT_RESOLUTION = "user-feature-select-resolution";
  public static final String FEATURE_RECORD_AUDIO_GAIN = "feature-record-audio-gain";
  public static final String FEATURE_SHARE_SHOW_SOCIAL_NETWORKS = "feature-show-social-networks";
  public static final String FEATURE_SHOW_MORE_APPS = "feature-show-more-apps";
  public static final String FEATURE_SHOW_TUTORIALS = "feature-show-tutorials";

  /**
   * Default User features values
   */
  public static final boolean DEFAULT_FORCE_WATERMARK = false;
  public static final boolean DEFAULT_FTP = false;
  public static final boolean DEFAULT_SHOW_ADS = true;
  public static final boolean DEFAULT_VIMOJO_PLATFORM = false;
  public static final boolean DEFAULT_VIMOJO_STORE = true;
  public static final boolean DEFAULT_VOICE_OVER = false;
  public static final boolean DEFAULT_WATERMARK = true;
  public static final boolean DEFAULT_CAMERA_PRO = false;
  public static final boolean DEFAULT_SELECT_FRAME_RATE = false;
  public static final boolean DEFAULT_SELECT_RESOLUTION = false;
}
