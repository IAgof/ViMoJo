package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.content.Context;

import com.videonasocialmedia.camera.camera2.Camera2Wrapper;
import com.videonasocialmedia.camera.customview.AutoFitTextureView;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.GetVideoFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.record.presentation.mvp.presenters.RecordCamera2Presenter;
import com.videonasocialmedia.vimojo.record.presentation.mvp.views.RecordCamera2View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

/**
 * Created by alvaro on 26/01/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class RecordCamera2PresenterTest {

  RecordCamera2Presenter presenter;

  @Mock RecordCamera2View mockedRecordView;
  @Mock Context mockedContext;
  boolean mockedIsFrontCameraSelected;
  boolean mockedIsPrincipalViewSelected;
  boolean mockedIsRightControlsViewSelected;
  @Mock AutoFitTextureView mockedTextureView;
  boolean mockedExternalIntent;
  String directorySaveVideos;
  @Mock GetVideoFormatFromCurrentProjectUseCase mockedGetVideoFormatFromCurrentProjectUseCase;
  @Mock AddVideoToProjectUseCase mockedAddVideoToProjectUseCase;

  @Before
  public void injectMocks() {
    MockitoAnnotations.initMocks(this);

    presenter = new RecordCamera2Presenter(mockedContext, mockedRecordView,
        mockedIsFrontCameraSelected, mockedIsPrincipalViewSelected,
        mockedIsRightControlsViewSelected, mockedTextureView, mockedExternalIntent,
        directorySaveVideos, mockedGetVideoFormatFromCurrentProjectUseCase,
        mockedAddVideoToProjectUseCase);
  }

  @Test
  public void initViewsWithPrincipalAndRightControlsViewSelectedCallsCorrectRecordView(){
    boolean isPrincipalViewSelected = true;
    boolean isRightControlsViewSelected = true;

    presenter.initViews(mockedRecordView, isPrincipalViewSelected,
        isRightControlsViewSelected);

    verify(mockedRecordView).hideChronometer();
    verify(mockedRecordView).setResolutionSelected(720);

    verify(mockedRecordView).showPrincipalViews();
    verify(mockedRecordView).showRightControlsView();
  }

  @Test
  public void initViewsDefaultInitializationCallsCorrectRecordView(){
    boolean isPrincipalViewSelected = false;
    boolean isRightControlsViewSelected = false;

    presenter.initViews(mockedRecordView, isPrincipalViewSelected,
        isRightControlsViewSelected);

    verify(mockedRecordView).hideChronometer();
    verify(mockedRecordView).setResolutionSelected(720);

    verify(mockedRecordView).hidePrincipalViews();
    verify(mockedRecordView).hideRightControlsView();


  }

}
