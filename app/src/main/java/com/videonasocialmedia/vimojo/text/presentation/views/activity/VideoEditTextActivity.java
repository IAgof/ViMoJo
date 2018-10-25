package com.videonasocialmedia.vimojo.text.presentation.views.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;

import com.videonasocialmedia.videonamediaframework.model.VMComposition;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.effects.TextEffect;
import com.videonasocialmedia.videonamediaframework.playback.VMCompositionPlayer;
import com.videonasocialmedia.videonamediaframework.playback.VideonaPlayerExo;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.main.VimojoActivity;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.text.presentation.mvp.presenters.EditTextPreviewPresenter;
import com.videonasocialmedia.vimojo.text.presentation.mvp.views.EditTextView;
import com.videonasocialmedia.vimojo.utils.Constants;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.OnTouch;

import static com.videonasocialmedia.vimojo.utils.UIUtils.tintButton;

/**
 * Created by ruth on 1/09/16.
 */
public class VideoEditTextActivity extends VimojoActivity implements EditTextView,
    VMCompositionPlayer, VMCompositionPlayer.VMCompositionPlayerListener {
    private static String LOG_TAG = VideoEditTextActivity.class.getName();

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
    int videoIndexOnTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_text);
        ButterKnife.bind(this);
        getActivityPresentersComponent().inject(this);
        Intent intent = getIntent();
        videoIndexOnTrack = intent.getIntExtra(Constants.CURRENT_VIDEO_INDEX, 0);
        button_editText_top.setSelected(false);
        button_editText_center.setSelected(true);
        button_ediText_bottom.setSelected(false);
        presenter.setupActivityViews();
        setVMCompositionPlayerListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.updatePresenter(videoIndexOnTrack);
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.removePresenter();
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

    @OnClick(R.id.button_editText_top)
    public void onClickAddTextTop(){
      presenter.onClickPositionTop();
    }

    @OnClick(R.id.button_editText_center)
    public void onClickAddTextCenter() {
      presenter.onClickPositionCenter();
    }

    @OnClick(R.id.button_editText_bottom)
    public void onClickAddTextBottom() {
      presenter.onClickPositionBottom();
    }

    @NonNull
    private String getTextFromEditText() {
        return clipText.getText().toString();
    }

    @OnClick(R.id.button_editText_accept)
    public void onClickEditTextAccept() {
        presenter.setTextToVideo();
    }

    @OnClick (R.id.button_ok_or_edit_text)
    public void onClickOkOrEditText() {
        if (clipText.hasFocus()) {
            hideKeyboard();
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
      presenter.editTextCancel();
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
    public void updateProject() {
        presenter.updatePresenter(videoIndexOnTrack);
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

    @Override
    public void setPositionEditText(String position) {
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

    @Override
    public void setEditText(String textSelected) {
        clipText.setText(textSelected);
    }

    @Override
    public void hideKeyboard() {
      InputMethodManager keyboard =
          (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
      keyboard.hideSoftInputFromWindow(clipText.getWindowToken(), 0);
    }

    @Override
    public void showKeyboard() {
      InputMethodManager imm = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
      imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @Override
    public void navigateTo(Class cls, int currentVideoIndex) {
      Intent intent = new Intent(this, cls);
      intent.putExtra(Constants.CURRENT_VIDEO_INDEX, currentVideoIndex);
      startActivity(intent);
    }

    @OnCheckedChanged(R.id.edit_text_checkbox)
    public void onCheckboxShadowClicked(CompoundButton button, boolean checked) {
        presenter.setCheckboxShadow(checked);
    }

    private void tintText(int textColor) {
        clipText.setTextColor(getResources().getColor(textColor));
    }

    private void tintEditButtons(int tintList) {
      tintButton(button_editText_top, tintList);
      tintButton(button_editText_center, tintList);
      tintButton(button_ediText_bottom, tintList);
    }

    @OnTextChanged(R.id.text_activityText)
    void onTextChanged() {
      presenter.onTextChanged(clipText.getText().toString());
    }

    @Override
    public void attachView(Context context) {
        videonaPlayer.attachView(context);
    }

    @Override
    public void detachView() {
        videonaPlayer.detachView();
    }

    @Override
    public void setVMCompositionPlayerListener(VMCompositionPlayerListener
                                                       vmCompositionPlayerListener) {
        videonaPlayer.setVMCompositionPlayerListener(vmCompositionPlayerListener);
    }

    @Override
    public void init(VMComposition vmComposition) {
        videonaPlayer.init(vmComposition);
    }

    @Override
    public void initSingleClip(VMComposition vmComposition, int clipPosition) {
        videonaPlayer.initSingleClip(vmComposition, clipPosition);
    }

    @Override
    public void initSingleVideo(Video video) {
        videonaPlayer.initSingleVideo(video);
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
    public void seekToClip(int position) {
        videonaPlayer.seekToClip(position);
    }

    @Override
    public void setSeekBarLayoutEnabled(boolean seekBarEnabled) {
        videonaPlayer.setSeekBarLayoutEnabled(seekBarEnabled);
    }

    @Override
    public void setAspectRatioVerticalVideos(int height) {
        videonaPlayer.setAspectRatioVerticalVideos(height);
    }

    @Override
    public void setImageText(String text, String textPosition, boolean textWithShadow, int width,
                             int height) {
        videonaPlayer.setImageText(text, textPosition, textWithShadow, width, height);
    }

    @Override
    public void setVideoVolume(float volume) {
        videonaPlayer.setVideoVolume(volume);
    }

    @Override
    public void setVoiceOverVolume(float volume) {
        videonaPlayer.setVoiceOverVolume(volume);
    }

    @Override
    public void setMusicVolume(float volume) {
        videonaPlayer.setMusicVolume(volume);
    }

    @Override
    public void newClipPlayed(int currentClipIndex) {
    }

    @Override
    public void playerReady() {
      clipText.requestFocus();
      showKeyboard();
      presenter.playerReady();
    }
}