/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.share.presentation.mvp.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.vimojo.auth.domain.usecase.GetAuthToken;
import com.videonasocialmedia.vimojo.domain.editor.AddLastVideoExportedToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.project.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.ExportProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.share.domain.GetFtpListUseCase;
import com.videonasocialmedia.vimojo.share.domain.ObtainNetworksToShareUseCase;
import com.videonasocialmedia.vimojo.share.presentation.mvp.views.ShareVideoView;
import com.videonasocialmedia.vimojo.share.presentation.views.utils.LoggedValidator;
import com.videonasocialmedia.vimojo.sync.UploadToPlatformQueue;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

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
    @Mock private ObtainNetworksToShareUseCase mockedShareNetworksProvider;
    @Mock private GetFtpListUseCase mockedFtpListUseCase;
    @Mock private GetAuthToken mockedGetAuthToken;
    @Mock private UploadToPlatformQueue mockedUploadToPlatformQueue;
    @Mock private LoggedValidator mockedLoggedValidator;

    @Before
    public void injectMocks() {
        MockitoAnnotations.initMocks(this);
        getAProject();
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
                mockedExportProjectUseCase, mockedShareNetworksProvider, mockedFtpListUseCase,
                mockedGetAuthToken, mockedUploadToPlatformQueue, mockedLoggedValidator);
        assertThat(shareVideoPresenter.userEventTracker, is(userEventTracker));
    }

    @Test
    public void shareVideoPresenterCallsTracking(){
        ShareVideoPresenter shareVideoPresenter = getShareVideoPresenter();
        Project videonaProject = getAProject();
        String socialNetwokId = "SocialNetwork";
        int totalVideosShared = 0;

        shareVideoPresenter.trackVideoShared(socialNetwokId);

        verify(mockedUserEventTracker).trackVideoShared(socialNetwokId,videonaProject,
                totalVideosShared);
    }

    @Test
    public void clickUploadToPlatformShowErrorIfThereAreNotWifiOrMobileNetworkConnected() {
        ShareVideoPresenter shareVideoPresenter = getShareVideoPresenter();
        boolean isWifiConnected = false;
        boolean acceptUploadVideoMobileNetwork = false;
        boolean isMobileNetworkConnected = false;
        String videoPath = "";

        shareVideoPresenter.clickUploadToPlatform(isWifiConnected, acceptUploadVideoMobileNetwork,
            isMobileNetworkConnected, videoPath);

        verify(mockedShareVideoView).showError(null);
    }

    @Test
    public void clickUploadToPlatformShowDialogIfIsNeededAskPermissionForMobileUpload() {
        ShareVideoPresenter shareVideoPresenter = getShareVideoPresenter();
        boolean isWifiConnected = false;
        boolean acceptUploadVideoMobileNetwork = false;
        boolean isMobileNetworkConnected = true;
        String videoPath = "";

        shareVideoPresenter.clickUploadToPlatform(isWifiConnected, acceptUploadVideoMobileNetwork,
            isMobileNetworkConnected, videoPath);

        verify(mockedShareVideoView).showDialogUploadVideoWithMobileNetwork();
    }

    @Test
    public void clickUploadToPlatformNavigateToUserAuthIfUserNotLogged() {
        ShareVideoPresenter shareVideoPresenter = getShareVideoPresenter();
        boolean isWifiConnected = false;
        boolean acceptUploadVideoMobileNetwork = true;
        boolean isMobileNetworkConnected = true;
        String videoPath = "";

        when(mockedLoggedValidator.loggedValidate("")).thenReturn(false);

        shareVideoPresenter.clickUploadToPlatform(isWifiConnected, acceptUploadVideoMobileNetwork,
            isMobileNetworkConnected, videoPath);

        verify(mockedShareVideoView).showDialogNeedToRegisterLoginToUploadVideo();
    }

    @Test
    public void clickUpdateToPlatformNavigateToProjectDetailsIfAnyProjectInfoFieldsIsEmpty() {
        ShareVideoPresenter shareVideoPresenter = getShareVideoPresenter();
        boolean isWifiConnected = false;
        boolean acceptUploadVideoMobileNetwork = true;
        boolean isMobileNetworkConnected = true;
        String videoPath = "";
        Project project = getAProject();
        assertThat(project.getProjectInfo().getProductTypeList().size(), is(0));
        assertThat(project, is(shareVideoPresenter.currentProject));
         when(mockedLoggedValidator.loggedValidate("")).thenReturn(true);
        assertThat(shareVideoPresenter.isUserLogged(), is(true));

        shareVideoPresenter.clickUploadToPlatform(isWifiConnected, acceptUploadVideoMobileNetwork,
            isMobileNetworkConnected, videoPath);

        verify(mockedShareVideoView).showDialogNeedToCompleteDetailProjectFields();
    }

    public Project getAProject() {
        Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720,
            VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
        List<String> productType = new ArrayList<>();
        ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
        return Project.getInstance(projectInfo, "/path", "private/path", compositionProfile);
    }

    public Project getANewProject() {
        Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720,
            VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
        List<String> productType = new ArrayList<>();
        ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
        return new Project(projectInfo, "/path", "private/path", compositionProfile);
    }

    @NonNull
    private ShareVideoPresenter getShareVideoPresenter() {
        return new ShareVideoPresenter(mockContext, mockedShareVideoView, mockedUserEventTracker,
                mockSharedPrefs, mockedCreateDefaultProjectUseCase,
                mockedAddLastVideoExportedUseCase, mockedExportProjectUseCase,
                mockedShareNetworksProvider, mockedFtpListUseCase, mockedGetAuthToken,
            mockedUploadToPlatformQueue, mockedLoggedValidator);
    }
}
