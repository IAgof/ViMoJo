package com.videonasocialmedia.vimojo.export.domain;

import com.videonasocialmedia.camera.utils.VideoFormat;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;

/**
 * Created by alvaro on 2/09/16.
 */
public class GetVideoFormatFromCurrentProjectUseCase {

    public Project project;

    public GetVideoFormatFromCurrentProjectUseCase() {
        this.project = Project.getInstance(null, null, null);
    }

    public VideonaFormat getVideoTranscodedFormatFromCurrentProject(){
        VideonaFormat videonaFormat;

        VideoResolution resolution = project.getProfile().getVideoResolution();
        VideoQuality quality = project.getProfile().getVideoQuality();

        if(resolution!=null && quality!=null) {
            videonaFormat = new VideonaFormat(quality.getVideoBitRate(), resolution.getWidth(),
                    resolution.getHeight());
        } else {
            videonaFormat = new VideonaFormat();
        }

        return videonaFormat;
    }

    public VideoFormat getVideoRecordedFormatFromCurrentProjectUseCase() {

        VideoFormat videoFormat;

        VideoResolution resolution = project.getProfile().getVideoResolution();
        VideoQuality quality = project.getProfile().getVideoQuality();

        videoFormat = new VideoFormat(resolution.getWidth(), resolution.getHeight(),
            quality.getVideoBitRate());

        return videoFormat;
    }
}
