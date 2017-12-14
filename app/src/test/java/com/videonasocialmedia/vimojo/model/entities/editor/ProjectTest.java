package com.videonasocialmedia.vimojo.model.entities.editor;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.track.MediaTrack;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
    @Before
    public void setup() {
        Project.getInstance(null, null, null, null).clear();
    }

    @Test
    public void getInstanceCallsProjectRepositoryGetCurrentProjectIfInstanceIsNull() {
        assert Project.INSTANCE == null;

        Project.getInstance(null, null, null, null);
    }

    @Test
    public void clearShouldCreateANewNullProject() throws Exception {
        Project videonaProject = getAProject();

        videonaProject.clear();
        Project projectInstance = Project.getInstance(null, null, null, null);

        assertThat(videonaProject, not(projectInstance));
        assertThat(projectInstance.getTitle(), nullValue());
        assertThat(projectInstance.getProjectPath(), not(nullValue()));
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
            mediaTrack.insertItemAt(0, new Video("/path1", 1f));
            mediaTrack.insertItemAt(1, new Video("/path2", 1f));

            assertThat(videonaProject.numberOfClips(), is(2));
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            illegalItemOnTrack.printStackTrace();
        }
    }

    @Test
    public void getMusicReturnsMusicWhenMusicHasOneSet() throws IllegalItemOnTrack {
        Project videonaProject = getAProject();
        Music music = new Music(1, "Music title", 2, 3, "Music Author","", 0);

        videonaProject.getAudioTracks().get(0).insertItem(music);

        assertThat(videonaProject.getMusic(), is(music));
    }

    @Test
    public void getMusicReturnsNullIfNoMusicHasSet() {
        Project videonaProject = getAProject();

        assertThat(videonaProject.getMusic(), is(nullValue()));
    }

    @Test
    public void getVideoParamsFromProjectProfile(){
        Project videonaProject = getAProject();
        VideoResolution resolution = videonaProject.getProfile().getVideoResolution();
        VideoQuality quality = videonaProject.getProfile().getVideoQuality();
        VideoFrameRate frameRate = videonaProject.getProfile().getVideoFrameRate();

        assertThat("videoBitRate", 50*1000*1000, CoreMatchers.is(quality.getVideoBitRate()));
        assertThat("videoWidth", 1280, CoreMatchers.is(resolution.getWidth()));
        assertThat("videoHeight", 720, CoreMatchers.is(resolution.getHeight()));
        assertThat("frameRate", 25, CoreMatchers.is(frameRate.getFrameRate()));
    }

    @Test
    public void shouldSaveProfileVMCompositionDurationLastModificationIfProjectIsDuplicate()
            throws IllegalItemOnTrack {
        Project project = getAProject();
        Project duplicateProject = new Project(project);

        Assert.assertThat("copy project save duration ", duplicateProject.getDuration(),
            CoreMatchers.is(project.getDuration()));
        Assert.assertThat("copy project save last modification ",
                duplicateProject.getLastModification(),
                CoreMatchers.is(project.getLastModification()));
    }

    @Test
    public void shouldUpdateTitleUuidProjectPathIfProjectIsDuplicate() throws IllegalItemOnTrack {
        Project project = getAProject();
        Project duplicateProject = new Project(project);

        Assert.assertThat("copy project change profile ", duplicateProject.getProfile(),
            CoreMatchers.not(project.getProfile()));
        Assert.assertThat("copy project change VMComposition ", duplicateProject.getVMComposition(),
            CoreMatchers.not(project.getVMComposition()));
        Assert.assertThat("copy project change title ", duplicateProject.getTitle(),
            CoreMatchers.not(project.getTitle()));
        Assert.assertThat("copy project change uuid ", duplicateProject.getUuid(),
            CoreMatchers.not(project.getUuid()));
        Assert.assertThat("copy project change project path ", duplicateProject.getProjectPath(),
            CoreMatchers.not(project.getProjectPath()));
    }

    public Project getAProject() {
        Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720,
                VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
        return Project.getInstance("title", "/path", "private/path", compositionProfile);
    }
}
