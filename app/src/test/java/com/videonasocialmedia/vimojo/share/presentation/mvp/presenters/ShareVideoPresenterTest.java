/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.share.presentation.mvp.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;

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
import com.videonasocialmedia.vimojo.presentation.mvp.views.OptionsToShareList;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.share.domain.GetFtpListUseCase;
import com.videonasocialmedia.vimojo.share.domain.ObtainNetworksToShareUseCase;
import com.videonasocialmedia.vimojo.share.model.entities.FtpNetwork;
import com.videonasocialmedia.vimojo.share.model.entities.SocialNetwork;
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
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
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
@PrepareForTest({Environment.class, TextUtils.class})
public class ShareVideoPresenterTest {
    @Mock private MixpanelAPI mockedMixpanelAPI;
    @Mock private ShareVideoView mockedShareVideoView;
    @Mock private UserEventTracker mockedUserEventTracker;
    @Mock private SharedPreferences mockedSharedPreferences;
    @Mock private SharedPreferences.Editor mockedPreferencesEditor;
    @Mock private Context mockContext;
    @Mock ProjectRepository mockedProjectRepository;
    @Mock private CreateDefaultProjectUseCase mockedCreateDefaultProjectUseCase;
    @Mock private AddLastVideoExportedToProjectUseCase mockedAddLastVideoExportedUseCase;
    @Mock private ExportProjectUseCase mockedExportProjectUseCase;
    @Mock private ObtainNetworksToShareUseCase mockedShareNetworksProvider;
    @Mock private GetFtpListUseCase mockedFtpListUseCase;
    @Mock private GetAuthToken mockedGetAuthToken;
    @Mock private UploadToPlatformQueue mockedUploadToPlatformQueue;
    @Mock private LoggedValidator mockedLoggedValidator;
    private File mockedStorageDir;
    @Mock SocialNetwork mockedSocialNetwork;
    @Mock private RunSyncAdapterHelper mockedRunSyncAdapterHelper;

