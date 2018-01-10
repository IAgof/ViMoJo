package com.videonasocialmedia.camera.camera2;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.util.Log;

import com.videonasocialmedia.camera.camera2.wrappers.VideonaCameraCharacteristics;
import com.videonasocialmedia.camera.camera2.wrappers.VideonaCaptureRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.util.ReflectionHelpers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by jliarte on 21/06/17.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class, VideonaCaptureRequest.Builder.class})
public class Camera2WhiteBalanceHelperTest {
  @Mock private Camera2Wrapper mockedCameraWrapper;
  @Mock private VideonaCaptureRequest.Builder mockedPreviewBuilder;
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
  public void setWhiteBalanceModeSetsCameraAWBModeAndUpdatesPreview() throws Exception {
    setupWrapper();
    Camera2WhiteBalanceHelper wbHelper = new Camera2WhiteBalanceHelper(mockedCameraWrapper);
    wbHelper.setup();

    wbHelper.setWhiteBalanceMode(Camera2WhiteBalanceHelper.WB_MODE_AUTO);

    verify(mockedPreviewBuilder, times(2))
            .set(CaptureRequest.CONTROL_AWB_MODE, CameraMetadata.CONTROL_AWB_MODE_AUTO);
    verify(mockedCameraWrapper).updatePreview();
  }

  @Test
  public void setWhiteBalanceModeReturnsIfNotWBSupport() throws Exception {
    setupWrapper();
    Camera2WhiteBalanceHelper wbHelper =
            Mockito.spy(new Camera2WhiteBalanceHelper(mockedCameraWrapper));
    doReturn(false).when(wbHelper).whiteBalanceSelectionSupported();

    wbHelper.setWhiteBalanceMode(Camera2WhiteBalanceHelper.WB_MODE_AUTO);

    verify(mockedPreviewBuilder, never())
            .set(CaptureRequest.CONTROL_AWB_MODE, CameraMetadata.CONTROL_AWB_MODE_AUTO);
    verify(mockedCameraWrapper, never()).updatePreview();
  }

  private void setupWrapper() throws CameraAccessException {
    int [] supportedValues = {CameraMetadata.CONTROL_AWB_MODE_AUTO,
            CameraMetadata.CONTROL_AWB_MODE_DAYLIGHT};
    doReturn(supportedValues).when(mockedCharacteristics)
            .get(CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES);
    doReturn(mockedCharacteristics)
            .when(mockedCameraWrapper).getCurrentCameraCharacteristics();
    doReturn(mockedPreviewBuilder).when(mockedCameraWrapper).getPreviewBuilder();
  }

}