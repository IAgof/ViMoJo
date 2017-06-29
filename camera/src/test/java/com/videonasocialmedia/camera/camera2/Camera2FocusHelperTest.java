package com.videonasocialmedia.camera.camera2;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.util.Log;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
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

  @Ignore
  @Test
  public void setFocusModeSelectiveIsNotCalledIfIsNotSuported() throws CameraAccessException {
    setupCameraWrapper();
    Camera2FocusHelper focusHelper = new Camera2FocusHelper(mockedCameraWrapper);
    doReturn(false).when(focusHelper).isFocusSelectionSupported();

    CameraFeatures.SupportedValues supportedFocusSelectionValues = focusHelper
        .getSupportedFocusSelectionModes();

  }

  @Ignore
  @Test
  public void setFocusModeAutoSetsCameraSettingsAndUpdatesPreview() throws CameraAccessException {
    setupCameraWrapper();
    Camera2FocusHelper focusHelper = new Camera2FocusHelper(mockedCameraWrapper);

    focusHelper.setFocusSelectionMode(Camera2FocusHelper.AF_MODE_AUTO);

    verify(mockedPreviewBuilder)
        .set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
    verify(mockedPreviewBuilder)
        .set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_VIDEO);
    verify(mockedCameraWrapper).updatePreview();
  }

  private void setupCameraWrapper() throws CameraAccessException {
    int[] supportedValues = {CameraMetadata.CONTROL_AF_MODE_OFF,
        CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_VIDEO};
    doReturn(supportedValues).when(mockedCharacteristics)
        .get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES);
    doReturn(2).when(mockedCharacteristics).get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF);
    doReturn(mockedCharacteristics)
        .when(mockedCameraWrapper).getCurrentCameraCharacteristics();
    doReturn(mockedPreviewBuilder).when(mockedCameraWrapper).getPreviewBuilder();
  }


}
