package com.example.irg0;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.irg0.helpers.Person;
import com.example.irg0.helpers.PersonBase;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;


public class MainActivity extends AppCompatActivity {
    public static PersonBase base;

    public void onIR(View view) {
        Intent intent = new Intent(this, IRActivity.class);
        startActivity(intent);
    }

    public void onFriendList(View view) {
        Intent intent = new Intent(this, AddFriendActivity.class);
        startActivity(intent);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("personBase", "");
        base = gson.fromJson(json, PersonBase.class);
        if (base == null) {
            base = new PersonBase();
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ImageView imageView = findViewById(R.id.imageView);
        String number = "+7-999-999-99-99";
        Bitmap bitmap = bitMatrixToBitmap(makeQR(number));
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(base);
        editor.putString("personBase", json);
        editor.apply();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private BitMatrix makeQR(String text) {
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, 1000, 1000);
            return matrix;
        } catch (Exception ignored) {
            return null;
        }
    }

    private Bitmap bitMatrixToBitmap(BitMatrix bitMatrix) {
        if (bitMatrix == null) {
            return null;
        }
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }

        return bitmap;
    }
}

//Сохранение базы на устройстве
//Сканер qr для получения id при добавлении человека
//Блокировать ориентацию экрана