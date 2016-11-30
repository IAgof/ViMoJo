package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMusicListUseCase;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.MusicListView;

import java.util.List;

/**
 * Created by ruth on 13/09/16.
 */
public class MusicListPresenter implements OnVideosRetrieved {

    private List<Music> availableMusic;
    private MusicListView soundListView;
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;

    public MusicListPresenter(MusicListView soundListView) {
        GetMusicListUseCase getMusicListUseCase = new GetMusicListUseCase();
        availableMusic = getMusicListUseCase.getAppMusic();
        getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        this.soundListView = soundListView;
    }

    public void onResume() {
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
        soundListView.bindVideoList(videoList);
    }

    @Override
    public void onNoVideosRetrieved() {
        //TODO Show error
        soundListView.resetPreview();
    }
}
