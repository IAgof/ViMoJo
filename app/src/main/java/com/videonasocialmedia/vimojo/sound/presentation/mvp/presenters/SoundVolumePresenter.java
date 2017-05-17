package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;


import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.domain.UpdateCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.settings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.AddVoiceOverToProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.UpdateAudioTrackProjectUseCase;
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
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    private AddVoiceOverToProjectUseCase addVoiceOverToProjectUseCase;
    public UserEventTracker userEventTracker;
    public Project currentProject;
    private UpdateAudioTrackProjectUseCase updateAudioTrackProjectUseCase;
    private UpdateCurrentProjectUseCase updateCurrentProjectUseCase;

    @Inject
    public SoundVolumePresenter(SoundVolumeView soundVolumeView,
                                AddVoiceOverToProjectUseCase addVoiceOverToProjectUseCase,
                                GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
                                GetPreferencesTransitionFromProjectUseCase
                                    getPreferencesTransitionFromProjectUseCase,
                                UpdateAudioTrackProjectUseCase updateAudioTrackProjectUseCase,
                                UpdateCurrentProjectUseCase updateCurrentProjectUseCase) {
        this.soundVolumeView = soundVolumeView;
        this.addVoiceOverToProjectUseCase = addVoiceOverToProjectUseCase;
        this.getMediaListFromProjectUseCase = getMediaListFromProjectUseCase;
        this.getPreferencesTransitionFromProjectUseCase =
            getPreferencesTransitionFromProjectUseCase;
        this.updateAudioTrackProjectUseCase = updateAudioTrackProjectUseCase;
        this.updateCurrentProjectUseCase = updateCurrentProjectUseCase;
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

        updateAudioTrackProjectUseCase.addedNewTrack(Constants.INDEX_AUDIO_TRACK_VOICE_OVER);
        addVoiceOverToProjectUseCase.setVoiceOver(voiceOverPath, volume);
        updateAudioTrackProjectUseCase.setAudioTrackVolume(currentProject.getAudioTracks()
            .get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER), volume);
        updateCurrentProjectUseCase.updateProject();
        soundVolumeView.goToSoundActivity();
    }

}
