package com.videonasocialmedia.vimojo.trim.domain;

import android.app.IntentService;
import android.content.Intent;

import com.videonasocialmedia.transcoder.MediaTranscoderListener;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;

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
// TODO(javi.cabanas): 4/8/16 we need to map someway the video on one side of the intent to catch it on the other side i.e. we need an unique id to make sure the video is the one in the project
       //video= getExtras(video)
        int startTimeMs, finishTimeMs;

        startTimeMs = intent.getIntExtra("startTimeMs", 0);
        finishTimeMs = intent.getIntExtra("finishTimeMs", 0);

        ModifyVideoDurationUseCase modifyVideoDurationUseCase = new ModifyVideoDurationUseCase();
        modifyVideoDurationUseCase.trimVideo(video, startTimeMs, finishTimeMs, this);
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
