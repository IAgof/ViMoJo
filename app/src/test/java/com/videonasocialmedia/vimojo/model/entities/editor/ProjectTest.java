package com.videonasocialmedia.vimojo.model.entities.editor;

import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.track.MediaTrack;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

/**
 * Created by jliarte on 11/05/16.
 */

@RunWith(MockitoJUnitRunner.class)
public class ProjectTest {
    private Project currentProject;

    @Before
    public void setup() {
        getAProject();
    }

    @Test
    public void projectClipsIs0OnAnEmtyProject() {

        assertThat(currentProject.numberOfClips(), is(0));
    }

    @Test
    public void projectNumberOfClipsIsMediaTrackItemsLength() {
        MediaTrack mediaTrack = currentProject.getMediaTrack();
        try {
            mediaTrack.insertItemAt(0, new Video("/path1", 1f));
            mediaTrack.insertItemAt(1, new Video("/path2", 1f));

            assertThat(currentProject.numberOfClips(), is(2));
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            illegalItemOnTrack.printStackTrace();
        }
    }

    @Test
    public void getMusicReturnsMusicWhenMusicHasOneSet() throws IllegalItemOnTrack {
        Music music = new Music(1, "Music title", 2, 3, "Music Author","", 0);

        currentProject.getAudioTracks().get(0).insertItem(music);

        assertThat(currentProject.getMusic(), is(music));
    }

    @Test
    public void getMusicReturnsNullIfNoMusicHasSet() {
        assertThat(currentProject.getMusic(), is(nullValue()));
    }

    @Test
    public void getVideoParamsFromProjectProfile(){
        VideoResolution resolution = currentProject.getProfile().getVideoResolution();
        VideoQuality quality = currentProject.getProfile().getVideoQuality();
        VideoFrameRate frameRate = currentProject.getProfile().getVideoFrameRate();

        assertThat("videoBitRate", 50*1000*1000, CoreMatchers.is(quality.getVideoBitRate()));
        assertThat("videoWidth", 1280, CoreMatchers.is(resolution.getWidth()));
        assertThat("videoHeight", 720, CoreMatchers.is(resolution.getHeight()));
        assertThat("frameRate", 25, CoreMatchers.is(frameRate.getFrameRate()));
    }

    @Test
    public void shouldSaveProfileVMCompositionDurationLastModificationIfProjectIsDuplicate()
            throws IllegalItemOnTrack {
        Project duplicateProject = new Project(currentProject);

        Assert.assertThat("copy project save duration ", duplicateProject.getDuration(),
            CoreMatchers.is(currentProject.getDuration()));
        Assert.assertThat("copy project save last modification ",
                duplicateProject.getLastModification(),
                CoreMatchers.is(currentProject.getLastModification()));
    }

    @Test
    public void shouldUpdateUuidProjectPathIfProjectIsDuplicate() throws IllegalItemOnTrack {
        Project duplicateProject = new Project(currentProject);

        Assert.assertThat("copy project change profile ", duplicateProject.getProfile(),
            CoreMatchers.not(currentProject.getProfile()));
        Assert.assertThat("copy project change VMComposition ", duplicateProject.getVMComposition(),
            CoreMatchers.not(currentProject.getVMComposition()));
        Assert.assertThat("copy project change uuid ", duplicateProject.getUuid(),
            CoreMatchers.not(currentProject.getUuid()));
        Assert.assertThat("copy project change project path ", duplicateProject.getProjectPath(),
            CoreMatchers.not(currentProject.getProjectPath()));
    }

    @Test
    public void shouldCopyTitleDescriptionProductTypeListIfProjectIsDuplicate()
        throws IllegalItemOnTrack {
        Project duplicateProject = new Project(currentProject);

        Assert.assertThat("copy project keep title",
            duplicateProject.getProjectInfo().getTitle(), is(currentProject.getProjectInfo().getTitle()));

        Assert.assertThat("copy project keep description",
            duplicateProject.getProjectInfo().getDescription(),
            is(currentProject.getProjectInfo().getDescription()));

        Assert.assertThat("copy project keep product type list",
            duplicateProject.getProjectInfo().getProductTypeList(),
            is(currentProject.getProjectInfo().getProductTypeList()));
    }

    public void getAProject() {
        Profile compositionProfile = new Profile(VideoResolution.Resolution.HD720,
                VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
        List<String> productType = new ArrayList<>();
        ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
        currentProject = new Project(projectInfo, "/path", "private/path", compositionProfile);
    }
}
