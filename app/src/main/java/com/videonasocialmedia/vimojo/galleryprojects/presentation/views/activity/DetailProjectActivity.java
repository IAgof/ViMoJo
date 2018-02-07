package com.videonasocialmedia.vimojo.galleryprojects.presentation.views.activity;

import android.app.AlertDialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.presenters.DetailProjectPresenter;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.mvp.views.DetailProjectView;
import com.videonasocialmedia.vimojo.utils.TimeUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    private boolean[] checkedProductTypes;
    private String[] productTypes;

    enum ProductType {
        DIRECT_FAILURE, RAW_VIDEOS, SPOOLERS, TOTAL, GRAPHIC, PIECE
    }
    private HashMap<ProductType, Boolean> productTypeIdsMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_project);
        ButterKnife.bind(this);
        getActivityPresentersComponent().inject(this);
        presenter.init();
        editTextTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                showAcceptTitleButton();
            }
        });
        editTextDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                showAcceptDescriptionButton();
            }
        });

        initProductTypeIdsMap();
        initMultipleChoiceProductTypes();
    }

    private void initProductTypeIdsMap() {
        // TODO: 7/2/18 Get this values from current project 
        productTypeIdsMap = new HashMap<>();
        productTypeIdsMap.put(ProductType.DIRECT_FAILURE, false);
        productTypeIdsMap.put(ProductType.RAW_VIDEOS, false);
        productTypeIdsMap.put(ProductType.SPOOLERS, false);
        productTypeIdsMap.put(ProductType.TOTAL, false);
        productTypeIdsMap.put(ProductType.GRAPHIC, false);
        productTypeIdsMap.put(ProductType.PIECE, false);
    }

    private void initMultipleChoiceProductTypes() {

        // String array for alert dialog multi choice items
        productTypes = new String[]{
                getString(R.string.detail_project_product_type_direct_failure),
                getString(R.string.detail_project_product_type_raw_videos),
                getString(R.string.detail_project_product_type_spoolers),
                getString(R.string.detail_project_product_type_total),
                getString(R.string.detail_project_product_type_graphic),
                getString(R.string.detail_project_product_type_piece)
        };

        // Boolean array for initial selected items
        checkedProductTypes = new boolean[]{
                productTypeIdsMap.get(ProductType.DIRECT_FAILURE),
                productTypeIdsMap.get(ProductType.RAW_VIDEOS),
                productTypeIdsMap.get(ProductType.SPOOLERS),
                productTypeIdsMap.get(ProductType.TOTAL),
                productTypeIdsMap.get(ProductType.GRAPHIC),
                productTypeIdsMap.get(ProductType.PIECE)
        };
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
    }

    @Override
    public void hideAcceptTitleButton() {
        buttonAcceptTitle.setVisibility(View.GONE);
        hideKeyboard(editTextTitle);
    }

    @Override
    public void showAcceptDescriptionButton() {
        buttonAcceptDescription.setVisibility(View.VISIBLE);
        showKeyboard();
        editTextTitle.requestFocus();
    }

    @Override
    public void hideAcceptDescriptionButton() {
        buttonAcceptDescription.setVisibility(View.GONE);
        hideKeyboard(editTextDescription);
    }

    @Override
    public void expandDetailsInfo() {
        layoutDetailsInfo.setVisibility(View.VISIBLE);
    }

    @Override
    public void shrinkDetailsInfo() {
        layoutDetailsInfo.setVisibility(View.GONE);
    }

    @OnClick(R.id.detail_project_title_edit_text)
    public void onClickTitleEditText() {
        presenter.titleClicked();
    }

    @OnClick(R.id.detail_project_title_accept_button)
    public void onClickTitleAcceptButton() {
        presenter.titleAccepted();
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

    }

    @OnClick(R.id.button_detail_project_cancel)
    public void onClickCancelDetailProject() {
        finish();
    }

    @OnClick(R.id.detail_project_product_type_values)
    public void onClickProductTypes() {
        showProductTypeMultipeDialog();
    }

    private void showProductTypeMultipeDialog() {
        // Build an AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailProjectActivity.this);

        // Convert the productTypes array to list
        final List<String> productTypeList = Arrays.asList(productTypes);

        builder.setMultiChoiceItems(productTypes, checkedProductTypes, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                // Update the current focused item's checked status
                checkedProductTypes[which] = isChecked;

                // Get the current focused item
                String currentItem = productTypeList.get(which);

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
                for (int i = 0; i<checkedProductTypes.length; i++){
                    boolean checked = checkedProductTypes[i];
                    if (checked) {
                        appendProductTypeText(textViewProductType, productTypeList.get(i),
                                ContextCompat.getColor(DetailProjectActivity.this, R.color.colorAccent));
                    }
                }
            }
        });

        textViewProductType.setText(getString(R.string.detail_project_product_type));

        builder.show();

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
