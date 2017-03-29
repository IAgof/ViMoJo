package com.videonasocialmedia.vimojo.domain.editor;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnAddMediaFinishedListener;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.sound.domain.RemoveMusicFromProjectUseCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.verify;


/**
 * Created by alvaro on 3/10/16.
 */

public class RemoveMusicFromProjectUseCaseTest {

    @Mock OnAddMediaFinishedListener mockedOnAddMediaFinishedListener;
    @Mock ProjectRepository mockedProjectRepository;
    @InjectMocks RemoveMusicFromProjectUseCase injectedUseCase;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception {
        // FIXME: tests are not independent as Project keeps state between tests
        Project singletonProject = Project.getInstance(null, null, null);
        singletonProject.clear();
    }

    @Test
    public void removeMusicFromProjectDeleteItemFromAudioTrack(){
        Project project = getAProjectWithMusicAdded();
        Music music = project.getMusic();
        assertNotNull(music);

        injectedUseCase.removeMusicFromProject(music, 0);

        assertNull(project.getMusic());
    }

    @Test
    public void removeMusicFromProjectCallsProjectRepositoryUpdate() {
        Project currentProject = getAProjectWithMusicAdded();
        Music music = currentProject.getMusic();

        injectedUseCase.removeMusicFromProject(music, 0);

        verify(mockedProjectRepository).update(currentProject);
    }

    private Project getAProjectWithMusicAdded() {
        Profile profile = Profile.getInstance(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
                VideoFrameRate.FrameRate.FPS25);
        String rootPath = "projectRootPath";
        String title = "project title";
        Project project = Project.getInstance(title, rootPath, profile);
        Music musicToAdd = new Music(42, "musicNameId", 3, 2, "","", 0);
        try {
            project.getAudioTracks().get(0).insertItemAt(0, musicToAdd);
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            illegalItemOnTrack.printStackTrace();
        }
//        new AddMusicToProjectUseCase().addMusicToTrack(musicToAdd, 0, mockedOnAddMediaFinishedListener);
        return project;
    }
}
