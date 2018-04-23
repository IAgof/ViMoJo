package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.utils.ElementChangedListener;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.domain.editor.GetAudioFromProjectUseCase;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnRemoveMediaFinishedListener;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.GetPreferencesTransitionFromProjectUseCase;
import android.content.Context;

import com.videonasocialmedia.vimojo.sound.domain.AddAudioUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMusicListUseCase;
import com.videonasocialmedia.vimojo.sound.domain.ModifyTrackUseCase;
import com.videonasocialmedia.vimojo.sound.domain.RemoveAudioUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;

import com.videonasocialmedia.vimojo.presentation.mvp.presenters.GetMusicFromProjectCallback;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnAddMediaFinishedListener;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.presentation.mvp.views.MusicDetailView;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import java.util.List;

import javax.inject.Inject;


/**
 *
 */
public class MusicDetailPresenter implements OnVideosRetrieved, GetMusicFromProjectCallback,
        ElementChangedListener{

    private final ProjectInstanceCache projectInstanceCache;
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    private GetAudioFromProjectUseCase getAudioFromProjectUseCase;
    private GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase;
    private MusicDetailView musicDetailView;
    public UserEventTracker userEventTracker;
    public Project currentProject;
    private Music musicSelected;
    private Context context;
    private AddAudioUseCase addAudioUseCase;
    private RemoveAudioUseCase removeAudioUseCase;
    private ModifyTrackUseCase modifyTrackUseCase;

    @Inject
    public MusicDetailPresenter(
            MusicDetailView musicDetailView, Context context, UserEventTracker userEventTracker,
            GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
            GetAudioFromProjectUseCase getAudioFromProjectUseCase,
            GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase,
            AddAudioUseCase addAudioUseCase, RemoveAudioUseCase removeAudioUseCase,
            ModifyTrackUseCase modifyTrackUseCase, ProjectInstanceCache projectInstanceCache) {
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
        this.projectInstanceCache = projectInstanceCache;
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
            public void onRemoveMediaItemFromTrackSuccess() {
                userEventTracker.trackMusicSet(currentProject);
                musicDetailView.goToSoundActivity();
            }
            @Override
            public void onRemoveMediaItemFromTrackError() {
                musicDetailView.showError(context
                    .getString(R.string.alert_dialog_title_message_removing_music));
            }
        });
    }

    private Music retrieveLocalMusic(String musicPath) {
        Music result = null;
        GetMusicListUseCase getMusicListUseCase = new GetMusicListUseCase(context);
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
        getAudioFromProjectUseCase.getVoiceOverFromProject(currentProject, new GetMusicFromProjectCallback() {
            @Override
            public void onMusicRetrieved(Music voiceOver) {
                musicDetailView.setVoiceOver(voiceOver);
            }
        });
    }

    public void setVolume(float volume) {
        // Now setVolume update MusicTrackVolume until Vimojo support setVolume by clip.
        modifyTrackUseCase.setTrackVolume(currentProject, currentProject.getAudioTracks()
            .get(Constants.INDEX_AUDIO_TRACK_MUSIC), volume);
    }

    @Override
    public void onObjectUpdated() {
        musicDetailView.updateProject();
    }
}
