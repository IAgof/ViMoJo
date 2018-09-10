/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.main.modules;

import com.videonasocialmedia.vimojo.cameraSettings.repository.CameraSettingsDataSource;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.repository.project.ProfileRepository;

import static org.mockito.Mockito.mock;

/**
 * Created by alvaro on 25/4/18.
 */

public class MockedVimojoApplicationModule extends VimojoApplicationModule {

  public MockedVimojoApplicationModule(VimojoApplication application) {
    super(application);
  }

  @Override
  ProfileRepository provideProfileRepository(CameraSettingsDataSource cameraSettingsRepository,
                                             boolean amIAVerticalApp,
                                             String defaultResolutionSetting) {
    return mock(ProfileRepository.class);
  }

}
