package com.videonasocialmedia.vimojo.presentation.views.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.record.presentation.views.activity.RecordCamera2Activity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by alvaro on 12/01/17.
 */

public class GoToRecordOrGalleryActivity extends EditorActivity {

  @Bind(R.id.fab_edit_room)
  FloatingActionsMenu fabMenu;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    inflateLinearLayout(R.id.container_layout, R.layout.activity_go_to_record_or_gallery);
    ButterKnife.bind(this);
    hideFabAndBottomBar();
  }

  private void hideFabAndBottomBar() {
    fabMenu.setVisibility(View.GONE);
  }

  @Nullable @OnClick(R.id.button_go_to_record)
  public void onClickGoToRecord(){
    //navigateTo(RecordActivity.class);
    navigateTo(RecordCamera2Activity.class);
  }

  @Nullable @OnClick(R.id.button_go_to_gallery)
  public void onClickGoToGallery(){
    navigateTo(GalleryActivity.class);
  }
}
