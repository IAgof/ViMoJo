package com.videonasocialmedia.vimojo.text.presentation.mvp.presenters;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.model.media.effects.TextEffect;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.export.domain.GetVideonaFormatFromCurrentProjectUseCase;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.repository.video.VideoRealmRepository;
import com.videonasocialmedia.vimojo.text.domain.ModifyVideoTextAndPositionUseCase;
import com.videonasocialmedia.vimojo.text.presentation.mvp.views.EditTextView;
import com.videonasocialmedia.videonamediaframework.utils.TextToDrawable;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.IntentConstants;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by ruth on 1/09/16.
 */
public class EditTextPreviewPresenter implements OnVideosRetrieved {

    private final String LOG_TAG = getClass().getSimpleName();
    private final Drawable drawableFadeTransitionVideo;
    private final VideonaFormat videoFormat;
    TextToDrawable drawableGenerator;

    private Video videoToEdit;
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;
    private EditTextView editTextView;
    protected UserEventTracker userEventTracker;
    protected Project currentProject;

    public EditTextPreviewPresenter(EditTextView editTextView, UserEventTracker userEventTracker,
                                    GetMediaListFromProjectUseCase getMediaListFromProjectUseCase) {
        this.editTextView = editTextView;
        this.userEventTracker = userEventTracker;
        this.getMediaListFromProjectUseCase = getMediaListFromProjectUseCase;

        this.currentProject = loadCurrentProject();
        // TODO:(alvaro.martinez) 23/11/16 Use Dagger for this injection
        GetVideonaFormatFromCurrentProjectUseCase getVideonaFormatFromCurrentProjectUseCase =
            new GetVideonaFormatFromCurrentProjectUseCase();
        videoFormat = getVideonaFormatFromCurrentProjectUseCase.getVideonaFormatFromCurrentProject();
        drawableGenerator = new TextToDrawable(VimojoApplication.getAppContext());
        drawableFadeTransitionVideo = VimojoApplication.getAppContext().getDrawable(R.drawable.alpha_transition_black);
    }

    private Project loadCurrentProject() {
        // TODO(jliarte): this should make use of a repository or use case to load the Project
        return Project.getInstance(null,null,null);
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

       /* Context appContext = VimojoApplication.getAppContext();
        Intent textToVideoServiceIntent = new Intent(appContext, ExportTempBackgroundService.class);
        textToVideoServiceIntent.putExtra(IntentConstants.VIDEO_ID, videoToEdit.getUuid());
        textToVideoServiceIntent.putExtra(IntentConstants.IS_TEXT_ADDED, true);
        textToVideoServiceIntent.putExtra(IntentConstants.TEXT_TO_ADD, text);
        textToVideoServiceIntent.putExtra(IntentConstants.TEXT_POSITION, textPositionSelected.name());
        textToVideoServiceIntent.putExtra(IntentConstants.VIDEO_TEMP_DIRECTORY,
            currentProject.getProjectPathIntermediateFiles());
        textToVideoServiceIntent.putExtra(IntentConstants.VIDEO_TEMP_DIRECTORY_FADE_AUDIO,
            currentProject.getProjectPathIntermediateFileAudioFade());
        appContext.startService(textToVideoServiceIntent);*/

        //Inject
        ModifyVideoTextAndPositionUseCase modifyVideoTextAndPositionUseCase =
            new ModifyVideoTextAndPositionUseCase(new VideoRealmRepository());

        modifyVideoTextAndPositionUseCase.addTextToVideo(drawableFadeTransitionVideo, videoToEdit,
            videoFormat, text, textPositionSelected.name(), currentProject.getProjectPathIntermediateFileAudioFade());

        userEventTracker.trackClipAddedText("center", text.length(), currentProject);
    }
}

