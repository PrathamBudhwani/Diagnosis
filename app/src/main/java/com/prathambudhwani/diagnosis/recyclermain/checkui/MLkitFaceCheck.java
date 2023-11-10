package com.prathambudhwani.diagnosis.recyclermain.checkui;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.util.List;

import com.prathambudhwani.diagnosis.LCOFaceDetection;
import com.prathambudhwani.diagnosis.R;
import com.prathambudhwani.diagnosis.ResultDialog;

public class MLkitFaceCheck extends AppCompatActivity {
    private Button cameraButton;
    public static final int REQUEST_IMAGE_CAPTURE = 124;
    private FirebaseVisionImage image;
    private FirebaseVisionFaceDetector detector;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mlkit_face_check);

        FirebaseApp.initializeApp(this);

        cameraButton = findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the CAMERA permission is granted.
                if (ContextCompat.checkSelfPermission(MLkitFaceCheck.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // Request the CAMERA permission.
                    ActivityCompat.requestPermissions(MLkitFaceCheck.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                } else {
                    // CAMERA permission is already granted; proceed with camera capture.
                    startCameraCapture();
                }
            }
        });
    }

    // Handle the result of the permission request.
    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // CAMERA permission granted; proceed with camera capture.
                startCameraCapture();
            } else {
                // CAMERA permission denied; you can show a message or handle it accordingly.
                Toast.makeText(this, "CAMERA permission is required to capture images.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCameraCapture() {
        // Making a new intent for opening the camera.
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        } else {
            // If the image is not captured, set a toast to display an error message.
            Toast.makeText(MLkitFaceCheck.this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap bitmap = (Bitmap) extras.get("data");
                detectFace(bitmap);
            }
        }
    }

    private void detectFace(Bitmap bitmap) {
        FirebaseVisionFaceDetectorOptions options = new FirebaseVisionFaceDetectorOptions.Builder()
                .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                .build();

        try {
            image = FirebaseVisionImage.fromBitmap(bitmap);
            detector = FirebaseVision.getInstance().getVisionFaceDetector(options);
        } catch (Exception e) {
            e.printStackTrace();
        }

        detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                        String resultText = "";
                        int i = 1;
                        for (FirebaseVisionFace face : firebaseVisionFaces) {
                            resultText += "\nFACE NUMBER. " + i + ":\n";
                            resultText += "Smile: " + (face.getSmilingProbability() * 100) + "%\n";
                            resultText += "Left eye open: " + (face.getLeftEyeOpenProbability() * 100) + "%\n";
                            resultText += "Right eye open: " + (face.getRightEyeOpenProbability() * 100) + "%\n";
                            i++;
                        }

                        if (firebaseVisionFaces.size() == 0) {
                            Toast.makeText(MLkitFaceCheck.this, "NO FACE DETECT", Toast.LENGTH_SHORT).show();
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putString(LCOFaceDetection.RESULT_TEXT, resultText);
                            DialogFragment resultDialog = new ResultDialog();
                            resultDialog.setArguments(bundle);
                            resultDialog.setCancelable(true);
                            resultDialog.show(getSupportFragmentManager(), LCOFaceDetection.RESULT_DIALOG);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MLkitFaceCheck.this, "Oops, Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
