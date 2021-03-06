package com.videonasocialmedia.vimojo.sound.presentation.mvp.presenters;

import android.content.Context;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.AudioTrack;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer;
import com.videonasocialmedia.vimojo.asset.domain.usecase.RemoveMedia;
import com.videonasocialmedia.vimojo.composition.domain.RemoveTrack;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateComposition;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateTrack;
import com.videonasocialmedia.vimojo.domain.editor.GetAudioFromProjectUseCase;
import com.videonasocialmedia.vimojo.domain.editor.GetMusicListUseCase;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnAddMediaFinishedListener;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnRemoveMediaFinishedListener;
import com.videonasocialmedia.vimojo.presentation.mvp.views.MusicDetailView;
import com.videonasocialmedia.vimojo.sound.domain.AddAudioUseCase;
import com.videonasocialmedia.vimojo.sound.domain.ModifyTrackUseCase;
import com.videonasocialmedia.vimojo.sound.domain.RemoveAudioUseCase;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.view.BackgroundExecutor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by jliarte on 10/06/16.
 */
public class MusicDetailPresenterTest {
    @Mock private Context mockedContext;
    @Mock private MusicDetailView mockedMusicDetailView;
    @Mock private UserEventTracker mockedUserEventTracker;
    @Mock private AddAudioUseCase mockedAddAudioUseCase;
    @Mock private RemoveAudioUseCase mockedRemoveAudioUseCase;
    @Mock private ModifyTrackUseCase mockedModifyTrackUseCase;
    @Mock private GetMusicListUseCase mockedGetMusicListUseCase;

    @Mock ProjectInstanceCache mockedProjectInstanceCache;

    @Mock Music mockedMusic;
    private Project currentProject;
    private String musicPath = "music/path";
    private List<Music> musicList = new ArrayList<>();
    @Mock UpdateComposition mockedUpdateComposition;
    private boolean amIAVerticalApp;
    @Mock RemoveMedia mockedRemoveMedia;
    @Mock UpdateTrack mockedUpdateTrack;
    @Mock RemoveTrack mockedDeleteTrack;
    @Mock BackgroundExecutor mockedBackgroundExecutor;
    @Mock ListenableFuture mockedListenableFuture;
    @Mock FutureCallback mockedFutureCallback;
    @Mock Crashlytics mockedCrashlytics;

    @Before
    public void injectMocks() {
        MockitoAnnotations.initMocks(this);
        setAProject();
        when(mockedProjectInstanceCache.getCurrentProject()).thenReturn(currentProject);
        getAMusicList();
        when(mockedGetMusicListUseCase.getAppMusic()).thenReturn(musicList);
    }

    @Test
    public void constructorSetsUserTracker() {
        UserEventTracker userEventTracker = UserEventTracker.getInstance();
        MusicDetailPresenter musicDetailPresenter =
                getMusicDetailPresenter(userEventTracker);

        assertThat(musicDetailPresenter.userEventTracker, is(userEventTracker));
    }

    @Test
    public void updatePresenterSetsCurrentProject() {
        MusicDetailPresenter musicDetailPresenter =
                getMusicDetailPresenter(mockedUserEventTracker);

        musicDetailPresenter.updatePresenter(musicPath);

        assertThat(musicDetailPresenter.currentProject, is(currentProject));
    }

