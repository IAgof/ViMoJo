package com.videonasocialmedia.vimojo.settings.licensesVimojo.presenter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.videonasocialmedia.vimojo.settings.licensesVimojo.domain.GetLicenseVimojoListUseCase;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.presentation.mvp.presenters.LicenseDetailPresenter;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.presentation.mvp.views.LicenseDetailView;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.model.LicenseVimojo;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.source.VimojoLicensesProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by ruth on 24/08/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class LicenseDetailPresenterTest {

  @Mock private LicenseDetailView licenseDetailView;
  @Mock private Context mockedContext;
  @Mock private GetLicenseVimojoListUseCase getLicenseVimojoListUseCase;
  @Mock private VimojoLicensesProvider vimojoLicencesRepository;
  @Mock private LicenseVimojo licenseVimojo;
  @Mock @Parameterized.Parameter ArrayList<LicenseVimojo> licenseVimojoList;

  @InjectMocks LicenseDetailPresenter licenseDetailPresenter;

  @Before public void injectMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testLicenseDetailPresenterCallsGetLicenseVimojoCaseUse() {
    licenseDetailPresenter=getLicenseDetailPresenter();

    verify(getLicenseVimojoListUseCase).getLicenceList();
  }

  @Test
  public void testGetLicenseIfIdCoPassedIsEqualToIdLicenseOfList(){
    licenseDetailPresenter=getLicenseDetailPresenter();
    String idLicense = "1";
    licenseVimojoList = new ArrayList<>();
    LicenseVimojo license1 = new LicenseVimojo("1", "licenseContent");
    LicenseVimojo license2 = new LicenseVimojo("2", "licenseContent2");
    licenseVimojoList.add(license1);
    licenseVimojoList.add(license2);
    when(getLicenseVimojoListUseCase.getLicenceList()).thenReturn(licenseVimojoList);

    licenseVimojo= getLicenseDetailPresenter().retrieveLicense(idLicense);

    assertThat(licenseVimojo.getIdLicenseVimojo(), is("1"));
  }

  @Test
  public void testSetTitleAndContentCallsLicenseDetailView() {
    licenseDetailPresenter=getLicenseDetailPresenter();

    licenseDetailPresenter.setContentAndTitleLicense(licenseVimojo);

    verify(licenseDetailView).setContentLicense(licenseVimojo);
    verify(licenseDetailView).setTitleToolbar(licenseVimojo);
  }

  @NonNull LicenseDetailPresenter getLicenseDetailPresenter (){
    return new LicenseDetailPresenter(licenseDetailView, mockedContext, getLicenseVimojoListUseCase);
  }

}
