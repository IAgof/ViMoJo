package com.videonasocialmedia.vimojo.userProfile.presentation.views;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.auth.presentation.view.activity.UserAuthActivity;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.presentation.views.customviews.CircleImageView;
import com.videonasocialmedia.vimojo.userProfile.presentation.mvp.presenters.UserProfilePresenter;
import com.videonasocialmedia.vimojo.userProfile.presentation.mvp.views.UserProfileView;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.Utils;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserProfileActivity extends VimojoActivity implements UserProfileView {

  @Inject
  UserProfilePresenter presenter;

  @BindView(R.id.image_user_profile)
  CircleImageView image_user;
  @BindView(R.id.user_profile_prefession)
  TextView profession;
  @BindView(R.id.user_profile_username)
  TextView username;
  @BindView(R.id.user_profile_email)
  TextView email;
  @Nullable
  @BindView(R.id.text_dialog)
  EditText editTextDialog;
  @BindView(R.id.number_clips_recorded)
  TextView numberClipsRecorded;
  @BindView(R.id.number_projects_edited)
  TextView numberProjectsEdited;
  @BindView(R.id.number_projects_shared)
  TextView numberProjectsShared;
  @BindView(R.id.backButton)
  ImageButton backButton;
  private ProgressDialog progressDialog;
  private final String userThumbPath = Constants.PATH_APP_TEMP + File.separator + Constants.USER_PROFILE_THUMB;
  private final int REQUEST_ICON_USER_PROFILE = 200;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user_profile);
    ButterKnife.bind(this);
    getActivityPresentersComponent().inject(this);
    createProgressDialog();
  }

  private void createProgressDialog() {
    progressDialog = new ProgressDialog(UserProfileActivity.this, R.style.VideonaDialog);
    progressDialog.setTitle(R.string.alert_dialog_title_user_profile);
    progressDialog.setMessage(getString(R.string.dialog_getting_user_profile));
    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    progressDialog.setIndeterminate(true);
    progressDialog.setProgressNumberFormat(null);
    progressDialog.setProgressPercentFormat(null);
    progressDialog.setCanceledOnTouchOutside(false);
    progressDialog.setCancelable(false);
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

  private void showDialogUserAddThumb() {
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
    presenter.getInfoVideosRecordedEditedShared();
    presenter.setupUserInfo();
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

  @Override
  public void showVideosShared(final String videosShared) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        numberProjectsShared.setText(videosShared);
      }
    });
  }

  @Override
  public void showError(int stringId) {
    Snackbar.make(email, stringId ,Snackbar.LENGTH_LONG).show();
  }

  @Override
  public void navigateToUserAuth() {
    Intent intent = new Intent(this, UserAuthActivity.class);
    startActivity(intent);
  }

  @OnClick(R.id.backButton)
  public void onBackButtonClicked(){
    onBackPressed();
  }

  @OnClick(R.id.user_profile_username)
  public void onClickUsername() {
    presenter.onClickUsername(isEmptyField(username));
  }

  @OnClick(R.id.user_profile_email)
  public void onClickEmail() {
    presenter.onClickEmail(isEmptyField(email));
  }

  private boolean isEmptyField(TextView textView) {
    return textView.getText().toString().equals("");
  }

}
