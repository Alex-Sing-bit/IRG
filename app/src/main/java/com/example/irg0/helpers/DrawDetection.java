package com.example.irg0.helpers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.irg0.MainActivity;
import com.google.mlkit.vision.face.Face;

public class DrawDetection {
    static int COLOR = Color.BLUE;

    public static Bitmap drawDetection(Bitmap bitmap, String id, Face face) {
        if (face == null) {
            return bitmap;
        }
        int intID = -1;
        try {
            intID = Integer.parseInt(id);
        } catch (Exception e) {

        }

        return drawDetection(bitmap, intID, face.getBoundingBox());
    }
    private static Bitmap drawDetection(Bitmap bitmap, int id, Rect faceBounds) {
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(COLOR);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        assert faceBounds != null;
        canvas.drawRect(faceBounds, paint);

        paint.setTextSize(50);
        paint.setColor(COLOR);
        Person p = null;
        if (id > 0) {
            p = MainActivity.base.getPerson(id);
        }

        canvas.drawText((p == null ? "Неизвестная персона" : p.getName()),
                faceBounds.right + 10, faceBounds.top + 20, paint);
        canvas.drawText((p == null ? "" : p.getBirthday().toString()),
                faceBounds.right + 10, faceBounds.top + 80, paint);
        canvas.drawText((p == null ? "" : p.getInfo()),
                faceBounds.right + 10, faceBounds.top + 160, paint);

        return bitmap;
    }
}
