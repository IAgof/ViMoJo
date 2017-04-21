package com.videonasocialmedia.vimojo.record.presentation.mvp.presenters;

import android.content.Context;
import android.hardware.camera2.CaptureRequest;

import com.videonasocialmedia.camera.camera2.Camera2Wrapper;
import com.videonasocialmedia.camera.camera2.Camera2WrapperListener;
import com.videonasocialmedia.camera.customview.AutoFitTextureView;
import com.videonasocialmedia.camera.utils.VideoCameraFormat;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.model.media.Profile;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.track.MediaTrack;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelperListener;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.GetVideoFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditorActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.GalleryActivity;
import com.videonasocialmedia.vimojo.record.domain.AdaptVideoRecordedToVideoFormatUseCase;
import com.videonasocialmedia.vimojo.record.presentation.mvp.presenters.RecordCamera2Presenter;
import com.videonasocialmedia.vimojo.record.presentation.mvp.views.RecordCamera2View;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import dagger.Provides;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

/**
 * Created by alvaro on 26/01/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class RecordCamera2PresenterTest {

  RecordCamera2Presenter presenter;

  @Mock RecordCamera2View mockedRecordView;
  @Mock Context mockedContext;
  boolean isFrontCameraSelected = true;
  boolean isPrincipalViewSelected = true;
  boolean isRightControlsViewSelected = true;
  @Mock AutoFitTextureView mockedTextureView;
  String directorySaveVideos;
  @Mock GetVideoFormatFromCurrentProjectUseCase mockedGetVideoFormatFromCurrentProjectUseCase;
  @Mock AddVideoToProjectUseCase mockedAddVideoToProjectUseCase;
  @Mock AdaptVideoRecordedToVideoFormatUseCase mockedAdaptVideoRecordedToVideoFormatUseCase;
  @Mock VideonaFormat mockedVideoFormat;
  @Mock TranscoderHelperListener mockedTranscoderHelperListener;
  @Mock Camera2WrapperListener mockedCamera2WrapperListener;

  @Before
  public void injectMocks() {
    MockitoAnnotations.initMocks(this);
    getAProject();
  }

  @After
  public void tearDown() {
    Project.getInstance(null, null, null).clear();
  }

  @Test
  public void initViewsWithPrincipalAndRightControlsViewSelectedCallsCorrectRecordView(){

    presenter = new RecordCamera2Presenter(mockedContext, mockedRecordView,
        isFrontCameraSelected, isPrincipalViewSelected,
        isRightControlsViewSelected, mockedTextureView, directorySaveVideos,
        mockedGetVideoFormatFromCurrentProjectUseCase,
        mockedAddVideoToProjectUseCase, mockedAdaptVideoRecordedToVideoFormatUseCase);

    presenter.initViews();

    verify(mockedRecordView).hideChronometer();
    verify(mockedRecordView).setResolutionSelected(720);

    verify(mockedRecordView).showPrincipalViews();
    verify(mockedRecordView).showRightControlsView();
  }

  @Test
  public void initViewsDefaultInitializationCallsCorrectRecordView(){
    isPrincipalViewSelected = false;
    isRightControlsViewSelected = false;

    presenter = new RecordCamera2Presenter(mockedContext, mockedRecordView,
        isFrontCameraSelected, isPrincipalViewSelected,
        isRightControlsViewSelected, mockedTextureView, directorySaveVideos,
        mockedGetVideoFormatFromCurrentProjectUseCase,
        mockedAddVideoToProjectUseCase, mockedAdaptVideoRecordedToVideoFormatUseCase);

    presenter.initViews();

    verify(mockedRecordView).hideChronometer();
    verify(mockedRecordView).setResolutionSelected(720);

    verify(mockedRecordView).hidePrincipalViews();
    verify(mockedRecordView).hideRightControlsView();
  }

  @Test
  public void navigateEditOrGalleryButtonCallsGalleryIfThereIsNotVideos(){
    getAProject().clear();
    int numVideosInProject = getAProject().getVMComposition().getMediaTrack().getNumVideosInProject();
    assertThat("There is not videos in project ", numVideosInProject, is(0));

    presenter = new RecordCamera2Presenter(mockedContext, mockedRecordView,
        isFrontCameraSelected, isPrincipalViewSelected,
        isRightControlsViewSelected, mockedTextureView, directorySaveVideos,
        mockedGetVideoFormatFromCurrentProjectUseCase,
        mockedAddVideoToProjectUseCase, mockedAdaptVideoRecordedToVideoFormatUseCase);

    presenter.navigateToEditOrGallery();

    verify(mockedRecordView).navigateTo(GalleryActivity.class);
  }

  @Test
  public void navigateEditOrGalleryCallsEditActivityIfThereAreVideosInProject()
      throws IllegalItemOnTrack {

    Video video = new Video("dcim/fakeVideo");
    Project project = getAProject();
    MediaTrack track = project.getMediaTrack();
    track.insertItem(video);
    track.insertItem(video);

    int numVideosInProject = getAProject().getVMComposition().getMediaTrack().getNumVideosInProject();
    assertThat("There are videos in project", numVideosInProject, is(2));

    // TODO:(alvaro.martinez) 6/04/17 Assert also there are not videos pending to adapt, transcoding

    presenter = new RecordCamera2Presenter(mockedContext, mockedRecordView,
        isFrontCameraSelected, isPrincipalViewSelected,
        isRightControlsViewSelected, mockedTextureView, directorySaveVideos,
        mockedGetVideoFormatFromCurrentProjectUseCase,
        mockedAddVideoToProjectUseCase, mockedAdaptVideoRecordedToVideoFormatUseCase);

    presenter.navigateToEditOrGallery();

    verify(mockedRecordView).navigateTo(EditActivity.class);
  }

  @Ignore
  @Test
  public void navigateEditOrGalleryCallsShowProgressAdaptingVideoIfThereAreVideosPendingToAdapt()
      throws IllegalItemOnTrack, IOException {

    // TODO:(alvaro.martinez) 6/04/17  Prepare this test, i don't know how to mock adapting video process and fake futures.isDone to false.
    Video video = new Video("dcim/fakeVideo");
    Project project = getAProject();
    MediaTrack track = project.getMediaTrack();
    track.insertItem(video);
    track.insertItem(video);

    int numVideos = getAProject().getVMComposition().getMediaTrack().getNumVideosInProject();
    assertThat("There are videos in project", numVideos, is(2));

    mockedAdaptVideoRecordedToVideoFormatUseCase.adaptVideo(video, mockedVideoFormat,
        directorySaveVideos, mockedTranscoderHelperListener);

    presenter = new RecordCamera2Presenter(mockedContext, mockedRecordView,
        isFrontCameraSelected, isPrincipalViewSelected,
        isRightControlsViewSelected, mockedTextureView, directorySaveVideos,
        mockedGetVideoFormatFromCurrentProjectUseCase,
        mockedAddVideoToProjectUseCase, mockedAdaptVideoRecordedToVideoFormatUseCase);

    presenter.navigateToEditOrGallery();

    verify(mockedRecordView).showProgressAdaptingVideo();
  }


  public Project getAProject() {
    return Project.getInstance("title", "/path",
        Profile.getInstance(VideoResolution.Resolution.HD720, VideoQuality.Quality.HIGH,
            VideoFrameRate.FrameRate.FPS25));
  }
}
