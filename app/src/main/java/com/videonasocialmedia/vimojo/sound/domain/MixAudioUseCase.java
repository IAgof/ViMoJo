package com.videonasocialmedia.vimojo.sound.domain;

import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.audio_mixer.AudioMixer;
import com.videonasocialmedia.transcoder.audio_mixer.listener.OnAudioMixerListener;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.domain.editor.AddMusicToProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Music;
import com.videonasocialmedia.vimojo.utils.Constants;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Future;

/**
 * Created by alvaro on 22/09/16.
 */

public class MixAudioUseCase implements OnAudioMixerListener {

    private final AddMusicToProjectUseCase addMusicToProjectUseCase;
    String outputFile = Constants.PATH_APP_TEMP + File.separator + "AudioMixed.m4a";
    private OnMixAudioListener listener;

    public MixAudioUseCase(OnMixAudioListener listener){

        addMusicToProjectUseCase = new AddMusicToProjectUseCase();
        this.listener = listener;
    }

    public void mixAudio(String inputFileOne, String inputFileTwo, float volume){

        try {
            Future<Void> mFuture = MediaTranscoder.getInstance().mixTwoAudioFiles(inputFileOne, inputFileTwo,
                    volume, Constants.PATH_APP_TEMP_AUDIO, outputFile, (OnAudioMixerListener) this);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onAudioMixerSuccess(String outputFile) {

        Music musicMixed = new Music(R.drawable.gatito_rules_pressed, "Voice over recorded", R.raw.audio_hiphop,
                outputFile, R.color.folk, "Author", "04:35");
        addMusicToProjectUseCase.addMusicToTrack(musicMixed, 0);

        listener.onMixAudioSuccess();
    }

    @Override
    public void onAudioMixerProgress(String progress) {

    }

    @Override
    public void onAudioMixerError(String error) {
        listener.onMixAudioSuccess();
    }

    @Override
    public void onAudioMixerCanceled() {

    }
}
