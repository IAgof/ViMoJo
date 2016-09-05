package com.videonasocialmedia.vimojo.text.presentation.mvp;

import android.content.Context;
import android.content.Intent;

import com.videonasocialmedia.vimojo.VimojoApplication;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.export.ExportTempBackgroundService;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Media;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.utils.ExportIntentConstants;
import com.videonasocialmedia.vimojo.utils.UserEventTracker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alvaro on 5/09/16.
 */
public class TextPreviewPresenter implements OnVideosRetrieved {


    private Video videoToEdit;
    protected UserEventTracker userEventTracker;
    protected Project currentProject;
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;

    public TextPreviewPresenter(UserEventTracker userEventTracker) {
        getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        this.currentProject = loadCurrentProject();
        this.userEventTracker = userEventTracker;
    }

    private Project loadCurrentProject() {
        // TODO(jliarte): this should make use of a repository or use case to load the Project
        return Project.getInstance(null, null, null);
    }

    public void init(int videoToTrimIndex) {
        List<Media> videoList = getMediaListFromProjectUseCase.getMediaListFromProject();
        if (videoList != null) {
            ArrayList<Video> v = new ArrayList<>();
            videoToEdit = (Video) videoList.get(videoToTrimIndex);
            v.add(videoToEdit);
            onVideosRetrieved(v);
        }
    }

    public void setTextToVideo(String text, int sizeX, int sizeY, int posX, int posY){

        Context appContext = VimojoApplication.getAppContext();
        Intent textToVideoServiceIntent = new Intent(appContext, ExportTempBackgroundService.class);
        textToVideoServiceIntent.putExtra(ExportIntentConstants.VIDEO_ID, videoToEdit.getIdentifier());
        textToVideoServiceIntent.putExtra(ExportIntentConstants.IS_TEXT_ADDED, true);
        textToVideoServiceIntent.putExtra(ExportIntentConstants.TEXT_SIZE_X, sizeX);
        textToVideoServiceIntent.putExtra(ExportIntentConstants.TEXT_SIZE_Y, sizeY);
        textToVideoServiceIntent.putExtra(ExportIntentConstants.TEXT_POSITION_X, posX);
        textToVideoServiceIntent.putExtra(ExportIntentConstants.TEXT_POSITION_Y, posY);
        appContext.startService(textToVideoServiceIntent);
        userEventTracker.trackClipAddedText("center", text.length(), currentProject);
    }

    @Override
    public void onVideosRetrieved(List<Video> videoList) {
        Video video = videoList.get(0);
    }

    @Override
    public void onNoVideosRetrieved() {

    }
}
