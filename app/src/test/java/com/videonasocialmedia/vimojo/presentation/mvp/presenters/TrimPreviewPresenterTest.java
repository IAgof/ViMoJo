package com.videonasocialmedia.vimojo.presentation.mvp.presenters;


import android.support.annotation.NonNull;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.trim.domain.ModifyVideoDurationUseCase;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;

import com.videonasocialmedia.vimojo.trim.presentation.mvp.views.TrimView;
import com.videonasocialmedia.vimojo.trim.presentation.mvp.presenters.TrimPreviewPresenter;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Mockito.verify;

/**
 * Created by jliarte on 10/06/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class TrimPreviewPresenterTest {
    @InjectMocks private TrimPreviewPresenter trimPreviewPresenter;
    @Mock private ModifyVideoDurationUseCase modifyVideoDurationUseCase;
    @Mock private TrimView mockedTrimView;
    @Mock private MixpanelAPI mockedMixpanelAPI;
    @Mock private UserEventTracker mockedUserEventTracker;

    @Mock GetMediaListFromProjectUseCase mockedGetMediaListFromProjectUseCase;
    @Mock ModifyVideoDurationUseCase mockedModifyVideoDurationUseCase;

    @Before
    public void injectMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
        Project.getInstance(null, null, null).clear();
    }

    @Test
    public void constructorSetsUserTracker() {
        UserEventTracker userEventTracker = UserEventTracker.getInstance(mockedMixpanelAPI);
        TrimPreviewPresenter trimPreviewPresenter = new TrimPreviewPresenter(mockedTrimView,
            userEventTracker, mockedGetMediaListFromProjectUseCase,
            mockedModifyVideoDurationUseCase);

        assertThat(trimPreviewPresenter.userEventTracker, is(userEventTracker));
    }

    @Test
    public void constructorSetsCurrentProject() {
        TrimPreviewPresenter trimPreviewPresenter = getTrimPreviewPresenter();
        Project videonaProject = getAProject();

        assertThat(trimPreviewPresenter.currentProject, is(videonaProject));
    }


    @Test
    public void setTrimCallsTracking() {
        Project videonaProject = getAProject();

        //trimPreviewPresenter.setTrim(0, 10);
        trimPreviewPresenter.trackVideoTrimmed();

        verify(mockedUserEventTracker).trackClipTrimmed(videonaProject);
    }

    @Test
    public void setTrimUpdateVideoTimes(){

    }

    @Test
    public void advanceBackwardStartTrimmingCallsUpdateStartTrimmingRangeSeekBar(){
        TrimPreviewPresenter presenter = getTrimPreviewPresenter();
        int advancePrecision = 600; //ms
        int startTimeMs = 1200; //ms

        presenter.advanceBackwardStartTrimming(advancePrecision, startTimeMs);

        verify(mockedTrimView).updateStartTrimmingRangeSeekBar(anyFloat());
    }

    public void advanceForwardStartTrimmingCallsUpdateStartTrimmingRangeSeekBar(){
        TrimPreviewPresenter presenter = getTrimPreviewPresenter();
        int advancePrecision = 600; //ms
        int startTimeMs = 1200; //ms

        presenter.advanceForwardStartTrimming(advancePrecision, startTimeMs);

        verify(mockedTrimView).updateStartTrimmingRangeSeekBar(anyFloat());
    }

    @Test
    public void advanceBackwardEndTrimmingCallsUpdateFinishTrimmingRangeSeekBar(){
        TrimPreviewPresenter presenter = getTrimPreviewPresenter();
        int advancePrecision = 600; //ms
        int endTimeMs = 1200; //ms

        presenter.advanceBackwardEndTrimming(advancePrecision, endTimeMs);

        verify(mockedTrimView).updateFinishTrimmingRangeSeekBar(anyFloat());
    }

    @Test
    public void advanceForwardEndTrimmingCallsUpdateFinishTrimmingRangeSeekBar() {
        TrimPreviewPresenter presenter = getTrimPreviewPresenter();
        int advancePrecision = 600; //ms
        int endTimeMs = 1200; //ms

        presenter.advanceBackwardEndTrimming(advancePrecision, endTimeMs);

        verify(mockedTrimView).updateFinishTrimmingRangeSeekBar(anyFloat());
    }

    @NonNull
    private TrimPreviewPresenter getTrimPreviewPresenter() {
        return new TrimPreviewPresenter(mockedTrimView,
            mockedUserEventTracker, mockedGetMediaListFromProjectUseCase,
            mockedModifyVideoDurationUseCase);
    }

    public Project getAProject() {
        return Project.getInstance("title", "/path", Profile.getInstance(VideoResolution.Resolution.HD720,
                VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25));
    }
}
