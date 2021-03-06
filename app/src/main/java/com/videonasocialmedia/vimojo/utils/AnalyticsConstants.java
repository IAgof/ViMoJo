package com.videonasocialmedia.vimojo.utils;

/**
 * Created by Veronica Lago Fominaya on 28/01/2016.
 */
public class AnalyticsConstants {
    /**
     * Event Names
     */
    public final static String APP_STARTED = "App Started";
    public final static String VIDEO_RECORDED = "Video Recorded";
    public final static String VIDEO_EDITED = "Video Edited";
    public final static String VIDEO_EXPORTED = "Video Exported";
    public static final String VIDEO_SHARED = "Video Shared";
    public final static String USER_INTERACTED = "User Interacted";
    public final static String FILTER_SELECTED = "Filter Selected";
    public static final String TIME_IN_ACTIVITY = "Time in Activity";
    public final static String BETA_LEAVED = "Beta Leaved";
    public final static String APP_SHARED = "App Shared";
    public final static String LINK_CLICKED = "Link Clicked";
    public final static String PROJECT_EDITED = "Project Edited";
    public final static String USER_LOGGED_IN = "User Logged In";

    /**
     * User Traits
     */
    public final static String PREHISTERIC = "prehisteric";
    public final static String CREATED = "created";
    public final static String TYPE = "type";
    public static final String APP_USE_COUNT = "appUseCount";
    public final static String RESOLUTION = "resolution";
    public final static String QUALITY = "quality";
    public final static String FRAME_RATE = "frame_rate";
    public final static String TOTAL_VIDEOS_RECORDED = "totalVideosRecorded";
    public final static String LAST_VIDEO_RECORDED = "lastVideoRecorded";
    public static final String TOTAL_VIDEOS_SHARED = "totalVideosShared";
    public static final String LAST_VIDEO_SHARED = "lastVideoShared";
    public final static String LOCALE = "locale";
    public final static String LANG = "lang";

    /**
     * User Traits values
     */
    public final static String USER_TYPE_FREE = "free";
    public final static String USER_TYPE_BETA = "beta user";
    public static final String LAST_GIFT_DOWNLOADED = "lastGiftDownloaded";
    public static final String LAST_GIFT_DOWNLOADED_DATE = "lastGiftDownloadedDate";
    public static final String TOTAL_GIFTS_DOWNLOADED = "totalGiftsDownloaded";
    public static final String TOTAL_FILTERS_USED = "totalFiltersUsed";

    /**
     * Super properties
     */
    public static final String FIRST_TIME = "firstTime";
    public static final String APP = "app";
    public static final String FLAVOR = "flavor";

    /**
     * Values
     */
    public final static String DATE = "date"; // Beta Leaved event property
    public final static String TYPE_ORGANIC = "organic"; // App Started event property

    public final static String INIT_STATE = "initState"; // App Started event property
    public final static String FILTER_TYPE_COLOR = "color";
    public final static String FILTER_TYPE_DISTORTION = "distortion";
    public final static String FILTER_TYPE_OVERLAY = "overlay";
    public final static String ACTIVITY = "activity";
    public final static String RECORDING = "recording";
    public final static String INTERACTION = "interaction";
    public final static String INTERACTION_OPEN_SETTINGS = "settings opened";
    public final static String INTERACTION_OPEN_DRAWER = "drawer opened";
    public final static String RESULT = "result";
    public final static String VIDEO_LENGTH = "videoLength";
    public final static String NUMBER_OF_CLIPS = "numberOfClips";
    public final static String NAME = "name";
    public final static String CODE = "code";
    public static final String CHANGE_FLASH = "change flash";
    public static final String RECORD = "record";
    public static final String START = "start";
    public static final String STOP = "stop";
    public static final String CHANGE_CAMERA = "change camera";
    public static final String CAMERA_BACK = "back";
    public static final String CAMERA_FRONT = "front";
    public static final String SOCIAL_NETWORK = "socialNetwork";
    public static final String SOCIAL_NETWORK_PLATFORM = "VimojoPlatform";
    public static final String SOCIAL_NETWORK_FTP = "Ftp";
    public static final String SOCIAL_NETWORK_UNKNOWN = "Unknown";
    public static final String SOCIAL_NETWORK_OTHER = "Other network";
    public static final String CLEAR_FILTER = "clearFilter";
    public static final String SET_FILTER_GROUP = "setFilterGroup";
    public static final String FILTER_GROUP_OVERLAY = "overlay";
    public static final String FILTER_GROUP_SHADER = "shader";
    public static final String FILTERS_COMBINED = "filtersCombined";
    public static final String COMBINED = "combined";

