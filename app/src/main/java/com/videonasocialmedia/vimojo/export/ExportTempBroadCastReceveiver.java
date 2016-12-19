package com.videonasocialmedia.vimojo.export;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.project.ProjectRealmRepository;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.IntentConstants;

import java.util.List;

/**
 *
 */
public class ExportTempBroadCastReceveiver extends BroadcastReceiver {

    private static final int MAX_NUM_TRIES_TO_EXPORT_VIDEO = 4;
    private final View parent;
    private ProjectRepository projectRepository = new ProjectRealmRepository();
    public ExportTempBroadCastReceveiver(View parent) {
        this.parent = parent;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //// TODO:(alvaro.martinez) 22/08/16 Here manage error trimming case, define new user story
        boolean result = intent.getBooleanExtra("videoExported", false);
        String videoId = intent.getStringExtra("videoId");
        if(!result){
            relaunchTranscoder(videoId);
        }

    }

    private void relaunchTranscoder(String videoId) {

        Video video = getVideo(videoId);

        if(video.getNumTriesToExportVideo() < MAX_NUM_TRIES_TO_EXPORT_VIDEO) {

            Context appContext = VimojoApplication.getAppContext();
            Intent trimServiceIntent = new Intent(appContext, ExportTempBackgroundService.class);
            trimServiceIntent.putExtra(IntentConstants.VIDEO_ID, videoId);
            trimServiceIntent.putExtra(IntentConstants.RELAUNCH_EXPORT_TEMP, true);
            // TODO:(alvaro.martinez) 22/11/16 use project tmp path
            Project project = projectRepository.getCurrentProject();
            trimServiceIntent.putExtra(IntentConstants.VIDEO_TEMP_DIRECTORY,
                project.getProjectPath() + Constants.FOLDER_INTERMEDIATE_FILES);
            appContext.startService(trimServiceIntent);
        } else {
            // TODO:(alvaro.martinez) 28/09/16 Define user experience
        }
    }

    private Video getVideo(String videoId) {
        GetMediaListFromProjectUseCase getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        List<Video> videoList = getMediaListFromProjectUseCase.getVideoListFromProject();
        if (videoList != null) {
            for (Video video : videoList) {
                if (video.getUuid().compareTo(videoId) == 0) {
                    return video;
                }
            }
        }
        return null;
    }
}
