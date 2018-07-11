package com.videonasocialmedia.vimojo.domain.editor;

import android.support.annotation.NonNull;

import com.videonasocialmedia.videonamediaframework.model.Constants;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.vimojo.cut.domain.model.Project;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.track.AudioTrack;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.GetMusicFromProjectCallback;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jliarte on 31/05/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class GetAudioFromProjectUseCaseTest {
    @Mock private GetMusicFromProjectCallback mockedListener;
    @Captor private ArgumentCaptor<Music> retrievedMusicCaptor;
    private Project currentProject;

    @Before
    public void injectDoubles() throws Exception {
        MockitoAnnotations.initMocks(this);
        getAProject();
    }


    @Test
    public void getMusicFromProjectReturnsProjectMusic() {
        Music project_music = new Music(1, "resourceName", 2,
            3, "music author","2", 0);
        ArrayList<AudioTrack> audioTracks = getAudioTracks(project_music);
        currentProject.setAudioTracks(audioTracks);

        new GetAudioFromProjectUseCase().getMusicFromProject(currentProject, mockedListener);

        Mockito.verify(mockedListener).onMusicRetrieved(retrievedMusicCaptor.capture());
        Music retrievedMusic = retrievedMusicCaptor.getValue();
        assertThat(retrievedMusic, is(project_music));
    }


    @Test
    public void getMusicFromProjectNotifiesWithNullIfNoMusic() {
        new GetAudioFromProjectUseCase().getMusicFromProject(currentProject, mockedListener);

        Mockito.verify(mockedListener).onMusicRetrieved(retrievedMusicCaptor.capture());
        assertThat("Music retrieved when no audio tracks", retrievedMusicCaptor.getValue(),
                CoreMatchers.<Music>nullValue());
    }

    @NonNull
    public ArrayList<AudioTrack> getAudioTracks(Music music) {
        ArrayList<AudioTrack> audioTracks = new ArrayList<AudioTrack>();
        AudioTrack audioTrack = new AudioTrack(Constants.INDEX_AUDIO_TRACK_MUSIC);
        try {
            audioTrack.insertItem(music);
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
//            illegalItemOnTrack.printStackTrace();
        }
        audioTracks.add(audioTrack);
        return audioTracks;
    }

    private void getAProject() {
        String rootPath = "project/root/path";
        String privatePath = "private/path";
        Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720,
                VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
        List<String> productType = new ArrayList<>();
        ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
        currentProject = new Project(projectInfo, rootPath, privatePath, compositionProfile);
    }
}
