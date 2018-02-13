package com.videonasocialmedia.vimojo.share.presentation.views.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.auth.presentation.view.activity.UserAuthActivity;
import com.videonasocialmedia.vimojo.ftp.presentation.services.FtpUploaderService;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.galleryprojects.presentation.views.activity.DetailProjectActivity;
import com.videonasocialmedia.vimojo.share.model.entities.FtpNetwork;
import com.videonasocialmedia.vimojo.share.model.entities.SocialNetwork;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditorActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.GoToRecordOrGalleryActivity;
import com.videonasocialmedia.vimojo.share.presentation.mvp.presenters.ShareVideoPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.views.OptionsToShareList;
import com.videonasocialmedia.vimojo.share.presentation.mvp.views.ShareVideoView;
import com.videonasocialmedia.vimojo.share.presentation.views.adapter.OptionsToShareAdapter;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayerExo;
import com.videonasocialmedia.vimojo.presentation.views.listener.OnOptionsToShareListClickListener;
import com.videonasocialmedia.vimojo.sound.presentation.views.activity.SoundActivity;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.IntentConstants;
import com.videonasocialmedia.vimojo.utils.Utils;

import java.io.File;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by root on 31/05/16.
 */
public class ShareActivity extends EditorActivity implements ShareVideoView,
        VideonaPlayer.VideonaPlayerListener, OnOptionsToShareListClickListener {
  public static final int NOTIFICATION_UPLOAD_COMPLETE_ID = 001;
  @Inject ShareVideoPresenter presenter;
    @Inject SharedPreferences sharedPreferences;

    @Nullable @BindView(R.id.linear_layout_activity_share)
    RelativeLayout coordinatorLayout;
    @Nullable @BindView(R.id.videona_player)
    VideonaPlayerExo videonaPlayer;
    @Nullable @BindView(R.id.options_to_share_list)
    RecyclerView optionsToShareList;
    @Nullable @BindView(R.id.text_dialog)
    EditText editTextDialog;
    @Nullable @BindView(R.id.bottomBar)
    BottomBar bottomBar;
    @BindView(R.id.fab_edit_room)
    FloatingActionsMenu fabMenu;

    private String videoPath;
    private OptionsToShareAdapter optionsShareAdapter;
    private int currentPosition;

  private ProgressDialog exportProgressDialog;
  private ProgressDialog checkingUserProgressDialog;
  private boolean acceptUploadVideoMobileNetwork;
  private boolean isWifiConnected = false;
  private boolean isMobileNetworConnected = false;
//  private BroadcastReceiver exportReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflateLinearLayout(R.id.container_layout, R.layout.activity_share);
        ButterKnife.bind(this);
        getActivityPresentersComponent().inject(this);
//        createExportReceiver();
        videonaPlayer.setListener(this);
        initOptionsShareList();
        restoreState(savedInstanceState);
        checkIntentExtras();
        bottomBar.selectTabWithId(R.id.tab_share);
        setupBottomBar(bottomBar);
        hideFab();
        initBarProgressDialog();
        checkNetworksAvailable();
    }

    // if user updates theme from drawer
    private void checkIntentExtras() {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            videoPath = bundle.getString("videoPath");
        }
    }

  @Override
  protected void onStart(){
    super.onStart();
  }

  @Override
  protected void onResume() {
    super.onResume();
    presenter.onResume();
    if (videoPath != null) {
      loadExportedVideoPreview(videoPath);
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    videonaPlayer.onPause();
//    unregisterReceiver(exportReceiver);
    exportProgressDialog.dismiss();
    checkingUserProgressDialog.dismiss();
  }

  @Override
  protected void onDestroy(){
    super.onDestroy();
  }

  // TODO(jliarte): 29/04/17 maybe we'll recover the receiver to allow user go to other app
//    private void createExportReceiver() {
//      exportReceiver = new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//          Bundle bundle = intent.getExtras();
//          if (bundle != null) {
//            String videoToSharePath = bundle.getString(ExportProjectService.FILEPATH);
//            int resultCode = bundle.getInt(ExportProjectService.RESULT);
//            if (resultCode == RESULT_OK) {
//              loadExportedVideoPreview(videoToSharePath);
//            } else {
//              showVideoExportError();
//            }
//          }
//        }
//      };
//    }

  private void initBarProgressDialog() {
    exportProgressDialog = new ProgressDialog(ShareActivity.this, R.style.VideonaDialog);
    exportProgressDialog.setTitle(R.string.dialog_title_export_project);
    exportProgressDialog.setMessage(getString(R.string.dialog_message_export_project));
    exportProgressDialog.setProgressStyle(exportProgressDialog.STYLE_HORIZONTAL);
    exportProgressDialog.setIndeterminate(true);
    exportProgressDialog.setProgressNumberFormat(null);
    exportProgressDialog.setProgressPercentFormat(null);
    exportProgressDialog.setCanceledOnTouchOutside(false);
    exportProgressDialog.setCancelable(false);

    checkingUserProgressDialog = new ProgressDialog(ShareActivity.this, R.style.VideonaDialog);
    checkingUserProgressDialog.setTitle(R.string.progress_dialog_title_checking_info_user);
    checkingUserProgressDialog.setMessage(getString(R.string.progress_dialog_message_checking_info_user));
    checkingUserProgressDialog.setProgressStyle(exportProgressDialog.STYLE_HORIZONTAL);
    checkingUserProgressDialog.setIndeterminate(true);
    checkingUserProgressDialog.setProgressNumberFormat(null);
    checkingUserProgressDialog.setProgressPercentFormat(null);
    checkingUserProgressDialog.setCanceledOnTouchOutside(false);
    checkingUserProgressDialog.setCancelable(false);
  }

  private void setupBottomBar(BottomBar bottomBar) {
    bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
      @Override
      public void onTabSelected(@IdRes int tabId) {
        switch (tabId){
          case(R.id.tab_editactivity):
            showDialogNewProject(R.id.button_edit_navigator);
            break;
          case (R.id.tab_sound):
            showDialogNewProject(R.id.button_music_navigator);
            break;
        }
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
    public void playPreview() {
        videonaPlayer.playPreview();
    }

    @Override
    public void pausePreview() {
        videonaPlayer.pausePreview();
    }

    public void showPreview() {
        List<Video> shareVideoList = Collections.singletonList(new Video(videoPath,
                Video.DEFAULT_VOLUME));
        videonaPlayer.initPreviewLists(shareVideoList);
        videonaPlayer.initPreview(currentPosition);
    }

    @Override
    public void showError(String message) {
      Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("currentPosition", videonaPlayer.getCurrentPosition());
        outState.putString("videoPath", videoPath);
        super.onSaveInstanceState(outState);
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
          currentPosition = savedInstanceState.getInt("currentPosition", 0);
          videoPath = savedInstanceState.getString("videoPath");
        }
    }

    public void navigateTo(Class cls) {
        Intent intent = new Intent(getApplicationContext(), cls);
        startActivity(intent);
    }

    @Optional @OnClick(R.id.fab_share_room)
    public void showMoreNetworks() {
        updateNumTotalVideosShared();
        presenter.trackVideoShared("Other network");
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("video/*");
        Uri uri = Utils.obtainUriToShare(this, videoPath);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, getString(R.string.share_using)));
    }

    private void updateNumTotalVideosShared() {
        presenter.updateNumTotalVideosShared();
    }

    @Override
    public void showOptionsShareList(List<OptionsToShareList> optionsToShareLists) {

        SocialNetwork saveToGallery = new SocialNetwork("SaveToGallery",getString(R.string.save_to_gallery), "", "",
                this.getResources().getDrawable(R.drawable.activity_share_save_to_gallery), "");
        optionsToShareLists.add(saveToGallery);
        optionsShareAdapter.setOptionShareLists(optionsToShareLists);
    }

    @Override
    public void hideShareNetworks() {
    }

    @Override
    public void showMoreNetworks(List<SocialNetwork> networks) {
    }

    @Override
    public void hideExtraNetworks() {
    }

    @Override
    public void onSocialNetworkClicked(SocialNetwork socialNetwork) {
        presenter.trackVideoShared(socialNetwork.getIdSocialNetwork());
        if (socialNetwork.getName().equals(getString(R.string.save_to_gallery))) {
            showMessage(R.string.video_saved);
            return;
        }
        presenter.shareVideo(videoPath, socialNetwork, this);
        updateNumTotalVideosShared();
    }

  @Override
  public void onVimojoPlatformClicked() {
    presenter.clickUploadToPlatform(isWifiConnected, acceptUploadVideoMobileNetwork,
        isMobileNetworConnected, videoPath);
  }

  private void checkNetworksAvailable() {
    ConnectivityManager connManager =
        (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    if (wifi.isConnected()) {
      isWifiConnected = true;
    } else {
      NetworkInfo mobileNetwork = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
      if(mobileNetwork.isConnected()) {
        isMobileNetworConnected = true;
      }
    }
  }

  @Override
  public void showDialogUploadVideoWithMobileNetwork() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.VideonaDialog);
    builder.setMessage(getResources().getString(R.string.upload_video_with_mobile_network));
    final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        switch (which) {
          case DialogInterface.BUTTON_POSITIVE:
            acceptUploadVideoMobileNetwork = true;
            onVimojoPlatformClicked();
            break;
          case DialogInterface.BUTTON_NEGATIVE:
            acceptUploadVideoMobileNetwork = false;
            break;
        }
      }
    };
    AlertDialog alertDialog = builder.setCancelable(true).
        setPositiveButton(R.string.dialog_accept_clean_project, dialogClickListener)
        .setNegativeButton(R.string.dialog_cancel_clean_project, dialogClickListener).show();
  }

  @Override
  public void showDialogNeedToRegisterLoginToUploadVideo() {
    // TODO: 9/2/18 Make Videona alertdialog info component
    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.VideonaDialog);
    builder.setMessage(getResources().getString(R.string.upload_video_register_login));
    final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        switch (which) {
          case DialogInterface.BUTTON_NEUTRAL:
            navigateToUserAuth();
            break;
        }
      }
    };
    AlertDialog alertDialog = builder.setCancelable(false).
        setNeutralButton("OK", dialogClickListener).show();
  }

  @Override
  public void showDialogNeedToCompleteDetailProjectFields() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.VideonaDialog);
    builder.setMessage(getResources().getString(R.string.upload_video_complete_project_info));
    final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        switch (which) {
          case DialogInterface.BUTTON_NEUTRAL:
            navigateToProjectDetails();
            break;
        }
      }
    };
    AlertDialog alertDialog = builder.setCancelable(false).
        setNeutralButton("OK", dialogClickListener).show();
  }

  @Override
  public void showProgressDialogCheckingInfoUse() {
    checkingUserProgressDialog.show();
  }

  @Override
  public void hideProgressDialogCheckingInfoUse() {
    checkingUserProgressDialog.dismiss();
  }


  private void navigateToUserAuth() {
    super.navigateTo(UserAuthActivity.class);
  }


  private void navigateToProjectDetails() {
    super.navigateTo(DetailProjectActivity.class);
  }

  @Override
    public void onFtpClicked(FtpNetwork ftp) {
        createDialogToInsertNameProject(ftp);
    }

    private void createDialogToInsertNameProject(final FtpNetwork ftpSelected) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_insert_text, null);
        editTextDialog = (EditText) dialogView.findViewById(R.id.text_dialog);
        editTextDialog.requestFocus();
        editTextDialog.setHint(R.string.text_hint_dialog_shareActivity);
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        String videoFtpName= editTextDialog.getText().toString();
                        renameFile(videoFtpName);
                        shareVideoWithFTP(ftpSelected);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.VideonaDialog);
        AlertDialog alertDialog = builder.setCancelable(false)
                .setTitle(R.string.title_dialog_sharedActivity)
                .setView(dialogView)
                .setPositiveButton(R.string.positiveButtonDialogShareActivity, dialogClickListener)
                .setNegativeButton(R.string.negativeButtonDialogShareActivity, dialogClickListener).show();
    }

    public void renameFile(String videoFtpName) {
        File file = new File(videoPath);
        String fileName = videoFtpName + ".mp4";
        File destinationFile = new File(Constants.PATH_APP, fileName);
        file.renameTo(destinationFile);
        videoPath = destinationFile.getPath();
    }

    public void shareVideoWithFTP(FtpNetwork ftp) {
        Intent intent = new Intent(this, FtpUploaderService.class);
        intent.putExtra("VIDEO_FOLDER_PATH", videoPath);
        intent.putExtra(IntentConstants.FTP_SELECTED, ftp.getIdFTP());
        startService(intent);
    }

    @Override
    public void showMessage(final int stringId) {
      runOnUiThread(new Runnable() {
          @Override
          public void run() {
              Snackbar snackbar = Snackbar.make(coordinatorLayout, stringId, Snackbar.LENGTH_LONG);
              snackbar.show();
          }
      });
    }

    private void showDialogNewProject(final int resourceButtonId) {
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        presenter.newDefaultProject(Constants.PATH_APP, Constants.PATH_APP_ANDROID);
                        navigateTo(GoToRecordOrGalleryActivity.class);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        if(resourceButtonId == R.id.button_music_navigator)
                            navigateTo(SoundActivity.class);
                        if(resourceButtonId == R.id.button_edit_navigator)
                            navigateTo(EditActivity.class);
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.VideonaDialog);
        AlertDialog alertDialogClearProject = builder.setCancelable(false)
                .setMessage(R.string.dialog_message_clean_project)
                .setPositiveButton(R.string.dialog_accept_clean_project, dialogClickListener)
                .setNegativeButton(R.string.dialog_cancel_clean_project, dialogClickListener).show();
    }

    @Override
    public void newClipPlayed(int currentClipIndex) {
    }

    @Override
    public void startVideoExport() {
      if (videoPath == null) {
        exportProgressDialog.show();
        presenter.startExport();
      }
    }

  @Override
    public void loadExportedVideoPreview(final String mediaPath) {
      final String destPath = getDestPath(mediaPath);
      final ShareActivity activity = this;
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          if (destPath != null) {
            videoPath = destPath;
            presenter.addVideoExportedToProject(videoPath);
            videonaPlayer.onShown(activity);
            showPreview();
          }
          exportProgressDialog.dismiss();
        }
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
  public void showVideoExportError(int cause) {
    exportProgressDialog.dismiss();
    showVideoExportErrorDialog(cause);
  }

  @Override
  public void showExportProgress(final String progressMsg) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (exportProgressDialog != null) {
          exportProgressDialog.setMessage(progressMsg);
        }
      }
    });
  }

  private void showVideoExportErrorDialog(final int cause) {
    final ShareActivity activity = this;
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        final DialogInterface.OnClickListener dialogClickListener = new
                DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                      case DialogInterface.BUTTON_NEUTRAL:
                        navigateTo(EditActivity.class);
                        break;
                    }
                  }
                };
        int dialog_message_export_error = R.string.dialog_message_export_error_unknown;
        switch (cause) {
          case Constants.EXPORT_ERROR_NO_SPACE_LEFT:
            dialog_message_export_error = R.string.dialog_message_export_error_no_space_left;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.VideonaDialog);
        AlertDialog alertDialogClearProject = builder.setCancelable(false)
                .setTitle(R.string.dialog_title_export_error)
                .setMessage(dialog_message_export_error)
                .setNeutralButton(R.string.ok, dialogClickListener).show();
      }
    });
  }

  // TODO(jliarte): 29/04/17 unused methods, delete them?
  private void onClickFabButton(final com.getbase.floatingactionbutton.FloatingActionButton fab) {
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
      }
    });
  }

  private void showMessage(String stringMessage) {
    exportProgressDialog.setMessage(stringMessage);
  }

}
