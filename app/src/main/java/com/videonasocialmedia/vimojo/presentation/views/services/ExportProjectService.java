package com.videonasocialmedia.vimojo.presentation.views.services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;

import com.videonasocialmedia.vimojo.domain.editor.export.ExportProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnExportFinishedListener;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.Utils;

/**
 * Created by  on 26/05/16.
 */
public class ExportProjectService extends IntentService implements OnExportFinishedListener {

    public static final String NOTIFICATION = Constants.NOTIFICATION_EXPORT_SERVICES_RECEIVER;
    public static final String FILEPATH = "filepath";
    public static final String RESULT = "result";
    private static final String TAG = "ExportProjectService";
    ExportProjectUseCase exportUseCase;

    //TODO Add persistence. Needed to navigate for ShareActivity if service has finished.

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public ExportProjectService() {
        super("ExportProjectService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        exportUseCase = new ExportProjectUseCase(this);
        exportUseCase.export();
    }

    @Override
    public void onExportError(String error) {
        publishResults(error, Activity.RESULT_CANCELED);
    }

    private void publishResults(String outputPath, int result) {
        Intent intent = new Intent(NOTIFICATION);
        if (result == Activity.RESULT_OK) {
            intent.putExtra(FILEPATH, outputPath);
        }
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }

    @Override
    public void onExportSuccess(Video video) {
        Utils.addFileToVideoGallery(video.getMediaPath().toString());
        publishResults(video.getMediaPath(), Activity.RESULT_OK);
    }

}