    public static final String INIT_STATE_FIRST_TIME = "firstTime";
    public static final String INIT_STATE_RETURNING = "returning";
    public static final String INIT_STATE_UPGRADE = "upgrade";
    public static final String APP_SHARED_NAME = "appShared";
    public static final String LINK = "link";
    public static final String SOURCE_APP = "sourceApp";
    public static final String DESTINATION = "destination";
    public static final String SOURCE_APP_VIDEONA = "Videona";
    public static final String DESTINATION_KAMARADA_PLAY = "Kamarada Google Play";

    /**
     * Record Events properties and values
     */
    public static final String RECORD_ACTION = "recordAction";
    public static final String RECORD_ACTION_CHANGE_CAMERA_BACK = "Change camera back";
    public static final String RECORD_ACTION_CHANGE_CAMERA_FRONT = "Change camera front";
    public static final String RECORD_ACTION_CHANGE_FLASH_ON = "Change flash ON";
    public static final String RECORD_ACTION_CHANGE_FLASH_OFF = "Change flash OFF";
    public static final String RECORD_ACTION_START_RECORDING = "Start";
    public static final String RECORD_ACTION_STOP_RECORDING = "Stop";
    /**
     * Edit Events properties and values
     */
    public static final String EDIT_ACTION = "editAction";
    public static final String EDIT_ACTION_REORDER = "Reorder";
    public static final String EDIT_ACTION_TRIM = "Trim";
    public static final String EDIT_ACTION_SPLIT = "Split";
    public static final String EDIT_ACTION_DUPLICATE = "Duplicate";
    public static final String EDIT_ACTION_MUSIC_SET = "Music set";
    public static final String EDIT_ACTION_MUSIC_REMOVE = "Music removed";
    public static final String EDIT_ACTION_VOICE_OVER_SET = "Voice over set";
    public static final String EDIT_ACTION_VOICE_OVER_REMOVED = "Voice over removed";
    public static final String EDIT_ACTION_TEXT = "Text to video";
    public static final String TEXT_POSITION = "Text position";
    public static final String TEXT_LENGTH = "Text length";
    public static final String TEXT_SHADOW = "Text shadow";
    public static final String NUMBER_OF_DUPLICATES = "numberOfDuplicates";
    public static final String MUSIC_TITLE = "musicTitle";

    /**
     * App Theme Events properties and values
     */
    public static final String ACTION_THEME_CHANGED = "themeChanged";
    public static final String ACTION_THEME_SELECTED = "themeSelected";
    public static final String THEME_CHANGE_SOURCE = "sourceThemeChanged";
    public static final String THEME_CHANGE_SOURCE_DRAWER = "sourceThemeDrawer";
    public static final String THEME_CHANGE_SOURCE_SETTINGS = "sourceThemeSettings";
    public static final String THEME_APP_ACTION_DARK = "Theme dark";
    public static final String THEME_APP_ACTION_LIGHT = "Theme light";

    /**
     * Camera Settings, user interactions
     */
    public static final String ACTION_RESOLUTION_CHANGED = "resolutionChanged";
    public static final String ACTION_RESOLUTION_SELECTED = "resolutionSelected";
    public static final String ACTION_QUALITY_CHANGED = "qualityChanged";
    public static final String ACTION_QUALITY_SELECTED = "qualitySelected";
    public static final String ACTION_FRAME_RATE_CHANGED = "frameRateChanged";
    public static final String ACTION_FRAME_RATE_SELECTED = "frameRateSelected";
    public static final String ACTION_INTERFACE_CAMERA_CHANGED = "interfaceCameraChanged";
    public static final String ACTION_INTERFACE_CAMERA_SELECTED = "interfaceCameraSelected";

    /**
     * Project Events properties and values
     */
    public static final String PROJECT_ACTION = "projectAction";
    public static final String PROJECT_ACTION_INFO = "Info";
    public static final String PROJECT_ACTION_TITLE = "Project title";
    public static final String PROJECT_ACTION_DESCRIPTION = "Project description";
    public static final String PROJECT_ACTION_PRODUCT_TYPE = "Project product type";

    /**
     * Interaction values
     */
    public final static String INTERACTION_CLICK_LOGIN = "loginClick";
    public final static String INTERACTION_CLICK_REGISTER = "registerClick";

    /**
     * User logged values
     */
    public static final String USER_WAS_LOGGED_IN = "userWasLoggedIn";
    public static final String AUTH0_ID = "auth0Id";
    public static final String USER_ID = "userId";
}
