package com.videonasocialmedia.vimojo.ftp.domain;

import android.os.Handler;
import android.os.Message;

import com.videonasocialmedia.vimojo.BuildConfig;
import com.videonasocialmedia.vimojo.ftp.FtpClient;

import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *
 */
public class FtpController implements CopyStreamListener {

    private static final int MSG_PRGOGRESS_UPDATED = 53;
    private static final int MSG_PROGRESS_FINISHED = 621;
    private static final int MSG_PROGRESS_ERROR = 540;

    volatile FTPHandler handler;

    long videoLength;

    private long previousTime;


    public void uploadVideo(final String user, final String password, final String srcPath, final ProgressListener progressListener) {
        handler = new FTPHandler(progressListener);
        Runnable runnable = new Runnable() {
            public void run() {
                String host = BuildConfig.FTP_HOST;// TODO(javi.cabanas): 29/6/16 fetch host from settings
//                host = "192.168.1.10";
                String title = getVideoTitle(srcPath);
                File videoFile = new File(srcPath);
                videoLength = videoFile.length();
//                String dst = /*"/user/" +*/ title;
                String dst = "/assets/" + title;
                final FtpClient uploader = new FtpClient();

                try {
                    previousTime = System.currentTimeMillis();
                    uploader.upload(host, user, password, srcPath, dst, FtpController.this);
                    handler.sendMessage(handler.obtainMessage(MSG_PROGRESS_FINISHED));
                } catch (IOException e) {
                    handler.sendMessage(handler.obtainMessage(MSG_PROGRESS_ERROR));
                }
                handler = null;
            }
        };

        Thread t = new Thread(runnable);
        t.start();
    }

    private String getVideoTitle(String videoPath) {
        Pattern pattern = Pattern.compile("\\w+(?:\\.\\w+)*$");
        Matcher matcher = pattern.matcher(videoPath);

        String videoTitle = "";
        if (matcher.find()) {
            videoTitle = matcher.group();
        }
        return videoTitle;
    }

    @Override
    public void bytesTransferred(CopyStreamEvent copyStreamEvent) {
        bytesTransferred(copyStreamEvent.getTotalBytesTransferred(),
                copyStreamEvent.getBytesTransferred(), copyStreamEvent.getStreamSize());
    }

    @Override
    public void bytesTransferred(long totalBytesTransferred,
                                 int bytesTransferred,
                                 long streamSize) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - previousTime > 700) {
            previousTime = currentTime;
            Integer progress = Math.round(totalBytesTransferred * 100 / videoLength);
            handler.sendMessage(handler.obtainMessage(MSG_PRGOGRESS_UPDATED, progress));
        }
    }

    private static class FTPHandler extends Handler {

        private WeakReference<ProgressListener> weakListener;

        public FTPHandler(ProgressListener listener) {
            weakListener = new WeakReference<>(listener);
        }

        /**
         * Called on Encoder thread
         *
         * @param inputMessage
         */
        @Override
        public void handleMessage(Message inputMessage) {
            int what = inputMessage.what;
            Object obj = inputMessage.obj;
            switch (what) {
                case MSG_PRGOGRESS_UPDATED:
                    weakListener.get().onProgressUpdated((Integer) obj);
                    break;
                case MSG_PROGRESS_ERROR:
                    weakListener.get().onErrorFinished();
                    break;
                case MSG_PROGRESS_FINISHED:
                    weakListener.get().onSuccessFinished();
                    break;
                default:
                    throw new RuntimeException("Unexpected msg what=" + what);
            }

        }
    }
}
