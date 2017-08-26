package com.videonasocialmedia.vimojo.settings.licensesVimojo.domain;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Created by ruth on 24/08/17.
 */
@RunWith(PowerMockRunner.class)
public class GetLicenseVimojoListUseCaseTest {
  @Mock Context mockedContext;

  @InjectMocks GetLicenseVimojoListUseCase injectedUseCase;

  @Before
  public void injectMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void getLicenseListFromRepositoryAndIsNotNull() {
    GetLicenseVimojoListUseCase getLicenseVimojoListUseCase = new GetLicenseVimojoListUseCase(mockedContext);

    getLicenseVimojoListUseCase.getLicenceList();

    assertThat("licenses are not null",getLicenseVimojoListUseCase.getLicenceList().size(), notNullValue());
  }

  @Test
  public void getLicenseListReturnSizeProperly() {
    GetLicenseVimojoListUseCase getLicenseVimojoListUseCase = new GetLicenseVimojoListUseCase(mockedContext);

    getLicenseVimojoListUseCase.getLicenceList();

    assertThat("licenses in Vimojo are two",getLicenseVimojoListUseCase.getLicenceList().size(), is(2));
  }

}
