package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import com.videonasocialmedia.vimojo.settings.domain.GetPreferencesTransitionFromProjectUseCase;
import android.content.Context;

import com.videonasocialmedia.vimojo.sound.domain.AddMusicToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMusicFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMusicListUseCase;
import com.videonasocialmedia.vimojo.sound.domain.RemoveMusicFromProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;

import com.videonasocialmedia.vimojo.presentation.mvp.presenters.GetMusicFromProjectCallback;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnAddMediaFinishedListener;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.presentation.mvp.views.MusicDetailView;
import com.videonasocialmedia.vimojo.sound.domain.UpdateMusicVolumeProjectUseCase;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import java.util.List;

import javax.inject.Inject;

/**
 *
 */
public class MusicDetailPresenter implements OnVideosRetrieved, GetMusicFromProjectCallback,
        OnAddMediaFinishedListener {
    private AddMusicToProjectUseCase addMusicToProjectUseCase;
    private RemoveMusicFromProjectUseCase removeMusicFromProjectUseCase;
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    private GetMusicFromProjectUseCase getMusicFromProjectUseCase;
    private GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase;
    private UpdateMusicVolumeProjectUseCase updateMusicVolumeProjectUseCase;
    private MusicDetailView musicDetailView;
    public UserEventTracker userEventTracker;
    public Project currentProject;
    private Music musicSelected;
    private Context context;

    @Inject
    public MusicDetailPresenter(MusicDetailView musicDetailView,
                                UserEventTracker userEventTracker,
                                AddMusicToProjectUseCase addMusicToProjectUseCase,
                                RemoveMusicFromProjectUseCase removeMusicFromProjectUseCase,
                                GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
                                GetMusicFromProjectUseCase getMusicFromProjectUseCase,
                                GetPreferencesTransitionFromProjectUseCase
                                    getPreferencesTransitionFromProjectUseCase,
                                UpdateMusicVolumeProjectUseCase updateMusicVolumeProjectUseCase,
                                Context context) {
        this.musicDetailView = musicDetailView;
        this.userEventTracker = userEventTracker;
        this.addMusicToProjectUseCase = addMusicToProjectUseCase;
        this.removeMusicFromProjectUseCase = removeMusicFromProjectUseCase;

        this.getMediaListFromProjectUseCase = getMediaListFromProjectUseCase;
        this.getMusicFromProjectUseCase = getMusicFromProjectUseCase;
        this.getPreferencesTransitionFromProjectUseCase =
            getPreferencesTransitionFromProjectUseCase;
        this.updateMusicVolumeProjectUseCase = updateMusicVolumeProjectUseCase;
        this.context = context;

        // TODO(jliarte): 1/12/16 should it be a parameter of use case method?
        this.currentProject = loadCurrentProject();
        musicSelected = new Music("");
    }

    private Project loadCurrentProject() {
        // TODO(jliarte): this should make use of a repository or use case to load the Project
        return Project.getInstance(null, null, null);
    }

    public void init(String musicPath) {
        musicSelected = retrieveLocalMusic(musicPath);
        obtainMusicsAndVideos();
        if(getPreferencesTransitionFromProjectUseCase.isVideoFadeTransitionActivated()){
            musicDetailView.setVideoFadeTransitionAmongVideos();
        }
    }

    private void obtainMusicsAndVideos() {
        getMusicFromProjectUseCase.getMusicFromProject(this);
        getMediaListFromProjectUseCase.getMediaListFromProject(this);
    }

    public void removeMusic(Music music) {
        removeMusicFromProjectUseCase.removeMusicFromProject(music, 0);
        userEventTracker.trackMusicSet(currentProject);
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
        addMusicToProjectUseCase.addMusicToTrack(music,
            currentProject.getVMComposition().INDEX_AUDIO_TRACKS_MUSIC, this);
    }

    @Override
    public void onVideosRetrieved(List<Video> videoList) {
        musicDetailView.bindVideoList(videoList);
    }

    @Override
    public void onNoVideosRetrieved() {
        //TODO (javi.cabanas) show error
    }

    @Override
    public void onMusicRetrieved(Music musicOnProject) {
        if (musicOnProject!= null && musicOnProject.getMediaPath()
                .compareTo(musicSelected.getMediaPath()) == 0) {
            musicDetailView.setMusic(musicOnProject, true);
        } else {
            musicDetailView.setMusic(musicSelected, false);
        }
    }

    @Override
    public void onAddMediaItemToTrackError() {
        // TODO(jliarte): 30/11/16 implement error processing
    }

    @Override
    public void onAddMediaItemToTrackSuccess(Media media) {
        userEventTracker.trackMusicSet(currentProject);
        musicDetailView.goToEdit(media.getTitle());
    }

    public void setVolume(float volume) {
        updateMusicVolumeProjectUseCase.setVolumeMusic(currentProject, volume);
    }
}
