package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.content.Context;

import com.videonasocialmedia.camera.customview.AutoFitTextureView;
import com.videonasocialmedia.vimojo.domain.editor.AddVideoToProjectUseCase;
import com.videonasocialmedia.vimojo.export.domain.GetVideoFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.record.presentation.mvp.presenters.RecordCamera2Presenter;
import com.videonasocialmedia.vimojo.record.presentation.mvp.views.RecordCamera2View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
  boolean isFrontCameraSelected = true;
  boolean isPrincipalViewSelected = true;
  boolean isRightControlsViewSelected = true;
  @Mock AutoFitTextureView mockedTextureView;
  boolean externalIntent;
  String directorySaveVideos;
  @Mock GetVideoFormatFromCurrentProjectUseCase mockedGetVideoFormatFromCurrentProjectUseCase;
  @Mock AddVideoToProjectUseCase mockedAddVideoToProjectUseCase;

  @Before
  public void injectMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void initViewsWithPrincipalAndRightControlsViewSelectedCallsCorrectRecordView(){

    presenter = new RecordCamera2Presenter(mockedContext, mockedRecordView,
        isFrontCameraSelected, isPrincipalViewSelected,
        isRightControlsViewSelected, mockedTextureView, externalIntent,
        directorySaveVideos, mockedGetVideoFormatFromCurrentProjectUseCase,
        mockedAddVideoToProjectUseCase);

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
        isRightControlsViewSelected, mockedTextureView, externalIntent,
        directorySaveVideos, mockedGetVideoFormatFromCurrentProjectUseCase,
        mockedAddVideoToProjectUseCase);

    presenter.initViews();

    verify(mockedRecordView).hideChronometer();
    verify(mockedRecordView).setResolutionSelected(720);

    verify(mockedRecordView).hidePrincipalViews();
    verify(mockedRecordView).hideRightControlsView();
  }

}
