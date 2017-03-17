package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;


import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.settings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.AddVoiceOverToProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.RemoveMusicFromProjectUseCase;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.SoundVolumeView;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by ruth on 19/09/16.
 */
public class SoundVolumePresenter implements OnVideosRetrieved {
    private GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase;
    private SoundVolumeView soundVolumeView;
    private RemoveMusicFromProjectUseCase removeMusicFromProjectUseCase;
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    private AddVoiceOverToProjectUseCase addVoiceOverToProject;

    public UserEventTracker userEventTracker;
    public Project currentProject;

    @Inject
    public SoundVolumePresenter(SoundVolumeView soundVolumeView,
                                RemoveMusicFromProjectUseCase removeMusicFromProjectUseCase,
                                AddVoiceOverToProjectUseCase addVoiceOverToProject,
                                GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
                                GetPreferencesTransitionFromProjectUseCase
                                    getPreferencesTransitionFromProjectUseCase) {
        this.soundVolumeView = soundVolumeView;
        this.removeMusicFromProjectUseCase = removeMusicFromProjectUseCase;
        this.addVoiceOverToProject = addVoiceOverToProject;
        this.getMediaListFromProjectUseCase = getMediaListFromProjectUseCase;
        this.getPreferencesTransitionFromProjectUseCase =
            getPreferencesTransitionFromProjectUseCase;
        this.currentProject = loadCurrentProject();
    }

    private Project loadCurrentProject() {
        return Project.getInstance(null, null, null);
    }

    public void init() {
        obtainVideos();
        if(getPreferencesTransitionFromProjectUseCase.isVideoFadeTransitionActivated()){
            soundVolumeView.setVideoFadeTransitionAmongVideos();
        }
        if(getPreferencesTransitionFromProjectUseCase.isAudioFadeTransitionActivated() &&
            !currentProject.getVMComposition().hasMusic()){
            soundVolumeView.setAudioFadeTransitionAmongVideos();
        }
    }

    private void obtainVideos() {
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

    public void setVoiceOver(String voiceOverPath, float volume) {
        // if music has been selected before, delete it
        removeMusicFromProject();

        addVoiceOverToProject.setVoiceOver(currentProject, voiceOverPath, volume);
        soundVolumeView.goToEditActivity();
    }


    public void removeMusicFromProject() {
        if (currentProject.hasMusic()) {
            removeMusicFromProjectUseCase.removeMusicFromProject(currentProject.getMusic(), 0);
        }
    }
}
