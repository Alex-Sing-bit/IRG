package com.example.irg0;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
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
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "CameraX";
    private final int PERMISSION_CODE = 10;
    private final String[] PERMISSION = new String[]{Manifest.permission.CAMERA};
    private ImageView preview;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private final YUVtoRGB translator = new YUVtoRGB();
    private BarcodeScanner barcodeDetector;
    private String barcodeMessage = "";

    private FaceDetector faceDetector;

    Face mainFace = null;

    private boolean allPermissionGranted() {
        return Arrays.stream(PERMISSION)
                .allMatch(permission -> ContextCompat.checkSelfPermission(getBaseContext(), permission) == PackageManager.PERMISSION_GRANTED);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        );

        BarcodeScannerOptions bOptions = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build();
        barcodeDetector = BarcodeScanning.getClient(bOptions);

        FaceDetectorOptions fOptions = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .enableTracking().build();
        faceDetector = FaceDetection.getClient(fOptions);

        preview = findViewById(R.id.preview);

        if (allPermissionGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSION,
                    PERMISSION_CODE);
        }
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
    private void startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(4800, 4800))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(MainActivity.this), image -> {
                    @OptIn(markerClass = ExperimentalGetImage.class) Image img = image.getImage();
                    Bitmap bitmap = translator.translateYUV(img, MainActivity.this);
                    InputImage inputImage = InputImage.fromBitmap(bitmap, 0);

                    //Отслеживание кода если не было другого.
                    // Исправь на кореляцию с лицом в кадре или отсутствием его
                    /*if (barcodeMessage.isEmpty()) {
                        barcodeDetector.process(inputImage)
                                .addOnSuccessListener(barcodes -> barcodes.stream()
                                        .findFirst()
                                        .ifPresent(barcode -> {
                                            drawBoundingBox(barcode, bitmap);
                                            preview.setRotation(image.getImageInfo().getRotationDegrees());
                                            image.close();
                                            barcodeMessage = barcode.getRawValue();
                                            Toast.makeText(MainActivity.this, barcodeMessage, Toast.LENGTH_SHORT).show();
                                        })).addOnFailureListener(e -> Log.e(TAG, "Error processing Image", e));
                    }
                    //Не видит лиц
                    else*/ if (mainFace == null) {
                        faceDetector.process(inputImage)
                                .addOnSuccessListener(faces -> faces.stream()
                                        .findFirst()
                                        .ifPresent(face -> {
                                            Toast.makeText(MainActivity.this, "ЛИЦО", Toast.LENGTH_SHORT).show();
                                            //drawBoundingBox(face, bitmap);
                                            //preview.setRotation(image.getImageInfo().getRotationDegrees());
                                            //image.close();
                                            mainFace = face;
                                            //Toast.makeText(MainActivity.this, "ЛИЦО", Toast.LENGTH_SHORT).show();
                                        })).addOnFailureListener(e -> Log.e(TAG, "Error processing Image", e));
                    }

                    preview.setRotation(image.getImageInfo().getRotationDegrees());
                    preview.setImageBitmap(bitmap);
                    image.close();
                });

                cameraProvider.bindToLifecycle(MainActivity.this, cameraSelector, imageAnalysis);

            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                Log.e(TAG, "Bind Error", e);
            }
        }, ContextCompat.getMainExecutor((this)));
    }

    private void drawBoundingBox(Barcode barcode, Bitmap bitmap) {
        drawBoundingBox(barcode.getBoundingBox(), bitmap);
    }

    private void drawBoundingBox(Face face, Bitmap bitmap) {
        face.getTrackingId();
        drawBoundingBox(face.getBoundingBox(), bitmap);
    }
    private void drawBoundingBox(Rect bounds, Bitmap bitmap) {
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        assert bounds != null;
        canvas.drawRect(bounds, paint);

        preview.setImageBitmap(bitmap);
    }
}