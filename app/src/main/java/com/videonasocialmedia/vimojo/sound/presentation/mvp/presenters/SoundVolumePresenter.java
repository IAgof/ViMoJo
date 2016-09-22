package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import android.content.Intent;
import android.support.design.widget.Snackbar;

import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
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
public class SoundVolumePresenter implements OnVideosRetrieved, OnMixAudioListener {

    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    private MixAudioUseCase mixAudioUseCase;
    private SoundVolumeView soundVolumeView;

    public UserEventTracker userEventTracker;
    public Project currentProject;

    public SoundVolumePresenter(SoundVolumeView soundVolumeView){
        this.soundVolumeView=soundVolumeView;
        getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        mixAudioUseCase = new MixAudioUseCase(this);
        this.currentProject = loadCurrentProject();
    }

    private Project loadCurrentProject() {
        return Project.getInstance(null, null, null);
    }

    public void onCreate() {
        getMediaListFromProjectUseCase.getMediaListFromProject(this);
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
}
