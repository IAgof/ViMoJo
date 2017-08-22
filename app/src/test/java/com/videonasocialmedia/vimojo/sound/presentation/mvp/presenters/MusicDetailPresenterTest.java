package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.AudioTrack;
import com.videonasocialmedia.vimojo.domain.editor.GetAudioFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Music;

import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnAddMediaFinishedListener;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnRemoveMediaFinishedListener;
import com.videonasocialmedia.vimojo.presentation.mvp.views.MusicDetailView;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer;
import com.videonasocialmedia.vimojo.settings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.AddAudioUseCase;
import com.videonasocialmedia.vimojo.sound.domain.ModifyTrackUseCase;
import com.videonasocialmedia.vimojo.sound.domain.RemoveAudioUseCase;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
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
    @Mock private Context mockedContext;
    @Mock private GetMediaListFromProjectUseCase mockedGetMediaListFromProjectUseCase;
    @Mock private GetAudioFromProjectUseCase mockedGetMusicFromProject;
    @Mock private GetPreferencesTransitionFromProjectUseCase
            mockedGetPreferencesTransitionsFromProject;
    @Mock private AddAudioUseCase mockedAddAudioUseCase;
    @Mock private RemoveAudioUseCase mockedRemoveAudioUseCase;
    @Mock private ModifyTrackUseCase mockedModifyTrackUseCase;

    @Mock Music mockedMusic;

    @Mock private OnRemoveMediaFinishedListener mockedOnRemoveMediaFinishedListener;

    @InjectMocks MusicDetailPresenter injectedPresenter;

    @Before
    public void injectMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
        Project.getInstance(null, null, null, null).clear();
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
    public void addMusicCallsGoToSoundActivityOnAddMediaItemFromTrackSuccess() {
        MusicDetailPresenter musicDetailPresenter =
            getMusicDetailPresenter(mockedUserEventTracker);
        Project videonaProject = getAProject();
        final Music music = new Music(1, "Music title", 2, 3, "Music Author", "3", 0);
        float volumeMusic = 0.85f;
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                OnAddMediaFinishedListener listener =
                    invocation.getArgumentAt(2, OnAddMediaFinishedListener.class);
                listener.onAddMediaItemToTrackSuccess(music);
                return null;
            }
        }).when(mockedAddAudioUseCase).addMusic(eq(music),
            eq(Constants.INDEX_AUDIO_TRACK_MUSIC),
            Matchers.any(OnAddMediaFinishedListener.class));

        musicDetailPresenter.addMusic(music, volumeMusic);

        verify(musicDetailView).goToSoundActivity();
        verify(mockedUserEventTracker).trackMusicSet(videonaProject);
    }

    @Test
    public void addMusicCallsGoToSoundActivityOnAddMediaItemFromTrackError() {
        MusicDetailPresenter musicDetailPresenter =
            getMusicDetailPresenter(mockedUserEventTracker);
        Project videonaProject = getAProject();
        final Music music = new Music(1, "Music title", 2, 3, "Music Author", "3", 0);
        float volumeMusic = 0.85f;
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                OnAddMediaFinishedListener listener =
                    invocation.getArgumentAt(2, OnAddMediaFinishedListener.class);
                listener.onAddMediaItemToTrackError();
                return null;
            }
        }).when(mockedAddAudioUseCase).addMusic(eq(music),
            eq(Constants.INDEX_AUDIO_TRACK_MUSIC),
            Matchers.any(OnAddMediaFinishedListener.class));

        musicDetailPresenter.addMusic(music, volumeMusic);

        verify(musicDetailView).showError(anyString());
    }

    @Test
    public void removeMusicCallsGoToSoundActivityOnRemoveMediaItemFromTrackSuccess() {
        MusicDetailPresenter musicDetailPresenter =
            getMusicDetailPresenter(mockedUserEventTracker);
        Project videonaProject = getAProject();
        Music music = new Music(1, "Music title", 2, 3, "Music Author", "3", 0);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                OnRemoveMediaFinishedListener listener =
                    invocation.getArgumentAt(2, OnRemoveMediaFinishedListener.class);
                listener.onRemoveMediaItemFromTrackSuccess();
                return null;
            }
        }).when(mockedRemoveAudioUseCase).removeMusic(eq(music),
            eq(Constants.INDEX_AUDIO_TRACK_MUSIC),
            Matchers.any(OnRemoveMediaFinishedListener.class));

        musicDetailPresenter.removeMusic(music);

        verify(musicDetailView).goToSoundActivity();
        verify(mockedUserEventTracker).trackMusicSet(videonaProject);
    }

    @Test
    public void removeMusicCallsShowErrorOnRemoveMediaItemFromTrackError() {
        MusicDetailPresenter musicDetailPresenter =
            getMusicDetailPresenter(mockedUserEventTracker);
        Music music = new Music(1, "Music title", 2, 3, "Music Author", "3", 0);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                OnRemoveMediaFinishedListener listener =
                    invocation.getArgumentAt(2, OnRemoveMediaFinishedListener.class);
                listener.onRemoveMediaItemFromTrackError();
                return null;
            }
        }).when(mockedRemoveAudioUseCase).removeMusic(eq(music),
            eq(Constants.INDEX_AUDIO_TRACK_MUSIC),
            Matchers.any(OnRemoveMediaFinishedListener.class));

        musicDetailPresenter.removeMusic(music);

        verify(musicDetailView).showError(anyString());
    }

    @Test
    public void addMusicCallsMusicSetVolume() throws IllegalItemOnTrack {
        MusicDetailPresenter musicDetailPresenter =
            getMusicDetailPresenter(mockedUserEventTracker);
        float volumeMusic = 0.85f;

        musicDetailPresenter.addMusic(mockedMusic, volumeMusic);

        verify(mockedMusic).setVolume(volumeMusic);
    }

    @Test
    public void setVolumeCallsModifyTrackUseCase() throws IllegalItemOnTrack {
        MusicDetailPresenter musicDetailPresenter =
            getMusicDetailPresenter(mockedUserEventTracker);
        Project project = getAProject();
        AudioTrack musicTrack = project.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_MUSIC);
        Music music = new Music(1, "Music title", 2, 3, "Music Author", "3", 0);
        float volumeMusic = 0.85f;
        musicTrack.insertItem(music);
        assertThat("Initial volume is 0.5f ", musicTrack.getVolume(), is(0.5f));

        musicDetailPresenter.setVolume(volumeMusic);

        verify(mockedModifyTrackUseCase).setTrackVolume(musicTrack, volumeMusic);
    }

    public Project getAProject() {
        return Project.getInstance("title", "/path", "private/path",
            Profile.getInstance(VideoResolution.Resolution.HD720,
                VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25));
    }

    @NonNull
    private MusicDetailPresenter getMusicDetailPresenter(UserEventTracker userEventTracker) {
        return new MusicDetailPresenter(musicDetailView, userEventTracker,
                mockedGetMediaListFromProjectUseCase, mockedGetMusicFromProject,
                mockedGetPreferencesTransitionsFromProject, mockedAddAudioUseCase,
                mockedRemoveAudioUseCase, mockedModifyTrackUseCase, mockedContext);
    }
}
