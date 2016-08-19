package com.videonasocialmedia.vimojo.trim.domain;

import android.app.IntentService;
import android.content.Intent;

import com.videonasocialmedia.transcoder.MediaTranscoderListener;
import com.videonasocialmedia.vimojo.domain.editor.GetMediaListFromProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Media;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;

import java.util.List;

/**
 *
 */
public class TrimBackgroundService extends IntentService implements MediaTranscoderListener {

    public static final String ACTION = "com.videonasocialmedia.android.service.receiver";
    Video video;

    public TrimBackgroundService() {
        super("TrimService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //video= getExtras(video)
        int videoId = intent.getIntExtra("videoId", -51456);
        int startTimeMs = intent.getIntExtra("startTimeMs", 0);
        int finishTimeMs = intent.getIntExtra("finishTimeMs", 0);

        getVideo(videoId);
        if (video != null) {
            ModifyVideoDurationUseCase modifyVideoDurationUseCase = new ModifyVideoDurationUseCase();
            video.setTempPath();
            modifyVideoDurationUseCase.trimVideo(video, startTimeMs, finishTimeMs, this);
        } else {
            onTranscodeFailed(null);
        }
    }

    private void getVideo(int videoId) {
        GetMediaListFromProjectUseCase getMediaListFromProjectUseCase = new GetMediaListFromProjectUseCase();
        List<Media> videoList = getMediaListFromProjectUseCase.getMediaListFromProject();
        if (videoList != null) {
            for (Media media : videoList) {
                if (media.getIdentifier() == videoId) {
                    video = (Video) media;
                }
            }
        }
    }

    @Override
    public void onTranscodeProgress(double v) {

    }

    @Override
    public void onTranscodeCompleted() {
        Intent intent = new Intent(ACTION);
        intent.putExtra("videoTrimmed", true);
        sendBroadcast(intent);
    }

    @Override
    public void onTranscodeCanceled() {
        video.deleteTempVideo();
        Intent intent = new Intent(ACTION);
        intent.putExtra("videoTrimmed", false);
        sendBroadcast(intent);
    }

    @Override
    public void onTranscodeFailed(Exception e) {
        video.deleteTempVideo();
        Intent intent = new Intent(ACTION);
        intent.putExtra("videoTrimmed", false);
        sendBroadcast(intent);
    }

}
