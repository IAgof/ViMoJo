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
    licenseList.add(new LicenseVimojo("Android Open Source Project",context.getString
        (R.string.licenseAndroidOpenSource)));
    licenseList.add(new LicenseVimojo("Android-Range-Seek-Bar",
        context.getString(R.string.licenseRangeSeekBar)));
    licenseList.add(new LicenseVimojo("AppIntro",context.getString(R.string.licenseAppIntro)));
    licenseList.add(new LicenseVimojo("BottomBar",context.getString(R.string.licenseRoughikeBottomBar)));
    licenseList.add(new LicenseVimojo("Butterknife", context.getString(R.string.licenseJakeWharton)));
    licenseList.add(new LicenseVimojo("Exo Player",context.getString(R.string.licenseExoPlayer)));
    licenseList.add(new LicenseVimojo("Dagger", context.getString(R.string.licenseDagger)));
    licenseList.add(new LicenseVimojo("Dexmaker", context.getString(R.string.licenseDexmaker)));
    licenseList.add(new LicenseVimojo("Dexter",context.getString(R.string.licenseKarumi)));
    licenseList.add(new LicenseVimojo("EventBus",context.getString(R.string.licenseEventBus)));
    licenseList.add(new LicenseVimojo("FloatingActionButton",context.getString
        (R.string.licenseFloatingActionButton)));
    licenseList.add(new LicenseVimojo("Glide", context.getString(R.string.licenseGlide)));
    licenseList.add(new LicenseVimojo("Glide Transformations",context.getString(R.string.licenseWasabeef)));
    licenseList.add(new LicenseVimojo("GSon", context.getString(R.string.licenseGoogleGSon)));
    licenseList.add(new LicenseVimojo("Guava", context.getString(R.string.licenseGuava)));
    licenseList.add(new LicenseVimojo("JSch ",context.getString(R.string.licenseJcraftJSch)));
    licenseList.add(new LicenseVimojo("JCodec", context.getString(R.string.licenseJCodec)));
    licenseList.add(new LicenseVimojo("JZlib ",context.getString(R.string.licenseJcraftJZ)));
    licenseList.add(new LicenseVimojo("Kickflip ",context.getString(R.string.licenseKickflip)));
    licenseList.add(new LicenseVimojo("Loading",context.getString(R.string.licenseYankaiVictor)));
    licenseList.add(new LicenseVimojo("MixPanel",context.getString(R.string.licenseMixPanel)));
    licenseList.add(new LicenseVimojo("MP4 Parser",context.getString(R.string.licenseMp4Parser)));
    licenseList.add(new LicenseVimojo("OKhttp",context.getString(R.string.licenseSquare)));
    licenseList.add(new LicenseVimojo("Realm Android Adapters",context.getString(R.string.licenseRealm)));
    licenseList.add(new LicenseVimojo("TwoWayView",context.getString(R.string.licenseTwoWayView)));
    licenseList.add(new LicenseVimojo("Vertical Seekbar",context.getString(R.string.licenseVerticalSeekbar)));
    licenseList.add(new LicenseVimojo("Apache Commons Net",context.getString(R.string.licenseApacheCommonsNet)));
    licenseList.add(new LicenseVimojo("OmRecorder", context.getString(R.string.licenseOmRecorder)));
    licenseList.add(new LicenseVimojo("Samsung SDK", context.getString(R.string.licenseSamsungSDK)));
  }
}
