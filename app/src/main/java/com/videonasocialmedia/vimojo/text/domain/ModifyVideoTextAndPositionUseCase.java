package com.videonasocialmedia.vimojo.text.domain;

import android.graphics.drawable.Drawable;

import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.MediaTranscoderListener;
import com.videonasocialmedia.transcoder.video.format.VideonaFormat;
import com.videonasocialmedia.videonamediaframework.pipeline.TranscoderHelper;
import com.videonasocialmedia.videonamediaframework.model.media.Video;
import com.videonasocialmedia.vimojo.main.VimojoApplication;
import com.videonasocialmedia.vimojo.model.entities.editor.Project;
import com.videonasocialmedia.vimojo.repository.video.VideoRepository;
import com.videonasocialmedia.videonamediaframework.utils.TextToDrawable;
import com.videonasocialmedia.vimojo.settings.domain.GetPreferencesTransitionFromProjectUseCase;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.io.IOException;

import javax.inject.Inject;

/**
 * Created by alvaro on 1/09/16.
 */
public class ModifyVideoTextAndPositionUseCase {

    // TODO:(alvaro.martinez) 23/11/16 Use Dagger for this injection
    protected TextToDrawable drawableGenerator = new TextToDrawable(VimojoApplication.getAppContext());
    private MediaTranscoder mediaTranscoder = MediaTranscoder.getInstance();
    protected TranscoderHelper transcoderHelper = new TranscoderHelper(drawableGenerator, mediaTranscoder);
    protected VideoRepository videoRepository;
    private GetPreferencesTransitionFromProjectUseCase getPreferencesTransitionFromProjectUseCase;


  /**
   * Default constructor with video repository.
   *
   * @param videoRepository the video repository.
   */
  @Inject public ModifyVideoTextAndPositionUseCase(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
        getPreferencesTransitionFromProjectUseCase = new GetPreferencesTransitionFromProjectUseCase();
    }

    public void addTextToVideo(Drawable drawableFadeTransition, Video videoToEdit, VideonaFormat format, String text, String textPosition,
                               MediaTranscoderListener listener) {
        try {

            boolean isVideoFadeTransitionActivated = getPreferencesTransitionFromProjectUseCase.isVideoFadeTransitionActivated();

            videoToEdit.setClipText(text);
            videoToEdit.setClipTextPosition(textPosition);
            videoToEdit.setTempPathFinished(false);
            // TODO:(alvaro.martinez) 22/11/16 use project tmp path
            videoToEdit.setTempPath(Constants.PATH_APP_TEMP_INTERMEDIATE_FILES);
            videoToEdit.setTextToVideoAdded(true);

            // TODO(jliarte): 19/10/16 move this logic to TranscoderHelper?
            if(videoToEdit.isTrimmedVideo()) {
                transcoderHelper.generateOutputVideoWithOverlayImageAndTrimming(
                    drawableFadeTransition, isVideoFadeTransitionActivated,
                    videoToEdit, format, listener);
            } else {
                transcoderHelper.generateOutputVideoWithOverlayImage(drawableFadeTransition,
                    isVideoFadeTransitionActivated, videoToEdit, format, listener);
            }
            videoRepository.update(videoToEdit);
        } catch (IOException e) {
            // TODO(javi.cabanas): 2/8/16 mangage io expception on external library and send onTranscodeFailed if neccessary
            listener.onTranscodeFailed(e);
        }
    }
}

