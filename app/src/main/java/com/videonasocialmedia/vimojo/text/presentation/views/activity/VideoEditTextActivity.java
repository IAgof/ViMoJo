package com.videonasocialmedia.vimojo.text.presentation.views.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.effects.TextEffect;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayer;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayerExo;
import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.text.presentation.mvp.presenters.EditTextPreviewPresenter;
import com.videonasocialmedia.vimojo.text.presentation.mvp.views.EditTextView;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.OnTouch;

import static com.videonasocialmedia.vimojo.utils.Constants.DEFAULT_PLAYER_HEIGHT_VERTICAL_MODE;
import static com.videonasocialmedia.vimojo.utils.UIUtils.tintButton;

/**
 * Created by ruth on 1/09/16.
 */
public class VideoEditTextActivity extends VimojoActivity implements EditTextView,
        VideonaPlayer.VideonaPlayerListener {
    private static String LOG_TAG = VideoEditTextActivity.class.getName();
    private final String STATE_BUTTON_TOP = "state_button_top";
    private final String STATE_BUTTON_CENTER = "state_button_center";
    private final String STATE_BUTTON_BOTTOM ="state_button_bottom" ;
    private final String VIDEO_POSITION = "video_position";
    private final String CURRENT_TEXT = "current_text";
    private final String THEME_DARK = "dark";

    @Inject EditTextPreviewPresenter presenter;

    @BindView(R.id.text_activityText)
    EditText clipText;
    @BindView(R.id.videona_player)
    VideonaPlayerExo videonaPlayer;
    @BindView(R.id.button_editText_center)
    ImageButton button_editText_center;
    @BindView(R.id.button_editText_top)
    ImageButton button_editText_top;
    @BindView(R.id.button_editText_bottom)
    ImageButton button_ediText_bottom;
    @BindView(R.id.button_ok_or_edit_text)
    Button buttonOkOrEditText;
    @BindView(R.id.edit_text_checkbox)
    CheckBox checkboxShadow;

    private Video video;
    int videoIndexOnTrack;
    private int currentPosition = 0;
    private String typedText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_text);
        ButterKnife.bind(this);

        getActivityPresentersComponent().inject(this);
        videonaPlayer.setSeekBarLayoutEnabled(false);
        videonaPlayer.setListener(this);

        Intent intent = getIntent();

        videoIndexOnTrack = intent.getIntExtra(Constants.CURRENT_VIDEO_INDEX, 0);
        presenter.updateColorButton();
        presenter.updateColorText();
        button_editText_top.setSelected(false);
        button_editText_center.setSelected(true);
        button_ediText_bottom.setSelected(false);
        restoreState(savedInstanceState);
        presenter.init(videoIndexOnTrack);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videonaPlayer.onShown(this);
        if (BuildConfig.FEATURE_VERTICAL_VIDEOS) {
            videonaPlayer.setAspectRatioVerticalVideos(DEFAULT_PLAYER_HEIGHT_VERTICAL_MODE);
        }
        presenter.updatePresenter();
    }

    @Override
    protected void onPause() {
        super.onPause();
        videonaPlayer.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videonaPlayer.onDestroy();
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(VIDEO_POSITION, 0);
            typedText = savedInstanceState.getString(CURRENT_TEXT,null);
            button_ediText_bottom.setSelected(savedInstanceState.getBoolean(STATE_BUTTON_BOTTOM));
            button_editText_center.setSelected(savedInstanceState.getBoolean(STATE_BUTTON_CENTER));
            button_editText_top.setSelected(savedInstanceState.getBoolean(STATE_BUTTON_TOP));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
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
        outState.putString(CURRENT_TEXT, getTextFromEditText());
        outState.putBoolean(STATE_BUTTON_TOP, button_editText_top.isSelected());
        outState.putBoolean(STATE_BUTTON_CENTER, button_editText_center.isSelected());
        outState.putBoolean(STATE_BUTTON_BOTTOM, button_ediText_bottom.isSelected());
        super.onSaveInstanceState(outState);
    }

    @OnClick(R.id.button_editText_top)
    public void onClickAddTextTop(){
        paintPositionEditText(TextEffect.TextPosition.TOP.name());
        updateTextInPreview();
        hideKeyboard(clipText);
    }

    @OnClick(R.id.button_editText_center)
    public void onClickAddTextCenter() {
        paintPositionEditText(TextEffect.TextPosition.CENTER.name());
        updateTextInPreview();
        hideKeyboard(clipText);
    }

    @OnClick(R.id.button_editText_bottom)
    public void onClickAddTextBottom() {
        paintPositionEditText(TextEffect.TextPosition.BOTTOM.name());
        updateTextInPreview();
        hideKeyboard(clipText);
    }

    @NonNull
    private String getTextFromEditText() {
        return clipText.getText().toString();
    }

    public void paintPositionEditText(String position) {
        if (position.equals(TextEffect.TextPosition.BOTTOM.name())) {
            button_editText_top.setSelected(false);
            button_editText_center.setSelected(false);
            button_ediText_bottom.setSelected(true);
        }
        if (position.equals(TextEffect.TextPosition.CENTER.name())) {
            button_editText_top.setSelected(false);
            button_editText_center.setSelected(true);
            button_ediText_bottom.setSelected(false);
        }
        if (position.equals(TextEffect.TextPosition.TOP.name())) {
            button_editText_top.setSelected(true);
            button_editText_center.setSelected(false);
            button_ediText_bottom.setSelected(false);
        }
    }

    @OnClick(R.id.button_editText_accept)
    public void onClickEditTextAccept() {
        presenter.setTextToVideo(getTextFromEditText(), getTextPositionSelected());
        navigateTo(EditActivity.class, videoIndexOnTrack);
        //finish();
    }

    @OnClick (R.id.button_ok_or_edit_text)
    public void onClickOkOrEditText() {
        if (clipText.hasFocus()) {
            hideKeyboard(clipText);
            buttonOkOrEditText.setText(getString(R.string.edit));
            clipText.clearFocus();
        } else {
            showKeyboard();
            buttonOkOrEditText.setText(getString(R.string.ok));
            clipText.setFocusable(true);
            clipText.requestFocus();
        }
    }

    @OnClick(R.id.button_editText_cancel)
    public void onClickEditTextCancel() {
        navigateTo(EditActivity.class, videoIndexOnTrack);
    }

    @OnTouch(R.id.text_activityText)
    boolean onTouch(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!clipText.hasFocus()) {
                buttonOkOrEditText.setText(getResources().getString(R.string.ok));
            }
        }
        return false;
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
        // (alvaro.martinez) 4/10/17 work on a copy to not modify original one until user accepts text
        video = new Video(movieList.get(0));
        ArrayList<Video> clipList = new ArrayList<>();
        clipList.add(video);
        if (video.hasText()) {
            clipText.setText(video.getClipText());
            paintPositionEditText(video.getClipTextPosition());
        }
        videonaPlayer.initPreviewLists(clipList);
        videonaPlayer.initPreview(currentPosition);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateProject() {
        presenter.updatePresenter();
    }

    @Override
    public void updateButtonToThemeDark() {
      tintEditButtons(R.color.button_color_theme_dark);
    }

    @Override
    public void updateButtonToThemeLight() {
        tintEditButtons(R.color.button_color_theme_light);
    }

    @Override
    public void updateTextToThemeDark() {
      tintText(R.color.textColorDark);
    }

    @Override
    public void updateTextToThemeLight() {
        tintText(R.color.textColorLight);
    }

    @Override
    public void setCheckboxShadow(boolean shadowActivated) {
        checkboxShadow.setChecked(shadowActivated);
    }

    private void updateTextInPreview() {
        Log.d(LOG_TAG, "updateTextInPreview ");
        String textPosition = getTextPositionSelected().name();
        String text = getTextFromEditText();
        videonaPlayer.setImageText(text, textPosition, checkboxShadow.isChecked(),
            Constants.DEFAULT_VIMOJO_WIDTH, Constants.DEFAULT_VIMOJO_HEIGHT);
    }


    @OnCheckedChanged(R.id.edit_text_checkbox)
    public void onCheckboxShadowClicked(CompoundButton button, boolean checked) {
        presenter.setCheckboxShadow(checked);
        updateTextInPreview();
    }

    private void tintText(int textColor) {
        clipText.setTextColor(getResources().getColor(textColor));
    }

    private void tintEditButtons(int tintList) {
      tintButton(button_editText_top, tintList);
      tintButton(button_editText_center, tintList);
      tintButton(button_ediText_bottom, tintList);
    }

  @Override
    public void newClipPlayed(int currentClipIndex) {
    }

    @Override
    public void playerReady() {
        updateTextInPreview();
        clipText.requestFocus();
        showKeyboard();
    }

    @OnTextChanged(R.id.text_activityText)
    void onTextChanged() {
        typedText = getTextFromEditText();
        updateTextInPreview();
        presenter.updateColorText();
    }

    public TextEffect.TextPosition getTextPositionSelected() {
        if (button_editText_top.isSelected()) {
            return TextEffect.TextPosition.TOP;
        }
        if (button_editText_center.isSelected()) {
            return TextEffect.TextPosition.CENTER;
        }
        if (button_ediText_bottom.isSelected()) {
            return TextEffect.TextPosition.BOTTOM;
        }
        // default
        return TextEffect.TextPosition.CENTER;
    }

    private void hideKeyboard(View v) {
        InputMethodManager keyboard =
                (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void showKeyboard(){
        InputMethodManager imm = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
}