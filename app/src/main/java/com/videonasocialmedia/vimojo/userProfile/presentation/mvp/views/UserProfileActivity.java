package com.videonasocialmedia.vimojo.userProfile.presentation.mvp.views;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.presentation.views.customviews.CircleImageView;
import com.videonasocialmedia.vimojo.userProfile.presentation.mvp.presenters.UserProfilePresenter;
import com.videonasocialmedia.vimojo.userProfile.presentation.views.UserProfileActivityView;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.Utils;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserProfileActivity extends VimojoActivity implements UserProfileActivityView {

  @Inject
  UserProfilePresenter presenter;

  @Bind(R.id.image_user_profile)
  CircleImageView image_user;
  @Bind(R.id.user_profile_prefession)
  TextView profession;
  @Bind(R.id.user_profile_username)
  TextView username;
  @Bind(R.id.user_profile_email)
  TextView email;
  @Nullable
  @Bind(R.id.text_dialog)
  EditText editTextDialog;
  @Bind(R.id.number_clips_recorded)
  TextView numberClipsRecorded;
  @Bind(R.id.number_projects_edited)
  TextView numberProjectsEdited;
  private ProgressDialog progressDialog;

  String userThumbPath = Constants.PATH_APP_TEMP + File.separator + Constants.USER_PROFILE_THUMB;
  private int REQUEST_ICON_USER_PROFILE = 200;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user_profile);
    ButterKnife.bind(this);
    getActivityPresentersComponent().inject(this);
    setupToolbar();
    createProgressDialog();
  }

  private void createProgressDialog() {
    progressDialog = new ProgressDialog(UserProfileActivity.this, R.style.VideonaDialog);
    progressDialog.setTitle(R.string.alert_dialog_title_user_profile);
    progressDialog.setMessage(getString(R.string.dialog_getting_user_profile));
    progressDialog.setProgressStyle(progressDialog.STYLE_HORIZONTAL);
    progressDialog.setIndeterminate(true);
    progressDialog.setProgressNumberFormat(null);
    progressDialog.setProgressPercentFormat(null);
    progressDialog.setCanceledOnTouchOutside(false);
    progressDialog.setCancelable(false);
  }

  private void setupToolbar() {
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayShowTitleEnabled(false);
    ActionBar ab = getSupportActionBar();
    ab.setDisplayHomeAsUpEnabled(true);
  }

  private void setUpAndCheckUserThumb() {
    image_user = (CircleImageView) findViewById(R.id.image_user_profile);
    image_user.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View view) {
        showDialogUserAddThumb();
      }
    });
    updateUserThumb(userThumbPath);
  }

  private void updateUserThumb(String path) {
    File thumb = new File(path);
    if (thumb.getName().compareTo(Constants.USER_PROFILE_THUMB) != 0) {
      try {
        Utils.copyFile(path, userThumbPath);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (thumb.exists()) {
      Glide.with(this)
          .load(userThumbPath)
          .diskCacheStrategy(DiskCacheStrategy.RESULT)
          .signature(new StringSignature(String.valueOf(thumb.lastModified())))
          .into(image_user);
    }
  }

  public void showDialogUserAddThumb() {
    // dialog pick or take photo
    final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        File file = new File(userThumbPath);
        Uri uri = Uri.fromFile(file);

        Intent userThumbSetter;
        switch (which) {
          case DialogInterface.BUTTON_NEUTRAL:
            // Pick from gallery button clicked
            userThumbSetter = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            userThumbSetter.setType("image/*");
            setIntentExtras(uri, userThumbSetter);
            startActivityForResult(userThumbSetter, REQUEST_ICON_USER_PROFILE);
            break;
        }
      }

      private void setIntentExtras(Uri uri, Intent takePicIntent) {
        takePicIntent.putExtra("crop", "true");
        takePicIntent.putExtra("outputX", 600);
        takePicIntent.putExtra("outputY", 600);
        takePicIntent.putExtra("aspectX", 1);
        takePicIntent.putExtra("aspectY", 1);
        takePicIntent.putExtra("scale", true);
        takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        takePicIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
      }
    };

    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this,
        R.style.VideonaDialog);
    builder.setMessage(R.string.dialog_editor_user_thumb_message)
        .setNeutralButton(R.string.dialog_editor_user_thumb_pick_photo, dialogClickListener).show();
  }

  @Override
  protected void onResume() {
    super.onResume();
    setUpAndCheckUserThumb();
    presenter.getInfoVideosRecordedEdited();
    presenter.getUserNameFromPreferences();
    presenter.getEmailFromPreferences();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == RESULT_OK && requestCode == REQUEST_ICON_USER_PROFILE && data != null) {
      if (data.getData() != null) {
        final String inPath = Utils.getPath(this, data.getData());
        if (inPath != null)
          updateUserThumb(inPath);
      }
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        onBackPressed();
        return true;
      default:
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void showPreferenceUserName(String userNamePreference) {
    username.setText(userNamePreference);
  }

  @Override
  public void showPreferenceEmail(String emailPreference) {
    email.setText(emailPreference);
  }

  @Override
  public void showLoading() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (!isFinishing()) {
          progressDialog.show();
        }
      }
    });
  }

  @Override
  public void hideLoading() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (progressDialog != null && progressDialog.isShowing()) {
          progressDialog.dismiss();
        }
      }
    });
  }

  @Override
  public void showVideosRecorded(final String videosRecorded) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        numberClipsRecorded.setText(videosRecorded);
      }
    });
  }

  @Override
  public void showVideosEdited(final String videosEdited) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        numberProjectsEdited.setText(videosEdited);
      }
    });
  }

  @OnClick(R.id.user_profile_username)
  public void showDialogUpdateUsername() {
    showDialogToUpdatePreference(username.getText().toString(), username);
  }

  private void showDialogToUpdatePreference(String text, final TextView textView) {
    View dialogView = getLayoutInflater().inflate(R.layout.dialog_insert_text, null);
    editTextDialog = (EditText) dialogView.findViewById(R.id.text_dialog);
    editTextDialog.setText(text);
    final DialogInterface.OnClickListener dialogClickListener =
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            hideKeyboard(editTextDialog);
            switch (which) {
              case DialogInterface.BUTTON_POSITIVE: {
                String textPreference = editTextDialog.getText().toString();
                if (textPreference.compareTo("") == 0)
                  return;
                textView.setText(textPreference);
                presenter.updateUserNamePreference(textPreference);
              }
              case DialogInterface.BUTTON_NEGATIVE:
                break;
            }
          }
        };

    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.VideonaDialog);
    AlertDialog alertDialog = builder.setCancelable(false)
        .setTitle("Cambiar nombre de usuario")
        .setView(dialogView)
        .setPositiveButton(R.string.positiveButton, dialogClickListener)
        .setNegativeButton(R.string.negativeButton, dialogClickListener)
        .setCancelable(false).show();

    editTextDialog.requestFocus();
    showKeyboard();
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

