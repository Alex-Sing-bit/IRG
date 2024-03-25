package com.example.irg0.helpers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.irg0.MainActivity;

public class DrawDetection {
    static int COLOR = Color.BLUE;

    public static Bitmap drawDetection(Bitmap bitmap, String id, Rect faceBounds) {
        int intID = -1;
        try {
            intID = Integer.parseInt(id);
        } catch (Exception e) {

        }

        return drawDetection(bitmap, intID, faceBounds);
    }
    public static Bitmap drawDetection(Bitmap bitmap, int id, Rect faceBounds) {
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(COLOR);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        assert faceBounds != null;
        canvas.drawRect(faceBounds, paint);

        paint.setTextSize(50);
        paint.setColor(COLOR);
        String name;
        if (id > 0) {
            name = MainActivity.base.getPerson(id).getName();
        } else {
            name = "Неизвестная персона";
        }
        canvas.drawText(name, faceBounds.right + 10, faceBounds.top + 20, paint);

        return bitmap;
    }
}
