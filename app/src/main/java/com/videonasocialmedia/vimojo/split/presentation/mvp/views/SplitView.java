package com.videonasocialmedia.vimojo.split.presentation.mvp.views;

import android.content.Context;

import com.videonasocialmedia.videonamediaframework.model.VMComposition;
import com.videonasocialmedia.vimojo.presentation.views.activity.EditActivity;

/**
 * Created by jca on 8/7/15.
 */
public interface SplitView {
    void initSplitView(int maxSeekBar);
    void showError(int stringResourceId);
    void updateSplitSeekbar(int progress);
    void refreshTimeTag(int currentPosition);
    void updateProject();
    void navigateTo(Class<EditActivity> editActivityClass, int videoIndexOnTrack);

    // Player views
    void attachView(Context context);
    void detachView();
    void setAspectRatioVerticalVideos(int height);
    void initSingleClip(VMComposition vmComposition, int clipPosition);
    void seekTo(int timeInMsec);
}
