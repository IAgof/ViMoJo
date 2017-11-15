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

import com.videonasocialmedia.vimojo.BuildConfig;

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
    final public static String FOLDER_NAME_VIMOJO_MASTERS = "Vimojo_Masters";
    final public static String FOLDER_NAME_VIMOJO = "Vimojo";
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

    // Default camera setting
    public static final boolean DEFAULT_CAMERA_PREF_INTERFACE_PRO_SELECTED = false;
    public static final String CAMERA_PREF_RESOLUTION_720 = "720p";
    public static final String CAMERA_PREF_RESOLUTION_1080 = "1080p";
    public static final String CAMERA_PREF_RESOLUTION_2160 = "4k";
    public static final String DEFAULT_CAMERA_PREF_RESOLUTION = CAMERA_PREF_RESOLUTION_1080;
    public static final String CAMERA_PREF_QUALITY_16 = "16 Mbps";
    public static final String CAMERA_PREF_QUALITY_32 = "32 Mbps";
    public static final String CAMERA_PREF_QUALITY_50 = "50 Mbps";
    public static final String DEFAULT_CAMERA_PREF_QUALITY = CAMERA_PREF_QUALITY_16;
    public static final String CAMERA_PREF_FRAME_RATE_24 = "24 fps";
    public static final String CAMERA_PREF_FRAME_RATE_25 = "25 fps";
    public static final String CAMERA_PREF_FRAME_RATE_30 = "30 fps";
    public static final String DEFAULT_CAMERA_PREF_FRAME_RATE = CAMERA_PREF_FRAME_RATE_30;

}
