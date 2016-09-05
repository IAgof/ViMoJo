package com.videonasocialmedia.vimojo.text.domain;

import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.MediaTranscoderListener;
import com.videonasocialmedia.transcoder.format.VideonaFormat;
import com.videonasocialmedia.transcoder.overlay.Overlay;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;

import java.io.IOException;

/**
 * Created by alvaro on 1/09/16.
 */
public class AddTextToVideoUseCase {

    public void addTextToVideo (Video videoToEdit, VideonaFormat format, Overlay overlay, MediaTranscoderListener listener) {
        try {
            videoToEdit.setTempPathFinished(false);
            MediaTranscoder.getInstance().transcodeAndOverlayImageToVideo(videoToEdit.getMediaPath(), videoToEdit.getTempPath(),
                    format, listener, overlay);
        } catch (IOException e) {
            // TODO(javi.cabanas): 2/8/16 mangage io expception on external library and send onTranscodeFailed if neccessary
        }
    }
}
