package com.videonasocialmedia.vimojo.split.domain;

import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.composition.domain.model.Project;

/**
 * Created by alvaro on 5/09/16.
 */
public interface OnSplitVideoListener {
    void onSuccessSplittingVideo(Project currentProject, Video initialVideo, Video endVideo);
    void showErrorSplittingVideo();
}
