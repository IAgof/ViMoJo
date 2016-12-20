package com.videonasocialmedia.vimojo.sound.domain;

import com.videonasocialmedia.transcoder.MediaTranscoder;
import com.videonasocialmedia.transcoder.audio.listener.OnAudioMixerListener;
import com.videonasocialmedia.videonamediaframework.utils.FileUtils;

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
public class MixAudioUseCase implements OnAudioMixerListener {
    private OnMixAudioListener listener;
    private String tempAudioPath;
    private String outputFilePath;

    public MixAudioUseCase(String outputFilePath) {
        this.outputFilePath = outputFilePath;
        cleanOutputFile();
    }

    private void cleanOutputFile() {
        File f = new File(outputFilePath);
        if (f.exists()) {
            f.delete();
        }
    }

    public void mixAudio(String inputFileOne, String inputFileTwo, float volume,
                         String tempAudioPath, OnMixAudioListener listener) {
        this.listener = listener;
        this.tempAudioPath = tempAudioPath;
        try {
            Future<Void> mFuture = MediaTranscoder.getInstance().mixAudioTwoFiles(inputFileOne,
                    inputFileTwo, volume, tempAudioPath, outputFilePath, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAudioMixerSuccess(String outputFileMixed) {
        this.outputFilePath = outputFileMixed;
        // TODO(jliarte): 17/12/16 new implementation call onMixAudioSuccess to finally update
        //                video in ShareActivity
        FileUtils.cleanDirectory(new File(tempAudioPath));
        listener.onMixAudioSuccess(outputFilePath);
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
        // TODO(jliarte): 20/12/16 pass some error code?
        listener.onMixAudioError();
    }

    /**
     * Created by alvaro on 22/09/16.
     */

    public interface OnMixAudioListener {
        void onMixAudioSuccess(String path);
        void onMixAudioError();
    }
}
