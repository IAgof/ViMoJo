package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.videonasocialmedia.videonamediaframework.model.VMComposition;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.utils.ElementChangedListener;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer;
import com.videonasocialmedia.vimojo.domain.editor.GetMusicListUseCase;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.MusicListView;
import com.videonasocialmedia.vimojo.utils.Constants;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by ruth on 13/09/16.
 */
public class MusicListPresenter implements ElementChangedListener {
    private final Context context;
    private MusicListView musicListView;
    private final VideonaPlayer videonaPlayerView;
    private final GetMusicListUseCase getMusicListUseCase;
    private Project currentProject;
    private final ProjectInstanceCache projectInstanceCache;
    protected boolean amIVerticalApp;

    @Inject
    public MusicListPresenter(Context context, MusicListView musicListView, VideonaPlayer
        videonaPlayerView, GetMusicListUseCase getMusicListUseCase,
                              ProjectInstanceCache projectInstanceCache,
                              @Named("amIAVerticalApp") boolean amIAVerticalApp) {
        this.context = context;
        this.musicListView = musicListView;
        this.videonaPlayerView = videonaPlayerView;
        this.getMusicListUseCase = getMusicListUseCase;
        this.projectInstanceCache = projectInstanceCache;
        this.amIVerticalApp = amIAVerticalApp;
    }

    public void updatePresenter() {
        this.currentProject = projectInstanceCache.getCurrentProject();
        currentProject.addListener(this);
        videonaPlayerView.attachView(context);
        loadPlayerFromProject();
        if (amIVerticalApp) {
            videonaPlayerView
                .setAspectRatioVerticalVideos(Constants.DEFAULT_PLAYER_HEIGHT_VERTICAL_MODE);
        }
        musicListView.showMusicList(getMusicListUseCase.getAppMusic());
    }

    private void loadPlayerFromProject() {
        VMComposition vmCompositionCopy = null;
        try {
            vmCompositionCopy = new VMComposition(currentProject.getVMComposition());
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            illegalItemOnTrack.printStackTrace();
            Crashlytics.log("Error getting copy VMComposition " + illegalItemOnTrack);
        }
        videonaPlayerView.init(vmCompositionCopy);
    }

    public void removePresenter() {
        videonaPlayerView.detachView();
    }

    @Override
    public void onObjectUpdated() {
        musicListView.updateProject();
    }

    public void selectMusic(Music music) {
        musicListView.navigateToDetailMusic(music.getMediaPath());
    }
}
