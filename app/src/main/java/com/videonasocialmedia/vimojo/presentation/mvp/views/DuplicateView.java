package com.videonasocialmedia.vimojo.presentation.mvp.views;

import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;

/**
 * Created by jca on 8/7/15.
 */
public interface DuplicateView {

    void initDuplicateView(Video video);

    void updateProject();

    void showError(String errorMessage);

    void navigateTo(Class<EditActivity> editActivityClass, int videoIndexOnTrack);
}
