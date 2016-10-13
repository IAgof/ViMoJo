package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.vimojo.model.entities.editor.Profile;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Music;

import com.videonasocialmedia.vimojo.presentation.mvp.views.MusicDetailView;
import com.videonasocialmedia.vimojo.presentation.mvp.views.VideonaPlayerView;
import com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters.MusicDetailPresenter;
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

/**
 * Created by jliarte on 10/06/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class MusicDetailPresenterTest {
    @Mock private MusicDetailView musicDetailView;
    @Mock private VideonaPlayerView playerView;
    @Mock private MixpanelAPI mockedMixpanelAPI;
    @Mock private UserEventTracker mockedUserEventTracker;

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
        MusicDetailPresenter musicDetailPresenter = new MusicDetailPresenter(musicDetailView, userEventTracker);

        assertThat(musicDetailPresenter.userEventTracker, is(userEventTracker));
    }

    @Test
    public void constructorSetsCurrentProject() {
        Project videonaProject = getAProject();

        MusicDetailPresenter musicDetailPresenter = new MusicDetailPresenter(musicDetailView, mockedUserEventTracker);

        assertThat(musicDetailPresenter.currentProject, is(videonaProject));
    }

    @Test
    public void addMusicCallsTrackMusicSet() {
        MusicDetailPresenter musicDetailPresenter = new MusicDetailPresenter(musicDetailView, mockedUserEventTracker);
        Project videonaProject = getAProject();
        Music music = new Music(1, "Music title", 2, 3, "Music Author", "3");
        musicDetailPresenter.onMusicRetrieved(music);

        musicDetailPresenter.addMusic(music);

        Mockito.verify(mockedUserEventTracker).trackMusicSet(videonaProject);
    }

    @Test
    public void removeMusicCallsTrackMusicSet() {
        MusicDetailPresenter musicDetailPresenter = new MusicDetailPresenter(musicDetailView, mockedUserEventTracker);
        Project videonaProject = getAProject();
        Music music = new Music(1, "Music title", 2, 3, "Music Author", "3");
        musicDetailPresenter.removeMusic(music);

        Mockito.verify(mockedUserEventTracker).trackMusicSet(videonaProject);
    }

    public Project getAProject() {
        return Project.getInstance("title", "/path", Profile.getInstance(Profile.ProfileType.free));
    }
}
