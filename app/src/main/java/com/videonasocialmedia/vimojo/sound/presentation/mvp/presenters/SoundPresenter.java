package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.domain.editor.GetMusicFromProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.GetMusicFromProjectCallback;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.settings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.views.SoundView;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by ruth on 13/09/16.
 */
public class SoundPresenter implements OnVideosRetrieved {

    private final Project currentProject;
    private final GetPreferencesTransitionFromProjectUseCase
            getPreferencesTransitionFromProjectUseCase;
    private final GetMusicFromProjectUseCase getMusicFromProjectUseCase;
    private SoundView soundView;
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;

    @Inject
    public SoundPresenter(SoundView soundView, GetMediaListFromProjectUseCase
        getMediaListFromProjectUseCase, GetPreferencesTransitionFromProjectUseCase
        getPreferencesTransitionFromProjectUseCase) {
        this.getMediaListFromProjectUseCase = getMediaListFromProjectUseCase;
        this.getPreferencesTransitionFromProjectUseCase = getPreferencesTransitionFromProjectUseCase;
        this.getMusicFromProjectUseCase = new GetMusicFromProjectUseCase();
        this.soundView = soundView;
        this.currentProject = loadCurrentProject();
    }

    public Project loadCurrentProject() {
        // TODO(jliarte): this should make use of a repository or use case to load the Project
        return Project.getInstance(null, null, null);
    }

    public void init() {
        obtainVideos();
        retrieveCompositionMusic();
        // TODO(jliarte): 9/03/17 should move this code fragments to VideonaPlayer to automatically set transitions form AVComposition info
        if(getPreferencesTransitionFromProjectUseCase.isVideoFadeTransitionActivated()){
            soundView.setVideoFadeTransitionAmongVideos();
        }
        if(getPreferencesTransitionFromProjectUseCase.isAudioFadeTransitionActivated() &&
            !currentProject.getVMComposition().hasMusic()){
            soundView.setAudioFadeTransitionAmongVideos();
        }
    }

    private void retrieveCompositionMusic() {
        if (currentProject.getVMComposition().hasMusic()) {
            getMusicFromProjectUseCase.getMusicFromProject(new GetMusicFromProjectCallback() {
                @Override
                public void onMusicRetrieved(Music music) {
                    soundView.setMusic(music);
                }
            });
        }
    }

    private void obtainVideos() {
        getMediaListFromProjectUseCase.getMediaListFromProject(this);
    }

    @Override
    public void onVideosRetrieved(List<Video> videoList) {
        soundView.bindVideoList(videoList);
    }

    @Override
    public void onNoVideosRetrieved() {
        //TODO Show error
        soundView.resetPreview();
    }
}
