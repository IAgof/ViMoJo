package com.videonasocialmedia.vimojo.trim.domain;

import android.media.MediaMetadataRetriever;
import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.importer.model.entities.VideoToAdapt;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptMemoryRepository;
import com.videonasocialmedia.vimojo.integration.AssetManagerAndroidTest;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.record.domain.AdaptVideoToFormatUseCase;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.*;

/**
 * Created by jliarte on 18/09/17.
 */
@RunWith(AndroidJUnit4.class)
public class ModifyVideoDurationUseCaseInstrumentationTest extends AssetManagerAndroidTest {
  private VideoToAdaptMemoryRepository videoToAdaptRepo;
  private String testPath;
  @Mock private VideoRepository videoRepo;
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
  public void testTrimUseCaseGeneratesTrimedVideo() throws IllegalItemOnTrack,
          IOException, ExecutionException, InterruptedException {
    String originalVideoPath = getAssetPath("vid_.mp4");
    Video video = new Video(originalVideoPath, Video.DEFAULT_VOLUME);
    video.setTempPath(testPath);
    Project project = setupProjectPath();
    project.getVMComposition().getMediaTrack().insertItem(video);
    ModifyVideoDurationUseCase modifyVideoDurationUseCase =
            new ModifyVideoDurationUseCase(videoRepo, videoToAdaptRepo);

    modifyVideoDurationUseCase.trimVideo(video, 100, 600, project);

    ListenableFuture<Video> transcodingTask = video.getTranscodingTask();
    transcodingTask.get();
    String transcodedVideoPath = video.getTempPath();
    File transcodedVideoFile = new File(transcodedVideoPath);
    assertThat(transcodedVideoFile.exists(), is(true));
    int trimedVideoDuration = Integer.parseInt(getVideoDuration(transcodedVideoPath));
    assertThat(Math.abs(trimedVideoDuration - 500), lessThanOrEqualTo(100));
  }

  @Test
  public void testTrimUseCaseGeneratesTrimedVideoAfterVideoAdapted() throws IllegalItemOnTrack,
          IOException, ExecutionException, InterruptedException {
    String originalVideoPath = getAssetPath("vid_.mp4");
    testPath = getInstrumentation().getTargetContext().getExternalCacheDir()
            .getAbsolutePath();
    Project project = setupProjectPath();
    Video video = new Video(originalVideoPath, Video.DEFAULT_VOLUME);
    video.setTempPath(testPath);
    int originalVideoDuration = Integer.parseInt(getVideoDuration(originalVideoPath));
    System.out.print("original video duration is " + originalVideoDuration);
    project.getVMComposition().getMediaTrack().insertItem(video);
    AdaptVideoToFormatUseCase adaptVideoToFormatUseCase =
            new AdaptVideoToFormatUseCase(videoToAdaptRepo, videoRepo);
    VideonaFormat videoFormat = new VideonaFormat(5000000, 1280, 720);
    String destPath = testPath + "/res.mp4";
    VideoToAdapt videoToAdapt = new VideoToAdapt(video, destPath, 0, 0, 0);
    ModifyVideoDurationUseCase modifyVideoDurationUseCase =
            new ModifyVideoDurationUseCase(videoRepo, videoToAdaptRepo);

    adaptVideoToFormatUseCase.adaptVideo(project, videoToAdapt, videoFormat, mockedAdaptListener);
    ListenableFuture<Video> adaptTask = video.getTranscodingTask();
    modifyVideoDurationUseCase.trimVideo(video, 0, 500, project);

    ListenableFuture<Video> transcodingTask = video.getTranscodingTask();
    adaptTask.get();
    transcodingTask.get();

    String transcodedVideoPath = video.getTempPath();
    File transcodedVideoFile = new File(transcodedVideoPath);
    assertThat(transcodedVideoFile.exists(), is(true));
    int trimedVideoDuration = Integer.parseInt(getVideoDuration(transcodedVideoPath));
    assertThat(Math.abs(trimedVideoDuration - 500), lessThanOrEqualTo(100));
  }

  private String getVideoDuration(String videoPath) {
    MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
    mediaMetadataRetriever.setDataSource(videoPath);
    return mediaMetadataRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
  }

  @NonNull
  private Project setupProjectPath() {
    currentProject.setProjectPath(testPath);
    return currentProject;
  }

  private void getCurrentProject() {
    Profile compositionProfile = new Profile(VideoResolution.Resolution.H_720P, VideoQuality.Quality.HIGH,
            VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject = new Project(projectInfo, testPath, testPath, compositionProfile);
  }


}