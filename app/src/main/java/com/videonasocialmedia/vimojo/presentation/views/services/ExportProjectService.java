package com.videonasocialmedia.vimojo.presentation.views.services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.videonasocialmedia.vimojo.export.domain.ExportProjectUseCase;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnExportFinishedListener;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.Utils;

import java.io.File;

/**
 * Created by  on 26/05/16.
 */
public class ExportProjectService extends IntentService implements OnExportFinishedListener {
    public static final String NOTIFICATION = Constants.NOTIFICATION_EXPORT_SERVICES_RECEIVER;
    public static final String FILEPATH = "filepath";
    public static final String RESULT = "result";
    private static final String TAG = "ExportProjectService";
    private ExportProjectUseCase exportUseCase;

    //TODO Add persistence. Needed to navigate for ShareActivity if service has finished.

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public ExportProjectService() {
        super("ExportProjectService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        exportUseCase = new ExportProjectUseCase();
        exportUseCase.export(Constants.PATH_WATERMARK, this);
    }

    @Override
    public void onExportError(String error) {
        Log.d(TAG, "exportError " + error);
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
        Log.d(TAG, "exportSuccess ");
        File f = new File(video.getMediaPath());
        String destPath = Constants.PATH_APP + File.separator + f.getName();
        File destFile = new File(destPath);
        f.renameTo(destFile);
        Utils.addFileToVideoGallery(destPath.toString());
        publishResults(destPath, Activity.RESULT_OK);
    }

    @Override
    public void onExportProgress(String progressMsg, int exportStage) {
        // TODO(jliarte): 28/04/17  do nothing as we're removing this class?
    }
}
