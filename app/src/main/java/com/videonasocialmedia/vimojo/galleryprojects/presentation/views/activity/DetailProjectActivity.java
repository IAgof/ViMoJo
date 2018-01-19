package com.videonasocialmedia.vimojo.galleryprojects.presentation.views.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.presenters.DetailProjectPresenter;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.views.DetailProjectView;
import com.videonasocialmedia.vimojo.utils.TimeUtils;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by alvaro on 19/12/16.
 */

public class DetailProjectActivity extends VimojoActivity implements DetailProjectView {

  @Inject DetailProjectPresenter presenter;

  @Bind(R.id.detail_project_duration)
  TextView textViewDuration;
  @Bind(R.id.detail_project_size)
  TextView textViewSize;
  @Bind(R.id.detail_project_quality)
  TextView textViewQuality;
  @Bind(R.id.detail_project_format)
  TextView textViewFormat;
  @Bind(R.id.detail_project_bitrate)
  TextView textViewBitRate;
  @Bind(R.id.detail_project_framerate)
  TextView textViewFrameRate;

  @Bind(R.id.detail_project_title)
  TextView textViewTitle;

  @Bind(R.id.detail_project_thumb)
  ImageView imageViewThumb;

  private boolean isTitleSelected = false;
  private String titleProject;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail_project);
    ButterKnife.bind(this);
    setupToolbar();
    getActivityPresentersComponent().inject(this);
    presenter.init();

  }

  private void setupToolbar() {
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayShowTitleEnabled(false);
    ActionBar ab = getSupportActionBar();
    ab.setDisplayHomeAsUpEnabled(true);
  }

  @Override
  public void onResume(){
    super.onResume();
  }

  @OnClick(R.id.detail_project_title)
  public void onClickProjectTitle() {
    View dialogView = getLayoutInflater().inflate(R.layout.dialog_editable_text, null);
    final EditText editText = (EditText) dialogView.findViewById(R.id.detail_project_title_dialog);
    editText.setText(titleProject);
    editText.setSelectAllOnFocus(true);

    final DialogInterface.OnClickListener dialogClickListener
        = new DialogInterface.OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {
        hideKeyboard(editText);
        if(which == DialogInterface.BUTTON_POSITIVE) {
            String userTextTitle = editText.getText().toString();
            if(userTextTitle.compareTo("") == 0)
              return;
            textViewTitle.setText(userTextTitle);
            isTitleSelected = true;
        }
      }
    };

    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.VideonaDialog);
    builder.setTitle(R.string.dialog_project_edit_title_message)
        .setView(dialogView)
        .setPositiveButton(R.string.dialog_project_edit_title_accept, dialogClickListener)
        .setNegativeButton(R.string.dialog_project_edit_title_cancel, dialogClickListener)
        .setCancelable(false)
        .show();

    // showKeyboard
    editText.requestFocus();
    showKeyboard();

  }

  private void showKeyboard(){
    InputMethodManager imm = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
  }

  private void hideKeyboard(View v) {
    InputMethodManager keyboard =
        (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    keyboard.hideSoftInputFromWindow(v.getWindowToken(), 0);
  }


  @Override
  public void showDetailProjectThumb(String pathThumb) {
    Glide.with(VimojoApplication.getAppContext())
        .load(pathThumb)
        .centerCrop()
        .error(R.drawable.fragment_gallery_no_image)
        .into(imageViewThumb);

  }

  @Override
  public void showTitleProject(String title) {
    textViewTitle.setText(title);
    titleProject = title;
    onClickProjectTitle();
  }

  @Override
  public void showDetailProjectInfo(int duration, double projectSizeMbVideoToExport, int width,
                                    double videoBitRate, int frameRate) {


    textViewDuration.append(": " + TimeUtils.toFormattedTimeWithMinutesAndSeconds(duration));
    textViewSize.append(": " + projectSizeMbVideoToExport + " Mb");
    textViewQuality.append(": " + width);
    textViewFormat.append(": " + "mp4");
    textViewBitRate.append(": " + videoBitRate + " Mbps");
    textViewFrameRate.append(": " + frameRate);

  }

  @OnClick(R.id.button_detail_project_accept)
  public void onClickAcceptDetailProject(){
    if(isTitleSelected)
      presenter.setTitleProject(textViewTitle.getText().toString());
    finish();
  }

  @OnClick(R.id.button_detail_project_cancel)
  public void onClickCancelDetailProject(){
    finish();
  }


}
