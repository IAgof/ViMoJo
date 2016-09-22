package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import android.content.Intent;
import android.support.design.widget.Snackbar;

import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMusicFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.RemoveMusicFromProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Music;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.GetMusicFromProjectCallback;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.presentation.views.services.ExportProjectService;
import com.videonasocialmedia.vimojo.sound.domain.MixAudioUseCase;
import com.videonasocialmedia.vimojo.sound.domain.OnMixAudioListener;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.SoundVolumeView;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import java.util.List;

/**
 * Created by ruth on 19/09/16.
 */
public class SoundVolumePresenter implements OnVideosRetrieved, OnMixAudioListener,
        GetMusicFromProjectCallback {

    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    private RemoveMusicFromProjectUseCase removeMusicFromProjectUseCase;
    private MixAudioUseCase mixAudioUseCase;
    private SoundVolumeView soundVolumeView;
    private GetMusicFromProjectUseCase getMusicFromProjectUseCase;

    public UserEventTracker userEventTracker;
    public Project currentProject;

    public SoundVolumePresenter(SoundVolumeView soundVolumeView){
        this.soundVolumeView=soundVolumeView;
        getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        removeMusicFromProjectUseCase = new RemoveMusicFromProjectUseCase();
        mixAudioUseCase = new MixAudioUseCase(this);
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

    public void setVolume(String mediaPath, String musicPath, float volume){
        mixAudioUseCase.mixAudio(mediaPath, musicPath,volume);
    }

    @Override
    public void onMixAudioSuccess() {
        soundVolumeView.goToEditActivity();
    }

    @Override
    public void onMixAudioError() {

    }

    @Override
    public void onMusicRetrieved(Music music) {
        soundVolumeView.setMusic(music);
    }

    public void removeMusicFromProject(Music removeMusic) {

        removeMusicFromProjectUseCase.removeMusicFromProject(removeMusic, 0);
    }
}
