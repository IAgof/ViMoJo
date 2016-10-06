package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import com.videonasocialmedia.vimojo.sound.domain.AddMusicToProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMusicFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMusicListUseCase;
import com.videonasocialmedia.vimojo.sound.domain.RemoveMusicFromProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Media;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Music;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;

import com.videonasocialmedia.vimojo.presentation.mvp.presenters.GetMusicFromProjectCallback;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnAddMediaFinishedListener;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.presentation.mvp.views.MusicDetailView;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import java.util.List;


/**
 *
 */
public class MusicDetailPresenter implements OnVideosRetrieved, GetMusicFromProjectCallback, OnAddMediaFinishedListener {

    private AddMusicToProjectUseCase addMusicToProjectUseCase;
    private RemoveMusicFromProjectUseCase removeMusicFromProjectUseCase;
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    private GetMusicFromProjectUseCase getMusicFromProjectUseCase;
    private MusicDetailView musicDetailView;
    public UserEventTracker userEventTracker;
    public Project currentProject;
    private Music musicSelected;

    public MusicDetailPresenter(MusicDetailView musicDetailView, UserEventTracker userEventTracker) {
        this.musicDetailView = musicDetailView;
        addMusicToProjectUseCase = new AddMusicToProjectUseCase();
        removeMusicFromProjectUseCase = new RemoveMusicFromProjectUseCase();
        getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        getMusicFromProjectUseCase = new GetMusicFromProjectUseCase();
        this.currentProject = loadCurrentProject();
        this.userEventTracker = userEventTracker;
        musicSelected = new Music("");
    }

    private Project loadCurrentProject() {
        // TODO(jliarte): this should make use of a repository or use case to load the Project
        return Project.getInstance(null, null, null);
    }

    public void onResume(String musicPath) {
        musicSelected = retrieveLocalMusic(musicPath);
        getMediaListFromProjectUseCase.getMediaListFromProject(this);
        getMusicFromProjectUseCase.getMusicFromProject(this);

    }

    public void removeMusic(Music music) {
        currentProject.setMusicOnProject(false);
        removeMusicFromProjectUseCase.removeMusicFromProject(music, 0);
        userEventTracker.trackMusicSet(currentProject);
    }

    private Music retrieveLocalMusic(String musicPath) {
        Music result = null;
        GetMusicListUseCase getMusicListUseCase = new GetMusicListUseCase();
        List<Music> musicList = getMusicListUseCase.getAppMusic();
        for (Music music : musicList) {
            if (musicPath.compareTo(music.getMediaPath()) == 0) {
                result = music;
            }
        }
        return result;
    }

    public void addMusic(Music music) {
        addMusicToProjectUseCase.addMusicToTrack(music, 0, this);
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
        if(musicOnProject!= null && musicOnProject.getMediaPath().compareTo(musicSelected.getMediaPath()) == 0) {
            musicDetailView.setMusic(musicOnProject, true);
        } else {
            musicDetailView.setMusic(musicSelected, false);
        }
    }

    @Override
    public void onAddMediaItemToTrackError() {

    }

    @Override
    public void onAddMediaItemToTrackSuccess(Media media) {
        currentProject.setMusicOnProject(true);
        userEventTracker.trackMusicSet(currentProject);
        musicDetailView.goToEdit(media.getTitle());
    }
}
