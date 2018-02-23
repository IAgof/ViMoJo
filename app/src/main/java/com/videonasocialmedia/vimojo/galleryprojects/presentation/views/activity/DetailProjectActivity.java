package com.videonasocialmedia.vimojo.galleryprojects.presentation.views.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.presenters.DetailProjectPresenter;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.views.DetailProjectView;
import com.videonasocialmedia.vimojo.utils.TimeUtils;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

/**
 * Created by alvaro on 19/12/16.
 */

public class DetailProjectActivity extends VimojoActivity implements DetailProjectView {

  @Inject
  DetailProjectPresenter presenter;

  @BindView(R.id.detail_project_title_accept_button)
  Button buttonAcceptTitle;
  @BindView(R.id.detail_project_description_accept_button)
  Button buttonAcceptDescription;
  @BindView(R.id.detail_project_details_info_layout)
  RelativeLayout layoutDetailsInfo;
  @BindView(R.id.detail_project_title_edit_text)
  EditText editTextTitle;
  @BindView(R.id.detail_project_description_edit_text)
  EditText editTextDescription;
  @BindView(R.id.detail_project_product_type_values)
  TextView textViewProductType;
  @BindView(R.id.detail_project_details_expand)
  ImageButton detailsExpandShrink;
  @BindView(R.id.detail_project_duration)
  TextView textViewDuration;
  @BindView(R.id.detail_project_size)
  TextView textViewSize;
  @BindView(R.id.detail_project_quality)
  TextView textViewQuality;
  @BindView(R.id.detail_project_format)
  TextView textViewFormat;
  @BindView(R.id.detail_project_bitrate)
  TextView textViewBitRate;
  @BindView(R.id.detail_project_framerate)
  TextView textViewFrameRate;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail_project);
    ButterKnife.bind(this);
    getActivityPresentersComponent().inject(this);
    presenter.init();
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  @Override
  public void showTitleProject(String title) {
    editTextTitle.setText(title);
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

  @Override
  public void showAcceptTitleButton() {
    buttonAcceptTitle.setVisibility(View.VISIBLE);
    showKeyboard();
    editTextTitle.requestFocus();
    editTextTitle.setFocusable(true);
  }

  @Override
  public void hideAcceptTitleButton() {
    buttonAcceptTitle.setVisibility(View.GONE);
    hideKeyboard(editTextTitle);
    editTextTitle.clearFocus();
  }

  @Override
  public void showAcceptDescriptionButton() {
    buttonAcceptDescription.setVisibility(View.VISIBLE);
    showKeyboard();
    editTextDescription.requestFocus();
    editTextDescription.setFocusable(true);
  }

  @Override
  public void hideAcceptDescriptionButton() {
    buttonAcceptDescription.setVisibility(View.GONE);
    hideKeyboard(editTextDescription);
    editTextDescription.clearFocus();
  }

  @Override
  public void expandDetailsInfo() {
    layoutDetailsInfo.setVisibility(View.VISIBLE);
    detailsExpandShrink.setImageResource(R.drawable.activity_detail_project_ic_shrink_info_details);
  }

  @Override
  public void shrinkDetailsInfo() {
    layoutDetailsInfo.setVisibility(View.GONE);
    detailsExpandShrink.setImageResource(R.drawable.activity_detail_project_ic_expand_info_details);
  }

  @Override
  public void showDescriptionProject(String description) {
    editTextDescription.setText(description);
  }

  @Override
  public void showProductTypeMultipleDialog(String[] productTypesList,
                                            boolean[] checkedProductTypes) {
    // Build an AlertDialog
    AlertDialog.Builder builder = new AlertDialog.Builder(DetailProjectActivity.this);
    builder.setMultiChoiceItems(productTypesList, checkedProductTypes, new DialogInterface.OnMultiChoiceClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        // Update the current focused item's checked status
        checkedProductTypes[which] = isChecked;
        if(isChecked) {
          presenter.addProductTypeSelected(which);
        } else {
          presenter.removeProductTypeSelected(which);
        }
      }
    });
    // Specify the dialog is not cancelable
    builder.setCancelable(false);
    // Set a title for alert dialog
    builder.setTitle(getString(R.string.detail_project_product_type));
    // Set the positive/yes button click listener
    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        // Do something when click positive button
        List<String> productTypeArrayList = Arrays.asList(productTypesList);
        for (int i = 0; i < checkedProductTypes.length; i++) {
          boolean checked = checkedProductTypes[i];
          if (checked) {
            appendProductTypeText(textViewProductType, productTypeArrayList.get(i),
                ContextCompat.getColor(DetailProjectActivity.this, R.color.colorAccent));
          }
        }
      }
    });
    textViewProductType.setText(getString(R.string.detail_project_product_type));
    builder.show();
  }

  @Override
  public void showProductTypeSelected(List<String> productTypeList, String[] productTypesTitles) {
    List<String> productTypeArrayList = Arrays.asList(productTypesTitles);
    for(String productType: productTypeList) {
      appendProductTypeText(textViewProductType,
          productTypeArrayList.get(productTypeList.indexOf(productType)),
          ContextCompat.getColor(DetailProjectActivity.this, R.color.colorAccent));
    }
  }


  @OnTouch(R.id.detail_project_title_edit_text)
  public boolean onClickTitleEditText() {
    presenter.titleClicked();
    return false;
  }

  @OnClick(R.id.detail_project_title_accept_button)
  public void onClickTitleAcceptButton() {
    presenter.titleAccepted();
  }

  @OnTouch(R.id.detail_project_description_edit_text)
  public boolean onClickDescriptionEditText() {
    presenter.descriptionClicked();
    return false;
  }

  @OnClick(R.id.detail_project_description_accept_button)
  public void onClickDescriptionAcceptButton() {
    presenter.descriptionAccepted();
  }

  @OnClick(R.id.detail_project_details_expand)
  public void onClickDetailsExpand() {
    presenter.detailsExpand(layoutDetailsInfo);
  }

  @OnClick(R.id.button_detail_project_accept)
  public void onClickAcceptDetailProject() {
    presenter.setDetailProject(editTextTitle.getText(), editTextDescription.getText());
    finish();
  }

  @OnClick(R.id.button_detail_project_cancel)
  public void onClickCancelDetailProject() {
    finish();
  }

  @OnClick(R.id.detail_project_product_type_values)
  public void onClickProductTypes() {
    presenter.onClickProductTypes();
  }

  public static void appendProductTypeText(TextView productType, String text, int color) {
    int start = productType.getText().length();
    productType.append(" " + text);
    int end = productType.getText().length();

    Spannable spannableText = (Spannable) productType.getText();
    spannableText.setSpan(new ForegroundColorSpan(color), start, end, 0);
  }

  private void showKeyboard() {
    InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
  }

  private void hideKeyboard(View v) {
    InputMethodManager keyboard =
        (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    keyboard.hideSoftInputFromWindow(v.getWindowToken(), 0);
  }

}
