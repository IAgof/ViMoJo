package com.videonasocialmedia.vimojo.utils;

import android.util.Log;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jliarte on 7/06/16.
 */
public class UserEventTracker {
    private final String TAG = getClass().getSimpleName();
    private static UserEventTracker userEventTrackerInstance;
    public MixpanelAPI mixpanel;
    public static final String MIXPANEL_EMAIL_ID = "$email";
    public static final String MIXPANEL_ACCOUNT_EMAIL_ID = "$account_email";
    public static final String MIXPANEL_USERNAME_ID = "$username";

    protected UserEventTracker(MixpanelAPI mixpanelAPI) {
        this.mixpanel = mixpanelAPI;
    }

    public static UserEventTracker getInstance(MixpanelAPI mixpanelAPI) {
        if (userEventTrackerInstance == null)
            userEventTrackerInstance = new UserEventTracker(mixpanelAPI);
        return userEventTrackerInstance;
    }

    public static void clear() {
        userEventTrackerInstance = null;
    }

    protected void trackEvent(Event event) {
        if (event != null) {
            mixpanel.track(event.getName(), event.getProperties());
        }
    }

    /***** App startup - from initAppActivity ******/

    public void trackUserProfileGeneralTraits() {
        mixpanel.getPeople().increment(AnalyticsConstants.APP_USE_COUNT, 1);
        JSONObject userProfileProperties = new JSONObject();
        String userType = AnalyticsConstants.USER_TYPE_FREE;
        if (BuildConfig.FLAVOR.equals("alpha")) {
            userType = AnalyticsConstants.USER_TYPE_BETA;
        }
        try {
            userProfileProperties.put(AnalyticsConstants.TYPE, userType);
            userProfileProperties.put(AnalyticsConstants.LOCALE,
                    Locale.getDefault().toString());
            userProfileProperties.put(AnalyticsConstants.LANG, Locale.getDefault().getISO3Language());
            mixpanel.getPeople().set(userProfileProperties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void trackAppStartupProperties(boolean state) {
        JSONObject appStartupSuperProperties = new JSONObject();
        int appUseCount;
        try {
            appUseCount = mixpanel.getSuperProperties().getInt(AnalyticsConstants.APP_USE_COUNT);
        } catch (JSONException e) {
            appUseCount = 0;
        }
        try {
            appStartupSuperProperties.put(AnalyticsConstants.APP_USE_COUNT, ++appUseCount);
            appStartupSuperProperties.put(AnalyticsConstants.FIRST_TIME, state);
            appStartupSuperProperties.put(AnalyticsConstants.APP, "ViMoJo");
            appStartupSuperProperties.put(AnalyticsConstants.FLAVOR, BuildConfig.FLAVOR);
            mixpanel.registerSuperProperties(appStartupSuperProperties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void trackUserProfile(String androidId) {
        mixpanel.identify(androidId);
        mixpanel.getPeople().identify(androidId);
        JSONObject userProfileProperties = new JSONObject();
        try {
            userProfileProperties.put(AnalyticsConstants.CREATED,
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new Date()));
            mixpanel.getPeople().setOnce(userProfileProperties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void trackCreatedSuperProperty() {
        JSONObject createdSuperProperty = new JSONObject();
        try {
            createdSuperProperty.put(AnalyticsConstants.CREATED,
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new Date()));
            mixpanel.registerSuperPropertiesOnce(createdSuperProperty);
        } catch (JSONException e) {
            Log.e("ANALYTICS", "Error sending created super property");
        }
    }

    public void trackAppStartup(String initState) {
        JSONObject initAppProperties = new JSONObject();
        try {
            initAppProperties.put(AnalyticsConstants.TYPE, AnalyticsConstants.TYPE_ORGANIC);
            initAppProperties.put(AnalyticsConstants.INIT_STATE, initState);
            mixpanel.track(AnalyticsConstants.APP_STARTED, initAppProperties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void trackClipsReordered(Project project) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(AnalyticsConstants.EDIT_ACTION,
                    AnalyticsConstants.EDIT_ACTION_REORDER);
            addProjectEventProperties(project, eventProperties);
            Event trackingEvent = new Event(AnalyticsConstants.VIDEO_EDITED, eventProperties);
            this.trackEvent(trackingEvent);
        } catch (JSONException e) {
            Log.d(TAG, "trackClipsReordered: error sending mixpanel VIDEO_EDITED reorder event");
            e.printStackTrace();
        }
    }

    public void trackClipTrimmed(Project project) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(AnalyticsConstants.EDIT_ACTION,
                    AnalyticsConstants.EDIT_ACTION_TRIM);
            addProjectEventProperties(project, eventProperties);
            Event trackingEvent = new Event(AnalyticsConstants.VIDEO_EDITED, eventProperties);
            this.trackEvent(trackingEvent);
        } catch (JSONException e) {
            Log.d(TAG, "trackClipTrimmed: error sending mixpanel VIDEO_EDITED trim event");
            e.printStackTrace();
        }
    }

    public void trackClipSplitted(Project project) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(AnalyticsConstants.EDIT_ACTION, AnalyticsConstants.EDIT_ACTION_SPLIT);
            addProjectEventProperties(project, eventProperties);
            Event trackingEvent = new Event(AnalyticsConstants.VIDEO_EDITED, eventProperties);
            this.trackEvent(trackingEvent);
        } catch (JSONException e) {
            Log.d(TAG, "trackClipSplitted: error sending mixpanel VIDEO_EDITED split event");
            e.printStackTrace();
        }
    }

    public void trackClipDuplicated(int copies, Project project) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(AnalyticsConstants.EDIT_ACTION,
                    AnalyticsConstants.EDIT_ACTION_DUPLICATE);
            eventProperties.put(AnalyticsConstants.NUMBER_OF_DUPLICATES, copies);
            addProjectEventProperties(project, eventProperties);
            Event trackingEvent = new Event(AnalyticsConstants.VIDEO_EDITED, eventProperties);
            this.trackEvent(trackingEvent);
        } catch (JSONException e) {
            Log.d(TAG, "trackClipDuplicated: error sending mixpanel VIDEO_EDITED duplicate event");
            e.printStackTrace();
        }
    }

    public void trackClipAddedText(String position, int textLength, Project project){

        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(AnalyticsConstants.EDIT_ACTION,
                    AnalyticsConstants.EDIT_ACTION_TEXT);
            eventProperties.put(AnalyticsConstants.TEXT_POSITION, position);
            eventProperties.put(AnalyticsConstants.TEXT_LENGTH, textLength);
            addProjectEventProperties(project, eventProperties);
            Event trackingEvent = new Event(AnalyticsConstants.VIDEO_EDITED, eventProperties);
            this.trackEvent(trackingEvent);
        } catch (JSONException e) {
            Log.d(TAG, "trackClipDuplicated: error sending mixpanel VIDEO_EDITED duplicate event");
            e.printStackTrace();
        }

    }

    public void trackMusicSet(Project project) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(AnalyticsConstants.EDIT_ACTION,
                    AnalyticsConstants.EDIT_ACTION_MUSIC_SET);
            String musicTitle = "";
            if (project.getMusic() != null) {
                musicTitle = project.getMusic().getTitle();
            }
            eventProperties.put(AnalyticsConstants.MUSIC_TITLE, musicTitle);
            addProjectEventProperties(project, eventProperties);
            Event trackingEvent = new Event(AnalyticsConstants.VIDEO_EDITED, eventProperties);
            this.trackEvent(trackingEvent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void trackVoiceOverSet(Project project) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(AnalyticsConstants.EDIT_ACTION,
                    AnalyticsConstants.EDIT_ACTION_VOICE_OVER_SET);
            addProjectEventProperties(project, eventProperties);
            Event trackingEvent = new Event(AnalyticsConstants.VIDEO_EDITED, eventProperties);
            this.trackEvent(trackingEvent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addProjectEventProperties(Project project, JSONObject eventProperties)
            throws JSONException {
        eventProperties.put(AnalyticsConstants.NUMBER_OF_CLIPS, project.numberOfClips());
        eventProperties.put(AnalyticsConstants.VIDEO_LENGTH, project.getDuration());
    }

    public void trackVideoSharedSuperProperties() {
        JSONObject updateSuperProperties = new JSONObject();
        int numPreviousVideosShared;
        try {
            numPreviousVideosShared =
                    mixpanel.getSuperProperties().getInt(AnalyticsConstants.TOTAL_VIDEOS_SHARED);
        } catch (JSONException e) {
            numPreviousVideosShared = 0;
        }
        try {
            updateSuperProperties.put(AnalyticsConstants.TOTAL_VIDEOS_SHARED,
                    ++numPreviousVideosShared);
            mixpanel.registerSuperProperties(updateSuperProperties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void trackVideoShared(String socialNetworkId, Project project,int totalVideoShared) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(AnalyticsConstants.SOCIAL_NETWORK, socialNetworkId);
            eventProperties.put(AnalyticsConstants.VIDEO_LENGTH, project.getDuration());
            VideoResolution videoResolution = project.getProfile().getVideoResolution();
            eventProperties.put(AnalyticsConstants.RESOLUTION, videoResolution.getWidth() + "x"
                    + videoResolution.getHeight());
            eventProperties.put(AnalyticsConstants.NUMBER_OF_CLIPS, project.numberOfClips());
            eventProperties.put(AnalyticsConstants.TOTAL_VIDEOS_SHARED, totalVideoShared);

            Event trackingEvent = new Event(AnalyticsConstants.VIDEO_SHARED, eventProperties);
            this.trackEvent(trackingEvent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void trackVideoSharedUserTraits() {
        mixpanel.getPeople().increment(AnalyticsConstants.TOTAL_VIDEOS_SHARED, 1);
        mixpanel.getPeople().set(AnalyticsConstants.LAST_VIDEO_SHARED,
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new Date()));
    }

    public void trackVideoStartRecording() {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(AnalyticsConstants.RECORD_ACTION,
                AnalyticsConstants.RECORD_ACTION_START_RECORDING);
            Event trackingEvent = new Event(AnalyticsConstants.USER_INTERACTED, eventProperties);
            this.trackEvent(trackingEvent);
        } catch (JSONException e) {
            Log.d(TAG, "trackVideoStartRecording: error sending mixpanel USER_INTERACTED start " +
                "event");
            e.printStackTrace();
        }
    }

    public void trackVideoStopRecording() {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(AnalyticsConstants.RECORD_ACTION,
                AnalyticsConstants.RECORD_ACTION_STOP_RECORDING);
            Event trackingEvent = new Event(AnalyticsConstants.USER_INTERACTED, eventProperties);
            this.trackEvent(trackingEvent);
        } catch (JSONException e) {
            Log.d(TAG, "trackVideoStopRecording: error sending mixpanel USER_INTERACTED stop " +
                "event");
            e.printStackTrace();
        }
    }

    public void trackChangeCamera(boolean isFrontCameraSelected) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(AnalyticsConstants.RECORD_ACTION, isFrontCameraSelected ?
                AnalyticsConstants.RECORD_ACTION_CHANGE_CAMERA_BACK
                : AnalyticsConstants.RECORD_ACTION_CHANGE_CAMERA_FRONT);
            Event trackingEvent = new Event(AnalyticsConstants.USER_INTERACTED, eventProperties);
            this.trackEvent(trackingEvent);
        } catch (JSONException e) {
            Log.d(TAG, "trackChangeCamera: error sending mixpanel USER_INTERACTED flash " +
                "event");
            e.printStackTrace();
        }
    }
    public void trackChangeFlashMode(boolean isFlashCameraSelected){
      JSONObject eventProperties = new JSONObject();
      try {
        eventProperties.put(AnalyticsConstants.RECORD_ACTION, isFlashCameraSelected ?
            AnalyticsConstants.RECORD_ACTION_CHANGE_FLASH_OFF
            : AnalyticsConstants.RECORD_ACTION_CHANGE_FLASH_ON);
        Event trackingEvent = new Event(AnalyticsConstants.USER_INTERACTED, eventProperties);
        this.trackEvent(trackingEvent);
      } catch (JSONException e) {
        Log.d(TAG, "trackChangeFlashMode: error sending mixpanel USER_INTERACTED change camera " +
            "event");
        e.printStackTrace();
      }
    }

    public void trackTotalVideosRecordedSuperProperty() {
        JSONObject totalVideoRecordedSuperProperty = new JSONObject();
        int numPreviousVideosRecorded;
        try {
            numPreviousVideosRecorded =
                mixpanel.getSuperProperties().getInt(AnalyticsConstants.TOTAL_VIDEOS_RECORDED);
        } catch (JSONException e) {
            numPreviousVideosRecorded = 0;
        }
        try {
            totalVideoRecordedSuperProperty.put(AnalyticsConstants.TOTAL_VIDEOS_RECORDED,
                ++numPreviousVideosRecorded);
            mixpanel.registerSuperProperties(totalVideoRecordedSuperProperty);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void trackVideoRecorded(Project currentProject, int totalVideosRecorded) {
        JSONObject videoRecordedProperties = new JSONObject();
        VideoResolution.Resolution resolution = currentProject.getProfile().getResolution();
        try {
            videoRecordedProperties.put(AnalyticsConstants.VIDEO_LENGTH,
                    currentProject.getDuration());
            videoRecordedProperties.put(AnalyticsConstants.RESOLUTION, resolution.name());
            videoRecordedProperties.put(AnalyticsConstants.TOTAL_VIDEOS_RECORDED,
                totalVideosRecorded);
            Event trackingEvent = new Event(AnalyticsConstants.VIDEO_RECORDED,
                    videoRecordedProperties);
            this.trackEvent(trackingEvent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void trackVideoRecordedUserTraits() {
        mixpanel.getPeople().increment(AnalyticsConstants.TOTAL_VIDEOS_RECORDED, 1);
        mixpanel.getPeople().set(AnalyticsConstants.LAST_VIDEO_RECORDED,
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new Date()));
    }

    public void trackThemeAppDrawerChanged(boolean isDarkTheme) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(AnalyticsConstants.INTERACTION,
                AnalyticsConstants.ACTION_THEME_CHANGED);
            eventProperties.put(AnalyticsConstants.ACTION_THEME_SELECTED, isDarkTheme ?
                AnalyticsConstants.THEME_APP_ACTION_DARK :
                    AnalyticsConstants.THEME_APP_ACTION_LIGHT);
            eventProperties.put(AnalyticsConstants.THEME_CHANGE_SOURCE,
                AnalyticsConstants.THEME_CHANGE_SOURCE_DRAWER);
            Event trackingEvent = new Event(AnalyticsConstants.USER_INTERACTED, eventProperties);
            this.trackEvent(trackingEvent);
        } catch (JSONException e) {
            Log.d(TAG, "trackThemeAppChanged: error sending mixpanel USER_INTERACTED drawer theme "
                    + "dark event");
            e.printStackTrace();
        }
    }

    public void trackThemeAppSettingsChanged(boolean isDarkTheme) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(AnalyticsConstants.INTERACTION,
                AnalyticsConstants.ACTION_THEME_CHANGED);
            eventProperties.put(AnalyticsConstants.ACTION_THEME_SELECTED, isDarkTheme ?
                AnalyticsConstants.THEME_APP_ACTION_DARK :
                AnalyticsConstants.THEME_APP_ACTION_LIGHT);
            eventProperties.put(AnalyticsConstants.THEME_CHANGE_SOURCE,
                AnalyticsConstants.THEME_CHANGE_SOURCE_SETTINGS);
            Event trackingEvent = new Event(AnalyticsConstants.USER_INTERACTED, eventProperties);
            this.trackEvent(trackingEvent);
        } catch (JSONException e) {
            Log.d(TAG, "trackThemeAppChanged: error sending mixpanel USER_INTERACTED settings theme"
                    + " dark event ");
            e.printStackTrace();
        }
    }

    public void trackChangeResolution(String resolution) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(AnalyticsConstants.INTERACTION,
                AnalyticsConstants.ACTION_RESOLUTION_CHANGED);
            eventProperties.put(AnalyticsConstants.ACTION_RESOLUTION_SELECTED, resolution);
            Event trackingEvent = new Event(AnalyticsConstants.USER_INTERACTED, eventProperties);
            this.trackEvent(trackingEvent);
        } catch (JSONException e) {
            Log.d(TAG, "trackResolutionChanged: error sending mixpanel USER_INTERACTED camera "
                + " resolution event ");
            e.printStackTrace();
        }
    }

