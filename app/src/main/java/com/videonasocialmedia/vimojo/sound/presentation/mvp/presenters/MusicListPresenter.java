package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import android.content.Context;

import com.videonasocialmedia.videonamediaframework.model.media.utils.ElementChangedListener;
import com.videonasocialmedia.vimojo.domain.editor.GetAudioFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMusicListUseCase;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.cut.domain.model.Project;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.GetMusicFromProjectCallback;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.MusicListView;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by ruth on 13/09/16.
 */
public class MusicListPresenter implements OnVideosRetrieved, GetMusicFromProjectCallback,
        ElementChangedListener {
    private final Context context;
    private Project currentProject;
    private final ProjectInstanceCache projectInstanceCache;
    private List<Music> availableMusic;
    private MusicListView musicListView;
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    private GetAudioFromProjectUseCase getAudioFromProjectUseCase;
    private GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase;

    @Inject
    public MusicListPresenter(
            MusicListView musicListView, Context context, GetMusicListUseCase getMusicListUseCase,
            GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
            GetAudioFromProjectUseCase getAudioFromProjectUseCase,
            GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase,
            ProjectInstanceCache projectInstanceCache) {
        this.context = context;
        availableMusic = getMusicListUseCase.getAppMusic();
        this.getMediaListFromProjectUseCase = getMediaListFromProjectUseCase;
        this.getAudioFromProjectUseCase = getAudioFromProjectUseCase;
        this.getPreferencesTransitionFromProjectUseCase = getPreferencesTransitionFromProjectUseCase;
        this.musicListView = musicListView;
        this.projectInstanceCache = projectInstanceCache;
    }

    public void updatePresenter() {
        this.currentProject = projectInstanceCache.getCurrentProject();
        currentProject.addListener(this);
        obtainMusicsAndVideos();
        if (getPreferencesTransitionFromProjectUseCase
                .isVideoFadeTransitionActivated(currentProject)) {
            musicListView.setVideoFadeTransitionAmongVideos();
        }
    }

    private void obtainMusicsAndVideos() {
        getAudioFromProjectUseCase.getMusicFromProject(currentProject, this);
        getMediaListFromProjectUseCase.getMediaListFromProject(currentProject, this);
    }

    public void onStart() {
        musicListView.showVideoList(availableMusic);
    }

    public void getAvailableMusic() {
        musicListView.showVideoList(availableMusic);
    }

    @Override
    public void onVideosRetrieved(List<Video> videoList) {
        musicListView.bindVideoList(videoList);
    }

    @Override
    public void onNoVideosRetrieved() {
        //TODO Show error
        musicListView.resetPreview();
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
