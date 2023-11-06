package com.prathambudhwani.diagnosis.recyclermain.checkui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.prathambudhwani.diagnosis.R;
import com.prathambudhwani.diagnosis.TestResult;

public class CheckSpeaker extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference testResultsRef = database.getReference("testResults");

    private MediaPlayer mediaPlayer;
    private TextView tvSpeakerStatus;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_speaker);

        Button btnCheckSpeaker = findViewById(R.id.btnCheckSpeaker);
        tvSpeakerStatus = findViewById(R.id.tvSpeakerStatus);

        btnCheckSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkSpeakerStatus();
                // Create a TestResult object
                TestResult testResult = new TestResult();
                testResult.setTestName("Speakers Test");
                testResult.setResult(checkSpeakerStatus() ? "Pass" : "Fail");
                testResult.setTimestamp(System.currentTimeMillis());
                testResult.setDeviceName(Build.MODEL);

                // Push the result to Firebase
                String key = testResultsRef.push().getKey();
                testResultsRef.child(key).setValue(testResult);
                Toast.makeText(CheckSpeaker.this, "Test Results Saved", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkSpeakerStatus() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mediaPlayer.setDataSource(this, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.test_tone));
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(mp -> {
                tvSpeakerStatus.setText("Speaker is working");
                Toast.makeText(this, "Speaker is working", Toast.LENGTH_SHORT).show();
                mediaPlayer.release();
            });
            mediaPlayer.start();
        } catch (Exception e) {
            tvSpeakerStatus.setText("Speaker is not working");
            Toast.makeText(this, "Speaker is not working", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}