package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.track.Track;
import com.videonasocialmedia.videonamediaframework.model.media.utils.ElementChangedListener;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.asset.domain.usecase.RemoveMedia;
import com.videonasocialmedia.vimojo.composition.domain.RemoveTrack;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateComposition;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateTrack;
import com.videonasocialmedia.vimojo.domain.editor.GetAudioFromProjectUseCase;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnRemoveMediaFinishedListener;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.GetPreferencesTransitionFromProjectUseCase;
import android.content.Context;
import android.util.Log;

import com.videonasocialmedia.vimojo.sound.domain.AddAudioUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMusicListUseCase;
import com.videonasocialmedia.vimojo.sound.domain.ModifyTrackUseCase;
import com.videonasocialmedia.vimojo.sound.domain.RemoveAudioUseCase;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;

import com.videonasocialmedia.vimojo.presentation.mvp.presenters.GetMusicFromProjectCallback;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnAddMediaFinishedListener;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.presentation.mvp.views.MusicDetailView;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.view.VimojoPresenter;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;


/**
 *
 */
public class MusicDetailPresenter extends VimojoPresenter implements OnVideosRetrieved,
    GetMusicFromProjectCallback, ElementChangedListener {
    private final String LOG_TAG = getClass().getSimpleName();
    private final ProjectInstanceCache projectInstanceCache;
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    private GetAudioFromProjectUseCase getAudioFromProjectUseCase;
    private GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase;
    private MusicDetailView musicDetailView;
    protected UserEventTracker userEventTracker;
    protected Project currentProject;
    private Music musicSelected;
    private Context context;
    private AddAudioUseCase addAudioUseCase;
    private RemoveAudioUseCase removeAudioUseCase;
    private ModifyTrackUseCase modifyTrackUseCase;
    private GetMusicListUseCase getMusicListUseCase;
    private UpdateComposition updateComposition;
    private RemoveMedia removeMedia;
    private UpdateTrack updateTrack;
    private RemoveTrack removeTrack;

    @Inject
    public MusicDetailPresenter(
            MusicDetailView musicDetailView, Context context, UserEventTracker userEventTracker,
            GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
            GetAudioFromProjectUseCase getAudioFromProjectUseCase,
            GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase,
            AddAudioUseCase addAudioUseCase, RemoveAudioUseCase removeAudioUseCase,
            ModifyTrackUseCase modifyTrackUseCase, GetMusicListUseCase getMusicListUseCase,
            ProjectInstanceCache projectInstanceCache, UpdateComposition updateComposition,
            RemoveMedia removeMedia, UpdateTrack updateTrack, RemoveTrack removeTrack) {
        this.musicDetailView = musicDetailView;
        this.userEventTracker = userEventTracker;
        this.getMediaListFromProjectUseCase = getMediaListFromProjectUseCase;
        this.getAudioFromProjectUseCase = getAudioFromProjectUseCase;
        this.getPreferencesTransitionFromProjectUseCase =
            getPreferencesTransitionFromProjectUseCase;
        this.context = context;
        this.addAudioUseCase = addAudioUseCase;
        this.removeAudioUseCase = removeAudioUseCase;
        this.modifyTrackUseCase = modifyTrackUseCase;
        this.getMusicListUseCase = getMusicListUseCase;
        this.projectInstanceCache = projectInstanceCache;
        this.updateComposition = updateComposition;
        this.removeMedia = removeMedia;
        this.updateTrack = updateTrack;
        this.removeTrack = removeTrack;
        musicSelected = new Music("", 0);
    }

    public void updatePresenter(String musicPath) {
        this.currentProject = projectInstanceCache.getCurrentProject();
        currentProject.addListener(this);
        musicSelected = retrieveLocalMusic(musicPath);
        // TODO:(alvaro.martinez) 12/04/17 Delete this force of volume when Vimojo support more
        // than one music, at this moment, music track same as music volume
        musicSelected.setVolume(currentProject.getAudioTracks()
            .get(Constants.INDEX_AUDIO_TRACK_MUSIC).getVolume());
        obtainMusicsAndVideos();
        if (getPreferencesTransitionFromProjectUseCase
                .isVideoFadeTransitionActivated(currentProject)) {
            musicDetailView.setVideoFadeTransitionAmongVideos();
        }
    }

    private void obtainMusicsAndVideos() {
        getAudioFromProjectUseCase.getMusicFromProject(currentProject, this);
        getMediaListFromProjectUseCase.getMediaListFromProject(currentProject, this);
    }

    public void removeMusic(final Music music) {
        removeAudioUseCase.removeMusic(currentProject, music, Constants.INDEX_AUDIO_TRACK_MUSIC,
            new OnRemoveMediaFinishedListener() {
            @Override
            public void onRemoveMediaItemFromTrackSuccess(List<Media> removedMedias) {
                userEventTracker.trackMusicSet(currentProject);
                Futures.addCallback(
                        executeUseCaseCall(() -> {
                            removeMedia.removeMedias(removedMedias);
                            updateComposition.updateComposition(currentProject);
                        }),
                        new FutureCallback<Object>() {
                            @Override
                            public void onSuccess(@Nullable Object result) {
                                musicDetailView.goToSoundActivity();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                Log.e(LOG_TAG, "Error updating composition on removeMusic.success", t);
                                musicDetailView.goToSoundActivity();
                            }
                        });
            }
            @Override
            public void onRemoveMediaItemFromTrackError() {
                musicDetailView.showError(context
                    .getString(R.string.alert_dialog_title_message_removing_music));
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
    }

    private Music retrieveLocalMusic(String musicPath) {
        Music result = null;
        List<Music> musicList = getMusicListUseCase.getAppMusic();
        for (Music music : musicList) {
            if (musicPath.compareTo(music.getMediaPath()) == 0) {
                result = music;
            }
        }
        return result;
    }

    public void addMusic(Music music, float volume) {
        music.setVolume(volume);
        addAudioUseCase.addMusic(currentProject, music, Constants.INDEX_AUDIO_TRACK_MUSIC,
            new OnAddMediaFinishedListener() {
            @Override
            public void onAddMediaItemToTrackSuccess(Media media) {
                userEventTracker.trackMusicSet(currentProject);
                executeUseCaseCall(() -> updateComposition.updateComposition(currentProject));
                musicDetailView.goToSoundActivity();
            }

            @Override
            public void onAddMediaItemToTrackError() {
                musicDetailView.showError(
                    context.getString(R.string.alert_dialog_title_message_adding_music));
            }
        });
    }

    @Override
    public void onVideosRetrieved(List<Video> videoList) {
        musicDetailView.bindVideoList(videoList);
        if(currentProject.hasVoiceOver()){
            retrieveVoiceOver();
        }
    }

    @Override
    public void onNoVideosRetrieved() {
        musicDetailView.showError("No videos retrieved");
    }

    @Override
    public void onMusicRetrieved(Music musicOnProject) {
        if (musicOnProject!= null && musicOnProject.getMediaPath()
                .compareTo(musicSelected.getMediaPath()) == 0) {
            // TODO:(alvaro.martinez) 12/04/17 Delete this force of volume when Vimojo support
            // more than one music, at this moment, music track same as music volume
            musicOnProject.setVolume(currentProject.getAudioTracks()
                .get(Constants.INDEX_AUDIO_TRACK_MUSIC).getVolume());
            musicDetailView.setMusic(musicOnProject, true);
        } else {
            musicDetailView.setMusic(musicSelected, false);
        }
    }

    private void retrieveVoiceOver() {
        getAudioFromProjectUseCase.getVoiceOverFromProject(currentProject,
                voiceOver -> musicDetailView.setVoiceOver(voiceOver));
    }

    public void setVolume(float volume) {
        // Now setVolume update MusicTrackVolume until Vimojo support setVolume by clip.
        modifyTrackUseCase.setTrackVolume(currentProject.getAudioTracks()
            .get(Constants.INDEX_AUDIO_TRACK_MUSIC), volume);
        executeUseCaseCall(() -> updateComposition.updateComposition(currentProject));
    }

    @Override
    public void onObjectUpdated() {
        musicDetailView.updateProject();
    }
}
