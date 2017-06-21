package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import android.content.Context;

import com.videonasocialmedia.vimojo.domain.editor.GetAudioFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMusicListUseCase;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.GetMusicFromProjectCallback;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.settings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.MusicListView;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by ruth on 13/09/16.
 */
public class MusicListPresenter implements OnVideosRetrieved, GetMusicFromProjectCallback {
    private final Context context;
    private List<Music> availableMusic;
    private MusicListView musicListView;
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    private GetAudioFromProjectUseCase getAudioFromProjectUseCase;
    private GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase;

    @Inject
    public MusicListPresenter(MusicListView musicListView, Context context,
                              GetMusicListUseCase getMusicListUseCase,
                              GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
                              GetAudioFromProjectUseCase getAudioFromProjectUseCase,
                              GetPreferencesTransitionFromProjectUseCase
                                  getPreferencesTransitionFromProjectUseCase) {
        this.context = context;
        availableMusic = getMusicListUseCase.getAppMusic();
        this.getMediaListFromProjectUseCase = getMediaListFromProjectUseCase;
        this.getAudioFromProjectUseCase = getAudioFromProjectUseCase;
        this.getPreferencesTransitionFromProjectUseCase = getPreferencesTransitionFromProjectUseCase;
        this.musicListView = musicListView;
    }

    public void init() {
        obtainMusicsAndVideos();
        if(getPreferencesTransitionFromProjectUseCase.isVideoFadeTransitionActivated()){
            musicListView.setVideoFadeTransitionAmongVideos();
        }
    }

    private void obtainMusicsAndVideos() {
        getAudioFromProjectUseCase.getMusicFromProject(this);
        getMediaListFromProjectUseCase.getMediaListFromProject(this);
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
        if(getAudioFromProjectUseCase.hasBeenMusicSelected()){
            musicListView.goToDetailActivity(music.getMediaPath());
        }
    }
}
