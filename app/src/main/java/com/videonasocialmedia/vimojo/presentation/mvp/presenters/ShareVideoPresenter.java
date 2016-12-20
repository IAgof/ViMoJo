package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.videonasocialmedia.videonamediaframework.pipeline.VideoAudioSwapper;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.domain.ClearProjectUseCase;
import com.videonasocialmedia.vimojo.domain.CreateDefaultProjectUseCase;
import com.videonasocialmedia.vimojo.domain.social.ObtainNetworksToShareUseCase;
import com.videonasocialmedia.vimojo.domain.social.GetFtpListUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.entities.social.FtpNetwork;
import com.videonasocialmedia.vimojo.model.entities.social.SocialNetwork;
import com.videonasocialmedia.vimojo.presentation.mvp.views.ShareVideoView;
import com.videonasocialmedia.vimojo.repository.project.ProfileRepository;
import com.videonasocialmedia.vimojo.repository.project.ProfileSharedPreferencesRepository;
import com.videonasocialmedia.vimojo.sound.domain.MixAudioUseCase;
import com.videonasocialmedia.vimojo.sound.domain.OnMixAudioListener;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.utils.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by jca on 11/12/15.
 */
public class ShareVideoPresenter implements VideoAudioSwapper.VideoAudioSwapperListener {
    private final Context context;
    private ObtainNetworksToShareUseCase obtainNetworksToShareUseCase;
    private GetFtpListUseCase getFtpListUseCase;
    private ClearProjectUseCase clearProjectUseCase;
    private CreateDefaultProjectUseCase createDefaultProjectUseCase;
    private ShareVideoView shareVideoView;
    protected Project currentProject;
    protected UserEventTracker userEventTracker;
    private SharedPreferences sharedPreferences;
    private List<FtpNetwork> ftpList;
    private List<SocialNetwork> socialNetworkList;
    private List optionToShareList;
    private SharedPreferences.Editor preferencesEditor;
    private ProfileRepository profileRepository;
    private MixAudioUseCase mixAudioUseCase;
    private VideoAudioSwapper videoAudioSwapper;
    private String videoExportedWithVoiceOverPath;
    private String videoExportedTemp;

    @Inject
    public ShareVideoPresenter(ShareVideoView shareVideoView, UserEventTracker userEventTracker,
                               SharedPreferences sharedPreferences, Context context,
                               ClearProjectUseCase clearProjectUseCase,
                               CreateDefaultProjectUseCase createDefaultProjectUseCase,
                               MixAudioUseCase mixAudioUseCase) {
        this.shareVideoView = shareVideoView;
        this.userEventTracker = userEventTracker;
        this.sharedPreferences = sharedPreferences;
        this.context = context;
        this.clearProjectUseCase = clearProjectUseCase;
        this.createDefaultProjectUseCase = createDefaultProjectUseCase;
        this.mixAudioUseCase = mixAudioUseCase;

        currentProject = loadCurrentProject();
    }

    private Project loadCurrentProject() {
        return Project.getInstance(null, null, null);
    }

    public void onCreate() {
        obtainNetworksToShareUseCase = new ObtainNetworksToShareUseCase();
        getFtpListUseCase = new GetFtpListUseCase();
        videoAudioSwapper = new VideoAudioSwapper(this);
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
        optionToShareList.addAll(ftpList);
        optionToShareList.addAll(socialNetworkList);
    }

    public void exportWithVoiceOver(final String videoExportedTemp) {
        if(!currentProject.hasVoiceOver())
            return;
        // TODO(jliarte): 16/12/16 move this logic to SDK VMCompositionExportSessionImpl
        this.videoExportedTemp = videoExportedTemp;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "V_EDIT_" + timeStamp + ".mp4";
        videoExportedWithVoiceOverPath = Constants.PATH_APP_EDITED + File.separator + fileName;
        mixAudioUseCase.mixAudio(videoExportedTemp, currentProject.getVoiceOverPath(),
                currentProject.getVoiceOverVolume(), new OnMixAudioListener() {
                    @Override
                    public void onMixAudioSuccess(String pathAudioMixed) {
                        videoAudioSwapper.export(videoExportedTemp, pathAudioMixed,
                                videoExportedWithVoiceOverPath);
                    }

                    @Override
                    public void onMixAudioError() {
                        // TODO(jliarte): 16/12/16 use constants or localized string?
                        shareVideoView.showError("Error applying your audio track.");
                    }
                });
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

    public void resetProject(String rootPath) {
        clearProjectDataFromSharedPreferences();
        clearProjectUseCase.clearProject(currentProject);
        profileRepository = new ProfileSharedPreferencesRepository(sharedPreferences, context);
        createDefaultProjectUseCase.loadOrCreateProject(rootPath,
                profileRepository.getCurrentProfile());
    }

    // TODO(jliarte): 23/10/16 should this be moved to activity or other outer layer? maybe a repo?
    private void clearProjectDataFromSharedPreferences() {
        sharedPreferences = VimojoApplication.getAppContext().getSharedPreferences(
                ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME,
                Context.MODE_PRIVATE);
        preferencesEditor = sharedPreferences.edit();
        preferencesEditor.putLong(ConfigPreferences.VIDEO_DURATION, 0);
        preferencesEditor.putInt(ConfigPreferences.NUMBER_OF_CLIPS, 0);
    }

    @Override
    public void onExportError(String s) {
        // TODO(jliarte): 16/12/16 use constants or localized string?
        shareVideoView.showError("Error exporting your final video.");
    }

    @Override
    public void onExportSuccess() {
        // update video Share player
        shareVideoView.setVideo(videoExportedWithVoiceOverPath);
    }
}