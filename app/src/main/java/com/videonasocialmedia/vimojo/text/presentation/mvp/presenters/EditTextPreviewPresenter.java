package com.videonasocialmedia.vimojo.text.presentation.mvp.presenters;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.videonasocialmedia.videonamediaframework.model.media.effects.TextEffect;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.export.ExportTempBackgroundService;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.text.presentation.mvp.views.EditTextView;
import com.videonasocialmedia.videonamediaframework.utils.TextToDrawable;
import com.videonasocialmedia.vimojo.utils.IntentConstants;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruth on 1/09/16.
 */
public class EditTextPreviewPresenter implements OnVideosRetrieved {

    private final String LOG_TAG = getClass().getSimpleName();
    private final TextToDrawable drawableGenerator = new TextToDrawable();

    private Video videoToEdit;
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    private EditTextView editTextView;
    protected UserEventTracker userEventTracker;
    protected Project currentProject;

    public EditTextPreviewPresenter(EditTextView editTextView, UserEventTracker userEventTracker) {
        this.editTextView = editTextView;
        getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        this.currentProject = loadCurrentProject();
        this.userEventTracker = userEventTracker;
    }

    private Project loadCurrentProject() {
        // TODO(jliarte): this should make use of a repository or use case to load the Project
        return Project.getInstance(null, null, null);
    }

    public void init(int videoToEditTextIndex) {
        List<Media> videoList = getMediaListFromProjectUseCase.getMediaListFromProject();
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

        Drawable drawable = drawableGenerator.createDrawableWithTextAndPosition(text, position, width, height);
        editTextView.showText(drawable);
    }

    public void setTextToVideo(String text, TextEffect.TextPosition textPositionSelected) {

        Context appContext = VimojoApplication.getAppContext();
        Intent textToVideoServiceIntent = new Intent(appContext, ExportTempBackgroundService.class);
        textToVideoServiceIntent.putExtra(IntentConstants.VIDEO_ID, videoToEdit.getIdentifier());
        textToVideoServiceIntent.putExtra(IntentConstants.IS_TEXT_ADDED, true);
        textToVideoServiceIntent.putExtra(IntentConstants.TEXT_TO_ADD, text);
        textToVideoServiceIntent.putExtra(IntentConstants.TEXT_POSITION, textPositionSelected.name());
        appContext.startService(textToVideoServiceIntent);
        userEventTracker.trackClipAddedText("center", text.length(), currentProject);
    }
}

