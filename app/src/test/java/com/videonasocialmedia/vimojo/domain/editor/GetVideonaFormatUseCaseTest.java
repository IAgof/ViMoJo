package com.videonasocialmedia.vimojo.domain.editor;

import com.videonasocialmedia.transcoder.format.VideonaFormat;
import com.videonasocialmedia.vimojo.export.domain.GetVideonaFormatUseCase;
import com.videonasocialmedia.vimojo.export.domain.OnGetVideonaFormatListener;
import com.videonasocialmedia.vimojo.model.entities.editor.Profile;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoFrameRate;
import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoQuality;
import com.videonasocialmedia.vimojo.model.entities.editor.utils.VideoResolution;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by alvaro on 5/09/16.
 */
public class GetVideonaFormatUseCaseTest implements OnGetVideonaFormatListener{

    @Mock
    OnGetVideonaFormatListener mockedGetVideonaFormatListener;
    VideonaFormat videonaFormat;


    @Before
    public void injectDoubles() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception {
        // FIXME: tests are not independent as Project keeps state between tests
        Project singletonProject = Project.getInstance(null, null, null);
        singletonProject.clear();
    }


    @Test
    public void constructorSetsProjectInstance() {
        Project videonaProject = getAProject();

        GetVideonaFormatUseCase getVideonaFormatUseCase = new GetVideonaFormatUseCase();
        assertThat("Project field set after construction", getVideonaFormatUseCase.project, is(videonaProject));
    }

    @Test
    public void getVideonaFormatFromProjectReturnCorrectFormat(){

        Project videonaProject = getAProject();

        GetVideonaFormatUseCase getVideonaFormatUseCase = new GetVideonaFormatUseCase();
        getVideonaFormatUseCase.project = videonaProject;
        getVideonaFormatUseCase.getVideonaFormatFromProject(this);

        assertThat("videoBitRate", 10*1000*1000, is(videonaFormat.getVideoBitrate()));
        assertThat("videoWidth", 1280, is(videonaFormat.getVideoWidth()));
        assertThat("videoHeight", 720, is(videonaFormat.getVideoHeight()));

    }

    private Project getAProject() {
        String title = "project title";
        String rootPath = "project/root/path";
        Profile profile = Profile.getInstance(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
                VideoFrameRate.FrameRate.FPS25);
        return Project.getInstance(title, rootPath, profile);
    }

    @Override
    public void onVideonaFormat(VideonaFormat videonaFormat) {
        this.videonaFormat = videonaFormat;
    }

    @Override
    public void onVideonaErrorFormat() {

    }
}
