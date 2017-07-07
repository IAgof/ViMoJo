/* Copyright 2014 Eddy Xiao <bewantbe@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.videonasocialmedia.vimojo.record.presentation.views.custom.picometer;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.SystemClock;
import android.util.Log;

import com.videonasocialmedia.vimojo.record.presentation.views.custom.picometer.utils.AnalyzerParameters;
import com.videonasocialmedia.vimojo.record.presentation.views.custom.picometer.utils.STFT;


/**
 * Read a snapshot of audio data at a regular interval, and compute the FFT
 *
 * @author suhler@google.com
 *         bewantbe@gmail.com
 */

public class PicometerSamplingLoop extends Thread {
  private final String TAG = "SamplingLoop";
  private volatile boolean isRunning = true;
  private STFT stft;   // use with care
  private AnalyzerParameters analyzerParam;

  private double[] spectrumDBcopy;   // XXX, transfers data from SamplingLoop to AnalyzerGraphic

  private PicometerAmplitudeDbListener listener;

  public PicometerSamplingLoop(AnalyzerParameters analyzerParam, PicometerAmplitudeDbListener
      listener) {
    this.listener = listener;
    this.analyzerParam = analyzerParam;
  }

  public PicometerSamplingLoop(PicometerAmplitudeDbListener listener) {
    this.listener = listener;
    this.analyzerParam = new AnalyzerParameters();
  }

  private void sleepWithoutInterrupt(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void run() {
    AudioRecord audioRecord;

    long tStart = SystemClock.uptimeMillis();

    long tEnd = SystemClock.uptimeMillis();
    if (tEnd - tStart < 500) {
      Log.i(TAG, "wait more.." + (500 - (tEnd - tStart)) + " ms");
      // Wait until previous instance of AudioRecord fully released.
      sleepWithoutInterrupt(500 - (tEnd - tStart));
    }

    int minBytes = AudioRecord.getMinBufferSize(analyzerParam.getSampleRate(),
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT);
    if (minBytes == AudioRecord.ERROR_BAD_VALUE) {
      Log.e(TAG, "SamplingLoop::run(): Invalid AudioRecord parameter.\n");
      return;
    }

        /*
          Develop -> Reference -> AudioRecord
             Data should be read from the audio hardware in chunks of sizes
             inferior to the total recording buffer size.
         */
    // Determine size of buffers for AudioRecord and AudioRecord::read()
    int readChunkSize = analyzerParam.getHopLen();  // Every hopLen one fft result (overlapped analyze window)
    readChunkSize = Math.min(readChunkSize, 2048);  // read in a smaller chunk, hopefully smaller delay
    int bufferSampleSize = Math.max(minBytes / analyzerParam.getBYTE_OF_SAMPLE(),
        analyzerParam.getFftLen() / 2) * 2;
    // tolerate up to about 1 sec.
    bufferSampleSize = (int) Math.ceil(1.0 * analyzerParam.getSampleRate() / bufferSampleSize)
        * bufferSampleSize;

    // Use the mic with AGC turned off. e.g. VOICE_RECOGNITION for measurement
    // The buffer size here seems not relate to the delay.
    // So choose a larger size (~1sec) so that overrun is unlikely.
    try {
      audioRecord = new AudioRecord(analyzerParam.getAudioSourceId(), analyzerParam.getSampleRate(),
          AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
          analyzerParam.getBYTE_OF_SAMPLE() * bufferSampleSize);
    } catch (IllegalArgumentException e) {
      Log.e(TAG, "Fail to initialize recorder.");
      return;
    }
    Log.i(TAG, "SamplingLoop::Run(): Starting recorder... \n" +
        "  source          : " + analyzerParam.getAudioSourceId() + "\n" +
        String.format("  sample rate     : %d Hz (request %d Hz)\n", audioRecord.getSampleRate(),
            analyzerParam.getSampleRate()) +
        String.format("  min buffer size : %d samples, %d Bytes\n", minBytes
            / analyzerParam.getBYTE_OF_SAMPLE(), minBytes) +
        String.format("  buffer size     : %d samples, %d Bytes\n", bufferSampleSize,
            analyzerParam.getBYTE_OF_SAMPLE() * bufferSampleSize) +
        String.format("  read chunk size : %d samples, %d Bytes\n", readChunkSize,
            analyzerParam.getBYTE_OF_SAMPLE() * readChunkSize) +
        String.format("  FFT length      : %d\n", analyzerParam.getFftLen()) +
        String.format("  nFFTAverage     : %d\n", analyzerParam.getnFFTAverage()));
    analyzerParam.setSampleRate(audioRecord.getSampleRate());

    if (audioRecord.getState() == AudioRecord.STATE_UNINITIALIZED) {
      Log.e(TAG, "SamplingLoop::run(): Fail to initialize AudioRecord()");
      // If failed somehow, leave user a chance to change preference.
      return;
    }

    short[] audioSamples = new short[readChunkSize];
    int numOfReadShort;

    stft = new STFT(analyzerParam);
    stft.setAWeighting(analyzerParam.isAWeighting());
    if (spectrumDBcopy == null || spectrumDBcopy.length != analyzerParam.getFftLen() / 2 + 1) {
      spectrumDBcopy = new double[analyzerParam.getFftLen() / 2 + 1];
    }

    // Start recording
    try {
      audioRecord.startRecording();
    } catch (IllegalStateException e) {
      Log.e(TAG, "Fail to start recording.");
      return;
    }

    // Main loop
    // When running in this loop (including when paused), you can not change properties
    // related to recorder: e.g. audioSourceId, sampleRate, bufferSampleSize
    // TODO: allow change of FFT length on the fly.
    while (isRunning) {
      // Read data
      numOfReadShort = audioRecord.read(audioSamples, 0, readChunkSize);   // pulling

      stft.feedData(audioSamples, numOfReadShort);

      // If there is new spectrum data, do plot
      if (stft.nElemSpectrumAmp() >= analyzerParam.getnFFTAverage()) {
        // Update spectrum or spectrogram
        final double[] spectrumDB = stft.getSpectrumAmpDB();
        System.arraycopy(spectrumDB, 0, spectrumDBcopy, 0, spectrumDB.length);

        stft.calculatePeak();
        listener.setMaxAmplituedDb(stft.maxAmpDB);
        sleepWithoutInterrupt(100);

      }
    }
    //Log.i(TAG, "SamplingLoop::Run(): Actual sample rate: " + recorderMonitor.getSampleRate());
    Log.i(TAG, "SamplingLoop::Run(): Stopping and releasing recorder.");
    audioRecord.stop();
    audioRecord.release();
  }

  public void finish() {
    isRunning = false;
    interrupt();
  }
}
