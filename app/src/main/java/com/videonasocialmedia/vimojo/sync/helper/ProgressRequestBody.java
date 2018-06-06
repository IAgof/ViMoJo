/*
 * Copyright (C) 2018 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

/**
 * Created by alvaro on 6/6/18.
 */
package com.videonasocialmedia.vimojo.sync.helper;

import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * ProgressRequestBody
 * Helper class to get progress while file is uploading/downloading
 *
 * https://stackoverflow.com/questions/33338181/is-it-possible-to-show-progress-bar-when-upload-image-via-retrofit-2/33384551#33384551
 */

public class ProgressRequestBody extends RequestBody {
  private File file;
  private String path;
  private UploadCallbacks uploadCallbacksListener;
  private String mimeType;

  private static final int DEFAULT_BUFFER_SIZE = 2048;

  public interface UploadCallbacks {
    void onProgressUpdate(int percentage);
    void onError();
    void onFinish();
  }

  public ProgressRequestBody(final File file, String mimeType,
                             final  UploadCallbacks listener) {
    this.file = file;
    uploadCallbacksListener = listener;
    this.mimeType = mimeType;
  }

  @Override
  public MediaType contentType() {
    return okhttp3.MediaType.parse(mimeType);
  }

  @Override
  public long contentLength() throws IOException {
    return file.length();
  }

  @Override
  public void writeTo(BufferedSink sink) throws IOException {
    long fileLength = file.length();
    byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
    FileInputStream in = new FileInputStream(file);
    long uploaded = 0;

    try {
      int read;
      Handler handler = new Handler(Looper.getMainLooper());
      while ((read = in.read(buffer)) != -1) {

        // update progress on UI thread
        handler.post(new ProgressUpdater(uploaded, fileLength));

        uploaded += read;
        sink.write(buffer, 0, read);
      }
    } finally {
      in.close();
    }
  }

  private class ProgressUpdater implements Runnable {
    private long uploaded;
    private long total;
    public ProgressUpdater(long uploaded, long total) {
      this.uploaded = uploaded;
      this.total = total;
    }

    @Override
    public void run() {
      uploadCallbacksListener.onProgressUpdate((int)(100 * uploaded / total));
    }
  }
}
