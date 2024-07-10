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

package com.racquetbuddy.audioanalyzer;

import android.content.res.Resources;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.audiofx.AutomaticGainControl;
import android.os.SystemClock;
import android.util.Log;


/**
 * Read a snapshot of audio data at a regular interval, and compute the FFT
 *
 * @author suhler@google.com
 * bewantbe@gmail.com
 * Ref:
 * https://developer.android.com/guide/topics/media/mediarecorder.html#example
 * https://developer.android.com/reference/android/media/audiofx/AutomaticGainControl.html
 * <p>
 * TODO:
 *   See also: High-Performance Audio
 *   https://developer.android.com/ndk/guides/audio/index.html
 *   https://developer.android.com/ndk/guides/audio/aaudio/aaudio.html
 */

public class SamplingLoop extends Thread {

    CalibrationLoad calibLoad = new CalibrationLoad();  // data for calibration of spectrum

    private volatile boolean isRunning = true;
    private final AnalyzerParameters analyzerParam;
    private final SoundAnalyzerCallback callback;

    private double[] spectrumDBcopy;   // XXX, transfers data from SamplingLoop to AnalyzerGraphic

    private void SleepWithoutInterrupt(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public SamplingLoop(SoundAnalyzerCallback callback, Resources res) {
        this.callback = callback;

        this.analyzerParam = new AnalyzerParameters(res);
        analyzerParam.wndFuncName = "Hanning";

        fillFftCalibration(analyzerParam, calibLoad);
    }

    void fillFftCalibration(AnalyzerParameters _analyzerParam, CalibrationLoad _calibLoad) {
        if (_calibLoad.freq == null || _calibLoad.freq.length == 0 || _analyzerParam == null) {
            return;
        }
        double[] freqTick = new double[_analyzerParam.fftLen / 2 + 1];
        for (int i = 0; i < freqTick.length; i++) {
            freqTick[i] = (double) i / _analyzerParam.fftLen * _analyzerParam.sampleRate;
        }
        _analyzerParam.micGainDB = AnalyzerUtil.interpLinear(_calibLoad.freq, _calibLoad.gain, freqTick);
        _analyzerParam.calibName = _calibLoad.name;
    }

    @Override
    public void run() {
        AudioRecord record;

        long tStart = SystemClock.uptimeMillis();
        long tEnd = SystemClock.uptimeMillis();
        String TAG = "SamplingLoop";
        if (tEnd - tStart < 500) {
            Log.i(TAG, "wait more.." + (500 - (tEnd - tStart)) + " ms");
            // Wait until previous instance of AudioRecord fully released.
            SleepWithoutInterrupt(500 - (tEnd - tStart));
        }

        int minBytes = AudioRecord.getMinBufferSize(analyzerParam.sampleRate, AudioFormat.CHANNEL_IN_MONO,
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
        int readChunkSize = analyzerParam.hopLen;  // Every hopLen one fft result (overlapped analyze window)
        readChunkSize = Math.min(readChunkSize, 2048);  // read in a smaller chunk, hopefully smaller delay
        int bufferSampleSize = Math.max(minBytes / analyzerParam.BYTE_OF_SAMPLE, analyzerParam.fftLen / 2) * 2;
        // tolerate up to about 1 sec.
        bufferSampleSize = (int) Math.ceil(1.0 * analyzerParam.sampleRate / bufferSampleSize) * bufferSampleSize;

        // Use the mic with AGC turned off. e.g. VOICE_RECOGNITION for measurement
        // The buffer size here seems not relate to the delay.
        // So choose a larger size (~1sec) so that overrun is unlikely.
        try {
            record = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION, analyzerParam.sampleRate, AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, analyzerParam.BYTE_OF_SAMPLE * bufferSampleSize);

        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Fail to initialize recorder.");
            return;
        }

        // Check Auto-Gain-Control status.
        if (AutomaticGainControl.isAvailable()) {
            AutomaticGainControl agc = AutomaticGainControl.create(
                    record.getAudioSessionId());
            if (agc != null && agc.getEnabled())
                Log.i(TAG, "SamplingLoop::Run(): AGC: enabled.");
            else
                Log.i(TAG, "SamplingLoop::Run(): AGC: disabled.");
        } else {
            Log.i(TAG, "SamplingLoop::Run(): AGC: not available.");
        }

        Log.i(TAG, "SamplingLoop::Run(): Starting recorder... \n" +
                "  source          : " + analyzerParam.audioSourceId + "\n" +
                String.format("  sample rate     : %d Hz (request %d Hz)\n", record.getSampleRate(), analyzerParam.sampleRate) +
                String.format("  min buffer size : %d samples, %d Bytes\n", minBytes / analyzerParam.BYTE_OF_SAMPLE, minBytes) +
                String.format("  buffer size     : %d samples, %d Bytes\n", bufferSampleSize, analyzerParam.BYTE_OF_SAMPLE * bufferSampleSize) +
                String.format("  read chunk size : %d samples, %d Bytes\n", readChunkSize, analyzerParam.BYTE_OF_SAMPLE * readChunkSize) +
                String.format("  FFT length      : %d\n", analyzerParam.fftLen) +
                String.format("  nFFTAverage     : %d\n", analyzerParam.nFFTAverage));
        analyzerParam.sampleRate = record.getSampleRate();

        if (record.getState() == AudioRecord.STATE_UNINITIALIZED) {
            Log.e(TAG, "SamplingLoop::run(): Fail to initialize AudioRecord()");
            return;
        }

        short[] audioSamples = new short[readChunkSize];
        byte[] byteAudioSamples = new byte[readChunkSize];
        int numOfReadShort;

        // use with care
        STFT stft = new STFT(analyzerParam);
        stft.setAWeighting(analyzerParam.isAWeighting);
        if (spectrumDBcopy == null || spectrumDBcopy.length != analyzerParam.fftLen / 2 + 1) {
            spectrumDBcopy = new double[analyzerParam.fftLen / 2 + 1];
        }

        RecorderMonitor recorderMonitor = new RecorderMonitor(analyzerParam.sampleRate, bufferSampleSize, "SamplingLoop::run()");
        recorderMonitor.start();

        try {
            record.startRecording();
        } catch (IllegalStateException e) {
            Log.e(TAG, "Fail to start recording.");
            return;
        }

        // Main loop
        // When running in this loop (including when paused), you can not change properties
        // related to recorder: e.g. audioSourceId, sampleRate, bufferSampleSize
        // TODO: allow change of FFT length on the fly.
        while (isRunning) {
            numOfReadShort = record.read(audioSamples, 0, readChunkSize);   // pulling

            stft.feedData(audioSamples, numOfReadShort);

            if (stft.nElemSpectrumAmp() >= analyzerParam.nFFTAverage) {

                stft.calculatePeak();

                record.read(byteAudioSamples, 0, readChunkSize);
                callback.onSoundDataReceived(stft.maxAmpFreq, stft.maxAmpDB, byteAudioSamples);
            }
        }
        Log.i(TAG, "SamplingLoop::Run(): Actual sample rate: " + recorderMonitor.getSampleRate());
        Log.i(TAG, "SamplingLoop::Run(): Stopping and releasing recorder.");
        record.stop();
        record.release();
    }

    public interface SoundAnalyzerCallback {
        void onSoundDataReceived(double frequency, double db, byte[] spectrogram);
    }

    public void finish() {
        isRunning = false;
        interrupt();
    }
}
