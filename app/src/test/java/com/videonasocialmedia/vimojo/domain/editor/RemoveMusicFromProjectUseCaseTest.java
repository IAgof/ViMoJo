package com.videonasocialmedia.vimojo.domain.editor;

import com.videonasocialmedia.vimojo.model.entities.editor.Profile;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Music;
import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoFrameRate;
import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoQuality;
import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoResolution;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnAddMediaFinishedListener;
import com.videonasocialmedia.vimojo.sound.domain.AddMusicToProjectUseCase;
import com.videonasocialmedia.vimojo.sound.domain.RemoveMusicFromProjectUseCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;


/**
 * Created by alvaro on 3/10/16.
 */

public class RemoveMusicFromProjectUseCaseTest {

    @Mock
    OnAddMediaFinishedListener mockedOnAddMediaFinishedListener;

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

    private Project getAProjectWithMusicAdded() {
        Profile profile = Profile.getInstance(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
                VideoFrameRate.FrameRate.FPS25);
        String rootPath = "projectRootPath";
        String title = "project title";
        Project project = Project.getInstance(title, rootPath, profile);
        Music musicToAdd = new Music(42, "musicNameId", 3, 2, "","");
        new AddMusicToProjectUseCase().addMusicToTrack(musicToAdd, 0, mockedOnAddMediaFinishedListener);
        return project;
    }

    @Test
    public void removeMusicFromProjectDeleteItemFromAudioTrack(){

        Project project = getAProjectWithMusicAdded();
        Music music = project.getMusic();

        assertNotNull(music);

        new RemoveMusicFromProjectUseCase().removeMusicFromProject(music, 0);

        assertNull(project.getMusic());


    }
}
