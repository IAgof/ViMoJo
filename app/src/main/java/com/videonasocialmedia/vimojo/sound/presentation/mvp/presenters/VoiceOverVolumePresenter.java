package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import android.content.Context;

import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.composition.domain.RemoveTrack;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateComposition;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateTrack;
import com.videonasocialmedia.vimojo.domain.editor.GetAudioFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnRemoveMediaFinishedListener;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.ModifyTrackUseCase;
import com.videonasocialmedia.vimojo.sound.domain.RemoveAudioUseCase;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.VoiceOverVolumeView;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.view.BackgroundExecutor;
import com.videonasocialmedia.vimojo.view.VimojoPresenter;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import static com.videonasocialmedia.videonamediaframework.model.Constants.INDEX_AUDIO_TRACK_VOICE_OVER;

/**
 * Created by ruth on 19/09/16.
 */
public class VoiceOverVolumePresenter extends VimojoPresenter implements OnVideosRetrieved {
    private final ProjectInstanceCache projectInstanceCache;
    private GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase;
    private VoiceOverVolumeView voiceOverVolumeView;
    private Context context;
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    protected UserEventTracker userEventTracker;
    protected Project currentProject;
    private GetAudioFromProjectUseCase getAudioFromProjectUseCase;
    private ModifyTrackUseCase modifyTrackUseCase;
    private RemoveAudioUseCase removeAudioUseCase;
    private UpdateComposition updateComposition;
    private UpdateTrack updateTrack;
    private RemoveTrack removeTrack;
    private boolean amIAVerticalApp;

    @Inject
    public VoiceOverVolumePresenter(
        Context context, VoiceOverVolumeView voiceOverVolumeView,
        GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
        GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase,
        GetAudioFromProjectUseCase getAudioFromProjectUseCase,
        ModifyTrackUseCase modifyTrackUseCase, RemoveAudioUseCase removeAudioUseCase,
        ProjectInstanceCache projectInstanceCache, UpdateComposition updateComposition,
        @Named("amIAVerticalApp") boolean amIAVerticalApp,
        UpdateTrack updateTrack, RemoveTrack removeTrack, BackgroundExecutor backgroundExecutor) {
        super(backgroundExecutor);
        this.context = context;
        this.voiceOverVolumeView = voiceOverVolumeView;
        this.getMediaListFromProjectUseCase = getMediaListFromProjectUseCase;
        this.getPreferencesTransitionFromProjectUseCase =
            getPreferencesTransitionFromProjectUseCase;
        this.getAudioFromProjectUseCase = getAudioFromProjectUseCase;
        this.modifyTrackUseCase = modifyTrackUseCase;
        this.removeAudioUseCase = removeAudioUseCase;
        this.projectInstanceCache = projectInstanceCache;
        this.updateComposition = updateComposition;
        this.amIAVerticalApp = amIAVerticalApp;
        this.updateTrack = updateTrack;
        this.removeTrack = removeTrack;
    }

    public void updatePresenter() {
        this.currentProject = projectInstanceCache.getCurrentProject();
        obtainVideos();
        retrieveMusic();
        if (getPreferencesTransitionFromProjectUseCase.isVideoFadeTransitionActivated(currentProject)) {
            voiceOverVolumeView.setVideoFadeTransitionAmongVideos();
        }
        if (getPreferencesTransitionFromProjectUseCase.isAudioFadeTransitionActivated(currentProject) &&
            !currentProject.getVMComposition().hasMusic()) {
            voiceOverVolumeView.setAudioFadeTransitionAmongVideos();
        }
        if (amIAVerticalApp) {
            voiceOverVolumeView.setAspectRatioVerticalVideos();
        }
    }

    private void obtainVideos() {
        getMediaListFromProjectUseCase.getMediaListFromProject(currentProject, this);
    }

    private void retrieveMusic() {
        if (currentProject.getVMComposition().hasMusic()) {
            getAudioFromProjectUseCase.getMusicFromProject(currentProject, music -> {
                voiceOverVolumeView.setMusic(music);
                Track trackMusic = currentProject.getAudioTracks()
                        .get(Constants.INDEX_AUDIO_TRACK_MUSIC);
                if (trackMusic.isMuted()) {
                    voiceOverVolumeView.muteMusic();
                }
            });
        }
    }

    @Override
    public void onVideosRetrieved(List<Video> videoList) {
        voiceOverVolumeView.bindVideoList(videoList);
        Track videoTrack = currentProject.getMediaTrack();
        if (videoTrack.isMuted()) {
            voiceOverVolumeView.muteVideo();
        }
    }

    @Override
    public void onNoVideosRetrieved() {
        voiceOverVolumeView.resetPreview();
    }

    public void setVoiceOverVolume(float volume) {
        modifyTrackUseCase.setTrackVolume(currentProject.getAudioTracks()
                .get(Constants.INDEX_AUDIO_TRACK_VOICE_OVER), volume);
        voiceOverVolumeView.goToSoundActivity();
        executeUseCaseCall(() -> updateComposition.updateComposition(currentProject));
    }

    public void deleteVoiceOver() {
        executeUseCaseCall(() -> {
            Music voiceOver = currentProject.getVoiceOver();
            removeAudioUseCase.removeMusic(currentProject, voiceOver,
                    INDEX_AUDIO_TRACK_VOICE_OVER, new OnRemoveMediaFinishedListener() {
                        @Override
                        public void onRemoveMediaItemFromTrackSuccess(List<Media> removedMedias) {

                        }

                        @Override
                        public void onRemoveMediaItemFromTrackError() {
                            voiceOverVolumeView.showError(context.getString(R.string
                                    .alert_dialog_title_message_adding_voice_over));
                        }

                        @Override
                        public void onTrackUpdated(Track track) {
                            updateTrack.update(track);
                        }

                        @Override
                        public void onTrackRemoved(Track track) {
                            removeTrack.remove(track);
                        }
                    });
        });
    }

}
