package com.videonasocialmedia.vimojo.utils;

/**
 * Created by alvaro on 5/09/16.
 */
public class IntentConstants {

    public final static String VIDEO_ID = "videoId";
    public final static String VIDEO_EXPORTED = "videoExportedNavigateToShareActivity";
    public final static String THEME_APP="themeAppSelected";

    /**
     * Trimming files
     */
    public final static String IS_VIDEO_TRIMMED = "isVideoTrimmed";
    public final static String START_TIME_MS = "startTimeMs";
    public final static String FINISH_TIME_MS = "finishTimeMs";


    /**
     * Adding text to files
     */
    public final static String IS_TEXT_ADDED = "isTextAdded";
    public final static String TEXT_TO_ADD = "textToAdd";
    public static final String TEXT_POSITION = "textPosition";

    /**
     * Voice over activity
     */
    public static final String VOICE_OVER_RECORDED_PATH = "voiceOverRecordedPath";

    public static final String VIDEO_TEMP_DIRECTORY = "intermediates_temp_directory";
    public static final String VIDEO_TEMP_DIRECTORY_FADE_AUDIO =
        "intermediates_temp_directory_fade_audio";
    /**
     * Relaunch
     */
    public static final String RELAUNCH_EXPORT_TEMP= "relaunch_export_temp";

    /**
     * Music Detail
     */
    public static final String MUSIC_DETAIL_SELECTED= "music_detail_selected";

    /**
     * Ftp Selected
     *
     */
    public static final String FTP_SELECTED = "ftp_selected";

  /**
   * Adapt Video Recorded
   */
  public static final String VIDEO_RECORDED_ORIG = "video_recorded_orig";
  public static final String VIDEO_RECORDED_DEST = "video_recorded_dest";


    /**
     * Record Activity
     */

    public static final String BATTERY_NOTIFICATION = "battery_notifications";

    public static final String BATTERY_STATUS = "battery_status";
    public static final String BATTERY_LEVEL = "battery_level";
    public static final String BATTERY_SCALE = "battery_scale" ;

  public static final String JACK_PLUG_NOTIFICATION = "jack_plug_notifications";
  public static final String MICROPHONE_STATUS = "microphone";

  /**
   * LicenseVimojo Activity
   */

  public static final String LICENSE_SELECTED = "license_selected";

  /**
   * Cancel upload
   */
  public static final String ACTION_START_UPLOAD = "action_start_upload";

  /**
   * Cancel upload
   */
  public static final String ACTION_CANCEL_UPLOAD = "action_cancel_upload";

   /**
   * Activate/Pause upload
   */
  public static final String ACTION_PAUSE_ACTIVATE_UPLOAD = "action_pause_activate_upload";

  /**
   * Activate upload
   */
  public static final String ACTION_ACTIVATE_UPLOAD = "action_activate_upload";

  /**
   * Remove upload
   */
  public static final String ACTION_REMOVE_UPLOAD = "action_remove_upload";

  /**
   * Video upload UUID
   */
  public static final String VIDEO_UPLOAD_UUID = "video_upload_uuid";

}
