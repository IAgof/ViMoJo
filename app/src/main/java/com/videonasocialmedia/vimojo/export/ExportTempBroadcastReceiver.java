package com.videonasocialmedia.vimojo.export;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.utils.IntentConstants;

import java.util.List;

/**
 *
 */
public class ExportTempBroadcastReceveiver extends BroadcastReceiver {
    private static final int MAX_NUM_TRIES_TO_EXPORT_VIDEO = 4;
    private final View parent;
    private GetMediaListFromProjectUseCase getMediaListFromProjectUseCase;

    public ExportTempBroadcastReceveiver(View parent) {
        this.parent = parent;
        getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //// TODO:(alvaro.martinez) 22/08/16 Here manage error trimming case, define new user story
        boolean result = intent.getBooleanExtra("videoExportedNavigateToShareActivity", false);
        String videoId = intent.getStringExtra("videoId");
        if (!result) {
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
            Project project = Project.getInstance(null,null,null);
            trimServiceIntent.putExtra(IntentConstants.VIDEO_TEMP_DIRECTORY,
                project.getProjectPathIntermediateFiles());
            trimServiceIntent.putExtra(IntentConstants.VIDEO_TEMP_DIRECTORY_FADE_AUDIO,
                project.getProjectPathIntermediateFileAudioFade());
            appContext.startService(trimServiceIntent);
        } else {
            // TODO:(alvaro.martinez) 28/09/16 Define user experience
        }
    }

    private Video getVideo(String videoId) {
        List<Media> videoList = getMediaListFromProjectUseCase.getMediaListFromProject();
        if (videoList != null) {
            for (Media video : videoList) {
                if (((Video) video).getUuid().compareTo(videoId) == 0) {
                    return (Video) video;
                }
            }
        }
        return null;
    }
}
