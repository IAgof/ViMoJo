package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.SoundView;

import java.util.List;

/**
 * Created by ruth on 13/09/16.
 */
public class SoundPresenter implements OnVideosRetrieved {

    private SoundView soundView;
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;

    public SoundPresenter(SoundView soundView) {
        getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        this.soundView = soundView;
    }

    public void getMediaListFromProject() {
        getMediaListFromProjectUseCase.getMediaListFromProject(this);
    }


    @Override
    public void onVideosRetrieved(List<Video> videoList) {
        soundView.bindVideoList(videoList);
    }

    @Override
    public void onNoVideosRetrieved() {
        //TODO Show error
        soundView.resetPreview();
    }
}
