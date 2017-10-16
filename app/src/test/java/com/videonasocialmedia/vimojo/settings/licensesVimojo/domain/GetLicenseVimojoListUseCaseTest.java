package com.videonasocialmedia.vimojo.settings.licensesVimojo.domain;


import com.videonasocialmedia.vimojo.settings.licensesVimojo.source.VimojoLicensesProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

/**
 * Created by ruth on 24/08/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class GetLicenseVimojoListUseCaseTest {
  @Mock VimojoLicensesProvider mockedLicenseProvider;

  @InjectMocks GetLicenseVimojoListUseCase injectedUseCase;

  @Before
  public void injectMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void getLicenseListCallsVimojoLicenseProviderGetAll() {
    injectedUseCase.getLicenceList();

    verify(mockedLicenseProvider).getAll();
  }

}
