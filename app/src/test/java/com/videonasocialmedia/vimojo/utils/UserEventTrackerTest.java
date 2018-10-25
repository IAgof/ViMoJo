package com.videonasocialmedia.vimojo.utils;

import android.content.Context;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.model.sources.ProductTypeProvider;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by jliarte on 7/06/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class UserEventTrackerTest {

    private Project currentProject;
    @Mock Context mockedContext;
    @Mock UserEventTracker.TrackerIntegration mockedTracker;
    @Mock UserEventTracker.TrackerIntegration.Factory mockedFactoryFirebase;
    @Mock UserEventTracker.TrackerIntegration.Factory mockedFactoryMixpanel;


    @Before
    public void injectMocks() {
        MockitoAnnotations.initMocks(this);
        getAProject();
    }

    @After
    public void tearDown() {
        UserEventTracker.clear();
    }


    @Test
    public void getInstanceReturnsATrackerObject() {
        UserEventTracker userEventTracker = getMockedUserEventTracker();

        assertThat("UserEventTracker is set", userEventTracker, notNullValue());
        assertThat("UserEventTracker object type", userEventTracker,
            instanceOf(UserEventTracker.class));
    }

    @Test
    public void getInstanceReturnsSameTrackerObject() {
        UserEventTracker userEventTracker = UserEventTracker.getInstance();
        UserEventTracker userEventTracker2 = UserEventTracker.getInstance();

        assertThat("Same instances", userEventTracker, is(userEventTracker2));
    }

    @Test
    public void trackEventCallsTracker() {
        UserEventTracker mockedUserEventTracker = Mockito.spy(getMockedUserEventTracker());
        UserEventTracker.Event event = new UserEventTracker.Event(AnalyticsConstants.VIDEO_EDITED,
            null);
        ArrayList<UserEventTracker.TrackerIntegration> trackers = new ArrayList<>();
        trackers.add(mockedTracker);
        mockedUserEventTracker.trackers = trackers;

        mockedUserEventTracker.trackEvent(event);

        Mockito.verify(mockedTracker).track(event);
    }

    @Captor
    ArgumentCaptor<UserEventTracker.Event> eventCaptor;

    @Test
    public void trackVideoStartRecordingCallsTrackWithEventNameAndProperties()
            throws JSONException {
        UserEventTracker userEventTracker = Mockito.spy(getMockedUserEventTracker());

        userEventTracker.trackVideoStartRecording();

        Mockito.verify(userEventTracker).trackEvent(eventCaptor.capture());
        UserEventTracker.Event trackedEvent = eventCaptor.getValue();
        assertThat(trackedEvent.getName(), is(AnalyticsConstants.USER_INTERACTED));
        assertThat(trackedEvent.getProperties().getString(AnalyticsConstants.RECORD_ACTION),
            is(AnalyticsConstants.RECORD_ACTION_START_RECORDING));
    }

    @Test
    public void trackVideoStopRecordingCallsTrackWithEventNameAndProperties() throws JSONException {
        UserEventTracker userEventTracker = Mockito.spy(getMockedUserEventTracker());

        userEventTracker.trackVideoStopRecording();

        Mockito.verify(userEventTracker).trackEvent(eventCaptor.capture());
        UserEventTracker.Event trackedEvent = eventCaptor.getValue();
        assertThat(trackedEvent.getName(), is(AnalyticsConstants.USER_INTERACTED));
        assertThat(trackedEvent.getProperties().getString(AnalyticsConstants.RECORD_ACTION),
            is(AnalyticsConstants.RECORD_ACTION_STOP_RECORDING));
    }

    @Test
    public void trackChangeCameraCallsTrackWithEventNameAndProperties() throws JSONException {
        UserEventTracker userEventTracker = Mockito.spy(getMockedUserEventTracker());
        boolean isFrontCameraSelected = false;

        userEventTracker.trackChangeCamera(isFrontCameraSelected);

        Mockito.verify(userEventTracker).trackEvent(eventCaptor.capture());
        UserEventTracker.Event trackedEvent = eventCaptor.getValue();
        assertThat(trackedEvent.getName(), is(AnalyticsConstants.USER_INTERACTED));
        assertThat(trackedEvent.getProperties().getString(AnalyticsConstants.RECORD_ACTION),
            is(AnalyticsConstants.RECORD_ACTION_CHANGE_CAMERA_FRONT));
    }

    @Test
    public void trackChangeFlashModeCallsTrackWithEventNameAndProperties() throws JSONException {
        UserEventTracker userEventTracker = Mockito.spy(getMockedUserEventTracker());
        boolean isFlashSelected = false;

        userEventTracker.trackChangeFlashMode(isFlashSelected);

        Mockito.verify(userEventTracker).trackEvent(eventCaptor.capture());
        UserEventTracker.Event trackedEvent = eventCaptor.getValue();
        assertThat(trackedEvent.getName(), is(AnalyticsConstants.USER_INTERACTED));
        assertThat(trackedEvent.getProperties().getString(AnalyticsConstants.RECORD_ACTION),
            is(AnalyticsConstants.RECORD_ACTION_CHANGE_FLASH_ON));
    }

    @Test
    public void trackVideoRecordedCallsTrackWithEventNameAndProperties() throws JSONException {
        UserEventTracker userEventTracker = Mockito.spy(getMockedUserEventTracker());
        int totalVideosRecorded = 2;

        userEventTracker.trackVideoRecorded(currentProject, totalVideosRecorded);

        Mockito.verify(userEventTracker).trackEvent(eventCaptor.capture());
        UserEventTracker.Event trackedEvent = eventCaptor.getValue();
        assertThat(trackedEvent.getProperties().getInt(AnalyticsConstants.VIDEO_LENGTH),
            is(currentProject.getDuration()));
        assertThat(trackedEvent.getProperties().getString(AnalyticsConstants.RESOLUTION),
            is(currentProject.getProfile().getResolution().name()));
        assertThat(trackedEvent.getProperties().getInt(AnalyticsConstants.TOTAL_VIDEOS_RECORDED),
            is(totalVideosRecorded));
    }

    @Test
    public void trackClipsReorderedCallsTrackWithEventNameAndProperties() throws JSONException {
        UserEventTracker userEventTracker = Mockito.spy(getMockedUserEventTracker());

        userEventTracker.trackClipsReordered(currentProject);

        Mockito.verify(userEventTracker).trackEvent(eventCaptor.capture());
        UserEventTracker.Event trackedEvent = eventCaptor.getValue();
        assertThat(trackedEvent.getName(), is(AnalyticsConstants.VIDEO_EDITED));
        assertThat(trackedEvent.getProperties().getString(AnalyticsConstants.EDIT_ACTION),
                is(AnalyticsConstants.EDIT_ACTION_REORDER));
        assertEvenPropertiesIncludeProjectCommonProperties(trackedEvent.getProperties(),
            currentProject);
    }

    @Test
    public void trackClipTrimmedCallsTrackWithEventNameAndProperties() throws JSONException {
        UserEventTracker userEventTracker = Mockito.spy(getMockedUserEventTracker());

        userEventTracker.trackClipTrimmed(currentProject);

        Mockito.verify(userEventTracker).trackEvent(eventCaptor.capture());
        UserEventTracker.Event trackedEvent = eventCaptor.getValue();
        assertThat(trackedEvent.getName(), is(AnalyticsConstants.VIDEO_EDITED));
        assertThat(trackedEvent.getProperties().getString(AnalyticsConstants.EDIT_ACTION),
                is(AnalyticsConstants.EDIT_ACTION_TRIM));
        assertEvenPropertiesIncludeProjectCommonProperties(trackedEvent.getProperties(),
            currentProject);
    }

    @Test
    public void trackClipSplitCallsTrackWithEventNameAndProperties() throws JSONException {
        UserEventTracker userEventTracker
            = Mockito.spy(getMockedUserEventTracker());

        userEventTracker.trackClipSplitted(currentProject);

        Mockito.verify(userEventTracker).trackEvent(eventCaptor.capture());
        UserEventTracker.Event trackedEvent = eventCaptor.getValue();
        assertThat(trackedEvent.getName(), is(AnalyticsConstants.VIDEO_EDITED));
        assertThat(trackedEvent.getProperties().getString(AnalyticsConstants.EDIT_ACTION),
                is(AnalyticsConstants.EDIT_ACTION_SPLIT));
        assertEvenPropertiesIncludeProjectCommonProperties(trackedEvent.getProperties(),
            currentProject);
    }

    @Test
    public void trackClipDuplicatedCallsTrackWithEventNameAndProperties() throws JSONException {
        UserEventTracker userEventTracker = Mockito.spy(getMockedUserEventTracker());
        int copies = 3;

        userEventTracker.trackClipDuplicated(copies, currentProject);

        Mockito.verify(userEventTracker).trackEvent(eventCaptor.capture());
        UserEventTracker.Event trackedEvent = eventCaptor.getValue();
        assertThat(trackedEvent.getName(), is(AnalyticsConstants.VIDEO_EDITED));
        assertThat(trackedEvent.getProperties().getString(AnalyticsConstants.EDIT_ACTION),
                is(AnalyticsConstants.EDIT_ACTION_DUPLICATE));
        assertThat(trackedEvent.getProperties().getInt(AnalyticsConstants.NUMBER_OF_DUPLICATES),
                is(copies));
        assertEvenPropertiesIncludeProjectCommonProperties(trackedEvent.getProperties(),
            currentProject);
    }

    @Test
    public void trackClipAddTextToClipCallsTrackWithEventAndProperties() throws JSONException {
        UserEventTracker userEventTracker = Mockito.spy(getMockedUserEventTracker());
        String position = "Center";
        int lengthText = 27;
        boolean isShadowChecked = false;

        userEventTracker.trackClipAddedText(position, lengthText, isShadowChecked, currentProject);

        Mockito.verify(userEventTracker).trackEvent(eventCaptor.capture());
        UserEventTracker.Event trackedEvent = eventCaptor.getValue();

        assertThat(trackedEvent.getName(), is(AnalyticsConstants.VIDEO_EDITED));
        assertThat(trackedEvent.getProperties().getString(AnalyticsConstants.EDIT_ACTION),
                is(AnalyticsConstants.EDIT_ACTION_TEXT));
        assertThat(trackedEvent.getProperties().getString(AnalyticsConstants.TEXT_POSITION),
                is(position));
        assertThat(trackedEvent.getProperties().getInt(AnalyticsConstants.TEXT_LENGTH),
                is(lengthText));
        assertThat(trackedEvent.getProperties().getBoolean(AnalyticsConstants.TEXT_SHADOW),
            is(isShadowChecked));
        assertEvenPropertiesIncludeProjectCommonProperties(trackedEvent.getProperties(),
            currentProject);

    }

    @Test
    public void trackMusicSetCallsTrackWithEventNameAndProperties()
            throws IllegalItemOnTrack, JSONException {
        UserEventTracker userEventTracker = Mockito.spy(getMockedUserEventTracker());
        Music music = new Music(1, "Music title", 2, 3, "Music Author","", 0);
        currentProject.getAudioTracks().get(0).insertItem(music);

        userEventTracker.trackMusicSet(currentProject);

        Mockito.verify(userEventTracker).trackEvent(eventCaptor.capture());
        UserEventTracker.Event trackedEvent = eventCaptor.getValue();
        assertThat(trackedEvent.getName(), is(AnalyticsConstants.VIDEO_EDITED));
        assertThat(trackedEvent.getProperties().getString(AnalyticsConstants.EDIT_ACTION),
                is(AnalyticsConstants.EDIT_ACTION_MUSIC_SET));
        assertThat(trackedEvent.getProperties().getString(AnalyticsConstants.MUSIC_TITLE),
                is(music.getTitle()));
        assertEvenPropertiesIncludeProjectCommonProperties(trackedEvent.getProperties(),
            currentProject);
    }

    @Test
    public void trackMusicSetTracksEmptyTitleIfMusicIsNull() throws JSONException {
        UserEventTracker userEventTracker = Mockito.spy(getMockedUserEventTracker());

        userEventTracker.trackMusicSet(currentProject);

        Mockito.verify(userEventTracker).trackEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getProperties().getString(AnalyticsConstants.MUSIC_TITLE),
                isEmptyString());
    }

    @Test
    public void trackVoiceOverSetCallsTrackWithEventNameAndProperties() throws IllegalItemOnTrack,
            JSONException {
        UserEventTracker userEventTracker = Mockito.spy(getMockedUserEventTracker());
        final float defaultVolume = 0.5f;
        int defaultDuration = 100;
        String mediaPath = "somePath";
        final Music voiceOver = new Music(mediaPath, defaultVolume, defaultDuration);
        currentProject.getAudioTracks().get(0).insertItem(voiceOver);

        userEventTracker.trackVoiceOverSet(currentProject);

        Mockito.verify(userEventTracker).trackEvent(eventCaptor.capture());
        UserEventTracker.Event trackedEvent = eventCaptor.getValue();
        assertThat(trackedEvent.getName(), is(AnalyticsConstants.VIDEO_EDITED));
        assertThat(trackedEvent.getProperties().getString(AnalyticsConstants.EDIT_ACTION),
                is(AnalyticsConstants.EDIT_ACTION_VOICE_OVER_SET));
        assertEvenPropertiesIncludeProjectCommonProperties(trackedEvent.getProperties(),
            currentProject);
    }

    @Test
    public void trackVideoSharedPropertiesCallsTrackWithEventNameAndProperties()
            throws JSONException {
        UserEventTracker userEventTracker = Mockito.spy(getMockedUserEventTracker());

        String socialNetworkId = "SocialNetwork";
        Profile profile = currentProject.getProfile();
        VideoResolution videoResolution = new VideoResolution(profile.getResolution());

        int totalVideoShared = 2;
        userEventTracker.trackVideoShared(socialNetworkId, currentProject,totalVideoShared);

        Mockito.verify(userEventTracker).trackEvent(eventCaptor.capture());
        UserEventTracker.Event trackedEvent = eventCaptor.getValue();
        assertThat(trackedEvent.getProperties().getString(AnalyticsConstants.SOCIAL_NETWORK),
                is(socialNetworkId));
        assertThat(trackedEvent.getProperties().getInt(AnalyticsConstants.VIDEO_LENGTH),
                is(currentProject.getDuration()));
        assertThat(trackedEvent.getProperties().getString(AnalyticsConstants.RESOLUTION),
                is(videoResolution.getWidth() + "x" + videoResolution.getHeight()));
        assertThat(trackedEvent.getProperties().getInt(AnalyticsConstants.NUMBER_OF_CLIPS),
                is(currentProject.numberOfClips()));
        assertThat(trackedEvent.getProperties().getInt(AnalyticsConstants.TOTAL_VIDEOS_SHARED),
                is(totalVideoShared));
    }

    public void assertEvenPropertiesIncludeProjectCommonProperties(
            JSONObject eventProperties, Project videonaProject) throws JSONException {
        assertThat(eventProperties.getInt(AnalyticsConstants.NUMBER_OF_CLIPS),
                is(videonaProject.numberOfClips()));
        assertThat(eventProperties.getInt(AnalyticsConstants.VIDEO_LENGTH),
                is(videonaProject.getDuration()));
    }

    @Test
    public void trackProjectInfoCallsTrackWithEventNameAndProperties() throws JSONException {
        UserEventTracker userEventTracker = Mockito.spy(getMockedUserEventTracker());
        String title = "title";
        String description = "description";
        List<String> productTypes = new ArrayList<>();
        productTypes.add(ProductTypeProvider.Types.LIVE_ON_TAPE.name());
        ProjectInfo projectInfo = new ProjectInfo(title, description, productTypes);
        currentProject.setProjectInfo(projectInfo);

        userEventTracker.trackProjectInfo(currentProject);

        Mockito.verify(userEventTracker).trackEvent(eventCaptor.capture());
        UserEventTracker.Event trackedEvent = eventCaptor.getValue();
        assertThat(trackedEvent.getProperties().getString(AnalyticsConstants.PROJECT_ACTION_TITLE),
            is(title));
        assertThat(trackedEvent.getProperties().getString(AnalyticsConstants.PROJECT_ACTION_DESCRIPTION),
            is(description));
        assertThat(trackedEvent.getProperties().getString(AnalyticsConstants.PROJECT_ACTION_PRODUCT_TYPE),
            is(productTypes.get(0)));
    }

    public void getAProject() {
        Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720,
                VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
        List<String> productType = new ArrayList<>();
        ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
        currentProject = new Project(projectInfo, "/path", "private/path", compositionProfile);
    }
    
    private UserEventTracker getMockedUserEventTracker() {
        ArrayList<UserEventTracker.TrackerIntegration.Factory> factories = new ArrayList<>();
        factories.add(mockedFactoryFirebase);
        factories.add(mockedFactoryMixpanel);
        return  new UserEventTracker(mockedContext, factories);
    }
}
