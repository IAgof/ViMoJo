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

public class UpdateVideoQualityToProjectUseCaseTest {

    @After
    public void tearDown() throws Exception {
        // FIXME: tests are not independent as Project keeps state between tests
        Project singletonProject = Project.getInstance(null, null, null);
        singletonProject.clear();
    }

    @Test
    public void profileUpdateQuality(){

        Project project = getAProject();
        Profile profile = project.getProfile();
        VideoQuality videoQuality = profile.getVideoQuality();

        assertThat("bitRate", 10*1000*1000, CoreMatchers.is(videoQuality.getVideoBitRate()));

        new UpdateVideoQualityToProjectUseCase().updateQuality(VideoQuality.Quality.LOW, project);

        VideoQuality updatedVideoQuality = profile.getVideoQuality();

        assertThat("updated bitRate", 5*1000*1000, CoreMatchers.is(updatedVideoQuality.getVideoBitRate()));
    }

    private Project getAProject() {
        String title = "project title";
        String rootPath = "project/root/path";
        Profile profile = Profile.getInstance(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
                VideoFrameRate.FrameRate.FPS25);
        return Project.getInstance(title, rootPath, profile);
    }
}
