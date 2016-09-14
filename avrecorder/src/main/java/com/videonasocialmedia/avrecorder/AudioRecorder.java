package com.videonasocialmedia.avrecorder;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.videonasocialmedia.avrecorder.view.GLCameraView;

import java.io.IOException;


/**
 * Records an Audio
 * <p/>
 * Example usage:
 * <ul>
 * <li>AVRecorder recorder = new AVRecorder(mSessionConfig);</li>
 * <li>recorder.startRecording();</li>
 * <li>recorder.stopRecording();</li>
 * <li>(Optional) recorder.reset(mNewSessionConfig);</li>
 * <li>(Optional) recorder.startRecording();</li>
 * <li>(Optional) recorder.stopRecording();</li>
 * <li>recorder.release();</li>
 * </ul>
 *
 * @hide
 */
public class AudioRecorder {

    protected MicrophoneEncoder mMicEncoder;
    private boolean mIsRecording;
    private boolean released;


    public AudioRecorder(SessionAudioConfig config) throws IOException{
        init(config);
    }

    private void init(SessionAudioConfig config) throws IOException {

        mMicEncoder = new MicrophoneEncoder(config);
        mIsRecording = false;
        released = false;

    }



    public void startRecording() {
        mIsRecording = true;
        mMicEncoder.startRecording();
    }

    public boolean isRecording() {
        return mIsRecording;
    }

    public void stopRecording() {
        Log.d("AVRecoder", "Stop: starting stop process");
        mMicEncoder.stopRecording();
        mIsRecording = false;
    }

    /**
     * Prepare for a subsequent recording. Must be called after {@link #stopRecording()}
     * and before {@link #release()}
     *
     * @param config
     */
    public void reset(SessionAudioConfig config) throws IOException {
        mMicEncoder.reset(config);
        mIsRecording = false;
    }

    /**
     * Release resources. Must be called after {@link #stopRecording()} After this call
     * this instance may no longer be used.
     */
    public void release() {
        mMicEncoder.release();
        released = true;

        // MicrophoneEncoder releases all it's resources when stopRecording is called
        // because it doesn't have any meaningful state
        // between recordings. It might someday if we decide to present
        // persistent audio volume meters etc.
        // Until then, we don't need to write MicrophoneEncoder.release()
    }

    public void onHostActivityPaused() {
       // mMicEncoder.onHostActivityPaused();
    }

    public void onHostActivityResumed() {
       // mMicEncoder.onHostActivityResumed();
    }


    public boolean isReleased() {
        return released;
    }

}
