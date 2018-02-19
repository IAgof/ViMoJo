package com.videonasocialmedia.vimojo.presentation.views.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayerExo;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.record.presentation.views.activity.RecordCamera2Activity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by alvaro on 12/01/17.
 */

public class GoToRecordOrGalleryActivity extends EditorActivity {

  @BindView(R.id.fab_edit_room)
  FloatingActionsMenu fabMenu;

  @Nullable @BindView(R.id.videona_player)
  VideonaPlayerExo videonaPlayer;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    inflateLinearLayout(R.id.container_layout, R.layout.activity_go_to_record_or_gallery);
    ButterKnife.bind(this);
    hideFabAndBottomBar();
    hideVideonaPlayer();
  }

  private void hideVideonaPlayer() {
    videonaPlayer.setVisibility(View.GONE);
  }

  private void hideFabAndBottomBar() {
    fabMenu.setVisibility(View.GONE);
  }

  @Optional @OnClick(R.id.button_go_to_record)
  public void onClickGoToRecord(){
    navigateTo(RecordCamera2Activity.class);
  }

  @Optional @OnClick(R.id.button_go_to_gallery)
  public void onClickGoToGallery(){
    navigateTo(GalleryActivity.class);
  }
}
