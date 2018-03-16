package com.videonasocialmedia.vimojo.export.domain;

import com.videonasocialmedia.camera.utils.VideoCameraFormat;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoFrameRate;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoQuality;
import com.videonasocialmedia.videonamediaframework.model.media.utils.VideoResolution;
import com.videonasocialmedia.vimojo.repository.project.ProjectRepository;
import com.videonasocialmedia.vimojo.utils.Constants;

/**
 * Created by alvaro on 2/09/16.
 */
public class GetVideoFormatFromCurrentProjectUseCase {

    private final ProjectRepository projectRepository;

    public GetVideoFormatFromCurrentProjectUseCase(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public VideoCameraFormat getVideoRecordedFormatFromCurrentProjectUseCase() {
        Project project = projectRepository.getCurrentProject();
        VideoCameraFormat videoCameraFormat;
        VideoResolution resolution = project.getProfile().getVideoResolution();
        VideoQuality quality = project.getProfile().getVideoQuality();
        VideoFrameRate frameRate = project.getProfile().getVideoFrameRate();
        videoCameraFormat = new VideoCameraFormat(resolution.getWidth(), resolution.getHeight(),
            quality.getVideoBitRate(), frameRate.getFrameRate());
        return videoCameraFormat;
    }

    public VideonaFormat getVideonaFormatFromCurrentProject(Project project) {
        VideonaFormat videonaFormat;
        VideoResolution resolution = project.getProfile().getVideoResolution();
        VideoQuality quality = project.getProfile().getVideoQuality();

        if (resolution != null && quality != null) {
            videonaFormat = new VideonaFormat(quality.getVideoBitRate(), resolution.getWidth(),
                resolution.getHeight());
        } else {
            videonaFormat = new VideonaFormat();
        }
        return videonaFormat;
    }

    public VideonaFormat getVideonaFormatToAdaptAudio() {
        return new VideonaFormat(Constants.DEFAULT_VIMOJO_AUDIO_BITRATE,
            Constants.DEFAULT_VIMOJO_AUDIO_CHANNELS);
    }

    public VideonaFormat getVideonaFormatToAdaptVideoRecordedAudioAndVideo(Project project) {
        VideonaFormat videonaFormat;
        VideoResolution resolution = project.getProfile().getVideoResolution();
        VideoQuality quality = project.getProfile().getVideoQuality();
        if (resolution != null && quality != null) {
            videonaFormat = new VideonaFormat(quality.getVideoBitRate(), resolution.getWidth(),
                resolution.getHeight(), Constants.DEFAULT_VIMOJO_AUDIO_BITRATE,
                Constants.DEFAULT_VIMOJO_AUDIO_CHANNELS);
        } else {
            videonaFormat = new VideonaFormat(Constants.DEFAULT_VIMOJO_AUDIO_BITRATE,
                Constants.DEFAULT_VIMOJO_AUDIO_CHANNELS);
        }
        return videonaFormat;
    }
}
