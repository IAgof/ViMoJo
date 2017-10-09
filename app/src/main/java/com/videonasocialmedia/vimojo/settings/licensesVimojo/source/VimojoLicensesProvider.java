package com.videonasocialmedia.vimojo.settings.licensesVimojo.source;

import android.content.Context;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.settings.licensesVimojo.model.LicenseVimojo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */

public class VimojoLicensesProvider {

  private final Context context;
  private List<LicenseVimojo> licenseList = new ArrayList();

  public VimojoLicensesProvider(Context context) {
    this.context = context;
    populateLicenseList();
  }

  public List<LicenseVimojo> getAll() {
    return licenseList;
  }

  private void populateLicenseList() {
    licenseList.add(new LicenseVimojo("Design", context.getString(R.string.licenseDesign)));
    licenseList.add(new LicenseVimojo("Music", context.getString(R.string.licenseMusic)));
    licenseList.add(new LicenseVimojo("Exo Player",context.getString(R.string.licenseExoPlayer)));
    licenseList.add(new LicenseVimojo("Dagger", context.getString(R.string.licenseDagger)));
    licenseList.add(new LicenseVimojo("EventBus",context.getString(R.string.licenseEventBus)));
    licenseList.add(new LicenseVimojo("Glide", context.getString(R.string.licenseGlide)));
    licenseList.add(new LicenseVimojo("Google GSon", context.getString(R.string.licenseGoogleGSon)));
    licenseList.add(new LicenseVimojo("Guava", context.getString(R.string.licenseGuava)));
    licenseList.add(new LicenseVimojo("JCraft JSch ",context.getString(R.string.licenseJcraftJSch)));
    licenseList.add(new LicenseVimojo("JCraft JZlib ",context.getString(R.string.licenseJcraftJZ)));
    licenseList.add(new LicenseVimojo("JCodec", context.getString(R.string.licenseJCodec)));
    licenseList.add(new LicenseVimojo("Jerzy Chalupski. Floating Action Button",context.getString
        (R.string.licenseFloatingActionButton)));
    licenseList.add(new LicenseVimojo("Karumi",context.getString(R.string.licenseKarumi)));
    licenseList.add(new LicenseVimojo("MixPanel, Inc",context.getString(R.string.licenseMixPanel)));
    licenseList.add(new LicenseVimojo("Range SeekBar ",context.getString(R.string.licenseRangeSeekBar)));
    licenseList.add(new LicenseVimojo("Realm Android Adapters",context.getString(R.string.licenseRealm)));
    licenseList.add(new LicenseVimojo("Roughike. BottomBar",context.getString(R.string.licenseRoughikeBottomBar)));
    licenseList.add(new LicenseVimojo("Square OKhttp",context.getString(R.string.licenseSquare)));
    licenseList.add(new LicenseVimojo("The Android Open Source Project",context.getString
        (R.string.licenseAndroidOpenSource)));
    licenseList.add(new LicenseVimojo("Vertical Seekbar",context.getString(R.string.licenseVerticalSeekbar)));
    licenseList.add(new LicenseVimojo("Wasabeef",context.getString(R.string.licenseWasabeef)));
    licenseList.add(new LicenseVimojo("Yankai-Victor. Loading",context.getString(R.string.licenseYankaiVictor)));
    licenseList.add(new LicenseVimojo("Apache Commons Net",context.getString(R.string.licenseApacheCommonsNet)));

  }
}
