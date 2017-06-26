package com.videonasocialmedia.camera.camera2;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.util.Log;
import android.util.Range;
import android.util.Rational;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by jliarte on 21/06/17.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class Camera2MeteringModeHelperTest {
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
  public void setExposureCompensationSetsCameraExposureCompensationAndUpdatesPreview()
          throws Exception {
    setupCameraWrapper();
    Camera2MeteringModeHelper aeHelper = new Camera2MeteringModeHelper(mockedCameraWrapper);

    aeHelper.setExposureCompensation(2);

    verify(mockedPreviewBuilder)
            .set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, 2);
    verify(mockedCameraWrapper).updatePreview();
  }

  private void setupCameraWrapper() throws CameraAccessException {
    int [] supportedValues = {CameraMetadata.CONTROL_AE_MODE_OFF,
            CameraMetadata.CONTROL_AE_MODE_ON};
    Range<Integer> compensationRange = Mockito.mock(Range.class);
    doReturn(-12).when(compensationRange).getLower();
    doReturn(12).when(compensationRange).getUpper();
    Rational mockedRational = Mockito.mock(Rational.class);
    doReturn(0.6F).when(mockedRational).floatValue();
    doReturn(supportedValues).doReturn(1).doReturn(compensationRange).doReturn(mockedRational)
            .when(mockedCharacteristics).get(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES);
    doReturn(mockedCharacteristics)
            .when(mockedCameraWrapper).getCurrentCameraCharacteristics();
    doReturn(mockedPreviewBuilder).when(mockedCameraWrapper).getPreviewBuilder();
  }

}