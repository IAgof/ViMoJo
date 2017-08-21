package com.videonasocialmedia.vimojo.settings.licensesVimojo.domain;

import android.content.Context;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.presentation.view.source.VimojoLicencesRepository;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.presentation.view.model.LicenseVimojo;
import java.util.List;
import javax.inject.Inject;

/**
 *
 */

public class GetLicenseVimojoListUseCase {
  private Context context;

  @Inject
  public GetLicenseVimojoListUseCase(Context context) {
    this.context = context;
  }

  public List<LicenseVimojo> getLicenceList() {
    return  new VimojoLicencesRepository(context).getAll();
  }

}