    @Test
    public void addMusicCallsGoToSoundTranckingAndUpdateOnAddMediaItemFromTrackSuccess() {
        MusicDetailPresenter musicDetailPresenter =
            getMusicDetailPresenter(mockedUserEventTracker);
        final Music music = new Music(1, "Music title", 2,
            3, "Music Author", "3", 0);
        float volumeMusic = 0.85f;
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                OnAddMediaFinishedListener listener = invocation.getArgument(3);
                listener.onAddMediaItemToTrackSuccess(music);
                return null;
            }
        }).when(mockedAddAudioUseCase).addMusic(eq(currentProject), eq(music),
            eq(Constants.INDEX_AUDIO_TRACK_MUSIC),
            any(OnAddMediaFinishedListener.class));
        when(mockedBackgroundExecutor.submit(any(Runnable.class))).then(new Answer<Runnable>() {
            @Override
            public Runnable answer(InvocationOnMock invocation) throws Throwable {
                Runnable runnable = invocation.getArgument(0);
                runnable.run();
                return null;
            }
        });

        musicDetailPresenter.addMusic(music, volumeMusic);

        verify(mockedUserEventTracker).trackMusicSet(currentProject);
        verify(mockedUpdateComposition).updateComposition(currentProject);
        verify(mockedMusicDetailView).goToSoundActivity();
    }

    @Test
    public void addMusicCallsShowErrorOnAddMediaItemFromTrackError() {
        MusicDetailPresenter musicDetailPresenter =
            getMusicDetailPresenter(mockedUserEventTracker);
        final Music music = new Music(1, "Music title", 2,
            3, "Music Author", "3", 0);
        float volumeMusic = 0.85f;
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                OnAddMediaFinishedListener listener = invocation.getArgument(3);
                listener.onAddMediaItemToTrackError();
                return null;
            }
        }).when(mockedAddAudioUseCase).addMusic(eq(currentProject), eq(music),
            eq(Constants.INDEX_AUDIO_TRACK_MUSIC),
            any(OnAddMediaFinishedListener.class));

        musicDetailPresenter.addMusic(music, volumeMusic);

        verify(mockedMusicDetailView).showError(null);
    }

    @Test
    public void removeMusicCallsGoToSoundActivityOnRemoveMediaItemFromTrackSuccess() {
        MusicDetailPresenter musicDetailPresenter =
            getMusicDetailPresenter(mockedUserEventTracker);
        Music music = new Music(1, "Music title", 2,
            3, "Music Author", "3", 0);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                OnRemoveMediaFinishedListener listener = invocation.getArgument(2);
                // TODO: 7/9/18 Fix today this test
                List<Media> removedMedias = null;
                listener.onRemoveMediaItemFromTrackSuccess(removedMedias);
                return null;
            }
        }).when(mockedRemoveAudioUseCase).removeMusic(eq(currentProject), eq(music),
            any(OnRemoveMediaFinishedListener.class));
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                FutureCallback<Object> futureCallback = invocation.getArgument(1);
                futureCallback.onSuccess(Object.class);
                return null;
            }
        }).when(mockedBackgroundExecutor)
            .addCallback(any(ListenableFuture.class), any(FutureCallback.class));
        when(mockedBackgroundExecutor.submit(any(Runnable.class))).then(
            (Answer<ListenableFuture<?>>) invocation -> {
                Runnable runnable = invocation.getArgument(0);
                runnable.run();
                return mockedListenableFuture;
        });

        musicDetailPresenter.removeMusic(music);

        verify(mockedUserEventTracker).trackMusicSet(currentProject);
        verify(mockedUpdateComposition).updateComposition(currentProject);
    }

    @Test
    public void removeMusicCallsShowErrorOnRemoveMediaItemFromTrackError() {
        MusicDetailPresenter musicDetailPresenter =
            getMusicDetailPresenter(mockedUserEventTracker);
        Music music = new Music(1, "Music title", 2,
            3, "Music Author", "3", 0);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                OnRemoveMediaFinishedListener listener = invocation.getArgument(2);
                listener.onRemoveMediaItemFromTrackError();
                return null;
            }
        }).when(mockedRemoveAudioUseCase).removeMusic(eq(currentProject), eq(music),
            any(OnRemoveMediaFinishedListener.class));

        musicDetailPresenter.removeMusic(music);

        verify(mockedMusicDetailView).showError(null);
        verify(mockedMusicDetailView).sendCrashlyticsLog(anyString());
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
    public void setVolumeCallsModifyTrackUseCase() throws IllegalItemOnTrack, InterruptedException {
        MusicDetailPresenter musicDetailPresenter = getMusicDetailPresenter();
        AudioTrack musicTrack = currentProject.getAudioTracks()
            .get(Constants.INDEX_AUDIO_TRACK_MUSIC);
        Music music = new Music(1, "Music title", 2,
            3, "Music Author", "3", 0);
        float volumeMusic = 0.85f;
        musicTrack.insertItem(music);
        assertThat("Initial volume is 0.5f ", musicTrack.getVolume(), is(0.5f));
        when(mockedBackgroundExecutor.submit(any(Runnable.class))).then(new Answer<Runnable>() {
            @Override
            public Runnable answer(InvocationOnMock invocation) throws Throwable {
                Runnable runnable = invocation.getArgument(0);
                runnable.run();
                return null;
            }
        });

        musicDetailPresenter.setVolume(volumeMusic);

        verify(mockedModifyTrackUseCase).setTrackVolume(musicTrack, volumeMusic);
        verify(mockedUpdateComposition).updateComposition(currentProject);
    }

    @NonNull
    private MusicDetailPresenter getMusicDetailPresenter(UserEventTracker userEventTracker) {
        MusicDetailPresenter musicDetailPresenter = new MusicDetailPresenter(
            mockedContext, mockedMusicDetailView, userEventTracker,
            mockedAddAudioUseCase, mockedRemoveAudioUseCase,
            mockedModifyTrackUseCase, mockedGetMusicListUseCase, mockedProjectInstanceCache,
            mockedUpdateComposition, amIAVerticalApp, mockedRemoveMedia, mockedUpdateTrack,
            mockedDeleteTrack, mockedBackgroundExecutor);
        musicDetailPresenter.currentProject = currentProject;
        return musicDetailPresenter;
    }

    private MusicDetailPresenter getMusicDetailPresenter() {
        MusicDetailPresenter musicDetailPresenter = new MusicDetailPresenter(
            mockedContext, mockedMusicDetailView, mockedUserEventTracker,
            mockedAddAudioUseCase, mockedRemoveAudioUseCase,
            mockedModifyTrackUseCase, mockedGetMusicListUseCase, mockedProjectInstanceCache,
            mockedUpdateComposition, amIAVerticalApp, mockedRemoveMedia, mockedUpdateTrack,
            mockedDeleteTrack, mockedBackgroundExecutor);
        musicDetailPresenter.currentProject = currentProject;
        return musicDetailPresenter;
    }

    private void setAProject() {
        Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720,
            VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
        List<String> productType = new ArrayList<>();
        ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
        currentProject = new Project(projectInfo, "/path", "private/path", compositionProfile);
        Music music = new Music("music/path", Music.DEFAULT_VOLUME, 59);
        try {
            currentProject.getAudioTracks().get(Constants.INDEX_AUDIO_TRACK_MUSIC).insertItem(music);
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            illegalItemOnTrack.printStackTrace();
        }
    }

    private void getAMusicList() {
        Music music = new Music("music/path", Music.DEFAULT_VOLUME, 59);
        musicList.add(music);
    }
}
