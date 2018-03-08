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
import android.text.TextUtils;

import com.google.common.util.concurrent.ListenableFuture;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.auth.domain.usecase.GetAuthToken;
import com.videonasocialmedia.vimojo.domain.editor.AddLastVideoExportedToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.project.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.ExportProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.model.sources.ProductTypeProvider;
import com.videonasocialmedia.vimojo.share.domain.GetFtpListUseCase;
import com.videonasocialmedia.vimojo.share.domain.ObtainNetworksToShareUseCase;
import com.videonasocialmedia.vimojo.share.presentation.mvp.views.ShareVideoView;
import com.videonasocialmedia.vimojo.share.presentation.views.utils.LoggedValidator;
import com.videonasocialmedia.vimojo.sync.UploadToPlatformQueue;
import com.videonasocialmedia.vimojo.sync.helper.RunSyncAdapterHelper;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.vimojoapiclient.model.AuthToken;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by alvaro on 24/08/16.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({TextUtils.class})
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
    @Mock private RunSyncAdapterHelper mockedRunSyncAdapterHelper;

    @Before
    public void injectMocks() {
        MockitoAnnotations.initMocks(this);
        getAProject();
        PowerMockito.mockStatic(TextUtils.class);
    }

    @After
    public void tearDown() {
        getAProject().clear();
        //Project.getInstance(null, null, null, null).clear();
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
                mockedGetAuthToken, mockedUploadToPlatformQueue, mockedLoggedValidator,
                mockedRunSyncAdapterHelper);
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
        ShareVideoPresenter spyShareVideoPresenter = spy(getShareVideoPresenter());
        boolean isWifiConnected = false;
        boolean acceptUploadVideoMobileNetwork = true;
        boolean isMobileNetworkConnected = true;
        String videoPath = "";
        doReturn(new AuthToken("", "")).when(mockedGetAuthToken).getAuthToken(any(Context.class));
        assertThat("User is logged", spyShareVideoPresenter.isUserLogged(), is(false));

        spyShareVideoPresenter.clickUploadToPlatform(isWifiConnected, acceptUploadVideoMobileNetwork,
            isMobileNetworkConnected, videoPath);

        verify(mockedShareVideoView).showDialogNeedToRegisterLoginToUploadVideo();
    }

    @Test
    public void clickUpdateToPlatformNavigateToProjectDetailsIfAnyProjectInfoFieldsIsEmpty() {
        ShareVideoPresenter spyShareVideoPresenter = spy(getShareVideoPresenter());
        boolean isWifiConnected = false;
        boolean acceptUploadVideoMobileNetwork = true;
        boolean isMobileNetworkConnected = true;
        String videoPath = "";
        Project project = getAProject();
        assertThat(project.getProjectInfo().getProductTypeList().size(), is(0));
        assertThat(project, is(spyShareVideoPresenter.currentProject));
        doReturn(new AuthToken("token", "")).when(mockedGetAuthToken).getAuthToken(any(Context.class));
        when(mockedLoggedValidator.loggedValidate("token")).thenReturn(true);
        assertThat("User is logged", spyShareVideoPresenter.isUserLogged(), is(true));
        assertThat("Project info product type is empty",
            project.getProjectInfo().getProductTypeList().size(), is(0));

        spyShareVideoPresenter.clickUploadToPlatform(isWifiConnected, acceptUploadVideoMobileNetwork,
            isMobileNetworkConnected, videoPath);

        verify(mockedShareVideoView).showDialogNeedToCompleteDetailProjectFields();
    }

    @Test
    public void clickUpdateToPlatformShowMessageUploadingVideoIfUserIsLoggedProjectInfoCompletedAndNetworkIsConnected() {
        ShareVideoPresenter spyShareVideoPresenter = spy(getShareVideoPresenter());
        // Device is connected to network
        boolean isWifiConnected = true;
        boolean acceptUploadVideoMobileNetwork = true;
        boolean isMobileNetworkConnected = true;
        String videoPath = "";
        Project project = getAProject();
        assertThat(project.getProjectInfo().getProductTypeList().size(), is(0));
        assertThat(project, is(spyShareVideoPresenter.currentProject));
        doReturn(new AuthToken("token", "")).when(mockedGetAuthToken).getAuthToken(any(Context.class));
        when(mockedLoggedValidator.loggedValidate("token")).thenReturn(true);
        assertThat("User is logged", spyShareVideoPresenter.isUserLogged(), is(true));
        List<String> productTypeList = new ArrayList<>();
        productTypeList.add(ProductTypeProvider.Types.LIVE_ON_TAPE.name());
        project.getProjectInfo().setProductTypeList(productTypeList);
        assertThat("Project info product type is not empty",
            project.getProjectInfo().getProductTypeList().size(), is(1));

        spyShareVideoPresenter.clickUploadToPlatform(isWifiConnected, acceptUploadVideoMobileNetwork,
            isMobileNetworkConnected, videoPath);

        verify(mockedShareVideoView).showMessage(R.string.uploading_video);
    }

    @Test
    public void clickUpdateToPlatformShowDialogNotNetworkUploadingVideoIfUserIsLoggedProjectInfoCompletedAndNetworkIsConnected() {
        ShareVideoPresenter spyShareVideoPresenter = spy(getShareVideoPresenter());
        // Device is NOT connected to network
        boolean isWifiConnected = false;
        boolean acceptUploadVideoMobileNetwork = false;
        boolean isMobileNetworkConnected = false;
        String videoPath = "";
        Project project = getAProject();
        assertThat(project.getProjectInfo().getProductTypeList().size(), is(0));
        assertThat(project, is(spyShareVideoPresenter.currentProject));
        doReturn(new AuthToken("token", "")).when(mockedGetAuthToken).getAuthToken(any(Context.class));
        when(mockedLoggedValidator.loggedValidate("token")).thenReturn(true);
        assertThat("User is logged", spyShareVideoPresenter.isUserLogged(), is(true));
        List<String> productTypeList = new ArrayList<>();
        productTypeList.add(ProductTypeProvider.Types.LIVE_ON_TAPE.name());
        project.getProjectInfo().setProductTypeList(productTypeList);
        assertThat("Project info product type is not empty",
            project.getProjectInfo().getProductTypeList().size(), is(1));

        spyShareVideoPresenter.clickUploadToPlatform(isWifiConnected, acceptUploadVideoMobileNetwork,
            isMobileNetworkConnected, videoPath);

        verify(mockedShareVideoView).showDialogNotNetworkUploadVideoOnConnection();
    }

    @Test
    public void uploadVideoRunSyncAdapter() {
        ShareVideoPresenter shareVideoPresenter = getShareVideoPresenter();
        String videoPath = "";
        getAProject().clear();
        Project project = getAProject();
        boolean connectedToNetwork = true;
        ProjectInfo projectInfo = project.getProjectInfo();

        shareVideoPresenter.uploadVideo(videoPath, projectInfo.getTitle(), projectInfo.getDescription(),
            projectInfo.getProductTypeList(), connectedToNetwork);

        verify(mockedRunSyncAdapterHelper).runNowSyncAdapter();
    }


    public Project getAProject() {
        Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720,
            VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
        List<String> productType = new ArrayList<>();
        ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
        return Project.getInstance(projectInfo, "/path", "private/path", compositionProfile);
    }

    @NonNull
    private ShareVideoPresenter getShareVideoPresenter() {
        return new ShareVideoPresenter(mockContext, mockedShareVideoView, mockedUserEventTracker,
                mockSharedPrefs, mockedCreateDefaultProjectUseCase,
                mockedAddLastVideoExportedUseCase, mockedExportProjectUseCase,
                mockedShareNetworksProvider, mockedFtpListUseCase, mockedGetAuthToken,
            mockedUploadToPlatformQueue, mockedLoggedValidator, mockedRunSyncAdapterHelper);
    }
}
