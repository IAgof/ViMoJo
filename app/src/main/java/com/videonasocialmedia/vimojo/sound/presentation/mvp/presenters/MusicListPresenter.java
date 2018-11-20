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

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by ruth on 13/09/16.
 */
public class MusicListPresenter implements ElementChangedListener {
    private final Context context;
    private MusicListView musicListView;
    private final GetMusicListUseCase getMusicListUseCase;
    private Project currentProject;
    private final ProjectInstanceCache projectInstanceCache;
    protected boolean amIVerticalApp;

    @Inject
    public MusicListPresenter(Context context, MusicListView musicListView,
                              GetMusicListUseCase getMusicListUseCase,
                              ProjectInstanceCache projectInstanceCache,
                              @Named("amIAVerticalApp") boolean amIAVerticalApp) {
        this.context = context;
        this.musicListView = musicListView;
        this.getMusicListUseCase = getMusicListUseCase;
        this.projectInstanceCache = projectInstanceCache;
        this.amIVerticalApp = amIAVerticalApp;
    }

    public void updatePresenter() {
        this.currentProject = projectInstanceCache.getCurrentProject();
        currentProject.addListener(this);
        musicListView.attachView(context);
        loadPlayerFromProject();
        if (amIVerticalApp) {
            musicListView
                .setAspectRatioVerticalVideos(Constants.DEFAULT_PLAYER_HEIGHT_VERTICAL_MODE);
        }
        List<Music> musicList = getMusicListUseCase.getAppMusic();
        if (currentProject.hasMusic()) {
            int positionSelected = 0;
            for(Music music: musicList) {
                if (music.getTitle().equals(currentProject.getMusic().getTitle())) {
                    positionSelected = musicList.indexOf(music);
                }
            }
            musicListView.showMusicSelected(positionSelected);
        }
        musicListView.showMusicList(musicList);
    }

    private void loadPlayerFromProject() {
        VMComposition vmCompositionCopy = null;
        try {
            vmCompositionCopy = new VMComposition(currentProject.getVMComposition());
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            illegalItemOnTrack.printStackTrace();
            Crashlytics.log("Error getting copy VMComposition " + illegalItemOnTrack);
        }
        musicListView.init(vmCompositionCopy);
    }

    public void pausePresenter() {
        musicListView.detachView();
    }

    @Override
    public void onObjectUpdated() {
        musicListView.updateProject();
    }

    public void selectMusic(Music music) {
        musicListView.navigateToDetailMusic(music.getMediaPath());
    }
}
