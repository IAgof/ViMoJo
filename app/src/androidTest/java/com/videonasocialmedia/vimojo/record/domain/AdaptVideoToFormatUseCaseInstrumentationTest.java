package com.videonasocialmedia.vimojo.record.domain;

import android.support.test.runner.AndroidJUnit4;

import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.importer.model.entities.VideoToAdapt;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptMemoryRepository;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptRepository;
import com.videonasocialmedia.vimojo.integration.AssetManagerAndroidTest;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by jliarte on 27/09/17.
 */
@RunWith(AndroidJUnit4.class)
public class AdaptVideoToFormatUseCaseInstrumentationTest extends AssetManagerAndroidTest {
  private VideoToAdaptRepository videoToAdaptRepo;
  private String testPath;
  @Mock private VideoRepository videoRepository;
  @Mock private AdaptVideoToFormatUseCase.AdaptListener mockedAdaptListener;
  private Project currentProject;

  @Before
  public void setUp() {
    videoToAdaptRepo = new VideoToAdaptMemoryRepository();
    testPath = getInstrumentation().getTargetContext().getExternalCacheDir()
            .getAbsolutePath();
    MockitoAnnotations.initMocks(this);
    getCurrentProject();
  }

  @Test
  public void testAdaptVideoUpdatesMediaPath() throws IOException, ExecutionException, InterruptedException {
    String originalVideoPath = getAssetPath("vid_.mp4");
    Video video = spy(new Video(originalVideoPath, Video.DEFAULT_VOLUME));
    video.setTempPath(testPath);
    Project project = setupProjectPath();
    AdaptVideoToFormatUseCase adaptVideoToFormatUseCase =
            new AdaptVideoToFormatUseCase(videoToAdaptRepo, videoRepository);
    VideonaFormat videoFormat = new VideonaFormat(5000000, 1280, 720);
    String destPath = testPath + "/res.mp4";
    VideoToAdapt videoToAdapt = new VideoToAdapt(video, destPath, 0, 0, 0);

    adaptVideoToFormatUseCase.adaptVideo(project, videoToAdapt, videoFormat, mockedAdaptListener);
    videoToAdapt.getVideo().getTranscodingTask().get();

    assertThat(video.getMediaPath(), is(destPath));
    verify(videoRepository).update(video);
    verify(video).notifyChanges();
  }

  private Project setupProjectPath() {
    currentProject.setProjectPath(testPath);
    return currentProject;
  }

  private void getCurrentProject() {
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject = new Project(projectInfo, testPath, testPath, null);
  }

}