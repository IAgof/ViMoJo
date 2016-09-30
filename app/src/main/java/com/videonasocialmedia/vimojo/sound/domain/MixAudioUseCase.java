package com.videonasocialmedia.vimojo.sound.domain;

import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.audio_mixer.AudioMixer;
import com.videonasocialmedia.transcoder.audio_mixer.listener.OnAudioMixerListener;
import com.videonasocialmedia.vimojo.R;
import com.videonasocialmedia.vimojo.domain.editor.AddMusicToProjectUseCase;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Media;
import com.videonasocialmedia.vimojo.model.entities.editor.media.Music;
import com.videonasocialmedia.vimojo.presentation.mvp.presenters.OnAddMediaFinishedListener;
import com.videonasocialmedia.vimojo.utils.Constants;
import com.videonasocialmedia.vimojo.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Future;

/**
 * Created by alvaro on 22/09/16.
 */

public class MixAudioUseCase implements OnAudioMixerListener, OnAddMediaFinishedListener {

    private final AddMusicToProjectUseCase addMusicToProjectUseCase;
    String outputFile = Constants.PATH_APP_TEMP + File.separator + "AudioMixed" + ".m4a";
    private OnMixAudioListener listener;
    private float volume = 0.5f;

    public MixAudioUseCase(OnMixAudioListener listener){

        addMusicToProjectUseCase = new AddMusicToProjectUseCase();
        this.listener = listener;

        File f = new File(outputFile);
        if(f.exists())
            f.delete();
    }

    public void mixAudio(String inputFileOne, String inputFileTwo, float volume){

        this.volume = volume;

        try {
            Future<Void> mFuture = MediaTranscoder.getInstance().mixTwoAudioFiles(inputFileOne, inputFileTwo,
                    volume, Constants.PATH_APP_TEMP_AUDIO, outputFile, (OnAudioMixerListener) this);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onAudioMixerSuccess(String outputFileMixed) {

        addMusicToProjectUseCase.addMusicToTrack(new Music(outputFileMixed, volume), 0, this);
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

    @Override
    public void onAddMediaItemToTrackError() {

    }

    @Override
    public void onAddMediaItemToTrackSuccess(Media media) {
        Utils.cleanDirectory(new File(Constants.PATH_APP_TEMP_AUDIO));
        listener.onMixAudioSuccess();

    }
}
