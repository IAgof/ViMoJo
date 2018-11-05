package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.VMComposition;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.composition.domain.RemoveTrack;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateComposition;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateTrack;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnRemoveMediaFinishedListener;
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
import static com.videonasocialmedia.vimojo.utils.Constants.*;

/**
 * Created by ruth on 19/09/16.
 */
public class VoiceOverVolumePresenter extends VimojoPresenter {
    private final ProjectInstanceCache projectInstanceCache;
    private Context context;
    private VoiceOverVolumeView voiceOverVolumeView;
    protected UserEventTracker userEventTracker;
    protected Project currentProject;
    private ModifyTrackUseCase modifyTrackUseCase;
    private RemoveAudioUseCase removeAudioUseCase;
    private UpdateComposition updateComposition;
    private UpdateTrack updateTrack;
    private RemoveTrack removeTrack;
    private boolean amIAVerticalApp;

    @Inject
    public VoiceOverVolumePresenter(
        Context context, VoiceOverVolumeView voiceOverVolumeView,
        ModifyTrackUseCase modifyTrackUseCase, RemoveAudioUseCase removeAudioUseCase,
        ProjectInstanceCache projectInstanceCache, UpdateComposition updateComposition,
        @Named("amIAVerticalApp") boolean amIAVerticalApp,
        UpdateTrack updateTrack, RemoveTrack removeTrack, BackgroundExecutor backgroundExecutor,
        UserEventTracker userEventTracker) {
        super(backgroundExecutor, userEventTracker);
        this.context = context;
        this.voiceOverVolumeView = voiceOverVolumeView;
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
        voiceOverVolumeView.attachView(context);
        loadPlayerFromProject();
        if (amIAVerticalApp) {
            voiceOverVolumeView.setAspectRatioVerticalVideos(DEFAULT_PLAYER_HEIGHT_VERTICAL_MODE);
        }
    }

    public void pausePresenter() {
        voiceOverVolumeView.detachView();
    }

    private void loadPlayerFromProject() {
        VMComposition vmCompositionCopy = null;
        try {
            vmCompositionCopy = new VMComposition(currentProject.getVMComposition());
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            illegalItemOnTrack.printStackTrace();
            Crashlytics.log("Error getting copy VMComposition " + illegalItemOnTrack);
        }
        voiceOverVolumeView.init(vmCompositionCopy);
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

    public void setVolumeProgress(int progress) {
        voiceOverVolumeView.setVoiceOverVolume(progress *0.01f);
        voiceOverVolumeView.updateTagVolume(progress+" % ");
    }
}
