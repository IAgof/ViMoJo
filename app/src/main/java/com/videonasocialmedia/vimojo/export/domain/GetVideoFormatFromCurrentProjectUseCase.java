package com.videonasocialmedia.vimojo.export.domain;

import com.videonasocialmedia.camera.utils.VideoCameraFormat;
import com.videonasocialmedia.transcoder.video.format.VideoTranscoderFormat;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;

/**
 * Created by alvaro on 2/09/16.
 */
public class GetVideoFormatFromCurrentProjectUseCase {

    public Project project;

    public GetVideoFormatFromCurrentProjectUseCase() {
    }

    public VideoTranscoderFormat getVideoTranscodedFormatFromCurrentProject(){

        project = Project.getInstance(null, null, null);

        VideoTranscoderFormat videoTranscoderFormat;

        VideoResolution resolution = project.getProfile().getVideoResolution();
        VideoQuality quality = project.getProfile().getVideoQuality();

        if(resolution!=null && quality!=null) {
            videoTranscoderFormat = new VideoTranscoderFormat(quality.getVideoBitRate(), resolution.getWidth(),
                    resolution.getHeight());
        } else {
            videoTranscoderFormat = new VideoTranscoderFormat();
        }

        return videoTranscoderFormat;
    }

    public VideoCameraFormat getVideoRecordedFormatFromCurrentProjectUseCase() {

        project = Project.getInstance(null, null, null);

        VideoCameraFormat videoCameraFormat;

        VideoResolution resolution = project.getProfile().getVideoResolution();
        VideoQuality quality = project.getProfile().getVideoQuality();

        videoCameraFormat = new VideoCameraFormat(resolution.getWidth(), resolution.getHeight(),
            quality.getVideoBitRate());

        return videoCameraFormat;
    }
}
