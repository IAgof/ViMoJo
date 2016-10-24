package com.videonasocialmedia.vimojo.model.entities.editor;

import com.videonasocialmedia.vimojo.model.entities.editor.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Music;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.model.entities.editor.track.MediaTrack;
import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoQuality;
import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoResolution;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

/**
 * Created by jliarte on 11/05/16.
 */

@RunWith(MockitoJUnitRunner.class)
public class ProjectTest {
    @Mock ProjectRepository mockedProjectRepository;
    @InjectMocks Project injectedProject;

    @After
    public void tearDown() {
        Project.getInstance(null, null, null).clear();
    }

    @Test
    public void getInstanceCallsProjectRepositoryGetCurrentProjectIfInstanceIsNull() {
        assert Project.INSTANCE == null;

        Project.getInstance(null, null, null);
    }

    @Test
    public void clearShoudCreateANewNullProject() throws Exception {
        Project videonaProject = getAProject();

        videonaProject.clear();
        Project projectInstance = Project.getInstance(null, null, null);

        assertThat(videonaProject, not(projectInstance));
        assertThat(projectInstance.getTitle(), nullValue());
        assertThat(projectInstance.getProjectPath(), is("null/projects/null"));
        assertThat(projectInstance.getProfile(), nullValue());
    }

    @Test
    public void projectClipsIs0OnAnEmtyProject() {
        Project videonaProject = getAProject();

        assertThat(videonaProject.numberOfClips(), is(0));
    }

    @Test
    public void projectNumberOfClipsIsMediaTrackItemsLength() {
        Project videonaProject = getAProject();
        MediaTrack mediaTrack = videonaProject.getMediaTrack();
        try {
            mediaTrack.insertItemAt(0, new Video("/path1"));
            mediaTrack.insertItemAt(1, new Video("/path2"));

            assertThat(videonaProject.numberOfClips(), is(2));
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            illegalItemOnTrack.printStackTrace();
        }
    }

    @Test
    public void getMusicReturnsMusicWhenMusicHasOneSet() throws IllegalItemOnTrack {
        Project videonaProject = getAProject();
        Music music = new Music(1, "Music title", 2, 3, "Music Author","");

        videonaProject.getAudioTracks().get(0).insertItem(music);

        assertThat(videonaProject.getMusic(), is(music));
    }

    @Test
    public void getMusicReturnsNullIfNoMusicHasSet() {
        Project videonaProject = getAProject();

        assertThat(videonaProject.getMusic(), is(nullValue()));
    }

    @Test
    public void getVideoParamsFromProjectProfileFree(){
        Project videonaProject = getAProject();
        VideoResolution resolution = videonaProject.getProfile().getVideoResolution();
        VideoQuality quality = videonaProject.getProfile().getVideoQuality();

        assertThat("videoBitRate", 5000*1000, CoreMatchers.is(quality.getVideoBitRate()));
        assertThat("videoWidth", 1280, CoreMatchers.is(resolution.getWidth()));
        assertThat("videoHeight", 720, CoreMatchers.is(resolution.getHeight()));
    }

    public Project getAProject() {
        return new Project("project title", "root path", Profile.getInstance(Profile.ProfileType.free));
    }
}
