package com.videonasocialmedia.vimojo.text.presentation.mvp.presenters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.TypedValue;

import com.videonasocialmedia.videonamediaframework.model.media.effects.TextEffect;
import com.videonasocialmedia.videonamediaframework.model.media.utils.ElementChangedListener;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.text.domain.ModifyVideoTextAndPositionUseCase;
import com.videonasocialmedia.vimojo.text.presentation.mvp.views.EditTextView;
import com.videonasocialmedia.videonamediaframework.utils.TextToDrawable;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by ruth on 1/09/16.
 */

public class EditTextPreviewPresenter implements OnVideosRetrieved, ElementChangedListener {
    private final String LOG_TAG = EditTextPreviewPresenter.class.getSimpleName();
    private final ProjectInstanceCache projectInstanceCache;

    private TextToDrawable drawableGenerator;

    private Video videoToEdit;

    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    private ModifyVideoTextAndPositionUseCase modifyVideoTextAndPositionUseCase;


    private EditTextView editTextView;
    private Context context;
    protected UserEventTracker userEventTracker;
    protected Project currentProject;
    private final String THEME_DARK = "dark";
    private int videoToEditTextIndex;
    private boolean isShadowChecked;


    @Inject
    public EditTextPreviewPresenter(
            EditTextView editTextView, Context context, UserEventTracker userEventTracker,
            GetMediaListFromProjectUseCase getMediaListFromProjectUseCase,
            ModifyVideoTextAndPositionUseCase modifyVideoTextAndPositionUseCase,
            ProjectInstanceCache projectInstanceCache) {
        this.editTextView = editTextView;
        this.context = context;
        this.userEventTracker = userEventTracker;
        this.getMediaListFromProjectUseCase = getMediaListFromProjectUseCase;
        this.modifyVideoTextAndPositionUseCase = modifyVideoTextAndPositionUseCase;
        this.projectInstanceCache = projectInstanceCache;
    }

    public void init(int videoToEditTextIndex) {
        this.videoToEditTextIndex = videoToEditTextIndex;
    }

    public void updatePresenter() {
        this.currentProject = projectInstanceCache.getCurrentProject();
        currentProject.addListener(this);
        List<Media> videoList = getMediaListFromProjectUseCase.getMediaListFromProject(currentProject);
        if (videoList != null) {
            ArrayList<Video> v = new ArrayList<>();
            videoToEdit = (Video) videoList.get(videoToEditTextIndex);
            v.add(videoToEdit);
            if (videoToEdit.hasClipTextShadow()) {
                isShadowChecked = true;
                editTextView.setCheckboxShadow(true);
            }
            onVideosRetrieved(v);
        }
    }

    @Override
    public void onVideosRetrieved(List<Video> videoList) {
        editTextView.showPreview(videoList);
    }

    @Override
    public void onNoVideosRetrieved() {
        editTextView.showError("No videos");
    }

    public void setTextToVideo(String text, TextEffect.TextPosition textPositionSelected) {

        modifyVideoTextAndPositionUseCase.addTextToVideo(currentProject, videoToEdit, text,
                textPositionSelected.name(), isShadowChecked);

        userEventTracker.trackClipAddedText(textPositionSelected.name(), text.length(),
            isShadowChecked, currentProject);
    }

    @Override
    public void onObjectUpdated() {
        editTextView.updateProject();
    }

  public void updateColorButton() {
      TypedValue currentTheme = getCurrentTheme();
      if (currentTheme.string.equals(THEME_DARK)) {
          editTextView.updateButtonToThemeDark();
      } else {
          editTextView.updateButtonToThemeLight();
      }
  }

    public void updateColorText() {
        TypedValue currentTheme = getCurrentTheme();
        if (currentTheme.string.equals(THEME_DARK)) {
            editTextView.updateTextToThemeDark();
        } else {
            editTextView.updateTextToThemeLight();
        }
    }

    @NonNull
    public TypedValue getCurrentTheme() {
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.themeName, outValue, true);
        return outValue;
    }

    public void setCheckboxShadow(boolean isShadowChecked) {
        this.isShadowChecked = isShadowChecked;
    }
}

