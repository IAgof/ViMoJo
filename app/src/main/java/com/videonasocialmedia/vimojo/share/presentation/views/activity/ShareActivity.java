package com.videonasocialmedia.vimojo.share.presentation.views.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.roughike.bottombar.BottomBar;
import com.videonasocialmedia.videonamediaframework.pipeline.VMCompositionExportSession;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.ftp.presentation.services.FtpUploaderService;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.views.activity.DetailProjectActivity;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.share.model.entities.FtpNetwork;
import com.videonasocialmedia.vimojo.share.model.entities.SocialNetwork;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditorActivity;
import com.videonasocialmedia.vimojo.share.presentation.mvp.presenters.ShareVideoPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.views.OptionsToShareList;
import com.videonasocialmedia.vimojo.share.presentation.mvp.views.ShareVideoView;
import com.videonasocialmedia.vimojo.share.presentation.views.adapter.OptionsToShareAdapter;
import com.videonasocialmedia.vimojo.presentation.views.listener.OnOptionsToShareListClickListener;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.IntentConstants;
import com.videonasocialmedia.vimojo.utils.Utils;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;


/**
 * Activity for sharing video final render to different networks and save locally.
 */
public class ShareActivity extends EditorActivity implements ShareVideoView,
    VideonaPlayer.VideonaPlayerListener, OnOptionsToShareListClickListener {

  private String LOG_TAG = ShareActivity.class.getCanonicalName();

  private static final int REQUEST_FILL_PROJECT_DETAILS = 54831;
  private static final int REQUEST_USER_AUTH = 54832;
  @Inject
  ShareVideoPresenter presenter;

  @Inject
  SharedPreferences sharedPreferences;
  @Nullable
  @BindView(R.id.linear_layout_activity_share)
  RelativeLayout coordinatorLayout;
  @Nullable
  @BindView(R.id.options_to_share_list)
  RecyclerView optionsToShareList;
  @Nullable
  @BindView(R.id.text_dialog)
  EditText editTextDialog;
  @Nullable
  @BindView(R.id.bottomBar)
  BottomBar bottomBar;
  @BindView(R.id.fab_edit_room)
  FloatingActionsMenu fabMenu;
  @Nullable
  @BindView(R.id.fab_share_room)
  FloatingActionButton fabShareRoom;

  TextView exportDialogMessage;

  private OptionsToShareAdapter optionsShareAdapter;
  private Dialog exportDialog;
  private ProgressDialog checkingUserProgressDialog;
  private AlertDialog exportErrorDialog;
  private boolean isAcceptedUploadWithMobileNetwork;
  private boolean isWifiConnected = false;
  private boolean isMobileNetworkConnected = false;
  private AdView adView;
  private View exportDialogView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    inflateLinearLayout(R.id.container_layout, R.layout.activity_share);
    ButterKnife.bind(this);
    getActivityPresentersComponent().inject(this);
    initOptionsShareList();
    bottomBar.selectTabWithId(R.id.tab_share);
    setupBottomBar(bottomBar);
    hideFab();
    initBarProgressDialog();
    checkNetworksAvailable();
  }

  @Override
  protected void onStart() {
    super.onStart();
  }

  @Override
  protected void onResume() {
    super.onResume();
    presenter.updatePresenter(projectHasBeenExported, videoExportedPath);
  }

  @Override
  protected void onPause() {
    super.onPause();
    exportDialog.dismiss();
    checkingUserProgressDialog.dismiss();
    if (exportErrorDialog != null) {
      exportErrorDialog.dismiss();
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    presenter.destroy();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK) {
      switch (requestCode) {
        case REQUEST_FILL_PROJECT_DETAILS:
        case REQUEST_USER_AUTH: // (jliarte): 27/02/18 by now the action is the same
          onVimojoPlatformClicked();
          break;
      }
    }
  }

  private void initBarProgressDialog() {
    LayoutInflater dialogLayout = LayoutInflater.from(ShareActivity.this);
    exportDialogView = dialogLayout.inflate(R.layout.dialog_progress_export, null);

    exportDialog = new Dialog(ShareActivity.this, R.style.VideonaDialog);
    //exportDialog.setContentView(DialogView);
    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
    lp.copyFrom(exportDialog.getWindow().getAttributes());
    lp.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
    lp.height = (int) (getResources().getDisplayMetrics().heightPixels * 0.65);
    exportDialog.getWindow().setAttributes(lp);

    exportDialogMessage = (TextView) exportDialogView.findViewById(R.id.exportDialogMessage);

    Button cancel = (Button) exportDialogView.findViewById(R.id.cancel_btn);
    cancel.setOnClickListener(v -> {
        exportDialog.dismiss();
        presenter.cancelExportation();
    });
    exportDialog.setCancelable(false);
    exportDialog.setCanceledOnTouchOutside(false);

    checkingUserProgressDialog = new ProgressDialog(ShareActivity.this, R.style.VideonaDialog);
    checkingUserProgressDialog.setTitle(R.string.progress_dialog_title_checking_info_user);
    checkingUserProgressDialog.setMessage(getString(R.string.progress_dialog_message_checking_user_auth));
    checkingUserProgressDialog.setProgressStyle(checkingUserProgressDialog.STYLE_HORIZONTAL);
    checkingUserProgressDialog.setIndeterminate(true);
    checkingUserProgressDialog.setProgressNumberFormat(null);
    checkingUserProgressDialog.setProgressPercentFormat(null);
    checkingUserProgressDialog.setCanceledOnTouchOutside(false);
    checkingUserProgressDialog.setCancelable(false);
  }

  @NonNull
  private DialogInterface.OnClickListener onClickCancelExportation() {
    return (dialog, which) -> presenter.cancelExportation();
  }

  private void setupBottomBar(BottomBar bottomBar) {
    bottomBar.setOnTabSelectListener(tabId -> {
      switch (tabId) {
        case (R.id.tab_editactivity):
          showNewProjectCreationDialog(R.id.button_edit_navigator);
          break;
        case (R.id.tab_sound):
          showNewProjectCreationDialog(R.id.button_music_navigator);
          break;
      }
    });
  }

  private void initOptionsShareList() {
    optionsShareAdapter = new OptionsToShareAdapter(this);
    int orientation = LinearLayoutManager.VERTICAL;
    optionsToShareList.setLayoutManager(
        new LinearLayoutManager(this, orientation, false));
    optionsToShareList.setAdapter(optionsShareAdapter);
  }

  private void hideFab() {
    fabMenu.setVisibility(View.GONE);
  }

  @Override
  public void showError(String message) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  }

  public void navigateTo(Class cls) {
    Intent intent = new Intent(getApplicationContext(), cls);
    startActivity(intent);
  }

  @Optional
  @OnClick(R.id.fab_share_room)
  public void showMoreNetworks() {
    presenter.onMoreSocialNetworkClicked();
  }

  @Override
  public void showOptionsShareList(List<OptionsToShareList> optionsToShareLists) {
    SocialNetwork saveToGallery = new SocialNetwork("SaveToGallery",
        getString(R.string.save_to_gallery), "", "",
        this.getResources().getDrawable(R.drawable.activity_share_save_to_gallery),
        "");
    optionsToShareLists.add(saveToGallery);
    optionsShareAdapter.setOptionShareLists(optionsToShareLists);
  }

  @Override
  public void onSocialNetworkClicked(SocialNetwork socialNetwork) {
    presenter.onSocialNetworkClicked(socialNetwork);
  }

  @Override
  public void onVimojoPlatformClicked() {
    checkNetworksAvailable();
    presenter.onVimojoPlatformClicked(isWifiConnected, isAcceptedUploadWithMobileNetwork,
        isMobileNetworkConnected);
  }

  private void checkNetworksAvailable() {
    ConnectivityManager connManager =
        (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    NetworkInfo mobileNetwork = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
    isWifiConnected = wifi.isConnected();
    isMobileNetworkConnected = mobileNetwork.isConnected();
  }

  @Override
  public void showDialogUploadVideoWithMobileNetwork() {
    runOnUiThread(() -> {
      AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.VideonaDialog);
      builder.setMessage(getResources().getString(R.string.upload_video_with_mobile_network));
      final DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
        switch (which) {
          case DialogInterface.BUTTON_POSITIVE:
            isAcceptedUploadWithMobileNetwork = true;
            onVimojoPlatformClicked();
            break;
          case DialogInterface.BUTTON_NEGATIVE:
            isAcceptedUploadWithMobileNetwork = false;
            break;
        }
      };
      builder.setCancelable(true)
          .setPositiveButton(R.string.dialog_accept_clean_project, dialogClickListener)
          .setNegativeButton(R.string.dialog_cancel_clean_project, dialogClickListener).show();
    });
  }

  @Override
  public void showDialogNeedToRegisterLoginToUploadVideo() {
    runOnUiThread(() -> {
      // TODO: 9/2/18 Make Videona alertdialog info component
      AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.VideonaDialog);
      builder.setMessage(getResources().getString(R.string.upload_video_register_login));
      final DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
        switch (which) {
          case DialogInterface.BUTTON_NEUTRAL:
            presenter.performLoginAndSaveAccount(this);
            break;
        }
      };
      builder.setCancelable(false).setNeutralButton("OK", dialogClickListener).show();
    });
  }

  @Override
  public void showDialogNeedToCompleteDetailProjectFields() {
    runOnUiThread(() -> {
      AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.VideonaDialog);
      builder.setMessage(getResources().getString(R.string.upload_video_complete_project_info));
      final DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
        switch (which) {
          case DialogInterface.BUTTON_NEUTRAL:
            navigateToProjectDetails();
            break;
        }
      };
      builder.setCancelable(false).setNeutralButton("OK", dialogClickListener).show();
    });
  }

  @Override
  public void showProgressDialogCheckingUserAuth() {
    runOnUiThread(() -> {
      checkingUserProgressDialog.show();
    });
  }

  @Override
  public void hideProgressDialogCheckingUserAuth() {
    checkingUserProgressDialog.dismiss();
  }

  @Override
  public void showDialogNotNetworkUploadVideoOnConnection() {
    runOnUiThread(() -> {
      AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.VideonaDialog);
      builder.setMessage(getResources().getString(R.string.upload_video_with_network_connected));
      final DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
        switch (which) {
          case DialogInterface.BUTTON_NEUTRAL:
            break;
        }
      };
      builder.setCancelable(false).setNeutralButton("OK", dialogClickListener).show();
    });
  }

  @Override
  public void pauseVideoPlayerPreview() {
    runOnUiThread(() -> pausePreview());
  }

  @Override
  public void hideExportProgressDialogCanceled() {
    runOnUiThread(() -> {
      if (exportDialog != null) {
        exportDialog.dismiss();
      }
    });
  }

  @Override
  public void hideShowMoreSocialNetworks() {
    fabShareRoom.setVisibility(View.GONE);
  }

  @Override
  public void showDialogVideoIsBeingSendingToPlatform() {
    runOnUiThread(() -> {
      AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.VideonaDialog);
      builder.setMessage(getResources().getString(R.string.upload_video_is_being_sending));
      final DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
        switch (which) {
          case DialogInterface.BUTTON_NEUTRAL:
            break;
        }
      };
      builder.setCancelable(false).setNeutralButton("OK", dialogClickListener).show();
    });
  }

  @Override
  public void successLoginAuth0() {
    onVimojoPlatformClicked();
  }

  @Override
  public void showDialogInstagramStoriesDuration() {
    runOnUiThread(() -> {
      AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.VideonaDialog);
      builder.setMessage(getResources().getString(R.string.share_instagram_duration_warning));
      final DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
        switch (which) {
          case DialogInterface.BUTTON_POSITIVE:
            presenter.exportOrProcessNetwork(OptionsToShareList.typeSocialNetwork);
            break;
          case DialogInterface.BUTTON_NEGATIVE:
            break;
        }
      };
      builder.setCancelable(false).setNegativeButton(getString(R.string.share_cancel),
          dialogClickListener);
      builder.setCancelable(false).setPositiveButton(getString(R.string.share_continue),
          dialogClickListener);
      builder.show();
    });
  }

  @Override
  public void showAdsView() {
    adView = (AdView) exportDialogView.findViewById(R.id.adView);
    adView.loadAd(new AdRequest.Builder().build());
    exportDialog.setContentView(exportDialogView);
  }

  @Override
  public void hideAdsCardView() {
    CardView adsCardView = (CardView) exportDialogView.findViewById(R.id.adsCardView);
    adsCardView.setVisibility(View.GONE);
    exportDialog.setContentView(exportDialogView);
  }

  private void navigateToProjectDetails() {
    Intent intent = new Intent(this, DetailProjectActivity.class);
    startActivityForResult(intent, REQUEST_FILL_PROJECT_DETAILS);
  }

  @Override
  public void onFtpClicked(FtpNetwork ftp) {
    presenter.onFtpClicked(ftp);
  }

  @Override
  public void createDialogToInsertNameProject(final FtpNetwork ftpSelected, String videoPath) {
    runOnUiThread(() -> {
      View dialogView = getLayoutInflater().inflate(R.layout.dialog_insert_text, null);
      editTextDialog = dialogView.findViewById(R.id.text_dialog);
      editTextDialog.requestFocus();
      editTextDialog.setHint(R.string.text_hint_dialog_shareActivity);
      final DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
        switch (which) {
          case DialogInterface.BUTTON_POSITIVE:
            String videoFtpName = editTextDialog.getText().toString();
            renameFile(videoFtpName, videoPath);
            shareVideoWithFTP(ftpSelected, videoPath);
            break;
          case DialogInterface.BUTTON_NEGATIVE:
            break;
        }
      };

      new AlertDialog.Builder(this, R.style.VideonaDialog).setCancelable(false)
          .setTitle(R.string.title_dialog_sharedActivity)
          .setView(dialogView)
          .setPositiveButton(R.string.positiveButtonDialogShareActivity, dialogClickListener)
          .setNegativeButton(R.string.negativeButtonDialogShareActivity, dialogClickListener)
          .show();
    });
  }

  @Override
  public void showIntentOtherNetwork(String videoPath) {
    Intent intent = new Intent(Intent.ACTION_SEND);
    intent.setType("video/*");
    Uri uri = Utils.obtainUriToShare(this, videoPath);
    intent.putExtra(Intent.EXTRA_STREAM, uri);
    startActivity(Intent.createChooser(intent, getString(R.string.share_using)));
  }

  @Override
  public void shareVideo(String videoPath, SocialNetwork socialNetworkSelected) {
    final ComponentName name = new ComponentName(socialNetworkSelected.getAndroidPackageName(),
        socialNetworkSelected.getAndroidActivityName());
    Uri uri = Utils.obtainUriToShare(this, videoPath);
    Intent intent = new Intent(Intent.ACTION_SEND);
    intent.setType("video/*");
    intent.putExtra(android.content.Intent.EXTRA_SUBJECT,
        VimojoApplication.getAppContext().getResources().getString(R.string.sharedWithVideona));
    intent.putExtra(Intent.EXTRA_TEXT,
        VimojoApplication.getAppContext().getResources().getString(R.string.videonaTags));
    intent.putExtra(Intent.EXTRA_STREAM, uri);
    intent.addCategory(Intent.CATEGORY_DEFAULT);
    intent.setComponent(name);
    startActivity(intent);
  }

  public void renameFile(String videoFtpName, String videoPath) {
    File file = new File(videoPath);
    String fileName = videoFtpName + ".mp4";
    File destinationFile = new File(Constants.PATH_APP, fileName);
    file.renameTo(destinationFile);
    videoPath = destinationFile.getPath();
  }

  public void shareVideoWithFTP(FtpNetwork ftp, String videoPath) {
    Intent intent = new Intent(this, FtpUploaderService.class);
    intent.putExtra("VIDEO_FOLDER_PATH", videoPath);
    intent.putExtra(IntentConstants.FTP_SELECTED, ftp.getIdFTP());
    startService(intent);
  }

  @Override
  public void showMessage(final int stringId) {
    runOnUiThread(() -> {
      Snackbar snackbar = Snackbar.make(coordinatorLayout, stringId, Snackbar.LENGTH_LONG);
      snackbar.show();
    });
  }

  @Override
  public void showProgressDialogVideoExporting() {
    exportDialog.show();
  }

  @Override
  public void loadExportedVideoPreview(final String mediaPath) {
    final String destPath = getDestPath(mediaPath);
    runOnUiThread(() -> {
      if (destPath != null) {
        projectHasBeenExported = true;
        presenter.updateHasBeenProjectExported(true);
        presenter.addVideoExportedToProject(mediaPath);
        initVideoPlayerFromFilePath(mediaPath);
      }
      exportDialog.dismiss();
    });
  }

  @NonNull
  private String getDestPath(String mediaPath) {
    // TODO(jliarte): 28/04/17 move to use case?
    File f = new File(mediaPath);
    String destPath = Constants.PATH_APP + File.separator + f.getName();
    File destFile = new File(destPath);
    f.renameTo(destFile);
    Utils.addFileToVideoGallery(destPath);
    return destPath;
  }

  @Override
  public void showVideoExportError(int cause, Exception exception) {
    exportDialog.dismiss();
    showVideoExportErrorDialog(cause);
    Crashlytics.logException(exception);
  }

  @Override
  public void showExportProgress(final int progressMsg) {
    runOnUiThread(() -> {
      if (exportDialog != null) {
        String progressMessage = "";
        switch (progressMsg) {
          case VMCompositionExportSession.EXPORT_STAGE_WAIT_FOR_TRANSCODING:
            progressMessage = getString(R.string.export_wait_for_transcoding);
            break;
          case VMCompositionExportSession.EXPORT_STAGE_WRITE_VIDEO_TO_DISK:
            progressMessage = getString(R.string.export_write_video_to_disk);
            break;
          case VMCompositionExportSession.EXPORT_STAGE_JOIN_VIDEOS:
            progressMessage = getString(R.string.export_join_videos);
            break;
          case VMCompositionExportSession.EXPORT_STAGE_ADD_AUDIO_TRACKS:
            progressMessage = getString(R.string.export_add_audio_tracks);
            break;
          case VMCompositionExportSession.EXPORT_STAGE_MIX_AUDIO:
            progressMessage = getString(R.string.export_mix_audio);
            break;
          case VMCompositionExportSession.EXPORT_STAGE_APPLY_AUDIO_MIXED:
            progressMessage = getString(R.string.export_apply_audio_mixed);
            break;
          case VMCompositionExportSession.EXPORT_STAGE_APPLY_WATERMARK:
            progressMessage = getString(R.string.export_apply_watermark);
            break;
        }
        exportDialogMessage.setText(progressMessage);
      }
    });
  }

  private void showVideoExportErrorDialog(final int cause) {
    final ShareActivity activity = this;
    runOnUiThread(() -> {
      final DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
        switch (which) {
          case DialogInterface.BUTTON_NEUTRAL:
            dialog.dismiss();
            break;
        }
      };
      int dialog_message_export_error = R.string.dialog_message_export_error_unknown;
      switch (cause) {
        case VMCompositionExportSession.EXPORT_STAGE_APPLY_WATERMARK_ERROR:
          dialog_message_export_error = R.string.export_apply_watermark_error;
          break;
        case VMCompositionExportSession.EXPORT_STAGE_MIX_AUDIO_ERROR:
          dialog_message_export_error = R.string.export_mix_audio_error;
          break;
        case VMCompositionExportSession.EXPORT_STAGE_APPLY_WATERMARK_RESOURCE_ERROR:
          dialog_message_export_error = R.string.export_apply_watermark_resource_error;
          break;
        case VMCompositionExportSession.EXPORT_STAGE_JOIN_VIDEOS_ERROR:
          dialog_message_export_error = R.string.export_join_videos_error;
          break;
        case VMCompositionExportSession.EXPORT_STAGE_WAIT_FOR_TRANSCODING_ERROR:
          dialog_message_export_error = R.string.export_wait_for_transcoding_error;
          break;
      }
      AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.VideonaDialog);
      builder.setCancelable(false).setTitle(R.string.dialog_title_export_error)
          .setMessage(dialog_message_export_error)
          .setNeutralButton(R.string.ok, dialogClickListener);
      exportErrorDialog = builder.create();
      exportErrorDialog.show();
    });
  }
}
