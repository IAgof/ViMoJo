package com.videonasocialmedia.vimojo.sound.domain;

import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.audio_mixer.listener.OnAudioEffectListener;
import com.videonasocialmedia.vimojo.export.domain.ExportSwapAudioToVideoUseCase;
import com.videonasocialmedia.vimojo.export.domain.OnExportEndedListener;
import com.videonasocialmedia.vimojo.export.utils.TranscoderHelper;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Video;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.io.File;
import java.io.IOException;

/**
 * Created by alvaro on 23/10/16.
 */

public class SetAudioFadeInFadeOutToFileUseCase implements OnAudioEffectListener, OnExportEndedListener {

    private MediaTranscoder mediaTranscoder = MediaTranscoder.getInstance();
    protected TranscoderHelper transcoderHelper = new TranscoderHelper(mediaTranscoder);

    private ExportSwapAudioToVideoUseCase exportSwapAudioToVideoUseCase;

    private Video videoToFadeInFadeOut;

    String tempFileAudio = Constants.FOLDER_VIDEONA_TEMP_AUDIO + File.separator + "AudioFadeInOut" + ".m4a";

    public SetAudioFadeInFadeOutToFileUseCase(){

    }

    public void setAudioFadeInFadeOutToFile(Video videoToEdit, int timeFadeInMs, int timeFadeOutMs)
            throws IOException {

        videoToFadeInFadeOut = videoToEdit;
        videoToFadeInFadeOut.setTempPath();
        transcoderHelper.generateFileWithAudioFadeInFadeOut(videoToEdit.getMediaPath(), timeFadeInMs,
                timeFadeOutMs, Constants.FOLDER_VIDEONA_TEMP_AUDIO, tempFileAudio, this);
    }

    @Override
    public void onAudioEffectSuccess(String outputFile) {
        exportSwapAudioToVideoUseCase = new ExportSwapAudioToVideoUseCase(videoToFadeInFadeOut.getMediaPath(),
                outputFile, videoToFadeInFadeOut.getTempPath(), this);
        exportSwapAudioToVideoUseCase.export();
    }

    @Override
    public void onAudioEffectProgress(String progress) {

    }

    @Override
    public void onAudioEffectError(String error) {

    }

    @Override
    public void onAudioEffectCanceled() {

    }

    @Override
    public void onExportError(String error) {

    }

    @Override
    public void onExportSuccess(Video video) {

    }
}
