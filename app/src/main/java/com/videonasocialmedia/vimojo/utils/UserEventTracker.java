package com.videonasocialmedia.vimojo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.utils.tracker.MixpanelTracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jliarte on 7/06/16.
 */
public class UserEventTracker {
    private final String LOG_TAG = getClass().getSimpleName();
    private static UserEventTracker userEventTrackerInstance;
    public static final String MIXPANEL_EMAIL_ID = "$email";
    public static final String MIXPANEL_ACCOUNT_EMAIL_ID = "$account_email";
    public static final String MIXPANEL_USERNAME_ID = "$username";
    private Context context;
    protected ArrayList<TrackerIntegration> trackers;

    protected UserEventTracker(Context context, ArrayList<TrackerIntegration.Factory> factories) {
        this.trackers = new ArrayList<>();
        this.context = context;
        initializeTrackers(factories);
    }

    private void initializeTrackers(ArrayList<TrackerIntegration.Factory> factories) {
        for (TrackerIntegration.Factory factory : factories) {
            TrackerIntegration<?> tracker = factory.create(this);
            if (tracker != null) {
                this.trackers.add(tracker);
            }
        }
    }

    public static void setSingletonInstance(UserEventTracker singletonInstance) {
        UserEventTracker.userEventTrackerInstance = singletonInstance;
    }

    public static UserEventTracker getInstance() {
        return userEventTrackerInstance;
    }

    public static void clear() {
        userEventTrackerInstance = null;
    }

    public Context getApplication() {
        return context;
    }

    private void identify(String id) {
        for (TrackerIntegration integration : trackers) {
            integration.identify(id);
        }
    }

    protected void trackEvent(Event event) {
        if (event != null) {
            for (TrackerIntegration integration : trackers) {
                integration.track(event);
            }
        }
    }

    private void registerSuperProperties(JSONObject superProperties) {
        if (superProperties != null) {
            for (TrackerIntegration integration : trackers) {
                integration.registerSuperProperties(superProperties);
            }
        }
    }

    private void registerSuperPropertiesOnce(JSONObject superProperties) {
        if (superProperties != null) {
            for (TrackerIntegration integration : trackers) {
                integration.registerSuperPropertiesOnce(superProperties);
            }
        }
    }

    private void setUserProperties(String propertyName, String propertyValue) {
        for (TrackerIntegration integration : trackers) {
            integration.setUserProperties(propertyName, propertyValue);
        }
    }

    private void setUserProperties(String propertyName, boolean propertyValue) {
        for (TrackerIntegration integration : trackers) {
            integration.setUserProperties(propertyName, propertyValue);
        }
    }

    private void setUserProperties(JSONObject userProperties) {
        if (userProperties != null) {
            for (TrackerIntegration integration : trackers) {
                integration.setUserProperties(userProperties);
            }
        }
    }

    private void setUserPropertiesOnce(String propertyName, String propertyValue) {
        for (TrackerIntegration integration : trackers) {
            integration.setUserPropertiesOnce(propertyName, propertyValue);
        }
    }

    private void setUserPropertiesOnce(JSONObject userProperties) {
        if (userProperties != null) {
            for (TrackerIntegration integration : trackers) {
                integration.setUserPropertiesOnce(userProperties);
            }
        }
    }

    private void incrementUserProperty(String propertyName, int increment) {
        for (TrackerIntegration integration : trackers) {
            integration.incrementUserProperty(propertyName, increment);
        }
    }

    public void flush() {
        for (TrackerIntegration integration : trackers) {
            integration.flush();
        }
    }

    private int getMixpanelSuperProperty(String propertyName, int defValue) {
        int value = defValue;
        for (TrackerIntegration trackerIntegration : trackers) {
            if (trackerIntegration instanceof MixpanelTracker) {
                value = trackerIntegration.getSuperProperty(propertyName, defValue);
            }
        }
        return value;
    }

    private String getMixpanelSuperProperty(String propertyName, String defValue) {
        String value = defValue;
        for (TrackerIntegration trackerIntegration : trackers) {
            if (trackerIntegration instanceof MixpanelTracker) {
                value = trackerIntegration.getSuperProperty(propertyName, defValue);
            }
        }
        return value;
    }

    public void startView(Class<? extends VimojoActivity> activity) {
        for (TrackerIntegration integration : trackers) {
            integration.startView(activity);
        }
    }

