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
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnAddMediaFinishedListener;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnRemoveMediaFinishedListener;
import com.videonasocialmedia.vimojo.presentation.mvp.views.MusicDetailView;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.settings.mainSettings.domain.GetPreferencesTransitionFromProjectUseCase;
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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by jliarte on 10/06/16.
 */
public class MusicDetailPresenterTest {
    @Mock private MusicDetailView musicDetailView;
    @Mock private MixpanelAPI mockedMixpanelAPI;
    @Mock private UserEventTracker mockedUserEventTracker;
    @Mock private ProjectRepository mockedProjectRepository;
    @Mock private Context mockedContext;
    @Mock private GetMediaListFromProjectUseCase mockedGetMediaListFromProjectUseCase;
    @Mock private GetAudioFromProjectUseCase mockedGetMusicFromProject;
    @Mock private GetPreferencesTransitionFromProjectUseCase
            mockedGetPreferencesTransitionsFromProject;
    @Mock private AddAudioUseCase mockedAddAudioUseCase;
    @Mock private RemoveAudioUseCase mockedRemoveAudioUseCase;
    @Mock private ModifyTrackUseCase mockedModifyTrackUseCase;

    @Mock Music mockedMusic;

    private Project currentProject;

    @Before
    public void injectMocks() {
        MockitoAnnotations.initMocks(this);
        getAProject();
        when(mockedProjectRepository.getCurrentProject()).thenReturn(currentProject);
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

        MusicDetailPresenter musicDetailPresenter =
                getMusicDetailPresenter(mockedUserEventTracker);

        assertThat(musicDetailPresenter.currentProject, is(currentProject));
    }

    @Test
    public void addMusicCallsGoToSoundActivityOnAddMediaItemFromTrackSuccess() {
        MusicDetailPresenter musicDetailPresenter =
            getMusicDetailPresenter(mockedUserEventTracker);
        final Music music = new Music(1, "Music title", 2, 3, "Music Author", "3", 0);
        float volumeMusic = 0.85f;
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                OnAddMediaFinishedListener listener = invocation.getArgument(2);
                listener.onAddMediaItemToTrackSuccess(music);
                return null;
            }
        }).when(mockedAddAudioUseCase).addMusic(eq(music),
            eq(Constants.INDEX_AUDIO_TRACK_MUSIC),
            Matchers.any(OnAddMediaFinishedListener.class));

        musicDetailPresenter.addMusic(music, volumeMusic);

        verify(musicDetailView).goToSoundActivity();
        verify(mockedUserEventTracker).trackMusicSet(currentProject);
    }

    @Test
    public void addMusicCallsGoToSoundActivityOnAddMediaItemFromTrackError() {
        MusicDetailPresenter musicDetailPresenter =
            getMusicDetailPresenter(mockedUserEventTracker);
        final Music music = new Music(1, "Music title", 2, 3, "Music Author", "3", 0);
        float volumeMusic = 0.85f;
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                OnAddMediaFinishedListener listener = invocation.getArgument(2);
                listener.onAddMediaItemToTrackError();
                return null;
            }
        }).when(mockedAddAudioUseCase).addMusic(eq(music),
            eq(Constants.INDEX_AUDIO_TRACK_MUSIC),
            Matchers.any(OnAddMediaFinishedListener.class));

        musicDetailPresenter.addMusic(music, volumeMusic);

        verify(musicDetailView).showError(null);
    }

    @Test
    public void removeMusicCallsGoToSoundActivityOnRemoveMediaItemFromTrackSuccess() {
        MusicDetailPresenter musicDetailPresenter =
            getMusicDetailPresenter(mockedUserEventTracker);
        Music music = new Music(1, "Music title", 2, 3, "Music Author", "3", 0);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                OnRemoveMediaFinishedListener listener = invocation.getArgument(2);
                listener.onRemoveMediaItemFromTrackSuccess();
                return null;
            }
        }).when(mockedRemoveAudioUseCase).removeMusic(eq(music),
            eq(Constants.INDEX_AUDIO_TRACK_MUSIC),
            Matchers.any(OnRemoveMediaFinishedListener.class));

        musicDetailPresenter.removeMusic(music);

        verify(musicDetailView).goToSoundActivity();
        verify(mockedUserEventTracker).trackMusicSet(currentProject);
    }

    @Test
    public void removeMusicCallsShowErrorOnRemoveMediaItemFromTrackError() {
        MusicDetailPresenter musicDetailPresenter =
            getMusicDetailPresenter(mockedUserEventTracker);
        Music music = new Music(1, "Music title", 2, 3, "Music Author", "3", 0);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                OnRemoveMediaFinishedListener listener = invocation.getArgument(2);
                listener.onRemoveMediaItemFromTrackError();
                return null;
            }
        }).when(mockedRemoveAudioUseCase).removeMusic(eq(music),
            eq(Constants.INDEX_AUDIO_TRACK_MUSIC),
            Matchers.any(OnRemoveMediaFinishedListener.class));

        musicDetailPresenter.removeMusic(music);

        verify(musicDetailView).showError(null);
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
        AudioTrack musicTrack = currentProject.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_MUSIC);
        Music music = new Music(1, "Music title", 2, 3, "Music Author", "3", 0);
        float volumeMusic = 0.85f;
        musicTrack.insertItem(music);
        assertThat("Initial volume is 0.5f ", musicTrack.getVolume(), is(0.5f));

        musicDetailPresenter.setVolume(volumeMusic);

        verify(mockedModifyTrackUseCase).setTrackVolume(currentProject, musicTrack, volumeMusic);
    }

    public void getAProject() {
        Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720,
                VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
        List<String> productType = new ArrayList<>();
        ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
        currentProject = new Project(projectInfo, "/path", "private/path", compositionProfile);
    }

    @NonNull
    private MusicDetailPresenter getMusicDetailPresenter(UserEventTracker userEventTracker) {
        return new MusicDetailPresenter(musicDetailView, userEventTracker,mockedProjectRepository,
                mockedGetMediaListFromProjectUseCase, mockedGetMusicFromProject,
                mockedGetPreferencesTransitionsFromProject, mockedAddAudioUseCase,
                mockedRemoveAudioUseCase, mockedModifyTrackUseCase, mockedContext);
    }
}
