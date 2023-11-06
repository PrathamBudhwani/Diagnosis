package com.prathambudhwani.diagnosis.recyclermain.checkui;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.hardware.Camera;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.prathambudhwani.diagnosis.R;
import com.prathambudhwani.diagnosis.TestResult;

import java.io.File;


public class CheckCamera extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference testResultsRef = database.getReference("testResults");
    private Camera camera;

    private SurfaceView cameraPreview;

    private static final int CAMERA_PERMISSION_REQUEST = 1;
    private static final int Camera_Permission_Code = 1;
    Button captureCameraButton;
    ImageView ivUser;
    ActivityResultLauncher<Uri> takePictureLauncher;
    Uri imageUri;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_camera);
        cameraPreview = findViewById(R.id.cameraPreview);

        if (checkCameraPermission()) {
            initializeCamera();
        }

        Button captureButton = findViewById(R.id.captureButton);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (camera != null) {
                    // Capture a photo (you can implement this part)

                } else {
                    Toast.makeText(CheckCamera.this, "Camera not initialized", Toast.LENGTH_SHORT).show();
                }

                // Create a TestResult object
                TestResult testResult = new TestResult();
                testResult.setTestName("Camera Test");
                testResult.setResult(checkCameraPermission() ? "Pass" : "Fail");
                testResult.setTimestamp(System.currentTimeMillis());
                testResult.setDeviceName(Build.MODEL);

                // Push the result to Firebase
                String key = testResultsRef.push().getKey();
                testResultsRef.child(key).setValue(testResult);
                Toast.makeText(CheckCamera.this, "Test Results Saved", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
            return false;
        }
        return true;
    }

    private void initializeCamera() {
        try {
            camera = Camera.open();
            camera.setDisplayOrientation(90);
            camera.setPreviewDisplay(cameraPreview.getHolder());
            camera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to open camera", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }
}
