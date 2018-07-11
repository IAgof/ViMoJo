package com.videonasocialmedia.vimojo.text.presentation.mvp.presenters;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.TypedValue;

import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.model.media.effects.TextEffect;
import com.videonasocialmedia.videonamediaframework.model.media.utils.ElementChangedListener;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.main.ProjectInstanceCache;
import com.videonasocialmedia.vimojo.cut.domain.model.Project;
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

    public void createDrawableWithText(String text, String position, int width, int height) {
        drawableGenerator = new TextToDrawable(context);
        Drawable drawable = drawableGenerator.createDrawableWithTextAndPosition(text, position,
            width, height);
        editTextView.showText(drawable);
    }

    public void setTextToVideo(String text, TextEffect.TextPosition textPositionSelected) {
        VideonaFormat videoFormat = currentProject.getVMComposition().getVideoFormat();

        modifyVideoTextAndPositionUseCase.addTextToVideo(currentProject, videoToEdit, text,
                textPositionSelected.name());

        userEventTracker.trackClipAddedText("center", text.length(), currentProject);
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
}

