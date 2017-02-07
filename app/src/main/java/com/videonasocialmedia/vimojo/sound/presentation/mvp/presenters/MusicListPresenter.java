package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import android.content.Context;

import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMusicFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMusicListUseCase;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.GetMusicFromProjectCallback;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.MusicListView;

import java.util.List;

/**
 * Created by ruth on 13/09/16.
 */
public class MusicListPresenter implements OnVideosRetrieved, GetMusicFromProjectCallback {
    private final Context context;
    private List<Music> availableMusic;
    private MusicListView musicListView;
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    private GetMusicFromProjectUseCase getMusicFromProjectUseCase;

    public MusicListPresenter(MusicListView musicListView, Context context) {
        this.context = context;
        GetMusicListUseCase getMusicListUseCase = new GetMusicListUseCase(this.context);
        availableMusic = getMusicListUseCase.getAppMusic();
        getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        getMusicFromProjectUseCase = new GetMusicFromProjectUseCase();
        this.musicListView = musicListView;
    }

    public void onResume() {
        getMusicFromProjectUseCase.getMusicFromProject(this);
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
        if(getMusicFromProjectUseCase.hasBeenMusicSelected()){
            musicListView.goToDetailActivity(music.getMediaPath());
        }
    }
}
