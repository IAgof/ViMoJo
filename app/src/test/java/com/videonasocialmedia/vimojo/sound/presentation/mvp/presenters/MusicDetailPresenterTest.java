package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMusicFromProjectUseCase;
import com.videonasocialmedia.vimojo.galleryprojects.domain.UpdateCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Music;

import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.presentation.mvp.views.MusicDetailView;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer;
import com.videonasocialmedia.vimojo.settings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.AddMusicToProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.RemoveMusicFromProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.UpdateAudioTrackProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.UpdateMusicVolumeProjectUseCase;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.verify;

/**
 * Created by jliarte on 10/06/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class MusicDetailPresenterTest {
    @Mock private MusicDetailView musicDetailView;
    @Mock private VideonaPlayer playerView;
    @Mock private MixpanelAPI mockedMixpanelAPI;
    @Mock private UserEventTracker mockedUserEventTracker;
    @Mock private RemoveMusicFromProjectUseCase mockedRemoveMusicUseCase;
    @Mock private AddMusicToProjectUseCase mockedAddMusicToProjectUseCase;
    @Mock private UpdateMusicVolumeProjectUseCase mockedUpdateMusicVolumeProjectUseCase;
    @Mock private Context mockedContext;
    @Mock private GetMediaListFromProjectUseCase mockedGetMediaListFromProjectUseCase;
    @Mock private GetMusicFromProjectUseCase mockedGetMusicFromProject;
    @Mock private GetPreferencesTransitionFromProjectUseCase mockedGetPreferencesTransitionsFromProject;
    @Mock UpdateAudioTrackProjectUseCase mockedUpdateAudioTrackProjectUseCase;
    @Mock UpdateCurrentProjectUseCase mockedUpdateCurrentProjectUseCase;

    @Before
    public void injectMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
        Project.getInstance(null, null, null).clear();
    }

    @Test
    public void constructorSetsUserTracker() {
        UserEventTracker userEventTracker = UserEventTracker.getInstance(mockedMixpanelAPI);
        MusicDetailPresenter musicDetailPresenter =
                getMusicDetailPresenter(userEventTracker);

        assertThat(musicDetailPresenter.userEventTracker, is(userEventTracker));
    }

    @Test
    public void constructorSetsCurrentProject() {
        Project videonaProject = getAProject();

        MusicDetailPresenter musicDetailPresenter =
                getMusicDetailPresenter(mockedUserEventTracker);

        assertThat(musicDetailPresenter.currentProject, is(videonaProject));
    }

    @Test
    public void onAddMediaItemToTrackSuccessCallsTrackMusicSet() {
        MusicDetailPresenter musicDetailPresenter =
                getMusicDetailPresenter(mockedUserEventTracker);
        Project videonaProject = getAProject();
        Music music = new Music(1, "Music title", 2, 3, "Music Author", "3", 0);
        musicDetailPresenter.onMusicRetrieved(music);

        musicDetailPresenter.onAddMediaItemToTrackSuccess(music);

        verify(mockedUserEventTracker).trackMusicSet(videonaProject);
    }

    @Test
    public void removeMusicCallsTrackMusicSet() {
        MusicDetailPresenter musicDetailPresenter =
                getMusicDetailPresenter(mockedUserEventTracker);
        Project videonaProject = getAProject();
        Music music = new Music(1, "Music title", 2, 3, "Music Author", "3", 0);
//        musicDetailPresenter.removeMusicFromProjectUseCase = mockedRemoveMusicUseCase;

        musicDetailPresenter.removeMusic(music);

        verify(mockedUserEventTracker).trackMusicSet(videonaProject);
    }

    @Test
    public void removeMusicCallsGoToSoundActivity(){
        MusicDetailPresenter musicDetailPresenter =
            getMusicDetailPresenter(mockedUserEventTracker);
        Project videonaProject = getAProject();
        Music music = new Music(1, "Music title", 2, 3, "Music Author", "3", 0);

        musicDetailPresenter.removeMusic(music);

        verify(musicDetailView).goToSoundActivity();
    }

    public Project getAProject() {
        return Project.getInstance("title", "/path", Profile.getInstance(VideoResolution.Resolution.HD720,
                VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25));
    }

    @NonNull
    private MusicDetailPresenter getMusicDetailPresenter(UserEventTracker userEventTracker) {
        return new MusicDetailPresenter(musicDetailView, userEventTracker,
                mockedAddMusicToProjectUseCase, mockedRemoveMusicUseCase,
                mockedGetMediaListFromProjectUseCase, mockedGetMusicFromProject,
                mockedGetPreferencesTransitionsFromProject, mockedUpdateMusicVolumeProjectUseCase,
                mockedUpdateAudioTrackProjectUseCase, mockedUpdateCurrentProjectUseCase,
                mockedContext);
    }
}
