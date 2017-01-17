package com.videonasocialmedia.vimojo.record.presentation.views.activity;

import android.os.Bundle;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.record.presentation.views.fragment.RecordCamera2Fragment;

/**
 * Created by alvaro on 16/01/17.
 */

public class RecordCamera2Activity extends VimojoActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_record_camera2);
    if (null == savedInstanceState) {
      getFragmentManager().beginTransaction()
          .replace(R.id.container, RecordCamera2Fragment.newInstance())
          .commit();
    }
  }
}
