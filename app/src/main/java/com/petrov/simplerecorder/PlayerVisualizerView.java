package com.petrov.simplerecorder;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class PlayerVisualizerView extends View {
    private Context context;
    private Paint paint;
    private List amplitudes = new ArrayList<Float>();
    private List peaks = new ArrayList<Float>();
    private RectF rectF;
    private List ampsDisplay = new ArrayList<Float>();
    private final float radius = 6f;
    private float width = 8f;
    private float diverse = 2f;
    private final float amp = 400f;
    private float displayWidth;
    private float displayHeight = 400f;

    public PlayerVisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        paint = new Paint();
        paint.setColor(Color.rgb(255, 255, 255));
        displayWidth = (float) getResources().getDisplayMetrics().widthPixels;
        rectF = new RectF(0, 0, 0, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRoundRect(rectF, radius, radius, paint);
    }

    public void addAmplitude(float width, float diverse, int peaks) {
        this.width = width;
        this.diverse = diverse;
        amplitudes.add(amp);
        if (amplitudes.size() > peaks) {
            ampsDisplay = amplitudes.subList(amplitudes.size() - peaks, amplitudes.size());
        } else {
            ampsDisplay = amplitudes;
        }
        for (int i = 0; i < ampsDisplay.size(); i++) {
            float left = 0 + i * (width + diverse);
            float top = displayHeight / 2 - (float) ampsDisplay.get(i) / 2;
            float right = left + width;
            float bottom = top + (float) ampsDisplay.get(i);

            rectF = new RectF(left, top, right, bottom);
        }
        invalidate();
    }

    public void cleanAmp() {
        rectF = new RectF(0, 0, 0, 0);
        amplitudes.clear();
        peaks.clear();
    }

    //TODO
    public void setRectF(float x) {
        float left = x + (width + diverse);
        float top = 400f;
        float right = left + width;
        float bottom = top + 400f;
        rectF = new RectF(left, top, right, bottom);
        System.out.println(x);
    }
}