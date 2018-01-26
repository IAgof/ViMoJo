/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.vimojo.domain.editor.AddLastVideoExportedToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.project.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.ExportProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.share.presentation.mvp.views.ShareVideoView;
import com.videonasocialmedia.vimojo.share.presentation.mvp.presenters.ShareVideoPresenter;
import com.videonasocialmedia.vimojo.share.domain.UploadVideoUseCase;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by alvaro on 24/08/16.
 */
public class ShareVideoPresenterTest {
    @Mock private MixpanelAPI mockedMixpanelAPI;
    @Mock private ShareVideoView mockedShareVideoView;
    @Mock private UserEventTracker mockedUserEventTracker;
    @Mock private SharedPreferences mockSharedPrefs;
    @Mock private Context mockContext;
    @Mock private CreateDefaultProjectUseCase mockedCreateDefaultProjectUseCase;
    @Mock private AddLastVideoExportedToProjectUseCase mockedAddLastVideoExportedUseCase;
    @Mock private ExportProjectUseCase mockedExportProjectUseCase;
    @Mock private UploadVideoUseCase mockedUploadVideoUseCase;

    @Before
    public void injectMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
        Project.getInstance(null, null, null, null).clear();
    }

    @Test
    public void constructorSetsCurrentProject() {
        Project videonaProject = getAProject();

        ShareVideoPresenter shareVideoPresenter = getShareVideoPresenter();

        assertThat(shareVideoPresenter.currentProject, is(videonaProject));
    }

    @Test
    public void constructorSetsUserTracker() {
        UserEventTracker userEventTracker = UserEventTracker.getInstance(mockedMixpanelAPI);
        ShareVideoPresenter shareVideoPresenter = new ShareVideoPresenter(mockContext,
            mockedShareVideoView, userEventTracker, mockSharedPrefs,
            mockedCreateDefaultProjectUseCase, mockedAddLastVideoExportedUseCase,
            mockedExportProjectUseCase, mockedUploadVideoUseCase);
        assertThat(shareVideoPresenter.userEventTracker, is(userEventTracker));
    }

    @Test
    public void shareVideoPresenterCallsTracking(){
        ShareVideoPresenter shareVideoPresenter = getShareVideoPresenter();
        Project videonaProject = getAProject();
        String socialNetwokId = "SocialNetwork";
        int totalVideosShared = 0;

        shareVideoPresenter.trackVideoShared(socialNetwokId);

        Mockito.verify(mockedUserEventTracker).trackVideoShared(socialNetwokId,videonaProject,
                totalVideosShared);
    }

    public Project getAProject() {
        Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720,
                VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
        return Project.getInstance("title", "/path", "private/path", compositionProfile);
    }


    @NonNull
    private ShareVideoPresenter getShareVideoPresenter() {
        return new ShareVideoPresenter(mockContext,
            mockedShareVideoView, mockedUserEventTracker, mockSharedPrefs,
            mockedCreateDefaultProjectUseCase, mockedAddLastVideoExportedUseCase,
            mockedExportProjectUseCase, mockedUploadVideoUseCase);
    }
}
