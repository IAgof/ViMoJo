package com.videonasocialmedia.vimojo.domain.editor;

import com.videonasocialmedia.vimojo.model.entities.editor.Profile;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoFrameRate;
import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoQuality;
import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoResolution;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by alvaro on 20/10/16.
 */

public class UpdateVideoResolutionToProjectUseCaseTest {

    @After
    public void tearDown() throws Exception {
        // FIXME: tests are not independent as Project keeps state between tests
        Project singletonProject = Project.getInstance(null, null, null);
        singletonProject.clear();
    }

    @Test
    public void profileUpdateResolution(){

        Project project = getAProject();
        Profile profile = project.getProfile();
        VideoResolution videoResolution = profile.getVideoResolution();

        assertThat("height", 720, CoreMatchers.is(videoResolution.getHeight()));
        assertThat("width", 1280, CoreMatchers.is(videoResolution.getWidth()));

        float format = 16/9;
        assertThat("format 16/9", format, CoreMatchers.is((float) (videoResolution.getWidth()/videoResolution.getHeight())));

        new UpdateVideoResolutionToProjectUseCase().updateResolution(VideoResolution.Resolution.HD1080, project);

        VideoResolution updatedVideoResolution = profile.getVideoResolution();

        assertThat("height", 1080, CoreMatchers.is(updatedVideoResolution.getHeight()));
        assertThat("width", 1920, CoreMatchers.is(updatedVideoResolution.getWidth()));

    }

    private Project getAProject() {
        String title = "project title";
        String rootPath = "project/root/path";
        Profile profile = Profile.getInstance(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
                VideoFrameRate.FrameRate.FPS25);
        return Project.getInstance(title, rootPath, profile);
    }
}
