package com.example.irg0;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

public class BarcodeDetect {
    private BarcodeScanner barcodeDetector;
    String barcodeString = "";

    public void onCreate() {
        BarcodeScannerOptions bOptions = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build();
        barcodeDetector = BarcodeScanning.getClient(bOptions);
    }

    public Bitmap onRun(Bitmap bitmap) {
        InputImage inputImage = InputImage.fromBitmap(bitmap, 0);

        barcodeDetector.process(inputImage)
                .addOnSuccessListener(barcodes -> barcodes.stream()
                        .findFirst()
                        .ifPresent(barcode -> {
                            barcodeString =  barcode.getRawValue();
                            //MainActivity.drawBoundingBox(barcode, bitmap);
                        })).addOnFailureListener(e -> Log.e("Barcode Scanner", "Error processing Image", e));

        return bitmap;
    }
}

