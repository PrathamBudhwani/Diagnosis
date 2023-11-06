package com.prathambudhwani.diagnosis;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class MicChecker {
    public static boolean isMicrophoneAvailable(Context context) {
        if (context != null) {
            PackageManager packageManager = context.getPackageManager();
            return packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    public static boolean isMicrophoneFunctional() {
        int bufferSize = AudioRecord.getMinBufferSize(44100, 16, 2);
        AudioRecord audioRecord = null;

        try {
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100, 16, 2, bufferSize);
            audioRecord.startRecording();
            audioRecord.stop();
            audioRecord.release();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (audioRecord != null) {
                audioRecord.release();
            }
        }

        return false;
    }
}
