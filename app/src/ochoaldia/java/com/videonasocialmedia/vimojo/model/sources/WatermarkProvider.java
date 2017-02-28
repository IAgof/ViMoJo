package com.videonasocialmedia.vimojo.model.sources;

import android.content.Context;

import com.videonasocialmedia.videonamediaframework.model.media.Watermark;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.io.File;

/**
 * Created by alvaro on 27/02/17.
 */

public class WatermarkProvider {

  Context context;

  public WatermarkProvider(Context context){
    this.context = context;
  }

  public Watermark getWatermark(){
    return new Watermark(Constants.PATH_APP_TEMP + File.separator + "watermark.png");

  }
}
