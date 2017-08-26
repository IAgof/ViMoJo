package com.videonasocialmedia.vimojo.settings.licensesVimojo.domain;

import android.content.Context;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.source.VimojoLicensesProvider;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.model.LicenseVimojo;
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
    return  new VimojoLicensesProvider(context).getAll();
  }

}
