package com.videonasocialmedia.vimojo.settings.licensesVimojo.source;

import android.content.Context;

import com.videonasocialmedia.vimojo.settings.licensesVimojo.model.LicenseVimojo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;


import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;


/**
 *
 */
@RunWith(PowerMockRunner.class)
public class VimojoLicensesProviderTest {

  @Mock Context mockedContext;
  @Mock  VimojoLicensesProvider vimojoLicensesProvider;

  @Before public void injectDoubles() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void constructorClassCallPopulateLicenseList() {

    vimojoLicensesProvider.populateLicenseList();

    verify(vimojoLicensesProvider).populateLicenseList();
  }

  @Test
  public void testAddLicensesInList() {
    VimojoLicensesProvider vimojoLicensesProvider = new VimojoLicensesProvider(mockedContext);
    List <LicenseVimojo> license = vimojoLicensesProvider.getAll();

    vimojoLicensesProvider.getAll();

    assertThat(license.size(), is(2));
  }
}
