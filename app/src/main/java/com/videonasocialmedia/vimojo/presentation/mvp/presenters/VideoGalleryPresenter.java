package com.videonasocialmedia.vimojo.presentation.mvp.presenters;

import android.app.Activity;

import com.videonasocialmedia.vimojo.domain.ObtainLocalVideosUseCase;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.mvp.views.VideoGalleryView;
import com.videonasocialmedia.vimojo.presentation.views.fragment.VideoGalleryFragment;

import java.util.List;

/**
 * Created by jca on 14/5/15.
 */
public class VideoGalleryPresenter implements OnVideosRetrieved {

    public static final int MASTERS_FOLDER = 0;
    public static final int EDITED_FOLDER = 1;

    private VideoGalleryView galleryView;
    private ObtainLocalVideosUseCase obtainLocalVideosUseCase;
    private Activity activity;

    public VideoGalleryPresenter(VideoGalleryView galleryView) {
        this.galleryView = galleryView;
        obtainLocalVideosUseCase = new ObtainLocalVideosUseCase();
    }

    @Override
    public void onVideosRetrieved(final List<Video> videoList) {
        activity = ((VideoGalleryFragment) galleryView).getActivity();
        if (activity != null) {
            ((VideoGalleryFragment)galleryView).getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    galleryView.hideLoading();
                    galleryView.showVideos(videoList);
                }
            });
        }
    }

    @Override
    public void onNoVideosRetrieved() {
        ((VideoGalleryFragment)galleryView).getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                galleryView.hideLoading();
                //TODO show error in view
            }
        });
    }

    public void start() {
    }

    public void stop() {
    }

    public void obtainVideos(int folder) {
        galleryView.showLoading();
        switch (folder) {
            case MASTERS_FOLDER:
                obtainLocalVideosUseCase.obtainRawVideos(this);
                break;
            case EDITED_FOLDER:
            default:
                obtainLocalVideosUseCase.obtainEditedVideos(this);
                break;
        }
    }


}
