package com.videonasocialmedia.vimojo.test.shadows;

/**
 * Created by jliarte on 23/10/16.
 */

import android.content.Context;
import android.support.multidex.MultiDex;

import org.robolectric.annotation.Implements;
import org.robolectric.annotation.Implementation;

/**
 * Shadow for {@link android.support.multidex.MultiDex}.
 */
@Implements(MultiDex.class)
public class ShadowMultiDex {

  @Implementation
  public static void install(Context context) {
    // Do nothing since with Robolectric nothing is dexed.
  }
}