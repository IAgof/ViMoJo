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

import java.io.File;

public class Constants {

    //TODO cambiar el endpoint a la dirección de producción
    public static final String API_ENDPOINT = "http://192.168.0.22/Videona/web/app_dev.php/api";
    //OAuth
    public static final String OAUTH_CLIENT_ID = "4_6c1bbez44j0okk8sckcssk4wocsgks044wsw0sogkw4gwc8gg0";
    public static final String OAUTH_CLIENT_SECRET = "64a2br3oixwk0kkw4wwscoocssss0cwg0og8g0ssggcs80owww";


    // Folders
    final public static String FOLDER_NAME_VIMOJO_MASTERS = "Vimojo_Masters";
    final public static String FOLDER_NAME_VIMOJO = "Vimojo";
    final public static String FOLDER_NAME_VIMOJO_TEMP = ".temporal";
//    final public static String FOLDER_NAME_VIMOJO_TEMP_AUDIO = ".tempAudio";

    final public static String PATH_APP = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DCIM) + File.separator + FOLDER_NAME_VIMOJO;

    final public static String PATH_APP_EDITED = PATH_APP;

    final public static String PATH_APP_MASTERS = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DCIM) + File.separator + FOLDER_NAME_VIMOJO_MASTERS;

    final public static String PATH_APP_TEMP = PATH_APP + File.separator + FOLDER_NAME_VIMOJO_TEMP;
    final public static String PATH_APP_TEMP_INTERMEDIATE_FILES = PATH_APP_TEMP
            + File.separator + "intermediate_files";

    final public static String VIDEO_TEMP_RECORD_FILENAME = "VID_temp.mp4";

    final public static String PATH_APP_TEMP_AUDIO = PATH_APP_TEMP_INTERMEDIATE_FILES +
        File.separator + "tempMixedAudio";
   

    final public static String AUDIO_MUSIC_FILE_EXTENSION = ".m4a";

    // Project
    //TODO define this values
    final public static String PROJECT_TITLE = "model";
    final public static String FOLDER_VIDEONA_PRIVATE_MODEL = "model";

    // EXTRAS INTENT EDIT
    final public static String CURRENT_VIDEO_INDEX = "current_video_index";
    final public static String VIDEO_TO_SHARE_PATH = "video_to_share_path";
    final public static String MUSIC_SELECTED_TITLE = "music_selected";
    final public static String MUSIC_AUDIO_MIXED_TITLE = "audio_mixed_music";
    final public static String OUTPUT_FILE_MIXED_AUDIO = PATH_APP_TEMP + File.separator + com.videonasocialmedia.videonamediaframework.model.Constants.MIXED_AUDIO_FILE_NAME;


    public static final String NOTIFICATION_EXPORT_SERVICES_RECEIVER = "com.videonasocialmedia.vimojo";

    public static final int DEFAULT_VIMOJO_WIDTH = 1280;
    public static final int DEFAULT_VIMOJO_HEIGHT = 720;
    public static final int DEFAULT_VIMOJO_BITRATE = 10*1000*1000;
    public static final int DEFAULT_VIMOJO_FRAME_RATE = 25;
}
