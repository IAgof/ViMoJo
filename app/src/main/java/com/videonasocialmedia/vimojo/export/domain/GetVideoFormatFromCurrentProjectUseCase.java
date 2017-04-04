package com.videonasocialmedia.vimojo.export.domain;

import com.videonasocialmedia.camera.utils.VideoCameraFormat;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;

/**
 * Created by alvaro on 2/09/16.
 */
public class GetVideoFormatFromCurrentProjectUseCase {

    public Project project;

    public GetVideoFormatFromCurrentProjectUseCase() {
        project = Project.getInstance(null, null, null);
    }

    public VideoCameraFormat getVideoRecordedFormatFromCurrentProjectUseCase() {
        VideoCameraFormat videoCameraFormat;
        VideoResolution resolution = project.getProfile().getVideoResolution();
        VideoQuality quality = project.getProfile().getVideoQuality();
        videoCameraFormat = new VideoCameraFormat(resolution.getWidth(), resolution.getHeight(),
            quality.getVideoBitRate());
        return videoCameraFormat;
    }
}
