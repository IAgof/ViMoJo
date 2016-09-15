package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMusicListUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Music;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.presentation.mvp.views.VideonaPlayerView;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.SoundListView;

import java.util.List;

/**
 * Created by ruth on 13/09/16.
 */
public class SoundListPresenter implements OnVideosRetrieved {

    private List<Music> availableMusic;
    private SoundListView soundListView;
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    private VideonaPlayerView playerView;

    public SoundListPresenter(SoundListView soundListView, VideonaPlayerView playerView) {
        this.playerView = playerView;
        GetMusicListUseCase getMusicListUseCase = new GetMusicListUseCase();
        availableMusic = getMusicListUseCase.getAppMusic();
        getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        this.soundListView = soundListView;
    }

    public void onCreate() {
        getMediaListFromProjectUseCase.getMediaListFromProject(this);
    }

    public void onStart() {
        soundListView.showVideoList(availableMusic);
    }

    public void getAvailableMusic() {
        soundListView.showVideoList(availableMusic);
    }

    @Override
    public void onVideosRetrieved(List<Video> videoList) {
        playerView.bindVideoList(videoList);
    }

    @Override
    public void onNoVideosRetrieved() {
        //TODO Show error
    }
}