    @Before
    public void injectMocks() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(Environment.class);
        mockedStorageDir = PowerMockito.mock(File.class);
        when(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)).
            thenReturn(mockedStorageDir);
        when(Environment.getExternalStorageDirectory()).thenReturn(mockedStorageDir);
        PowerMockito.mockStatic(TextUtils.class);
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
                mockedShareVideoView, userEventTracker, mockedSharedPreferences, mockedProjectRepository,
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
        Project project = getAProject();
        ShareVideoPresenter spyShareVideoPresenter = spy(getShareVideoPresenter());
        boolean isWifiConnected = true;
        boolean acceptUploadVideoMobileNetwork = true;
        boolean isMobileNetworkConnected = true;
        String videoPath = "";
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
        Project project = getAProject();
        ShareVideoPresenter spyShareVideoPresenter = spy(getShareVideoPresenter());
        // Device is connected to network
        boolean isWifiConnected = true;
        boolean acceptUploadVideoMobileNetwork = true;
        boolean isMobileNetworkConnected = true;
        String videoPath = "";
        assertThat(project.getProjectInfo().getProductTypeList().size(), is(0));
        assertThat(project, is(spyShareVideoPresenter.currentProject));
        doReturn(new AuthToken("token", "")).when(mockedGetAuthToken).getAuthToken(any(Context.class));
        when(mockedLoggedValidator.loggedValidate("token")).thenReturn(true);
        assertThat("User is logged", spyShareVideoPresenter.isUserLogged(), is(true));
        List<String> productType = new ArrayList<>();
        productType.add(ProjectInfo.ProductType.RAW_VIDEOS.name());
        project.getProjectInfo().setProductTypeList(productType);
        assertThat("Project info product type is not empty",
            project.getProjectInfo().getProductTypeList().size(), is(1));

        spyShareVideoPresenter.clickUploadToPlatform(isWifiConnected, acceptUploadVideoMobileNetwork,
            isMobileNetworkConnected, videoPath);

        verify(mockedShareVideoView).showMessage(R.string.uploading_video);
    }

    @Test
    public void clickUpdateToPlatformShowDialogNotNetworkUploadingVideoIfUserIsLoggedProjectInfoCompletedAndNetworkIsConnected() {
        Project project = getAProject();
        ShareVideoPresenter spyShareVideoPresenter = spy(getShareVideoPresenter());
        // Device is NOT connected to network
        boolean isWifiConnected = false;
        boolean acceptUploadVideoMobileNetwork = false;
        boolean isMobileNetworkConnected = false;
        String videoPath = "";
        assertThat(project.getProjectInfo().getProductTypeList().size(), is(0));
        assertThat(project, is(spyShareVideoPresenter.currentProject));
        doReturn(new AuthToken("token", "")).when(mockedGetAuthToken).getAuthToken(any(Context.class));
        when(mockedLoggedValidator.loggedValidate("token")).thenReturn(true);
        assertThat("User is logged", spyShareVideoPresenter.isUserLogged(), is(true));
        List<String> productType = new ArrayList<>();
        productType.add(ProjectInfo.ProductType.RAW_VIDEOS.name());
        project.getProjectInfo().setProductTypeList(productType);
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


    @Test
    public void initShowExportDialogIfIsAppIsExportingProject() {
        ShareVideoPresenter shareVideoPresenter = getShareVideoPresenter();
        boolean hasBeenProjectExported = false;
        String videoExportedPath = "somePath";
        boolean isAppExportingProject = true;

        shareVideoPresenter.init(hasBeenProjectExported, videoExportedPath, isAppExportingProject);

        verify(mockedShareVideoView).startVideoExport();
    }


    @Test
    public void exportOrProcessNetworkStartExportIfProjectHasNotBeenExported() {
        ShareVideoPresenter spyShareVideoPresenter = Mockito.spy(getShareVideoPresenter());
        int anyNetwork = OptionsToShareList.typeFtp;
        boolean hasBeenProjectExported = false;
        when(spyShareVideoPresenter.hasBeenProjectExported()).thenReturn(hasBeenProjectExported);

        spyShareVideoPresenter.exportOrProcessNetwork(anyNetwork);

        verify(spyShareVideoPresenter).startExport(anyNetwork);
    }

    @Test
    public void exportOrProcessNetworkProcessNetworkIfIsProjectHasBeenExported() {
        ShareVideoPresenter spyShareVideoPresenter = Mockito.spy(getShareVideoPresenter());
        int anyNetwork = OptionsToShareList.typeFtp;
        boolean hasBeenProjectExported = true;
        String videoExportedPath = "";
        when(spyShareVideoPresenter.hasBeenProjectExported()).thenReturn(hasBeenProjectExported);

        spyShareVideoPresenter.exportOrProcessNetwork(anyNetwork);

        verify(spyShareVideoPresenter).processNetworkClicked(anyNetwork, videoExportedPath);
    }

    @Test
    public void exportOrProcessVimojoNetworkIfProjectHasBeenExportedUploadToPlatform() {
        Project project = getAProject();
        ShareVideoPresenter spyShareVideoPresenter = Mockito.spy(getShareVideoPresenter());
        boolean hasBeenProjectExported = true;
        when(spyShareVideoPresenter.hasBeenProjectExported()).thenReturn(hasBeenProjectExported);
        boolean isWifiConnected = false;
        boolean acceptUploadVideoMobileNetwork = false;
        boolean isMobileNetworkConnected = false;
        String videoExportedPath = "";
        doReturn(new AuthToken("token", "")).when(mockedGetAuthToken).getAuthToken(any(Context.class));
        when(mockedLoggedValidator.loggedValidate("token")).thenReturn(true);
        assertThat("User is logged", spyShareVideoPresenter.isUserLogged(), is(true));
        List<String> productType = new ArrayList<>();
        productType.add(ProjectInfo.ProductType.RAW_VIDEOS.name());
        project.getProjectInfo().setProductTypeList(productType);
        assertThat("Project info product type is not empty",
            project.getProjectInfo().getProductTypeList().size(), is(1));

        spyShareVideoPresenter.exportOrProcessNetwork(OptionsToShareList.typeVimojoNetwork);

        verify(spyShareVideoPresenter).clickUploadToPlatform(isWifiConnected,
            acceptUploadVideoMobileNetwork, isMobileNetworkConnected, videoExportedPath);
    }

    @Test
    public void exportOrProcessFTPNetworkIfProjectHasBeenExportedCreateDialogFtp() {
        ShareVideoPresenter spyShareVideoPresenter = Mockito.spy(getShareVideoPresenter());
        boolean hasBeenProjectExported = true;
        when(spyShareVideoPresenter.hasBeenProjectExported()).thenReturn(hasBeenProjectExported);
        FtpNetwork ftpNetworkSelected = any(FtpNetwork.class);
        String videoExportedPath = "";

        spyShareVideoPresenter.exportOrProcessNetwork(OptionsToShareList.typeFtp);

        verify(mockedShareVideoView).createDialogToInsertNameProject(ftpNetworkSelected,
            videoExportedPath);
    }

    @Test
    public void exportOrProcessSocialNetworkIfProjectHasBeenExportedShareVideo() {
        ShareVideoPresenter spyShareVideoPresenter = Mockito.spy(getShareVideoPresenter());
        boolean hasBeenProjectExported = true;
        when(spyShareVideoPresenter.hasBeenProjectExported()).thenReturn(hasBeenProjectExported);
        when(spyShareVideoPresenter.getSocialNetworkSelected()).thenReturn(mockedSocialNetwork);
        when(spyShareVideoPresenter.getSocialNetworkSelected().getName()).thenReturn("SocialNetwork");
        when(mockedSharedPreferences.edit()).thenReturn(mockedPreferencesEditor);
        String videoExportedPath = "";

        spyShareVideoPresenter.exportOrProcessNetwork(OptionsToShareList.typeSocialNetwork);

        verify(mockedShareVideoView).shareVideo(videoExportedPath, mockedSocialNetwork);
    }

    @Test
    public void exportOrProcessMoreSocialNetworkIfProjectHasBeenExportedShowIntent() {
        ShareVideoPresenter spyShareVideoPresenter = Mockito.spy(getShareVideoPresenter());
        boolean hasBeenProjectExported = true;
        when(spyShareVideoPresenter.hasBeenProjectExported()).thenReturn(hasBeenProjectExported);
        when(mockedSharedPreferences.edit()).thenReturn(mockedPreferencesEditor);
        String videoExportedPath = "";

        spyShareVideoPresenter.exportOrProcessNetwork(OptionsToShareList.typeMoreSocialNetwork);

        verify(mockedShareVideoView).showIntentOtherNetwork(videoExportedPath);
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
            mockedSharedPreferences, mockedProjectRepository, mockedCreateDefaultProjectUseCase,
                mockedAddLastVideoExportedUseCase, mockedExportProjectUseCase,
                mockedShareNetworksProvider, mockedFtpListUseCase, mockedGetAuthToken,
            mockedUploadToPlatformQueue, mockedLoggedValidator, mockedRunSyncAdapterHelper);
    }
}
