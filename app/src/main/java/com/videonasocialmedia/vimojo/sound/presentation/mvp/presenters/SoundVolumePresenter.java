package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;


import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.sound.domain.AddVoiceOverToProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.OnMixAudioListener;
import com.videonasocialmedia.vimojo.sound.domain.RemoveMusicFromProjectUseCase;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.SoundVolumeView;
import com.videonasocialmedia.vimojo.utils.AndroidUtils;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import java.util.List;

/**
 * Created by ruth on 19/09/16.
 */
public class SoundVolumePresenter implements OnVideosRetrieved{

    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    protected RemoveMusicFromProjectUseCase removeMusicFromProjectUseCase;
    protected AddVoiceOverToProjectUseCase addVoiceOverToProject;
    private SoundVolumeView soundVolumeView;

    public Project currentProject;

    public SoundVolumePresenter(SoundVolumeView soundVolumeView){
        this.soundVolumeView = soundVolumeView;
        this.getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        this.addVoiceOverToProject = new AddVoiceOverToProjectUseCase();
        this.removeMusicFromProjectUseCase = new RemoveMusicFromProjectUseCase();
        this.currentProject = loadCurrentProject();
    }

    private Project loadCurrentProject() {
        return Project.getInstance(null, null, null);
    }

    public void init() {
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

    public void setVolume(String voiceOverPath, float volume) {
        addVoiceOverToProject.setVoiceOver(voiceOverPath, volume);
        soundVolumeView.goToEditActivity();
    }

    public void removeMusicFromProject() {
        if (currentProject.hasMusic()) {
            removeMusicFromProjectUseCase.removeMusicFromProject(currentProject.getMusic(), 0);
        }
    }
}
