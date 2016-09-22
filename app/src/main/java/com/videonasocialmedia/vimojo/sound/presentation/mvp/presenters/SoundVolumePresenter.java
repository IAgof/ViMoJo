package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMusicFromProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Music;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.GetMusicFromProjectCallback;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.SoundVolumeView;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import java.util.List;

/**
 * Created by ruth on 19/09/16.
 */
public class SoundVolumePresenter implements OnVideosRetrieved, GetMusicFromProjectCallback {

    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    private SoundVolumeView soundVolumeView;
    private GetMusicFromProjectUseCase getMusicFromProjectUseCase;

    public UserEventTracker userEventTracker;
    public Project currentProject;

    public SoundVolumePresenter(SoundVolumeView soundVolumeView){
        this.soundVolumeView=soundVolumeView;
        getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        getMusicFromProjectUseCase= new GetMusicFromProjectUseCase();
        this.currentProject = loadCurrentProject();
    }

    private Project loadCurrentProject() {
        return Project.getInstance(null, null, null);
    }

    public void onResume() {
        getMediaListFromProjectUseCase.getMediaListFromProject(this);
        getMusicFromProjectUseCase.getMusicFromProject(this);

    }

    @Override
    public void onVideosRetrieved(List<Video> videoList) {
        soundVolumeView.bindVideoList(videoList);

    }

    @Override
    public void onNoVideosRetrieved() {
        soundVolumeView.resetPreview();
    }

    @Override
    public void onMusicRetrieved(Music music) {
        soundVolumeView.setMusic(music);
    }
}
