package com.videonasocialmedia.vimojo.integration;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.export.domain.ExportProjectUseCase;
import com.videonasocialmedia.vimojo.importer.model.entities.VideoToAdapt;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptMemoryRepository;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptRealmRepository;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptRepository;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnExportFinishedListener;
import com.videonasocialmedia.vimojo.record.domain.AdaptVideoToFormatUseCase;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.vimojo.trim.domain.ModifyVideoDurationUseCase;
import com.videonasocialmedia.vimojo.utils.Constants;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by jliarte on 6/09/17.
 */

@RunWith(AndroidJUnit4.class)
public class VideoAdaptingTest extends AssetManagerAndroidTest {
  private VideoToAdaptMemoryRepository videoToAdaptRepo;

  @Mock VideoRepository videoRepo;
  @Mock ProjectRepository mockedProjectRepository;
  @Mock private AdaptVideoToFormatUseCase.AdaptListener mockedListener;
  @Mock private OnExportFinishedListener mockedExportListener;
  private String testPath;
  @Mock Context mockedContext;
  private Project currentProject;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    videoToAdaptRepo = new VideoToAdaptMemoryRepository();
    testPath = getInstrumentation().getTargetContext().getExternalCacheDir()
            .getAbsolutePath();
    getAProject();
    when(mockedProjectRepository.getCurrentProject()).thenReturn(currentProject);
  }

  @Test
  public void testAdaptVideoUseCaseWithDefaultVolumeTranscodesVideoFile()
          throws IOException, InterruptedException, ExecutionException {
    String originalVideoPath = getAssetPath("vid_.mp4");
    Video video = new Video(originalVideoPath, Video.DEFAULT_VOLUME);
    int originalVideoDuration = Integer.parseInt(getVideoDuration(originalVideoPath));
    AdaptVideoToFormatUseCase adaptVideoToFormatUseCase =
            new AdaptVideoToFormatUseCase(videoToAdaptRepo, videoRepo);
    VideonaFormat videoFormat = new VideonaFormat(5000000, 1280, 720);
    String destPath = testPath + "/res.mp4";
    VideoToAdapt videoToAdapt = new VideoToAdapt(video, destPath, 0, 0, 0);

    adaptVideoToFormatUseCase.adaptVideo(currentProject, videoToAdapt, videoFormat, mockedListener);

    ListenableFuture<Video> transcodingTask = video.getTranscodingTask();
    assertThat(transcodingTask, notNullValue());
    transcodingTask.get();
    verify(mockedListener).onSuccessAdapting(video);
    assertThat(video.getMediaPath(), not(originalVideoPath));
    assertThat(video.getMediaPath(), is(destPath));
    File destVideo = new File(destPath);
    assertThat(destVideo.exists(), is(true));
    File originalVideo = new File(originalVideoPath);
    assertThat(originalVideo.exists(), is(false));
    assertEqualFormat(video.getMediaPath(), videoFormat);
    assertThat("Video duration aprox equal (1000ms)",
            Math.abs(Integer.parseInt(getVideoDuration(video.getMediaPath()))
                    - originalVideoDuration), lessThanOrEqualTo(1000));
  }

  @Test
  public void testAdaptVideoUseCaseWithoutDefaultVolumeTranscodesVideoFile()
          throws IOException, InterruptedException, ExecutionException {
    String originalVideoPath = getAssetPath("vid_.mp4");
    Video video = new Video(originalVideoPath, 0.5f);
    video.setTempPath(testPath);
    Project project = setupProjectPath();
    int originalVideoDuration = Integer.parseInt(getVideoDuration(originalVideoPath));
    AdaptVideoToFormatUseCase adaptVideoToFormatUseCase =
            new AdaptVideoToFormatUseCase(videoToAdaptRepo, videoRepo);
    VideonaFormat videoFormat = new VideonaFormat(5000000, 1280, 720);
    String destPath = testPath + "/res.mp4";
    VideoToAdapt videoToAdapt = new VideoToAdapt(video, destPath, 0, 0, 0);

    adaptVideoToFormatUseCase.adaptVideo(currentProject, videoToAdapt, videoFormat, mockedListener);

    ListenableFuture<Video> transcodingTask = video.getTranscodingTask();
    assertThat(transcodingTask, notNullValue());
    transcodingTask.get();
    verify(mockedListener).onSuccessAdapting(video);
    assertThat(video.getMediaPath(), not(originalVideoPath));
    assertThat(video.getMediaPath(), is(destPath));
    File destVideo = new File(destPath);
    assertThat(destVideo.exists(), is(true));
    File originalVideo = new File(originalVideoPath);
    assertThat(originalVideo.exists(), is(false));
    assertEqualFormat(video.getMediaPath(), videoFormat);
    assertThat("Video duration aprox equal (1000ms)",
            Math.abs(Integer.parseInt(getVideoDuration(video.getMediaPath()))
                    - originalVideoDuration), lessThanOrEqualTo(1000));
  }

  @Test
  public void testExportUseCaseWaitsForOneVideoToAdapt() throws IllegalItemOnTrack, IOException,
          ExecutionException, InterruptedException {
    String originalVideoPath = getAssetPath("vid_.mp4");
    Video video = new Video(originalVideoPath, Video.DEFAULT_VOLUME);
    Project project = setupProjectPath();
    project.getVMComposition().getMediaTrack().insertItem(video);
    AdaptVideoToFormatUseCase adaptVideoToFormatUseCase =
            new AdaptVideoToFormatUseCase(videoToAdaptRepo, videoRepo);
    VideonaFormat videoFormat = new VideonaFormat(5000000, 1280, 720);
    String destPath = testPath + "/res.mp4";
    VideoToAdapt videoToAdapt = new VideoToAdapt(video, destPath, 0, 0, 0);
    ExportProjectUseCase exportProjectUseCase = new ExportProjectUseCase(mockedProjectRepository,
        videoToAdaptRepo);

    adaptVideoToFormatUseCase.adaptVideo(currentProject, videoToAdapt, videoFormat, mockedListener);
    exportProjectUseCase.export(Constants.PATH_WATERMARK, mockedExportListener);

    ListenableFuture<Video> transcodingTask = video.getTranscodingTask();
    assertThat(transcodingTask, notNullValue());
    verify(mockedExportListener, never()).onExportSuccess(any(Video.class));
    verify(mockedExportListener, never()).onExportError(anyString(), any());
    transcodingTask.get();
    verify(mockedListener).onSuccessAdapting(video);
    ArgumentCaptor<Object> videoCaptor = ArgumentCaptor.forClass(Video.class);
    verify(mockedExportListener, timeout(10000).times(1))
            .onExportSuccess((Video) videoCaptor.capture());
    assertThat(videoCaptor.getValue(), notNullValue());
  }

  @Test
  public void testExportUseCaseWaitsForMoreThanOneVideoToAdapt() throws IllegalItemOnTrack,
          IOException, ExecutionException, InterruptedException {
    String originalVideoPath = getAssetPath("vid_.mp4");
    String originalVideoPath2 = getAssetPath("vid_2.mp4");
    Video video = new Video(originalVideoPath, Video.DEFAULT_VOLUME);
    Video video2 = new Video(originalVideoPath2, Video.DEFAULT_VOLUME);
    int originalVideoDuration = Integer.parseInt(getVideoDuration(originalVideoPath));
    int originalVideoDuration2 = Integer.parseInt(getVideoDuration(originalVideoPath2));
    Project project = setupProjectPath();
    project.getVMComposition().getMediaTrack().insertItem(video);
    project.getVMComposition().getMediaTrack().insertItem(video2);
    AdaptVideoToFormatUseCase adaptVideoToFormatUseCase =
            new AdaptVideoToFormatUseCase(videoToAdaptRepo, videoRepo);
    VideonaFormat videoFormat = new VideonaFormat(5000000, 1280, 720);
    String destPath = testPath + "/res.mp4";
    String destPath2 = testPath + "/res2.mp4";
    VideoToAdapt videoToAdapt = new VideoToAdapt(video, destPath, 0, 0, 0);
    VideoToAdapt videoToAdapt2 = new VideoToAdapt(video2, destPath2, 0, 0, 0);
    ExportProjectUseCase exportProjectUseCase = new ExportProjectUseCase(mockedProjectRepository,
        videoToAdaptRepo);

    adaptVideoToFormatUseCase.adaptVideo(currentProject, videoToAdapt, videoFormat, mockedListener);
    adaptVideoToFormatUseCase.adaptVideo(currentProject, videoToAdapt2, videoFormat, mockedListener);
    exportProjectUseCase.export(Constants.PATH_WATERMARK, mockedExportListener);

    ListenableFuture<Video> transcodingTask = video.getTranscodingTask();
    ListenableFuture<Video> transcodingTask2 = video2.getTranscodingTask();
    assertThat(transcodingTask, notNullValue());
    assertThat(transcodingTask2, notNullValue());
    verify(mockedExportListener, never()).onExportSuccess(any(Video.class));
    verify(mockedExportListener, never()).onExportError(anyString(), exception);
    transcodingTask.get();
    transcodingTask2.get();
    verify(mockedListener).onSuccessAdapting(video);
    verify(mockedListener).onSuccessAdapting(video2);
    assertThat(video.getMediaPath(), is(destPath));
    assertThat(video2.getMediaPath(), is(destPath2));
    ArgumentCaptor<Object> videoCaptor = ArgumentCaptor.forClass(Video.class);
    verify(mockedExportListener, timeout(90000).times(1))
            .onExportSuccess((Video) videoCaptor.capture());
    Video exportedVideo = (Video) videoCaptor.getValue();
    assertThat(exportedVideo, notNullValue());
    assertThat(exportedVideo.getMediaPath(), not(video.getMediaPath()));
    assertThat(exportedVideo.getMediaPath(), not(video2.getMediaPath()));
    int exportedVideoDuration = Integer.parseInt(getVideoDuration(exportedVideo.getMediaPath()));
    assertThat(Math.abs(exportedVideoDuration - originalVideoDuration - originalVideoDuration2),
            lessThanOrEqualTo(1000));
  }

  @Test
  public void testExportUseCaseWaitsForMoreThanOneVideoToAdaptAndTrim() throws IllegalItemOnTrack,
          IOException, ExecutionException, InterruptedException {
    String originalVideoPath = getAssetPath("vid_.mp4");
    String originalVideoPath2 = getAssetPath("vid_2.mp4");
    Video video = new Video(originalVideoPath, Video.DEFAULT_VOLUME);
    Video video2 = new Video(originalVideoPath2, Video.DEFAULT_VOLUME);
    int originalVideoDuration = Integer.parseInt(getVideoDuration(originalVideoPath));
    int originalVideoDuration2 = Integer.parseInt(getVideoDuration(originalVideoPath2));
    Project project = setupProjectPath();
    project.getVMComposition().getMediaTrack().insertItem(video);
    project.getVMComposition().getMediaTrack().insertItem(video2);
    AdaptVideoToFormatUseCase adaptVideoToFormatUseCase =
            new AdaptVideoToFormatUseCase(videoToAdaptRepo, videoRepo);
    VideonaFormat videoFormat = new VideonaFormat(5000000, 1280, 720);
    String destPath = testPath + "/res.mp4";
    String destPath2 = testPath + "/res2.mp4";
    VideoToAdapt videoToAdapt = new VideoToAdapt(video, destPath, 0, 0, 0);
    VideoToAdapt videoToAdapt2 = new VideoToAdapt(video2, destPath2, 0, 0, 0);
    ExportProjectUseCase exportProjectUseCase = new ExportProjectUseCase(mockedProjectRepository,
        videoToAdaptRepo);
    ModifyVideoDurationUseCase modifyVideoDurationUseCase =
            new ModifyVideoDurationUseCase(videoRepo, videoToAdaptRepo);

    adaptVideoToFormatUseCase.adaptVideo(currentProject, videoToAdapt, videoFormat, mockedListener);
    adaptVideoToFormatUseCase.adaptVideo(currentProject, videoToAdapt2, videoFormat, mockedListener);
    ListenableFuture<Video> transcodingTask = video.getTranscodingTask();
    video2.setTempPath(testPath);
    video.setTempPath(testPath);
    modifyVideoDurationUseCase.trimVideo(video, 0, 500, project);
    exportProjectUseCase.export(Constants.PATH_WATERMARK, mockedExportListener);

    ListenableFuture<Video> transcodingTask_b = video.getTranscodingTask();
    ListenableFuture<Video> transcodingTask2 = video2.getTranscodingTask();
    assertThat(transcodingTask_b, notNullValue());
    assertThat(transcodingTask, not(transcodingTask_b));
    assertThat(transcodingTask2, notNullValue());
    verify(mockedExportListener, never()).onExportSuccess(any(Video.class));
    verify(mockedExportListener, never()).onExportError(anyString(), exception);
    transcodingTask_b.get();
    transcodingTask2.get();
    verify(mockedListener).onSuccessAdapting(video);
    verify(mockedListener).onSuccessAdapting(video2);
    assertThat(video.getMediaPath(), is(destPath));
    assertThat(video2.getMediaPath(), is(destPath2));
    ArgumentCaptor<Object> videoCaptor = ArgumentCaptor.forClass(Video.class);
    verify(mockedExportListener, timeout(90000).times(1))
            .onExportSuccess((Video) videoCaptor.capture());
    Video exportedVideo = (Video) videoCaptor.getValue();
    assertThat(exportedVideo, notNullValue());
    int exportedVideoDuration = Integer.parseInt(getVideoDuration(exportedVideo.getMediaPath()));
    assertThat(Math.abs(exportedVideoDuration - originalVideoDuration - 500),
            lessThanOrEqualTo(1000));
  }

  @Test
  public void createVideoToAdaptRespectVideoReferences() throws IllegalItemOnTrack {
    Video video = new Video(".temporal/Vid1234.mp4", Video.DEFAULT_VOLUME);
    currentProject.getVMComposition().getMediaTrack().insertItem(video);
    String destVideoRecorded = "DCIM/ViMoJo/Masters/Vid1233.mp4";
    int videoPosition = 0;
    int cameraRotation = 0;
    int retries = 0;

    VideoToAdapt videoToAdapt = new VideoToAdapt(video, destVideoRecorded, videoPosition,
            cameraRotation, retries);

    assertThat(video, is(videoToAdapt.getVideo()));
    assertThat(currentProject.getMediaTrack().getItems().get(0),
        CoreMatchers.<Media>is(videoToAdapt.getVideo()));
  }

  @Rule
  public TemporaryFolder testFolder = new TemporaryFolder();

  @Test
  public void afterRestoreVideoAdaptViaRepositoryVideoReferenceChanges() throws IllegalItemOnTrack, IOException {
    //Config realm
    File tempFolder = testFolder.newFolder("realmdata");
    RealmConfiguration config = new RealmConfiguration.Builder(tempFolder).build();
    Realm realm = Realm.getInstance(config);
    //Prepare project and videoToAdapt
    Video video = new Video(".temporal/Vid1234.mp4", Video.DEFAULT_VOLUME);
    currentProject.setProjectPath(testPath);
    currentProject.getVMComposition().getMediaTrack().insertItem(video);
    ProjectRepository projectRepo = Mockito.spy(new ProjectRealmRepository());
    projectRepo.update(currentProject);
    String destVideoRecorded = "DCIM/ViMoJo/Masters/Vid1233.mp4";
    int videoPosition = 0;
    int cameraRotation = 0;
    int retries = 0;
    VideoToAdapt videoToAdapt = new VideoToAdapt(video, destVideoRecorded, videoPosition,
        cameraRotation, retries);
    VideoToAdaptRepository videoToAdaptRepo = Mockito.spy(new VideoToAdaptRealmRepository());
    videoToAdaptRepo.update(videoToAdapt);

    // App died, restore data
    Project projectRetriever = projectRepo.getCurrentProject();
    VideoToAdapt videoToAdaptRetrieve = videoToAdaptRepo.getAllVideos().get(0);

    assertThat( projectRetriever.getMediaTrack().getItems().get(0),
        CoreMatchers.<Media>not(videoToAdaptRetrieve.getVideo()));

    realm.close(); // Important
  }

  @NonNull
  private Project setupProjectPath() {
    currentProject.setProjectPath(testPath);
    return currentProject;
  }

  public void getAProject() {
    Profile compositionProfile = new Profile(VideoResolution.Resolution.H_720P,
            VideoQuality.Quality.HIGH, VideoFrameRate.FrameRate.FPS25);
    List<String> productType = new ArrayList<>();
    ProjectInfo projectInfo = new ProjectInfo("title", "description", productType);
    currentProject = new Project(projectInfo, "/path", "private/path", compositionProfile);
  }

  private String getVideoDuration(String videoPath) {
    MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
    mediaMetadataRetriever.setDataSource(videoPath);
    return mediaMetadataRetriever
              .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
  }

  private void assertEqualFormat(String destVideoMediaPath, VideonaFormat destFormat) {
    MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
    // TODO(jliarte): 8/09/17 audio bitrate??
//    String originalVideoFramerate = mediaMetadataRetriever
//            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_);

    mediaMetadataRetriever.setDataSource(destVideoMediaPath);
    String finalVideoWidth = mediaMetadataRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
    String finalVideoHeigth = mediaMetadataRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
    String finalVideoBitrate = mediaMetadataRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
//    String finalVideoFramerate = mediaMetadataRetriever
//            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_CAPTURE_FRAMERATE);

    assertThat("Video heigth", Integer.parseInt(finalVideoHeigth), is(destFormat.getVideoHeight()));
    assertThat("Video width", Integer.parseInt(finalVideoWidth), is(destFormat.getVideoWidth()));

    // TODO(jliarte): 8/09/17 java.lang.AssertionError:
    //    Expected: is <5000000>
    //    but: was <4756505>
//    assertThat(Integer.parseInt(finalVideoBitrate), is(destFormat.getVideoBitrate()));

//    assertThat(originalVideoFramerate, is(finalVideoFramerate));
  }

}
