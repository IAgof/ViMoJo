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

public class UpdateVideoFrameRateToProjectUseCaseTest {

    @After
    public void tearDown() throws Exception {
        // FIXME: tests are not independent as Project keeps state between tests
        Project singletonProject = Project.getInstance(null, null, null);
        singletonProject.clear();
    }

    @Test
    public void profileUpdateFrameRate(){

        Project project = getAProject();
        Profile profile = project.getProfile();
        VideoFrameRate videoFrameRate = profile.getVideoFrameRate();

        assertThat("frameRate", 25, CoreMatchers.is(videoFrameRate.getFrameRate()));

        new UpdateVideoFrameRateToProjectUseCase().updateFrameRate(VideoFrameRate.FrameRate.FPS30, project);

        VideoFrameRate updatedVideoFrameRate = profile.getVideoFrameRate();
        assertThat("updatedFrameRate", 30, CoreMatchers.is(updatedVideoFrameRate.getFrameRate()));

    }

    private Project getAProject() {
        String title = "project title";
        String rootPath = "project/root/path";
        Profile profile = Profile.getInstance(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
                VideoFrameRate.FrameRate.FPS25);
        return Project.getInstance(title, rootPath, profile);
    }
}
