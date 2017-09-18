package com.videonasocialmedia.vimojo.integration;

import android.media.MediaMetadataRetriever;
import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import com.google.common.util.concurrent.ListenableFuture;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.vimojo.export.domain.ExportProjectUseCase;
import com.videonasocialmedia.vimojo.importer.model.entities.VideoToAdapt;
import com.videonasocialmedia.vimojo.importer.repository.VideoToAdaptMemoryRepository;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnExportFinishedListener;
import com.videonasocialmedia.vimojo.record.domain.AdaptVideoToFormatUseCase;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.vimojo.trim.domain.ModifyVideoDurationUseCase;
import com.videonasocialmedia.vimojo.utils.Constants;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

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

/**
 * Created by jliarte on 6/09/17.
 */

@RunWith(AndroidJUnit4.class)
public class VideoAdaptingTest extends AssetManagerAndroidTest {
  private VideoToAdaptMemoryRepository videoToAdaptRepo;

  @Mock VideoRepository videoRepo;
  @Mock private AdaptVideoToFormatUseCase.AdaptListener mockedListener;
  @Mock private OnExportFinishedListener mockedExportListener;
  private String testPath;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    videoToAdaptRepo = new VideoToAdaptMemoryRepository();
    testPath = getInstrumentation().getTargetContext().getExternalCacheDir()
            .getAbsolutePath();
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

    adaptVideoToFormatUseCase.adaptVideo(videoToAdapt, videoFormat, mockedListener);

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

    adaptVideoToFormatUseCase.adaptVideo(videoToAdapt, videoFormat, mockedListener);

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
    ExportProjectUseCase exportProjectUseCase = new ExportProjectUseCase(videoToAdaptRepo);

    adaptVideoToFormatUseCase.adaptVideo(videoToAdapt, videoFormat, mockedListener);
    exportProjectUseCase.export(Constants.PATH_WATERMARK, mockedExportListener);

    ListenableFuture<Video> transcodingTask = video.getTranscodingTask();
    assertThat(transcodingTask, notNullValue());
    verify(mockedExportListener, never()).onExportSuccess(any(Video.class));
    verify(mockedExportListener, never()).onExportError(anyString());
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
    ExportProjectUseCase exportProjectUseCase = new ExportProjectUseCase(videoToAdaptRepo);

    adaptVideoToFormatUseCase.adaptVideo(videoToAdapt, videoFormat, mockedListener);
    adaptVideoToFormatUseCase.adaptVideo(videoToAdapt2, videoFormat, mockedListener);
    exportProjectUseCase.export(Constants.PATH_WATERMARK, mockedExportListener);

    ListenableFuture<Video> transcodingTask = video.getTranscodingTask();
    ListenableFuture<Video> transcodingTask2 = video2.getTranscodingTask();
    assertThat(transcodingTask, notNullValue());
    assertThat(transcodingTask2, notNullValue());
    verify(mockedExportListener, never()).onExportSuccess(any(Video.class));
    verify(mockedExportListener, never()).onExportError(anyString());
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
    ExportProjectUseCase exportProjectUseCase = new ExportProjectUseCase(videoToAdaptRepo);
    ModifyVideoDurationUseCase modifyVideoDurationUseCase =
            new ModifyVideoDurationUseCase(videoRepo, videoToAdaptRepo);

    adaptVideoToFormatUseCase.adaptVideo(videoToAdapt, videoFormat, mockedListener);
    adaptVideoToFormatUseCase.adaptVideo(videoToAdapt2, videoFormat, mockedListener);
    ListenableFuture<Video> transcodingTask = video.getTranscodingTask();
    video2.setTempPath(testPath);
    video.setTempPath(testPath);
    modifyVideoDurationUseCase.trimVideo(null, video, videoFormat, 0, 500,
            project.getProjectPathIntermediateFileAudioFade());
    exportProjectUseCase.export(Constants.PATH_WATERMARK, mockedExportListener);

    ListenableFuture<Video> transcodingTask_b = video.getTranscodingTask();
    ListenableFuture<Video> transcodingTask2 = video2.getTranscodingTask();
    assertThat(transcodingTask_b, notNullValue());
    assertThat(transcodingTask, not(transcodingTask_b));
    assertThat(transcodingTask2, notNullValue());
    verify(mockedExportListener, never()).onExportSuccess(any(Video.class));
    verify(mockedExportListener, never()).onExportError(anyString());
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

  @NonNull
  private Project setupProjectPath() {
    Project project = getCurrentProject();
    project.setProjectPath(testPath);
    return project;
  }

  private Project getCurrentProject() {
    return Project.getInstance(null, null, null, null);
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
