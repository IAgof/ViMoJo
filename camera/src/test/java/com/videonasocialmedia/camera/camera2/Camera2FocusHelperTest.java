package com.videonasocialmedia.camera.camera2;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.MeteringRectangle;
import android.os.Handler;
import android.util.Log;

import com.videonasocialmedia.camera.camera2.wrappers.VideonaCameraCaptureSession;
import com.videonasocialmedia.camera.camera2.wrappers.VideonaCameraCharacteristics;
import com.videonasocialmedia.camera.camera2.wrappers.VideonaCaptureRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.util.ReflectionHelpers;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

/**
 * Created by alvaro on 28/06/17.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class, VideonaCaptureRequest.Builder.class})
public class Camera2FocusHelperTest {
  @Mock private Camera2Wrapper mockedCameraWrapper;
  @Mock private VideonaCaptureRequest.Builder mockedPreviewBuilder;
  @Mock private VideonaCameraCaptureSession mockedPreviewSession;
  private VideonaCameraCharacteristics mockedCharacteristics;

  @Before
  public void injectTestDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @Before
  public void setup() {
    PowerMockito.mockStatic(Log.class);
    ReflectionHelpers.setStaticField(android.os.Build.class, "MODEL", "lala");
    PowerMockito.mockStatic(VideonaCameraCharacteristics.class);
    mockedCharacteristics = PowerMockito.mock(VideonaCameraCharacteristics.class);
  }

  @Test
  public void setFocusModeSelectiveIsNotCalledIfIsNotSupported() throws CameraAccessException {
    setupCameraWrapperSelectiveModeNotSupported();
    Camera2FocusHelper focusHelper = new Camera2FocusHelper(mockedCameraWrapper);
    focusHelper.setup();

    CameraFeatures.SupportedValues supportedFocusSelectionValues = focusHelper
        .getSupportedFocusSelectionModes();

    for (String focusMode : supportedFocusSelectionValues.values) {
      assertThat("Mode selective is not supported", focusMode, not(Camera2FocusHelper.AF_MODE_REGIONS));
    }
  }

  @Test
  public void setFocusModeAutoSetsCameraSettingsAndUpdatesPreview() throws CameraAccessException {
    setupCameraWrapperAllModesSupported();
    Camera2FocusHelper focusHelper = new Camera2FocusHelper(mockedCameraWrapper);
    focusHelper.setup();

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
    Camera2Wrapper.CaptureResultParams captureResultParams = new Camera2Wrapper.CaptureResultParams();
    captureResultParams.captureResultHasFocusDistance = true;
    captureResultParams.captureResultFocusDistance = 0.03f;
    doReturn(captureResultParams).when(mockedCameraWrapper).getLastCaptureResultParams();
    focusHelper.setup();

    focusHelper.setFocusSelectionMode(Camera2FocusHelper.AF_MODE_MANUAL);

    verify(mockedPreviewBuilder)
        .set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_OFF);
    int value = (int) (100 - (100 * 0.03f) / focusHelper.minimumFocusDistance);
    float focusDistance = ((float) (100 - value)) * focusHelper.minimumFocusDistance / 100;
    verify(mockedPreviewBuilder)
        .set(CaptureRequest.LENS_FOCUS_DISTANCE, focusDistance);
    verify(mockedCameraWrapper).updatePreview();
  }

  @Test
  public void setFocusModeSelectiveSetsCameraSettingsAndUpdatesPreview() throws CameraAccessException {
    setupCameraWrapperAllModesSupported();
    Camera2FocusHelper focusHelper = new Camera2FocusHelper(mockedCameraWrapper);
    focusHelper.setup();
    MeteringRectangle[] focusMeteringRectangle = mockedCameraWrapper.getFullSensorAreaMeteringRectangle();

    focusHelper.setFocusSelectionMode(Camera2FocusHelper.AF_MODE_REGIONS);

    verify(mockedPreviewBuilder)
        .set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_IDLE);
    verify(mockedPreviewBuilder)
        .set(CaptureRequest.CONTROL_AF_REGIONS, focusMeteringRectangle);
    verify(mockedPreviewBuilder, atLeastOnce())
        .set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_AUTO);
    verify(mockedPreviewBuilder, atLeastOnce())
        .set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
    verify(mockedPreviewSession).capture(any(VideonaCaptureRequest.class),
            any(VideonaCameraCaptureSession.CaptureCallback.class), any(Handler.class));
    // TODO(jliarte): 16/11/17 update this test with actual calls
//    verify(mockedCameraWrapper).updatePreview();
  }

  private void setupCameraWrapperAllModesSupported() throws CameraAccessException {
    int[] supportedValues = {CameraMetadata.CONTROL_AF_MODE_OFF,
        CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_VIDEO};

    doReturn(supportedValues).doReturn(1).doReturn(10f).when(mockedCharacteristics)
        .get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES);
    doReturn(mockedCharacteristics)
        .when(mockedCameraWrapper).getCurrentCameraCharacteristics();
    doReturn(mockedPreviewBuilder).when(mockedCameraWrapper).getPreviewBuilder();
    doReturn(mockedPreviewSession).when(mockedCameraWrapper).getPreviewSession();
  }

  private void setupCameraWrapperSelectiveModeNotSupported() throws CameraAccessException {
    int[] supportedValues = {CameraMetadata.CONTROL_AF_MODE_OFF,
        CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_VIDEO};

    doReturn(supportedValues).doReturn(0).doReturn(10f).when(mockedCharacteristics)
            .get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);

//    doReturn(supportedValues).doReturn(0).when(mockedCharacteristics)
//            .get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES);

    doReturn(mockedCharacteristics)
        .when(mockedCameraWrapper).getCurrentCameraCharacteristics();
    doReturn(mockedPreviewBuilder).when(mockedCameraWrapper).getPreviewBuilder();
  }


}
