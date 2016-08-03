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

    public TrimBackgroundService() {
        super("TrimService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // TODO(javi.cabanas): 2/8/16 the video is not parcelable so it is not possible to pass through extras
        Video video;
        int startTimeMs, finishTimeMs;

        startTimeMs = intent.getIntExtra("startTimeMs", 0);
        finishTimeMs = intent.getIntExtra("finishTimeMs", 0);

        ModifyVideoDurationUseCase modifyVideoDurationUseCase = new ModifyVideoDurationUseCase();
        modifyVideoDurationUseCase.trimVideo(video, startTimeMs, finishTimeMs);
    }


    @Override
    public void onTranscodeProgress(double v) {

    }

    @Override
    public void onTranscodeCompleted() {
// TODO(javi.cabanas): 2/8/16 make video model aware of temporal video availability
        Intent intent = new Intent(ACTION);
        intent.putExtra("videoTrimmed", true);
        sendBroadcast(intent);
    }

    @Override
    public void onTranscodeCanceled() {
// TODO(javi.cabanas): 2/8/16 make video model aware of temporal video is not available
        Intent intent = new Intent(ACTION);
        intent.putExtra("videoTrimmed", false);
        sendBroadcast(intent);
    }

    @Override
    public void onTranscodeFailed(Exception e) {
// TODO(javi.cabanas): 2/8/16 make video model aware of temporal video is not available
        Intent intent = new Intent(ACTION);
        intent.putExtra("videoTrimmed", false);
        sendBroadcast(intent);
    }

}
