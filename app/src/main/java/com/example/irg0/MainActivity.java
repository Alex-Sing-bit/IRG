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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

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

    public static PersonBase base = new PersonBase();



    private boolean allPermissionGranted() {
        return Arrays.stream(PERMISSION)
                .allMatch(permission -> ContextCompat.checkSelfPermission(getBaseContext(), permission)
                        == PackageManager.PERMISSION_GRANTED);
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

        base.addToBase(new Person(123123, "OLEG", "Additional info Oleg"));
        base.addToBase(new Person(124125, "SASHA", "Additional info Sasha"));
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

                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(MainActivity.this), image -> {
                        @OptIn(markerClass = ExperimentalGetImage.class) Image img = image.getImage();
                        Bitmap bitmap = translator.translateYUV(img, MainActivity.this);
                        InputImage inputImage = InputImage.fromBitmap(bitmap, 0);

                        if (Objects.equals(detectionResult.getBarcodeMessage(), " ") && detectionResult.getMainFace() != null) {
                            barcodeDetector.process(inputImage)
                                    .addOnSuccessListener(barcodes -> barcodes.stream()
                                            .findFirst()
                                            .ifPresent(barcode -> {
                                                detectionResult.setBarcodeMessage(barcode.getRawValue());
                                            })).addOnFailureListener(e -> Log.e(TAG, "Error processing Image", e));
                        } else if (frameCount % UPDATE_RATE == 0) {
                            barcodeDetector.process(inputImage)
                                    .addOnSuccessListener(barcodes -> {
                                        if (barcodes.isEmpty()) {
                                            detectionResult.setBarcodeMessage(" ");
                                        }
                                    }).addOnFailureListener(e -> Log.e(TAG, "Error processing Image", e));
                        }

                        if (detectionResult.getMainFace() == null) {
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
                                            //Toast.makeText(MainActivity.this, "САМОЕ КРУПНОЕ ЛИЦО", Toast.LENGTH_SHORT).show();

                                            detectionResult.setMainFace(largestFace);
                                        }
                                    }).addOnFailureListener(e -> Log.e(TAG, "Error processing Image", e));
                        } else if (frameCount % UPDATE_RATE == 0) {
                            faceDetector.process(inputImage)
                                    .addOnSuccessListener(faces -> {
                                        if (faces.isEmpty()) {
                                            detectionResult.setMainFace(null);
                                            detectionResult.setBarcodeMessage(" ");
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

                cameraProvider.bindToLifecycle(MainActivity.this, cameraSelector, imageAnalysis);

            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                Log.e(TAG, "Bind Error", e);
            }
        }, ContextCompat.getMainExecutor((this)));
    }

    public static Bitmap drawBoundingBox(Barcode barcode, Bitmap bitmap) {
        return drawBoundingBox(barcode.getBoundingBox(), bitmap);
    }

    public static Bitmap drawBoundingBox(Face face, Bitmap bitmap) {
        return drawBoundingBox(face.getBoundingBox(), bitmap);
    }
    private static Bitmap drawBoundingBox(Rect bounds, Bitmap bitmap) {
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        assert bounds != null;
        canvas.drawRect(bounds, paint);

        String faceCoordinates = "X: " + bounds.left + ", Y: " + bounds.top + "\n Name: Tolya";
        paint.setTextSize(50);
        paint.setColor(Color.GREEN);
        canvas.drawText(faceCoordinates, bounds.left, bounds.top - 20, paint);

        return bitmap;
    }
}