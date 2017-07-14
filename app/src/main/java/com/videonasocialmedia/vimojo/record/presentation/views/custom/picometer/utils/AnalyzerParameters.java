/* Copyright 2017 Eddy Xiao <bewantbe@gmail.com>
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

package com.videonasocialmedia.vimojo.record.presentation.views.custom.picometer.utils;

import android.media.MediaRecorder;

/**
 * Basic properties of Analyzer.
 */

public class AnalyzerParameters {
  private int audioSourceId = MediaRecorder.AudioSource.CAMCORDER;
  private int sampleRate = 48000; //16000;
  private int fftLen = 2048;
  private int hopLen = 1024;
  private String wndFuncName = "Hanning";
  private int nFFTAverage = 2;
  private boolean isAWeighting = false;
  private final int BYTE_OF_SAMPLE = 2;
  private final double SAMPLE_VALUE_MAX = 32767.0;   // Maximum signal value

  private double[] micGainDB = null;  // should have fftLen/2 elements

  public AnalyzerParameters() {
  }

  public int getAudioSourceId() {
    return audioSourceId;
  }

  public int getSampleRate() {
    return sampleRate;
  }

  public void setSampleRate(int sampleRate) {
    this.sampleRate = sampleRate;
  }

  public int getFftLen() {
    return fftLen;
  }

  public int getHopLen() {
    return hopLen;
  }

  public String getWndFuncName() {
    return wndFuncName;
  }

  public int getnFFTAverage() {
    return nFFTAverage;
  }

  public boolean isAWeighting() {
    return isAWeighting;
  }

  public int getBYTE_OF_SAMPLE() {
    return BYTE_OF_SAMPLE;
  }

  public double[] getMicGainDB() {
    return micGainDB;
  }
}
