package com.videonasocialmedia.vimojo.settings.licensesVimojo.domain;

import com.videonasocialmedia.vimojo.settings.licensesVimojo.source.VimojoLicensesProvider;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.model.LicenseVimojo;
import java.util.List;
import javax.inject.Inject;

/**
 *
 */

public class GetLicenseVimojoListUseCase {
  private VimojoLicensesProvider vimojoLicensesProvider;

  @Inject
  public GetLicenseVimojoListUseCase(VimojoLicensesProvider vimojoLicensesProvider) {
    this.vimojoLicensesProvider = vimojoLicensesProvider;
  }

  public List<LicenseVimojo> getLicenceList() {
    return  vimojoLicensesProvider.getAll();
  }

}
