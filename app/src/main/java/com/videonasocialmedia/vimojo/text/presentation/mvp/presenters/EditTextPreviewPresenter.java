/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.vimojo.text.presentation.mvp.presenters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.TypedValue;

import com.crashlytics.android.Crashlytics;
import com.videonasocialmedia.videonamediaframework.model.VMComposition;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.videonamediaframework.model.media.effects.TextEffect;
import com.videonasocialmedia.videonamediaframework.model.media.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videonamediaframework.model.media.utils.ElementChangedListener;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.asset.domain.usecase.UpdateMedia;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;
import com.videonasocialmedia.vimojo.composition.domain.usecase.UpdateComposition;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;
import com.videonasocialmedia.vimojo.text.domain.ClipTextResultCallback;
import com.videonasocialmedia.vimojo.text.domain.ModifyVideoTextAndPositionUseCase;
import com.videonasocialmedia.vimojo.text.presentation.mvp.views.EditTextView;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import com.videonasocialmedia.vimojo.view.BackgroundExecutor;
import com.videonasocialmedia.vimojo.view.VimojoPresenter;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by ruth on 1/09/16.
 */

public class EditTextPreviewPresenter extends VimojoPresenter implements ElementChangedListener {
    private final String LOG_TAG = EditTextPreviewPresenter.class.getSimpleName();
    private Context context;
    private EditTextView editTextView;
    private final ProjectInstanceCache projectInstanceCache;
    private Video videoToEdit;
    private ModifyVideoTextAndPositionUseCase modifyVideoTextAndPositionUseCase;
    private UpdateMedia updateMedia;
    private UpdateComposition updateComposition;
    protected UserEventTracker userEventTracker;
    protected Project currentProject;
    private final String THEME_DARK = "dark";
    private int videoIndexOnTrack;
    protected boolean amIAVerticalApp;
    private String positionSelected = TextEffect.TextPosition.CENTER.name();
    private String textSelected = "";
    private boolean textHasShadow = false;
    protected boolean isPlayerReady = false;

    @Inject
    public EditTextPreviewPresenter(
        Context context, EditTextView editTextView, UserEventTracker userEventTracker,
        ModifyVideoTextAndPositionUseCase modifyVideoTextAndPositionUseCase, ProjectInstanceCache
            projectInstanceCache, UpdateMedia updateMedia, UpdateComposition updateComposition,
        @Named("amIAVerticalApp") boolean amIAVerticalApp,
        BackgroundExecutor backgroundExecutor) {
        super(backgroundExecutor, userEventTracker);
        this.context = context;
        this.editTextView = editTextView;
        this.userEventTracker = userEventTracker;
        this.modifyVideoTextAndPositionUseCase = modifyVideoTextAndPositionUseCase;
        this.projectInstanceCache = projectInstanceCache;
        this.updateMedia = updateMedia;
        this.updateComposition = updateComposition;
        this.amIAVerticalApp = amIAVerticalApp;
    }

    public void setupActivityViews() {
        updateColorButton();
        updateColorText();
    }

    public void updatePresenter(int videoToEditTextIndex) {
        this.videoIndexOnTrack = videoToEditTextIndex;
        this.currentProject = projectInstanceCache.getCurrentProject();
        currentProject.addListener(this);
        editTextView.attachView(context);
        editTextView.setVideonaPlayerListener();
        loadProjectVideo();
        if (amIAVerticalApp) {
            editTextView
                .setAspectRatioVerticalVideos(Constants.DEFAULT_PLAYER_HEIGHT_VERTICAL_MODE);
        }
        editTextView.showKeyboard();
    }

    public void pausePresenter() {
        editTextView.detachView();
    }

    private void loadProjectVideo() {
        videoToEdit = (Video) currentProject.getVMComposition().getMediaTrack().getItems()
            .get(videoIndexOnTrack);
        VMComposition vmCompositionCopy = null;
        try {
            vmCompositionCopy = new VMComposition(currentProject.getVMComposition());
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            illegalItemOnTrack.printStackTrace();
            Crashlytics.log("Error getting copy VMComposition " + illegalItemOnTrack);
        }
        Video videoCopy = (Video) vmCompositionCopy.getMediaTrack().getItems().get(videoIndexOnTrack);
        editTextView.initSingleClip(vmCompositionCopy, videoIndexOnTrack);
        if (videoCopy.hasText()) {
            positionSelected = videoCopy.getClipTextPosition();
            editTextView.setPositionEditText(positionSelected);
            textSelected = videoCopy.getClipText();
            editTextView.setEditText(textSelected);
            textHasShadow = videoCopy.hasClipTextShadow();
            if (textHasShadow) {
                editTextView.setCheckboxShadow(true);
            }
        }
    }
    public void setTextToVideo() {
        addCallback(modifyVideoTextAndPositionUseCase
            .addTextToVideo(currentProject, videoToEdit, textSelected, positionSelected,
                textHasShadow), new ClipTextResultCallback(currentProject, updateMedia,
            updateComposition));
        userEventTracker.trackClipAddedText(positionSelected, textSelected.length(),
            textHasShadow, currentProject);
        editTextView.navigateTo(EditActivity.class, videoIndexOnTrack);
    }

    @Override
    public void onObjectUpdated() {
        editTextView.updateProject();
    }

    private void updateColorButton() {
        TypedValue currentTheme = getCurrentTheme();
        if (currentTheme.string.equals(THEME_DARK)) {
            editTextView.updateButtonToThemeDark();
        } else {
            editTextView.updateButtonToThemeLight();
        }
    }

    private void updateColorText() {
        TypedValue currentTheme = getCurrentTheme();
        if (currentTheme.string.equals(THEME_DARK)) {
            editTextView.updateTextToThemeDark();
        } else {
            editTextView.updateTextToThemeLight();
        }
    }

    @NonNull
    private TypedValue getCurrentTheme() {
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.themeName, outValue, true);
        return outValue;
    }

    public void setCheckboxShadow(boolean isShadowChecked) {
        this.textHasShadow = isShadowChecked;
        if (isPlayerReady) {
            editTextView.setImageText(textSelected, positionSelected, textHasShadow);
        }
    }

    public void onClickPositionTop() {
        positionSelected = TextEffect.TextPosition.TOP.name();
        editTextView.setPositionEditText(positionSelected);
        editTextView.hideKeyboard();
        editTextView.setImageText(textSelected, positionSelected, textHasShadow);
    }

    public void onClickPositionCenter() {
        positionSelected = TextEffect.TextPosition.CENTER.name();
        editTextView.setPositionEditText(positionSelected);
        editTextView.hideKeyboard();
        editTextView.setImageText(textSelected, positionSelected, textHasShadow);
    }

    public void onClickPositionBottom() {
        positionSelected = TextEffect.TextPosition.BOTTOM.name();
        editTextView.setPositionEditText(positionSelected);
        editTextView.hideKeyboard();
        editTextView.setImageText(textSelected, positionSelected, textHasShadow);
    }

    public void onTextChanged(String text) {
        textSelected = text;
        if (isPlayerReady) {
            editTextView.setImageText(textSelected, positionSelected, textHasShadow);
        }
    }

    public void editTextCancel() {
        editTextView.navigateTo(EditActivity.class, videoIndexOnTrack);
    }

    public void playerReady() {
        isPlayerReady = true;
        editTextView.setImageText(textSelected, positionSelected, textHasShadow);
    }
}

