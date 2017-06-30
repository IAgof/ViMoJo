package com.videonasocialmedia.camera.camera2;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.MeteringRectangle;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

/**
 * Created by alvaro on 28/06/17.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class Camera2FocusHelperTest {

  @Mock private Camera2Wrapper mockedCameraWrapper;
  @Mock private CaptureRequest.Builder mockedPreviewBuilder;
  @Mock private CameraCharacteristics mockedCharacteristics;

  @Before
  public void injectTestDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @Before
  public void setup() {
    PowerMockito.mockStatic(Log.class);
  }

  @Test
  public void setFocusModeSelectiveIsNotCalledIfIsNotSupported() throws CameraAccessException {
    setupCameraWrapperSelectiveModeNotSupported();
    Camera2FocusHelper focusHelper = new Camera2FocusHelper(mockedCameraWrapper);

    CameraFeatures.SupportedValues supportedFocusSelectionValues = focusHelper
        .getSupportedFocusSelectionModes();

    for(String focusMode: supportedFocusSelectionValues.values){
      assertThat("Mode selective is not supported", focusMode, not(Camera2FocusHelper.AF_MODE_REGIONS));
    }
  }

  @Test
  public void setFocusModeAutoSetsCameraSettingsAndUpdatesPreview() throws CameraAccessException {
    setupCameraWrapperAllModesSupported();
    Camera2FocusHelper focusHelper = new Camera2FocusHelper(mockedCameraWrapper);

    focusHelper.setFocusSelectionMode(Camera2FocusHelper.AF_MODE_AUTO);

    verify(mockedPreviewBuilder)
        .set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
    verify(mockedPreviewBuilder)
        .set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_VIDEO);
    verify(mockedCameraWrapper).updatePreview();
  }

  @Test
  public void setFocusModeManualSetsCameraSettingsAndUpdatesPreview() throws CameraAccessException {
    setupCameraWrapperAllModesSupported();
    Camera2FocusHelper focusHelper = new Camera2FocusHelper(mockedCameraWrapper);

    focusHelper.setFocusSelectionMode(Camera2FocusHelper.AF_MODE_MANUAL);

    verify(mockedPreviewBuilder)
        .set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_OFF);
    verify(mockedPreviewBuilder)
        .set(CaptureRequest.LENS_FOCUS_DISTANCE, 5.0f);
    verify(mockedCameraWrapper).updatePreview();
  }

  @Test
  public void setFocusModeSelectiveSetsCameraSettingsAndUpdatesPreview() throws CameraAccessException {
    setupCameraWrapperAllModesSupported();
    Camera2FocusHelper focusHelper = new Camera2FocusHelper(mockedCameraWrapper);
    MeteringRectangle[] focusMeteringRectangle = mockedCameraWrapper.getFullSensorAreaMeteringRectangle();

    focusHelper.setFocusSelectionMode(Camera2FocusHelper.AF_MODE_REGIONS);

    verify(mockedPreviewBuilder)
        .set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
    verify(mockedPreviewBuilder)
        .set(CaptureRequest.CONTROL_AF_REGIONS, focusMeteringRectangle);
    verify(mockedPreviewBuilder, atLeastOnce())
        .set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_AUTO);
    verify(mockedPreviewBuilder, atLeastOnce())
        .set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
    verify(mockedCameraWrapper).updatePreview();

  }

  private void setupCameraWrapperAllModesSupported() throws CameraAccessException {
    int[] supportedValues = {CameraMetadata.CONTROL_AF_MODE_OFF,
        CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_VIDEO};

    doReturn(supportedValues).doReturn(1).doReturn(10f).when(mockedCharacteristics)
        .get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES);
    doReturn(mockedCharacteristics)
        .when(mockedCameraWrapper).getCurrentCameraCharacteristics();
    doReturn(mockedPreviewBuilder).when(mockedCameraWrapper).getPreviewBuilder();
  }

  private void setupCameraWrapperSelectiveModeNotSupported() throws CameraAccessException {
    int[] supportedValues = {CameraMetadata.CONTROL_AF_MODE_OFF,
        CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_VIDEO};

    doReturn(supportedValues).doReturn(0).when(mockedCharacteristics)
        .get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES);

    doReturn(mockedCharacteristics)
        .when(mockedCameraWrapper).getCurrentCameraCharacteristics();
    doReturn(mockedPreviewBuilder).when(mockedCameraWrapper).getPreviewBuilder();
  }


}
