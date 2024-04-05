package com.example.irg0.helpers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.irg0.MainActivity;
import com.google.mlkit.vision.face.Face;

import java.time.LocalDate;

public class DrawDetection {
    static int COLOR = Color.BLUE;

    public static Bitmap drawDetection(Bitmap bitmap, String id, Face face) {
        if (face == null) {
            return bitmap;
        }

        return drawDetection(bitmap, id, face.getBoundingBox());
    }
    private static Bitmap drawDetection(Bitmap bitmap, String id, Rect faceBounds) {
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(COLOR);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        assert faceBounds != null;
        canvas.drawRect(faceBounds, paint);

        paint.setTextSize(50);
        paint.setColor(COLOR);
        Person p = MainActivity.base.getPerson(Person.makeId(id));
        if (p == null) {
            return bitmap;
        }

        canvas.drawText(p.getName(),
                faceBounds.right + 10, faceBounds.top + 20, paint);
        LocalDate b = p.getBirthday();
        canvas.drawText((b == null ? " " : b.toString()),
                faceBounds.right + 10, faceBounds.top + 80, paint);
        canvas.drawText(p.getInfo(),
                faceBounds.right + 10, faceBounds.top + 160, paint);

        return bitmap;
    }
}
