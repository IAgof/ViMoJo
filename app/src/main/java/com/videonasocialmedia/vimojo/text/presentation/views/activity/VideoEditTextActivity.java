package com.videonasocialmedia.vimojo.text.presentation.views.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.GalleryActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.SettingsActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.VimojoActivity;
import com.videonasocialmedia.vimojo.text.presentation.mvp.presenters.EditTextPreviewPresenter;
import com.videonasocialmedia.vimojo.text.presentation.mvp.views.EditTextView;
import com.videonasocialmedia.vimojo.presentation.views.customviews.VideonaPlayer;
import com.videonasocialmedia.vimojo.presentation.views.listener.VideonaPlayerListener;
import com.videonasocialmedia.vimojo.text.presentation.views.customviews.EditTextMaxCharPerLine;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Created by ruth on 1/09/16.
 */
public class VideoEditTextActivity extends VimojoActivity implements EditTextView,VideonaPlayerListener{


    private static final String STATE_BUTTON_TOP = "state_button_top";
    private static final String STATE_BUTTON_CENTER = "state_button_center";
    private static final String STATE_BUTTON_BOTTOM ="state_button_bottom" ;
    private final String VIDEO_POSITION = "video_position";
    private final String CURRENT_TEXT = "current_text";
    private final String  TEXT_TO_ADD="image_of_text";

    @Bind(R.id.text_activityText)
    EditText textFile;
    @Bind(R.id.videona_player)
    VideonaPlayer videonaPlayer;
    @Bind(R.id.button_editText_center)
    ImageButton button_editText_center;
    @Bind(R.id.button_editText_top)
    ImageButton button_editText_top;
    @Bind(R.id.button_editText_bottom)
    ImageButton button_ediText_bottom;
    @Bind(R.id.imageVideoText)
    ImageView image_view_text;


    int videoIndexOnTrack;
    private EditTextPreviewPresenter presenter;
    private Video video;
    private String TAG = "VideoTextActivity";
    private int currentPosition = 0;
    private String text;
    private boolean stateButtomTopIsActivated =true;
    private boolean stateButtomCenterisActivated =false;
    private boolean stateButtonBottomIsActivated =false;
    private Drawable drawableText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_text);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        UserEventTracker userEventTracker = UserEventTracker.getInstance(MixpanelAPI.getInstance(this, BuildConfig.MIXPANEL_TOKEN));

        presenter = new EditTextPreviewPresenter(this, userEventTracker);

        videonaPlayer.setSeekBarEnabled(false);

        videonaPlayer.initVideoPreview(this);

        Intent intent = getIntent();
        videoIndexOnTrack = intent.getIntExtra(Constants.CURRENT_VIDEO_INDEX, 0);

        button_editText_top.setActivated(stateButtomTopIsActivated);
        button_editText_center.setActivated(stateButtomCenterisActivated);
        button_ediText_bottom.setActivated(stateButtonBottomIsActivated);

        restoreState(savedInstanceState);

        EditTextMaxCharPerLine.applyAutoWrap(textFile,30);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videonaPlayer.destroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        videonaPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.init(videoIndexOnTrack);
        image_view_text.setMaxHeight(videonaPlayer.getHeight());
        image_view_text.setMaxWidth(videonaPlayer.getWidth());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(VIDEO_POSITION, 0);
            text=savedInstanceState.getString(CURRENT_TEXT,null);
            stateButtonBottomIsActivated =savedInstanceState.getBoolean(STATE_BUTTON_BOTTOM);
            stateButtomCenterisActivated =savedInstanceState.getBoolean(STATE_BUTTON_CENTER);
            stateButtomTopIsActivated =savedInstanceState.getBoolean(STATE_BUTTON_TOP);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings_edit_options:
                navigateTo(SettingsActivity.class);
                return true;
            case R.id.action_settings_edit_gallery:
                navigateTo(GalleryActivity.class);
                return true;
            case R.id.action_settings_edit_tutorial:
                //navigateTo(TutorialActivity.class);
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    public void navigateTo(Class cls) {
        startActivity(new Intent(getApplicationContext(), cls));
    }

    @Override
    public void onBackPressed() {
        navigateTo(EditActivity.class, videoIndexOnTrack);
        finish();
    }

    private void navigateTo(Class cls, int currentVideoIndex) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(Constants.CURRENT_VIDEO_INDEX, currentVideoIndex);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(VIDEO_POSITION, videonaPlayer.getCurrentPosition());
        outState.putString(CURRENT_TEXT,text);
        outState.putBoolean(STATE_BUTTON_TOP, button_editText_top.isActivated());
        outState.putBoolean(STATE_BUTTON_CENTER, button_editText_center.isActivated());
        outState.putBoolean(STATE_BUTTON_BOTTOM, button_ediText_bottom.isActivated());
        //introducir imagen if path no es null
        super.onSaveInstanceState(outState);
    }



    @OnClick(R.id.button_editText_top)
    public void onClickAddTextTop(){
        text = getTextKeyboard();
        presenter.createDrawableWithText(text, Paint.Align.LEFT);
        button_editText_top.setActivated(true);
        button_editText_center.setActivated(false);
        button_ediText_bottom.setActivated(false);
    }

    @NonNull
    private String getTextKeyboard() {
        return textFile.getText().toString();
    }

    @OnClick(R.id.button_editText_center)
    public void onClickAddTextCenter(){
        text = getTextKeyboard();
        presenter.createDrawableWithText(text, Paint.Align.CENTER);
        button_editText_top.setActivated(false);
        button_editText_center.setActivated(true);
        button_ediText_bottom.setActivated(false);
    }

    @OnClick(R.id.button_editText_bottom)
    public void onClickAddTextBottom(){
        text= getTextKeyboard();
        presenter.createDrawableWithText(text, Paint.Align.LEFT);
        button_editText_top.setActivated(false);
        button_editText_center.setActivated(false);
        button_ediText_bottom.setActivated(true);
    }

    @OnClick(R.id.button_editText_accept)
    public void onClickTrimAccept() {

        presenter.setTextToVideo(text, 1280, 720);
        navigateTo(EditActivity.class, videoIndexOnTrack);
        finish();
    }

    @OnClick(R.id.button_editText_cancel)
    public void onClickTrimCancel() {
        navigateTo(EditActivity.class, videoIndexOnTrack);
    }


    @Override
    public void playPreview() {
        videonaPlayer.playPreview();
    }

    @Override
    public void pausePreview() {
        videonaPlayer.pausePreview();
    }

    @Override
    public void seekTo(int timeInMsec) {
        videonaPlayer.seekTo(timeInMsec);
    }

    @Override
    public void showPreview(List<Video> movieList) {
        video = movieList.get(0);
        videonaPlayer.initPreviewLists(movieList);
        videonaPlayer.initPreview(currentPosition);
        onTextChanged();
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showText(Drawable drawable) {
        drawableText = drawable;
        image_view_text.setImageDrawable(drawable);
    }


    @OnTextChanged(R.id.text_activityText)
    void onTextChanged() {

        if (null != textFile.getLayout() && textFile.getLayout().getLineCount() > 2) {
            showError("El máximo número de líneas es 2");

        } else {

            if (stateButtomTopIsActivated)
                onClickAddTextTop();
            if (stateButtomCenterisActivated)
                onClickAddTextCenter();
            if (stateButtonBottomIsActivated)
                onClickAddTextBottom();


        }
    }


    @Override
    public void newClipPlayed(int currentClipIndex) {

    }
}