    public void trackChangeCameraInterface(String interfaceSelected) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(AnalyticsConstants.INTERACTION,
                AnalyticsConstants.ACTION_INTERFACE_CAMERA_CHANGED);
            eventProperties.put(AnalyticsConstants.ACTION_INTERFACE_CAMERA_SELECTED,
                    interfaceSelected);
            Event trackingEvent = new Event(AnalyticsConstants.USER_INTERACTED, eventProperties);
            this.trackEvent(trackingEvent);
        } catch (JSONException e) {
            Log.d(TAG, "trackCameraInterfaceChanged: error sending mixpanel USER_INTERACTED camera "
                + " interface event ");
            e.printStackTrace();
        }
    }

    public void trackChangeFrameRate(String frameRate) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(AnalyticsConstants.INTERACTION,
                AnalyticsConstants.ACTION_FRAME_RATE_CHANGED);
            eventProperties.put(AnalyticsConstants.ACTION_FRAME_RATE_SELECTED, frameRate);
            Event trackingEvent = new Event(AnalyticsConstants.USER_INTERACTED, eventProperties);
            this.trackEvent(trackingEvent);
        } catch (JSONException e) {
            Log.d(TAG, "trackFrameRateChanged: error sending mixpanel USER_INTERACTED camera "
                + " frame rate event ");
            e.printStackTrace();
        }
    }

    public void trackChangeQuality(String quality) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(AnalyticsConstants.INTERACTION,
                AnalyticsConstants.ACTION_QUALITY_CHANGED);
            eventProperties.put(AnalyticsConstants.ACTION_QUALITY_SELECTED, quality);
            Event trackingEvent = new Event(AnalyticsConstants.USER_INTERACTED, eventProperties);
            this.trackEvent(trackingEvent);
        } catch (JSONException e) {
            Log.d(TAG, "trackQualityChanged: error sending mixpanel USER_INTERACTED camera "
                + " quality event ");
            e.printStackTrace();
        }
    }

    public void trackResolutionUserTraits(String value) {
        mixpanel.getPeople().set(AnalyticsConstants.RESOLUTION, value.toLowerCase());
    }

    public void trackQualityUserTraits(String value) {
        mixpanel.getPeople().set(AnalyticsConstants.QUALITY, value.toLowerCase());
    }

    public void trackFrameRateUserTraits(String value) {
        mixpanel.getPeople().set(AnalyticsConstants.FRAME_RATE, value.toLowerCase());
    }


    public void trackUpdateUserName(String userName) {
        mixpanel.getPeople().identify(mixpanel.getDistinctId());
        mixpanel.getPeople().set(MIXPANEL_USERNAME_ID, userName);
    }

    public void trackUpdateUserEmail(String email) {
        mixpanel.getPeople().identify(mixpanel.getDistinctId());
        mixpanel.getPeople().set(MIXPANEL_ACCOUNT_EMAIL_ID, email);
        mixpanel.getPeople().setOnce(MIXPANEL_EMAIL_ID, email);

    }

    public void trackProjectInfo(Project project) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(AnalyticsConstants.PROJECT_ACTION,
                AnalyticsConstants.PROJECT_ACTION_INFO);
            ProjectInfo projectInfo = project.getProjectInfo();
            eventProperties.put(AnalyticsConstants.PROJECT_ACTION_TITLE, projectInfo.getTitle());
            eventProperties.put(AnalyticsConstants.PROJECT_ACTION_DESCRIPTION, projectInfo.getDescription());
            for(String productType: projectInfo.getProductTypeList()) {
                eventProperties.put(AnalyticsConstants.PROJECT_ACTION_PRODUCT_TYPE, productType);
            }
            Event trackingEvent = new Event(AnalyticsConstants.PROJECT_EDITED, eventProperties);
            this.trackEvent(trackingEvent);
        } catch (JSONException e) {
            Log.d(TAG, "trackProjectInfo: error sending mixpanel PROJECT_EDITED project info event");
            e.printStackTrace();
        }
    }

    /*** Mix panel methods, flush, timeEventStart, timeEventPause ***/
    public void flush() {
        mixpanel.flush();
    }

    public void timeEventStart() {
        mixpanel.timeEvent(AnalyticsConstants.TIME_IN_ACTIVITY);
    }

    public void timeEventPause() {
        JSONObject activityProperties = new JSONObject();
        try {
            activityProperties.put(AnalyticsConstants.ACTIVITY, getClass().getSimpleName());
            mixpanel.track(AnalyticsConstants.TIME_IN_ACTIVITY, activityProperties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static class Event {
        private String name;
        private JSONObject properties;

        public Event(String name, JSONObject properties) {
            this.name = name;
            this.properties = properties;
        }

        public String getName() {
            return name;
        }

        public JSONObject getProperties() {
            return properties;
        }
    }
}
