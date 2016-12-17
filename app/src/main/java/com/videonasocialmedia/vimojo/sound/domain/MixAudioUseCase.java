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

/**
 * Created by alvaro on 22/09/16.
 */

/**
 * This use case mixes two audio files with relative volumes into a new one. It's used to mix
 * original audio tracks from video clips in project with a voice over track recorded by the user.
 *
 * Currently is called once, after the whole project has been exported, currently in ShareActivity,
 */
public class MixAudioUseCase implements OnAudioMixerListener, OnAddMediaFinishedListener {
    private AddMusicToProjectUseCase addMusicToProjectUseCase;
    private OnMixAudioListener listener;
    private float volume = Music.DEFAULT_MUSIC_VOLUME;
    // TODO(jliarte): 17/12/16 are those two the same path?
    private String outputFile = Constants.OUTPUT_FILE_MIXED_AUDIO;
    private String outputFileMixedPath;

    public MixAudioUseCase(AddMusicToProjectUseCase addMusicToProjectUseCase) {
        this.addMusicToProjectUseCase = addMusicToProjectUseCase;
        File f = new File(outputFile);
        if(f.exists())
            f.delete();
    }

    public void mixAudio(String inputFileOne, String inputFileTwo, float volume,
                         OnMixAudioListener listener) {
        this.volume = volume;
        this.listener = listener;
        try {
            Future<Void> mFuture = MediaTranscoder.getInstance().mixAudioTwoFiles(inputFileOne,
                    inputFileTwo, volume, Constants.PATH_APP_TEMP_AUDIO, outputFile, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAudioMixerSuccess(String outputFileMixed) {
        this.outputFileMixedPath = outputFileMixed;
        // TODO(jliarte): 17/12/16 old implementation set mixed audio as music in VMComposition,
        //                this way voice over persist in database as a music (current workarround)
        //                and was later used on final video export
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
        // TODO(jliarte): 17/12/16 new implementation call onMixAudioSuccess to finally update
        //                video in ShareActivity
        FileUtils.cleanDirectory(new File(Constants.PATH_APP_TEMP_AUDIO));
        listener.onMixAudioSuccess(outputFileMixedPath);
    }
}
