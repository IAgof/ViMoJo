package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.domain.editor.AddLastVideoExportedToProjectUseCase;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.domain.project.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.domain.social.ObtainNetworksToShareUseCase;
import com.videonasocialmedia.vimojo.domain.social.GetFtpListUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.entities.social.FtpNetwork;
import com.videonasocialmedia.vimojo.model.entities.social.SocialNetwork;
import com.videonasocialmedia.vimojo.presentation.mvp.views.ShareVideoView;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.DateUtils;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by jca on 11/12/15.
 */
public class ShareVideoPresenter {
    private final Context context;
    private ObtainNetworksToShareUseCase obtainNetworksToShareUseCase;
    private GetFtpListUseCase getFtpListUseCase;
    private CreateDefaultProjectUseCase createDefaultProjectUseCase;
    private ShareVideoView shareVideoView;
    protected Project currentProject;
    protected UserEventTracker userEventTracker;
    private SharedPreferences sharedPreferences;
    private List<FtpNetwork> ftpList;
    private List<SocialNetwork> socialNetworkList;
    private List optionToShareList;
    private SharedPreferences.Editor preferencesEditor;

    private AddLastVideoExportedToProjectUseCase addLastVideoExportedProjectUseCase;

    @Inject
    public ShareVideoPresenter(ShareVideoView shareVideoView, UserEventTracker userEventTracker,
                               SharedPreferences sharedPreferences, Context context,
                               CreateDefaultProjectUseCase createDefaultProjectUseCase,
                               AddLastVideoExportedToProjectUseCase
                                       addLastVideoExportedProjectUseCase) {
        this.shareVideoView = shareVideoView;
        this.userEventTracker = userEventTracker;
        this.sharedPreferences = sharedPreferences;
        this.context = context;
        this.createDefaultProjectUseCase = createDefaultProjectUseCase;
        this.addLastVideoExportedProjectUseCase = addLastVideoExportedProjectUseCase;

        currentProject = loadCurrentProject();
    }

    private Project loadCurrentProject() {
        return Project.getInstance(null, null, null);
    }

    public void onCreate() {
        obtainNetworksToShareUseCase = new ObtainNetworksToShareUseCase();
        getFtpListUseCase = new GetFtpListUseCase();
    }

    public void onResume() {
        obtainNetworksToShare();
        obtainListFtp();
        obtainListOptionsToShare(ftpList, socialNetworkList);
        shareVideoView.showOptionsShareList(optionToShareList);
    }

    private void obtainListFtp() {
        ftpList = getFtpListUseCase.getFtpList();
    }

    public void obtainNetworksToShare() {
       socialNetworkList = obtainNetworksToShareUseCase.obtainMainNetworks();
    }

    private void obtainListOptionsToShare(List<FtpNetwork> ftpList,
                                          List<SocialNetwork> socialNetworkList) {
        optionToShareList = new ArrayList();
        if(BuildConfig.FEATURE_FTP)
            optionToShareList.addAll(ftpList);
        optionToShareList.addAll(socialNetworkList);
    }

    public void shareVideo(String videoPath, SocialNetwork appToShareWith, Context ctx) {
        final ComponentName name = new ComponentName(appToShareWith.getAndroidPackageName(),
                appToShareWith.getAndroidActivityName());

        Uri uri = Utils.obtainUriToShare(ctx, videoPath);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("video/*");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                VimojoApplication.getAppContext().getResources().getString(R.string.sharedWithVideona));
        intent.putExtra(Intent.EXTRA_TEXT,
                VimojoApplication.getAppContext().getResources().getString(R.string.videonaTags));
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addCategory(Intent.CATEGORY_DEFAULT);

        intent.setComponent(name);

        ctx.startActivity(intent);
    }

    // TODO(jliarte): 15/12/16 safe delete this method - old way to show networks?
    public void obtainExtraAppsToShare() {
        List networks = obtainNetworksToShareUseCase.obtainSecondaryNetworks();
        shareVideoView.hideShareNetworks();
        shareVideoView.showMoreNetworks(networks);
    }

    public void updateNumTotalVideosShared() {
        int totalVideosShared = sharedPreferences.getInt(ConfigPreferences.TOTAL_VIDEOS_SHARED, 0);
        preferencesEditor = sharedPreferences.edit();
        preferencesEditor.putInt(ConfigPreferences.TOTAL_VIDEOS_SHARED, ++totalVideosShared);
        preferencesEditor.commit();
    }

    public int getNumTotalVideosShared() {
        return  sharedPreferences.getInt(ConfigPreferences.TOTAL_VIDEOS_SHARED, 0);
    }

    public String getResolution(){
        VideoResolution videoResolution = currentProject.getProfile().getVideoResolution();
        return videoResolution.getWidth() + "x" + videoResolution.getHeight();
    }

    public void trackVideoShared(String socialNetwork) {
        userEventTracker.trackVideoSharedSuperProperties();
        userEventTracker.trackVideoShared(socialNetwork, currentProject, getNumTotalVideosShared());
        userEventTracker.trackVideoSharedUserTraits();
    }

    public void newDefaultProject(String rootPath){
        clearProjectDataFromSharedPreferences();
        createDefaultProjectUseCase.createProject(rootPath);
    }

    // TODO(jliarte): 23/10/16 should this be moved to activity or other outer layer? maybe a repo?
    // TODO:(alvaro.martinez) 4/01/17 these data will no be saved in SharedPreferences, rewrite mixpanel tracking and delete.
    private void clearProjectDataFromSharedPreferences() {
        sharedPreferences = VimojoApplication.getAppContext().getSharedPreferences(
                ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME,
                Context.MODE_PRIVATE);
        preferencesEditor = sharedPreferences.edit();
        preferencesEditor.putLong(ConfigPreferences.VIDEO_DURATION, 0);
        preferencesEditor.putInt(ConfigPreferences.NUMBER_OF_CLIPS, 0);
    }

    public void addVideoExportedToProject(String videoPath) {
        addLastVideoExportedProjectUseCase.addLastVideoExportedToProject(videoPath,
            DateUtils.getDateRightNow());
    }
}