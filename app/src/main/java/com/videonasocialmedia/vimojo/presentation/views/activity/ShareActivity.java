package com.videonasocialmedia.vimojo.presentation.views.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.ftp.presentation.services.FtpUploaderService;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.model.entities.social.FtpNetwork;
import com.videonasocialmedia.vimojo.model.entities.social.SocialNetwork;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.ShareVideoPresenter;
import com.videonasocialmedia.vimojo.presentation.mvp.views.OptionsToShareList;
import com.videonasocialmedia.vimojo.presentation.mvp.views.ShareVideoView;
import com.videonasocialmedia.vimojo.presentation.views.adapter.OptionsToShareAdapter;
import com.videonasocialmedia.vimojo.presentation.views.customviews.ToolbarNavigator;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayerExo;
import com.videonasocialmedia.vimojo.presentation.views.listener.OnOptionsToShareListClickListener;
import com.videonasocialmedia.vimojo.sound.presentation.views.activity.SoundActivity;
import com.videonasocialmedia.vimojo.utils.ConfigPreferences;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.IntentConstants;
import com.videonasocialmedia.vimojo.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by root on 31/05/16.
 */
public class ShareActivity extends VimojoActivity implements ShareVideoView, VideonaPlayer.VideonaPlayerListener,
        OnOptionsToShareListClickListener {
    @Inject ShareVideoPresenter presenter;
    @Inject SharedPreferences sharedPreferences;

    @Nullable
    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @Nullable
    @Bind(R.id.videona_player)
    VideonaPlayerExo videonaPlayer;
    @Nullable
    @Bind(R.id.options_to_share_list)
    RecyclerView optionsToShareList;
    @Nullable
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Nullable
    @Bind(R.id.fab_share_room)
    FloatingActionButton fab;
    @Nullable
    @Bind(R.id.text_dialog)
    EditText editTextDialog;
    @Nullable
    @Bind(R.id.navigator)
    ToolbarNavigator navigator;

    private String videoPath;
    private OptionsToShareAdapter optionsShareAdapter;
    private int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ButterKnife.bind(this,ShareActivity.this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        getActivityPresentersComponent().inject(this);

        presenter.onCreate();
        videoPath = getIntent().getStringExtra(Constants.VIDEO_TO_SHARE_PATH);
        initOptionsShareList();
        videonaPlayer.setListener(this);
        restoreState(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        videonaPlayer.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videonaPlayer.onShown(this);
        presenter.onResume();
        showPreview();
    }

    private void initOptionsShareList() {
        optionsShareAdapter = new OptionsToShareAdapter(this);

        int orientation = LinearLayoutManager.VERTICAL;
        optionsToShareList.setLayoutManager(
                new LinearLayoutManager(this, orientation, false));
        optionsToShareList.setAdapter(optionsShareAdapter);
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
        List<Video> shareVideoList = new ArrayList<Video>();
        Video videoShare = new Video(videoPath);
        shareVideoList.add(videoShare);

        videonaPlayer.initPreviewLists(shareVideoList);
        videonaPlayer.initPreview(currentPosition);

        presenter.exportWithVoiceOver(videoPath);
    }

    @Override
    public void showError(String message) {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("currentPosition", videonaPlayer.getCurrentPosition());
        super.onSaveInstanceState(outState);
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt("currentPosition", 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings_edit_options:
                navigateTo(SettingsActivity.class);
                return true;
            case R.id.action_settings_edit_gallery:
                navigateTo(GalleryActivity.class);
                return true;
            default:

        }
        return super.onOptionsItemSelected(item);
    }

    public void navigateTo(Class cls) {
        Intent intent = new Intent(getApplicationContext(), cls);
        startActivity(intent);
    }

    @Nullable
    @OnClick(R.id.fab_share_room)
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

        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.VideonaAlertDialog);
        AlertDialog alertDialog = builder.setCancelable(false)
                .setTitle(R.string.title_dialog_sharedActivity)
                .setView(dialogView)
                .setPositiveButton(R.string.positiveButtonDialogShareActivity, dialogClickListener)
                .setNegativeButton(R.string.negativeButtonDialogShareActivity, dialogClickListener).show();
    }

    public void renameFile(String videoFtpName){
        File file = new File(videoPath);
        String fileName = videoFtpName + ".mp4";
        File destinationFile = new File(Constants.PATH_APP, fileName);
        file.renameTo(destinationFile);
        videoPath=destinationFile.getPath();
    }

    public void shareVideoWithFTP(FtpNetwork ftp){
        Intent intent = new Intent(this, FtpUploaderService.class);
        intent.putExtra("VIDEO_FOLDER_PATH", videoPath);
        intent.putExtra(IntentConstants.FTP_SELECTED, ftp.getIdFTP());
        startService(intent);
    }

    public void showMessage(final int stringToast) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, stringToast, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Nullable
    @OnClick (R.id.button_music_navigator)
    public void onMusicNavigatorClickListener(){
        showDialogClearProject(R.id.button_music_navigator);
    }

    @Nullable
    @OnClick (R.id.button_edit_navigator)
    public void onEditNavigatorClickListener(){
        showDialogClearProject(R.id.button_edit_navigator);
    }

    private void showDialogClearProject(final int resourceButtonId){
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        presenter.resetProject(sharedPreferences
                                .getString(ConfigPreferences.PRIVATE_PATH, ""));
                        navigator.navigateTo(EditActivity.class);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        if(resourceButtonId == R.id.button_music_navigator)
                            navigateTo(SoundActivity.class);
                        if(resourceButtonId == R.id.button_edit_navigator)
                            navigator.navigateTo(EditActivity.class);
                        if(resourceButtonId == R.id.navigator)
                            finish();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.VideonaAlertDialog);
        AlertDialog alertDialogClearProject = builder.setCancelable(false)
                .setMessage(R.string.dialog_message_clean_project)
                .setPositiveButton(R.string.dialog_accept_clean_project, dialogClickListener)
                .setNegativeButton(R.string.dialog_cancel_clean_project, dialogClickListener).show();
    }

    @Override
    public void newClipPlayed(int currentClipIndex) {
    }

    @Override
    public void setVideo(String videoOver){
        new File(videoPath).deleteOnExit();

        List<Video> shareVideoList = new ArrayList<Video>();
        Video videoShare = new Video(videoOver);
        shareVideoList.add(videoShare);

        videonaPlayer.initPreviewLists(shareVideoList);
        videonaPlayer.initPreview(currentPosition);

        videoPath = videoOver;
    }
}
