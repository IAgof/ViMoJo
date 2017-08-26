package com.videonasocialmedia.vimojo.settings.licensesVimojo.presenter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.videonasocialmedia.vimojo.settings.licensesVimojo.domain.GetLicenseVimojoListUseCase;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.model.LicenseVimojo;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.presentation.mvp.presenters.LicenseListPresenter;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.presentation.mvp.views.LicenseListView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.verify;

/**
 * Created by ruth on 26/08/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class LicenseListPresenterTest {
  @Mock private Context mockedContext;
  @Mock private List<LicenseVimojo> licenseList;
  @Mock  private LicenseListView licenseListView;
  @Mock private GetLicenseVimojoListUseCase getLicenseListUseCase;

  @InjectMocks LicenseListPresenter licenseListPresenter;

  @Before public void injectMock() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testConstructorClassCallsGetLicenseVimojoListUseCase() {
    licenseListPresenter =getLicenseListPresenter();

    verify(getLicenseListUseCase).getLicenceList();
  }

  @Test
  public void testConstructorClassCallsGetLicenseListView() {
    licenseListPresenter =getLicenseListPresenter();

    licenseListPresenter.getLicenseList();

    verify(licenseListView).showLicenseList(anyList());
  }

  @NonNull
  LicenseListPresenter getLicenseListPresenter() {
    return new LicenseListPresenter(licenseListView, mockedContext,getLicenseListUseCase);
  }
}
