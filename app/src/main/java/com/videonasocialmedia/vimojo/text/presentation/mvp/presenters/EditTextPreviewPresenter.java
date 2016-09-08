package com.videonasocialmedia.vimojo.text.presentation.mvp.presenters;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;

import com.videonasocialmedia.vimojo.VimojoApplication;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.export.ExportTempBackgroundService;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Media;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.text.presentation.mvp.views.EditTextView;
import com.videonasocialmedia.vimojo.text.presentation.views.activity.VideoEditTextActivity;
import com.videonasocialmedia.vimojo.text.util.TextToDrawable;
import com.videonasocialmedia.vimojo.utils.ExportIntentConstants;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruth on 1/09/16.
 */
public class EditTextPreviewPresenter implements OnVideosRetrieved {

    private final String LOG_TAG = getClass().getSimpleName();

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
        Video video = videoList.get(0);
    }

    @Override
    public void onNoVideosRetrieved() {
        editTextView.showError("No videos");
    }

    public void createDrawableWithText(String text, VideoEditTextActivity.TextPosition position) {

        Drawable drawable = TextToDrawable.createDrawableWithTextAndPosition(text,position.name());
        editTextView.showText(drawable);
    }

    public void setTextToVideo(String text, VideoEditTextActivity.TextPosition textPositionSelected) {

        Context appContext = VimojoApplication.getAppContext();
        Intent textToVideoServiceIntent = new Intent(appContext, ExportTempBackgroundService.class);
        textToVideoServiceIntent.putExtra(ExportIntentConstants.VIDEO_ID, videoToEdit.getIdentifier());
        textToVideoServiceIntent.putExtra(ExportIntentConstants.IS_TEXT_ADDED, true);
        textToVideoServiceIntent.putExtra(ExportIntentConstants.TEXT_TO_ADD, text);
        textToVideoServiceIntent.putExtra(ExportIntentConstants.TEXT_POSITION, textPositionSelected.name());
        appContext.startService(textToVideoServiceIntent);
        userEventTracker.trackClipAddedText("center", text.length(), currentProject);
    }
}

