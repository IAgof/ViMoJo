package com.videonasocialmedia.vimojo.galleryprojects.presentation.views.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
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
import com.videonasocialmedia.vimojo.model.entities.editor.ProjectInfo;
import com.videonasocialmedia.vimojo.utils.TimeUtils;

import java.util.ArrayList;
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

  public static final String DETAIL_PROJECT_TITLE = "DETAIL_PROJECT_TITLE";
  public static final String DETAIL_PROJECT_DESCRIPTION = "DETAIL_PROJECT_DESCRIPTION";
  public static final String DETAIL_PROJECT_PRODUCT_TYPES = "DETAIL_PROJECT_TITLE";

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

  private String[] productTypesTitles;
  private String[] productTypesSelected;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail_project);
    ButterKnife.bind(this);
    getActivityPresentersComponent().inject(this);
    initProductTyeTitles();
    presenter.init();
  }

  private String[] initProductTyeTitles() {
    productTypesTitles = new String[] {
        getString(R.string.detail_project_product_type_near_live),
        getString(R.string.detail_project_product_type_b_roll),
        getString(R.string.detail_project_product_type_files),
        getString(R.string.detail_project_product_type_interview),
        getString(R.string.detail_project_product_type_graphic),
        getString(R.string.detail_project_product_type_piece)
    };
    return productTypesTitles;
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    showTitleProject(savedInstanceState.getString(DETAIL_PROJECT_TITLE));
    showDescriptionProject(savedInstanceState.getString(DETAIL_PROJECT_DESCRIPTION));
    showProductTypeSelected(savedInstanceState.getStringArrayList(DETAIL_PROJECT_PRODUCT_TYPES));
    super.onRestoreInstanceState(savedInstanceState);
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    outState.putString(DETAIL_PROJECT_TITLE, editTextTitle.getText().toString());
    outState.putString(DETAIL_PROJECT_DESCRIPTION, editTextDescription.getText().toString());
    outState.putStringArray(DETAIL_PROJECT_PRODUCT_TYPES, productTypesSelected);
    super.onSaveInstanceState(outState);
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
  public void showProductTypeMultipleDialog(boolean[] checkedProductTypes) {
    List<String> productTypeSelectedList = new ArrayList<>();
    for(String productType: productTypesSelected) {
      productTypeSelectedList.add(productType);
    }
    // Build an AlertDialog
    AlertDialog.Builder builder = new AlertDialog.Builder(DetailProjectActivity.this);
    builder.setMultiChoiceItems(productTypesTitles, checkedProductTypes, new DialogInterface.OnMultiChoiceClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        // Update the current focused item's checked status
        checkedProductTypes[which] = isChecked;
        ProjectInfo.ProductType productType = getProductTypeFromPosition(which);
        if(isChecked) {
          presenter.addProductTypeSelected(productType);
          productTypeSelectedList.add(productType.name());
        } else {
          presenter.removeProductTypeSelected(productType);
          productTypeSelectedList.remove(productType.name());
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
        List<String> productTypeArrayList = Arrays.asList(productTypesTitles);
        for (int i = 0; i < checkedProductTypes.length; i++) {
          boolean checked = checkedProductTypes[i];
          if (checked) {
            appendProductTypeText(textViewProductType, productTypeArrayList.get(i),
                ContextCompat.getColor(DetailProjectActivity.this, R.color.colorAccent));
          }
        }
        productTypesSelected = productTypeSelectedList.toArray(new String[0]);
      }
    });
    textViewProductType.setText(getString(R.string.detail_project_product_type));
    builder.show();
  }

  @Override
  public void showProductTypeSelected(List<String> productTypeListSelected) {
    this.productTypesSelected = productTypeListSelected.toArray(new String[0]);
    List<String> productTypeArrayList = Arrays.asList(productTypesTitles);
    for(String productType: productTypeListSelected) {
      appendProductTypeText(textViewProductType,
          productTypeArrayList.get(getPositionFromProductType(productType)),
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

  @OnTouch({R.id.detail_project_description_scroll_view, R.id.detail_project_description_edit_text,
      R.id.detail_project_description_cardview}  )
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

  @OnClick(R.id.button_detail_project_info_accept)
  public void onClickAcceptInfoProject() {
    List<String> projectInfoProductTypeList = new ArrayList<>();
    for(String productTypeSelected: productTypesSelected) {
      projectInfoProductTypeList.add(productTypeSelected);
    }
    presenter.setProjectInfo(editTextTitle.getText().toString(),
        editTextDescription.getText().toString(), projectInfoProductTypeList);
    setResult(RESULT_OK);
    finish();
  }

  @OnClick(R.id.button_detail_project_info_cancel)
  public void onClickCancelInfoProject() {
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

  private int getPositionFromProductType(String productType) {
    if (productType.equals(ProjectInfo.ProductType.DIRECT_FAILURE.name())) {
      return 0;
    } else {
      if (productType.equals(ProjectInfo.ProductType.RAW_VIDEOS.name())) {
        return 1;
      } else {
        if (productType.equals(ProjectInfo.ProductType.SPOOLERS.name())) {
          return 2;
        } else {
          if (productType.equals(ProjectInfo.ProductType.TOTAL.name())) {
            return 3;
          } else {
            if (productType.equals(ProjectInfo.ProductType.GRAPHIC.name())) {
              return 4;
            } else {
              if (productType.equals(ProjectInfo.ProductType.PIECE.name())) {
                return 5;
              }
            }
          }
        }
      }
    }
    return -1;
  }

  public ProjectInfo.ProductType getProductTypeFromPosition(int position) {
    if (position == 0) {
      return ProjectInfo.ProductType.DIRECT_FAILURE;
    } else {
      if (position == 1) {
        return ProjectInfo.ProductType.RAW_VIDEOS;
      } else {
        if (position == 2) {
          return ProjectInfo.ProductType.SPOOLERS;
        } else {
          if (position == 3) {
            return ProjectInfo.ProductType.TOTAL;
          } else {
            if (position == 4) {
              return ProjectInfo.ProductType.GRAPHIC;
            } else {
              if (position == 5) {
                return ProjectInfo.ProductType.PIECE;
              }
            }
          }
        }
      }
    }
    return null;
  }

}
