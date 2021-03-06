package com.videonasocialmedia.vimojo.domain;

import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnVideosRetrieved;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by jca on 20/5/15.
 */
public class ObtainLocalVideosUseCase {

    private final String LOG_TAG = "ObtainLocalVideos";

    public void obtainEditedVideos(final OnVideosRetrieved listener) {
        obtainVideosFromPathAsync(Constants.PATH_APP_EDITED, new VideoRetrieverListener() {
                    @Override
                    public void onVideosRetrieved(ArrayList<Video> videoList) {
                        if (videoList == null) {
                            listener.onNoVideosRetrieved();
                        } else {
                            listener.onVideosRetrieved(videoList);
                        }
                    }
                });
    }

    public void obtainRawVideos(final OnVideosRetrieved listener) {
        obtainVideosFromPathAsync(Constants.PATH_APP_MASTERS, new VideoRetrieverListener() {
            @Override
            public void onVideosRetrieved(ArrayList<Video> videoList) {
                if (videoList == null) {
                    listener.onNoVideosRetrieved();
                } else {
                    listener.onVideosRetrieved(videoList);
                }
            }
        });
    }

    private void obtainVideosFromPathAsync(final String path, final VideoRetrieverListener listener) {
        new Thread() {
            @Override
            public void run() {
                ArrayList<Video> videos = null;
                File directory = new File(path);
                File[] files = directory.listFiles();

                if (files != null && files.length > 0) {
                    videos = new ArrayList<>();

                    Collections.sort(Arrays.asList(files), new Comparator<File>() {
                        public int compare(File f1, File f2) {
                            long d1 = f1.lastModified();
                            long d2 = f2.lastModified();
                            return d1 > d2 ? 1 : d1 < d2 ? -1 : 0;
                        }
                    });
                    for (int i = files.length - 1; i >= 0; i--) {
                        if (files[i].getName().endsWith(".mp4") && files[i].isFile())
                            videos.add(new Video(path + File.separator + files[i].getName(),
                                Video.DEFAULT_VOLUME));
                    }

                }

                listener.onVideosRetrieved(videos);
            }
        }.start();
    }

    public interface VideoRetrieverListener {
        void onVideosRetrieved(ArrayList<Video> videoList);
    }
}
