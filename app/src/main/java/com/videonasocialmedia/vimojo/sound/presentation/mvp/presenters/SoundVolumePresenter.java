package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;


import android.support.annotation.NonNull;

import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.domain.UpdateCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnAddMediaFinishedListener;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.settings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.AddAudioUseCase;
import com.videonasocialmedia.vimojo.sound.domain.AddVoiceOverToProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.UpdateAudioTrackProjectUseCase;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.SoundVolumeView;
import com.videonasocialmedia.vimojo.utils.FileUtils;
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
    private AddAudioUseCase addAudioUseCase;
    public UserEventTracker userEventTracker;
    public Project currentProject;

    @Inject
    public SoundVolumePresenter(SoundVolumeView soundVolumeView,
                                GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
                                GetPreferencesTransitionFromProjectUseCase
                                    getPreferencesTransitionFromProjectUseCase,
                                AddAudioUseCase addAudioUseCase) {
        this.soundVolumeView = soundVolumeView;
        this.getMediaListFromProjectUseCase = getMediaListFromProjectUseCase;
        this.getPreferencesTransitionFromProjectUseCase =
            getPreferencesTransitionFromProjectUseCase;
        this.addAudioUseCase = addAudioUseCase;
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

        Music voiceOver = getVoiceOverAsMusic(voiceOverPath, volume);

        addAudioUseCase.addMusic(voiceOver, Constants.INDEX_AUDIO_TRACK_VOICE_OVER,
            new OnAddMediaFinishedListener() {
            @Override
            public void onAddMediaItemToTrackSuccess(Media media) {
                soundVolumeView.goToSoundActivity();
            }

            @Override
            public void onAddMediaItemToTrackError() {
                soundVolumeView.showError("Problem adding voice over to project");
            }
        });
    }

    @NonNull
    private Music getVoiceOverAsMusic(String voiceOverPath, float volume) {
        Music voiceOver = new Music(voiceOverPath, volume, FileUtils.getDuration(voiceOverPath));
        voiceOver.setMusicTitle(com.videonasocialmedia.vimojo.utils.Constants.MUSIC_AUDIO_VOICEOVER_TITLE);
        voiceOver.setIconResourceId(R.drawable.activity_edit_audio_voice_over_icon);
        return voiceOver;
    }

}
