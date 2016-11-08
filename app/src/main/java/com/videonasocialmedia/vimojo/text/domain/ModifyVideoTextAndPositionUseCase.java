package com.videonasocialmedia.vimojo.text.domain;

import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.MediaTranscoderListener;
import com.videonasocialmedia.transcoder.format.VideonaFormat;
import com.videonasocialmedia.vimojo.export.utils.TranscoderHelper;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.repository.video.VideoRealmRepository;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.vimojo.text.util.TextToDrawable;

import java.io.IOException;

/**
 * Created by alvaro on 1/09/16.
 */
public class ModifyVideoTextAndPositionUseCase {

    private TextToDrawable drawableGenerator = new TextToDrawable();
    private MediaTranscoder mediaTranscoder = MediaTranscoder.getInstance();
    protected TranscoderHelper transcoderHelper = new TranscoderHelper(drawableGenerator, mediaTranscoder);
    protected VideoRepository videoRepository = new VideoRealmRepository();

    public void addTextToVideo(Video videoToEdit, VideonaFormat format, String text, String textPosition,
                               MediaTranscoderListener listener) {
        try {
            videoToEdit.setClipText(text);
            videoToEdit.setClipTextPosition(textPosition);
            videoToEdit.setTempPathFinished(false);
            videoToEdit.setTempPath();
            videoToEdit.setTextToVideoAdded(true);

            // TODO(jliarte): 19/10/16 move this logic to TranscoderHelper?
            if(videoToEdit.isTrimmedVideo()) {
                transcoderHelper.generateOutputVideoWithOverlayImageAndTrimming(videoToEdit, format, listener);
            } else {
                transcoderHelper.generateOutputVideoWithOverlayImage(videoToEdit, format, listener);
            }
            videoRepository.update(videoToEdit);
        } catch (IOException e) {
            // TODO(javi.cabanas): 2/8/16 mangage io expception on external library and send onTranscodeFailed if neccessary
            listener.onTranscodeFailed(e);
        }
    }
}

