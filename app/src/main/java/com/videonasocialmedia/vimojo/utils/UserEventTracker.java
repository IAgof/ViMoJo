package com.videonasocialmedia.vimojo.utils;

import android.util.Log;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jliarte on 7/06/16.
 */
public class UserEventTracker {
    private final String TAG = getClass().getSimpleName();
    private static UserEventTracker userEventTrackerInstance;
    public MixpanelAPI mixpanel;

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

    public void trackClipsReordered(Project project) {
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put(AnalyticsConstants.EDIT_ACTION, AnalyticsConstants.EDIT_ACTION_REORDER);
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
            eventProperties.put(AnalyticsConstants.EDIT_ACTION, AnalyticsConstants.EDIT_ACTION_TRIM);
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
            eventProperties.put(AnalyticsConstants.EDIT_ACTION, AnalyticsConstants.EDIT_ACTION_DUPLICATE);
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
            eventProperties.put(AnalyticsConstants.EDIT_ACTION, AnalyticsConstants.EDIT_ACTION_TEXT);
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
            eventProperties.put(AnalyticsConstants.EDIT_ACTION, AnalyticsConstants.EDIT_ACTION_MUSIC_SET);
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

    public void addProjectEventProperties(Project project, JSONObject eventProperties) throws JSONException {
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
            eventProperties.put(AnalyticsConstants.RESOLUTION, videoResolution.getWidth() + "x" + videoResolution.getHeight());
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
            videoRecordedProperties.put(AnalyticsConstants.VIDEO_LENGTH, currentProject.getDuration());
            videoRecordedProperties.put(AnalyticsConstants.RESOLUTION, resolution.name());
            videoRecordedProperties.put(AnalyticsConstants.TOTAL_VIDEOS_RECORDED,
                totalVideosRecorded);
            Event trackingEvent = new Event(AnalyticsConstants.VIDEO_RECORDED, videoRecordedProperties);
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