    public void endView(Class<? extends VimojoActivity> activity) {
        for (TrackerIntegration integration : trackers) {
            integration.endView(activity);
        }
    }

    /***** App startup - from initAppActivity ******/

    public void trackUserProfileGeneralTraits() {
        incrementUserProperty(AnalyticsConstants.APP_USE_COUNT, 1);
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
            setUserProperties(userProfileProperties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void trackAppStartupProperties(boolean firstTime) {
        JSONObject appStartupSuperProperties = new JSONObject();
        int appUseCount = getMixpanelSuperProperty(AnalyticsConstants.APP_USE_COUNT, 0);
        try {
            appStartupSuperProperties.put(AnalyticsConstants.APP_USE_COUNT, ++appUseCount);
            appStartupSuperProperties.put(AnalyticsConstants.FIRST_TIME, firstTime);
            appStartupSuperProperties.put(AnalyticsConstants.APP, "ViMoJo");
            appStartupSuperProperties.put(AnalyticsConstants.FLAVOR, BuildConfig.FLAVOR);
            this.registerSuperProperties(appStartupSuperProperties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void trackUserProfile(String androidId) {
        this.identify(androidId);
        JSONObject userProfileProperties = new JSONObject();
        try {
            userProfileProperties.put(AnalyticsConstants.CREATED,
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new Date()));
            this.setUserPropertiesOnce(userProfileProperties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void trackCreatedSuperProperty() {
        JSONObject createdSuperProperty = new JSONObject();
        try {
            createdSuperProperty.put(AnalyticsConstants.CREATED,
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new Date()));
            this.registerSuperPropertiesOnce(createdSuperProperty);
        } catch (JSONException e) {
            Log.e("ANALYTICS", "Error sending created super property");
        }
    }

    public void trackAppStartup(String initState) {
        JSONObject initAppProperties = new JSONObject();
        try {
            initAppProperties.put(AnalyticsConstants.TYPE, AnalyticsConstants.TYPE_ORGANIC);
            initAppProperties.put(AnalyticsConstants.INIT_STATE, initState);
            Event event = new Event(AnalyticsConstants.APP_STARTED, initAppProperties); // TODO(jliarte): 28/08/18 std firebase event name??
            this.trackEvent(event);
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
            Log.d(LOG_TAG, "trackClipsReordered: error sending mixpanel VIDEO_EDITED reorder event");
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
            Log.d(LOG_TAG, "trackClipTrimmed: error sending mixpanel VIDEO_EDITED trim event");
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
            Log.d(LOG_TAG, "trackClipSplitted: error sending mixpanel VIDEO_EDITED split event");
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
            Log.d(LOG_TAG, "trackClipDuplicated: error sending mixpanel VIDEO_EDITED duplicate event");
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
            Log.d(LOG_TAG, "trackClipDuplicated: error sending mixpanel VIDEO_EDITED duplicate event");
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
        int numPreviousVideosShared =
                    getMixpanelSuperProperty(AnalyticsConstants.TOTAL_VIDEOS_SHARED, 0);
        try {
            updateSuperProperties.put(AnalyticsConstants.TOTAL_VIDEOS_SHARED,
                    ++numPreviousVideosShared);
            this.registerSuperProperties(updateSuperProperties);
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
        incrementUserProperty(AnalyticsConstants.TOTAL_VIDEOS_SHARED, 1);
        setUserProperties(AnalyticsConstants.LAST_VIDEO_SHARED,
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
            Log.d(LOG_TAG, "trackVideoStartRecording: error sending mixpanel USER_INTERACTED start " +
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
            Log.d(LOG_TAG, "trackVideoStopRecording: error sending mixpanel USER_INTERACTED stop " +
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
            Log.d(LOG_TAG, "trackChangeCamera: error sending mixpanel USER_INTERACTED flash " +
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
        Log.d(LOG_TAG, "trackChangeFlashMode: error sending mixpanel USER_INTERACTED change camera " +
            "event");
        e.printStackTrace();
      }
    }

    public void trackTotalVideosRecordedSuperProperty() {
        JSONObject totalVideoRecordedSuperProperty = new JSONObject();
        int numPreviousVideosRecorded =
                getMixpanelSuperProperty(AnalyticsConstants.TOTAL_VIDEOS_RECORDED, 0);
        try {
            totalVideoRecordedSuperProperty.put(AnalyticsConstants.TOTAL_VIDEOS_RECORDED,
                ++numPreviousVideosRecorded);
            this.registerSuperProperties(totalVideoRecordedSuperProperty);
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
        incrementUserProperty(AnalyticsConstants.TOTAL_VIDEOS_RECORDED, 1);
        setUserProperties(AnalyticsConstants.LAST_VIDEO_RECORDED,
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
            Log.d(LOG_TAG, "trackThemeAppChanged: error sending mixpanel USER_INTERACTED drawer theme "
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
            Log.d(LOG_TAG, "trackThemeAppChanged: error sending mixpanel USER_INTERACTED settings theme"
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
            Log.d(LOG_TAG, "trackResolutionChanged: error sending mixpanel USER_INTERACTED camera "
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
            Log.d(LOG_TAG, "trackCameraInterfaceChanged: error sending mixpanel USER_INTERACTED camera "
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
            Log.d(LOG_TAG, "trackFrameRateChanged: error sending mixpanel USER_INTERACTED camera "
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
            Log.d(LOG_TAG, "trackQualityChanged: error sending mixpanel USER_INTERACTED camera "
                + " quality event ");
            e.printStackTrace();
        }
    }

    public void trackResolutionUserTraits(String value) {
        this.setUserProperties(AnalyticsConstants.RESOLUTION, value.toLowerCase());
    }

    public void trackQualityUserTraits(String value) {
        this.setUserProperties(AnalyticsConstants.QUALITY, value.toLowerCase());
    }

    public void trackFrameRateUserTraits(String value) {
        this.setUserProperties(AnalyticsConstants.FRAME_RATE, value.toLowerCase());
    }


    public void trackUpdateUserName(String userName) {
        // TODO(jliarte): 28/08/18 mixpanel.getDistinctId
//        this.identify(mixpanel.getDistinctId());
        setUserProperties(MIXPANEL_USERNAME_ID, userName);
    }

    public void trackUpdateUserEmail(String email) {
        // TODO(jliarte): 28/08/18 mixpanel.getDistinctId
//        this.identify(mixpanel.getDistinctId());
        setUserProperties(MIXPANEL_ACCOUNT_EMAIL_ID, email);
        setUserPropertiesOnce(MIXPANEL_EMAIL_ID, email);

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
            Log.d(LOG_TAG, "trackProjectInfo: error sending mixpanel PROJECT_EDITED project info event");
            e.printStackTrace();
        }
    }

    public String getUserFirstRun() {
        return getMixpanelSuperProperty(AnalyticsConstants.CREATED, "");
    }

    public void trackPrehistoricUser() {
        this.setUserProperties(AnalyticsConstants.PREHISTERIC, true);
        JSONObject superProps = new JSONObject();
        try {
            superProps.put(AnalyticsConstants.PREHISTERIC, true);
            this.registerSuperProperties(superProps);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error setting prehisteric super property");
        }
    }

    /** Fluent API for creating {@link UserEventTracker} instances. */
    public static class Builder {
        private final Context context;
        private final ArrayList<TrackerIntegration.Factory> trackerFactories;

        public Builder(Context context) {
            this.trackerFactories = new ArrayList<>();
            this.context = context;
        }

        public Builder use(TrackerIntegration.Factory factory) {
            trackerFactories.add(factory);
            return this;
        }

        public UserEventTracker build() {
            return new UserEventTracker(context, trackerFactories);
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

    public abstract static class TrackerIntegration<T> {
        public abstract void identify(String id);

        public abstract void track(Event event);

        public abstract void setUserProperties(JSONObject userProperties);

        public abstract void setUserProperties(String propertyName, String propertyValue);

        public abstract void setUserProperties(String propertyName, boolean propertyValue);

        public abstract void setUserPropertiesOnce(JSONObject userProperties);

        public abstract void setUserPropertiesOnce(String propertyName, String propertyValue);

        public abstract void registerSuperProperties(JSONObject superProperties);

        public abstract void registerSuperPropertiesOnce(JSONObject superProperties);

        public abstract int getSuperProperty(String propertyName, int defValue);

        public abstract String getSuperProperty(String propertyName, String defValue);

        public abstract void incrementUserProperty(String propertyName, int increment);

        public abstract void flush();

        public abstract void startView(Class<? extends VimojoActivity> activity);

        public abstract void endView(Class<? extends VimojoActivity> activity);

        public interface Factory {
            /**
             * Attempts to create a tracking integration. Returns integration, or null if failed.
             */
            TrackerIntegration<?> create(UserEventTracker userEventTracker);
        }
    }
}
