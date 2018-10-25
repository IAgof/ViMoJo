package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.videonasocialmedia.videonamediaframework.model.VMComposition;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.utils.ElementChangedListener;
import com.videonasocialmedia.videonamediaframework.playback.VMCompositionPlayer;
import com.videonasocialmedia.vimojo.domain.editor.GetAudioFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMusicListUseCase;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.GetMusicFromProjectCallback;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.MusicListView;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by ruth on 13/09/16.
 */
public class MusicListPresenter implements GetMusicFromProjectCallback, ElementChangedListener {
    private final Context context;
    private MusicListView musicListView;
    private final VMCompositionPlayer vmCompositionPlayerView;
    private Project currentProject;
    private final ProjectInstanceCache projectInstanceCache;
    private List<Music> availableMusic;
    private GetAudioFromProjectUseCase getAudioFromProjectUseCase;
    private GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase;
    private boolean amIVerticalApp;

    @Inject
    public MusicListPresenter(
            Context context, MusicListView musicListView, VMCompositionPlayer
            vmCompositionPlayerView, GetMusicListUseCase getMusicListUseCase,
            GetAudioFromProjectUseCase getAudioFromProjectUseCase,
            GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase,
            ProjectInstanceCache projectInstanceCache,
            @Named("amIAVerticalApp") boolean amIAVerticalApp) {
        this.context = context;
        this.musicListView = musicListView;
        this.vmCompositionPlayerView = vmCompositionPlayerView;
        availableMusic = getMusicListUseCase.getAppMusic();
        this.getAudioFromProjectUseCase = getAudioFromProjectUseCase;
        this.getPreferencesTransitionFromProjectUseCase = getPreferencesTransitionFromProjectUseCase;
        this.projectInstanceCache = projectInstanceCache;
        this.amIVerticalApp = amIAVerticalApp;
    }

    public void updatePresenter() {
        this.currentProject = projectInstanceCache.getCurrentProject();
        currentProject.addListener(this);
        vmCompositionPlayerView.attachView(context);
        loadPlayerFromProject();
        obtainMusic();
        if (getPreferencesTransitionFromProjectUseCase
                .isVideoFadeTransitionActivated(currentProject)) {
            musicListView.setVideoFadeTransitionAmongVideos();
        }
        if (amIVerticalApp) {
            vmCompositionPlayerView
                .setAspectRatioVerticalVideos(Constants.DEFAULT_PLAYER_HEIGHT_VERTICAL_MODE);
        }
    }

    private void loadPlayerFromProject() {
        VMComposition vmCompositionCopy = null;
        try {
            vmCompositionCopy = new VMComposition(currentProject.getVMComposition());
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            illegalItemOnTrack.printStackTrace();
            Crashlytics.log("Error getting copy VMComposition " + illegalItemOnTrack);
        }
        vmCompositionPlayerView.init(vmCompositionCopy);
    }

    public void removePresenter() {
        vmCompositionPlayerView.detachView();
    }

    private void obtainMusic() {
        getAudioFromProjectUseCase.getMusicFromProject(currentProject, this);
    }

    public void onStart() {
        musicListView.showMusicList(availableMusic);
    }

    public void getAvailableMusic() {
        musicListView.showMusicList(availableMusic);
    }

    @Override
    public void onMusicRetrieved(Music music) {
        if(getAudioFromProjectUseCase.hasBeenMusicSelected(currentProject)){
            musicListView.goToDetailActivity(music.getMediaPath());
        }
    }

    @Override
    public void onObjectUpdated() {
        musicListView.updateProject();
    }
}
