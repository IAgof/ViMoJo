package com.videonasocialmedia.vimojo.sound.domain;

import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.audio.listener.OnAudioMixerListener;
import com.videonasocialmedia.videonamediaframework.model.media.Media;
import com.videonasocialmedia.videonamediaframework.model.media.Music;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnAddMediaFinishedListener;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Future;

import javax.inject.Inject;

/**
 * Created by alvaro on 22/09/16.
 */

public class MixAudioUseCase implements OnAudioMixerListener, OnAddMediaFinishedListener {

    @Inject AddMusicToProjectUseCase addMusicToProjectUseCase;
    String outputFile = Constants.OUTPUT_FILE_MIXED_AUDIO;
    private OnMixAudioListener listener;
    private float volume = 0.5f;

    public MixAudioUseCase(OnMixAudioListener listener) {
        this.listener = listener;

        File f = new File(outputFile);
        if(f.exists())
            f.delete();
    }

    public void mixAudio(String inputFileOne, String inputFileTwo, float volume) {
        this.volume = volume;
        try {
            Future<Void> mFuture = MediaTranscoder.getInstance().mixAudioTwoFiles(inputFileOne,
                    inputFileTwo, 1-volume, Constants.PATH_APP_TEMP_AUDIO, outputFile, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAudioMixerSuccess(String outputFileMixed) {
        Music music = new Music(outputFileMixed, volume);
        music.setMusicTitle(Constants.MUSIC_AUDIO_MIXED_TITLE);
        addMusicToProjectUseCase.addMusicToTrack(music, 0, this);
    }

    @Override
    public void onAudioMixerProgress(String progress) {
    }

    @Override
    public void onAudioMixerError(String error) {
        listener.onMixAudioError();
    }

    @Override
    public void onAudioMixerCanceled() {
    }

    @Override
    public void onAddMediaItemToTrackError() {
    }

    @Override
    public void onAddMediaItemToTrackSuccess(Media media) {
        FileUtils.cleanDirectory(new File(Constants.PATH_APP_TEMP_AUDIO));
        listener.onMixAudioSuccess();
    }
}
