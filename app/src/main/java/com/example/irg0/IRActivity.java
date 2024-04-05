package com.example.irg0;

import static com.example.irg0.helpers.Person.isPhoneNumber;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.irg0.helpers.DetectionResult;
import com.example.irg0.helpers.DrawDetection;
import com.example.irg0.helpers.Person;
import com.example.irg0.helpers.PersonBase;
import com.example.irg0.helpers.YUVtoRGB;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class IRActivity extends AppCompatActivity {
    private final String TAG = "CameraX";
    private final int PERMISSION_CODE = 10;
    private final String[] PERMISSION = new String[]{Manifest.permission.CAMERA};
    private ImageView preview;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private @SuppressLint("RestrictedApi") ImageAnalysis imageAnalysis;
    private CameraSelector cameraSelector;

    private final YUVtoRGB translator = new YUVtoRGB();

    private BarcodeScanner barcodeDetector;
    private FaceDetector faceDetector;
    DetectionResult detectionResult = new DetectionResult();

    private  final int UPDATE_RATE = 5;

    public void onReturn(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private boolean allPermissionGranted() {
        return Arrays.stream(PERMISSION)
                .allMatch(permission -> ContextCompat.checkSelfPermission(getBaseContext(), permission)
                        == PackageManager.PERMISSION_GRANTED);
    }

    //НЕ РАБОТАЕТ ОТСЛЕЖИВАНИЕ
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_iractivity);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        );

        BarcodeScannerOptions bOptions = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build();
        barcodeDetector = BarcodeScanning.getClient(bOptions);

        FaceDetectorOptions fOptions = new FaceDetectorOptions.Builder()
                .enableTracking()
                .build();
        faceDetector = FaceDetection.getClient(fOptions);

        preview = findViewById(R.id.preview);

        imageAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(4600, 4600))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        if (allPermissionGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSION,
                    PERMISSION_CODE);
        }

        //MainActivity.base.addToBase(new Person(123123, "OLEG", "Additional info Oleg"));
        //MainActivity.base.addToBase(new Person(124125, "SASHA", "Additional info Sasha"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_CODE) {
            if (allPermissionGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "PermissionError", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private int frameCount = 0;
    private void startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(IRActivity.this), image -> {
                    @OptIn(markerClass = ExperimentalGetImage.class) Image img = image.getImage();
                    Bitmap bitmap = translator.translateYUV(img, IRActivity.this);
                    InputImage inputImage = InputImage.fromBitmap(bitmap, 0);

                    if (detectionResult.getMainFace() != null && Objects.equals(detectionResult.getBarcodeMessage(), " ")) {
                        barcodeDetector.process(inputImage)
                                .addOnSuccessListener(barcodes -> barcodes.stream()
                                        .findFirst()
                                        .ifPresent(barcode -> {
                                            String s = barcode.getRawValue();
                                            if (isPhoneNumber(s)) {
                                                Toast.makeText(this, "find", Toast.LENGTH_SHORT).show();
                                                detectionResult.setBarcodeMessage(s);
                                            }
                                        })).addOnFailureListener(e -> Log.e(TAG, "Error processing Image", e));
                    }

                    if (frameCount % UPDATE_RATE == 0) {
                        faceDetector.process(inputImage)
                                .addOnSuccessListener(faces -> {
                                    if (faces.isEmpty()) {
                                        detectionResult.setMainFace(null);
                                        detectionResult.setBarcodeMessage(" ");
                                    } else {
                                        Face largestFace = faces.stream()
                                                .max(Comparator.comparingInt(face ->
                                                        face.getBoundingBox().width() * face.getBoundingBox().height()))
                                                .orElse(null);
                                        detectionResult.setMainFace(largestFace);
                                    }
                                }).addOnFailureListener(e -> Log.e(TAG, "Error processing Image", e));
                    }

                    preview.setRotation(image.getImageInfo().getRotationDegrees());
                    preview.setImageBitmap(DrawDetection.drawDetection(bitmap,
                                detectionResult.getBarcodeMessage(),
                                detectionResult.getMainFace()));
                    image.close();
                    frameCount++;
                });

                cameraProvider.bindToLifecycle(IRActivity.this, cameraSelector, imageAnalysis);

            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                Log.e(TAG, "Bind Error", e);
            }
        }, ContextCompat.getMainExecutor((this)));
    }

}