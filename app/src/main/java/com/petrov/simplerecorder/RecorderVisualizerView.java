package com.petrov.simplerecorder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class RecorderVisualizerView extends View {
    private Context context;
    private Paint paint;
    private List amplitudes = new ArrayList<Float>();
    private List peaks = new ArrayList<Float>();
    private List ampsDisplay = new ArrayList<Float>();
    ;
    private final float radius = 6f;
    private float widths = 10f;
    private float diverse = 3f;
    private float divider = 1;
    private int maxPeaks;

    private float displayWidth;
    private float displayHeight = 400f;

    public RecorderVisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        paint = new Paint();
        paint.setColor(Color.rgb(0, 250, 120));
        displayWidth = (float) getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Object rect : peaks) {
            canvas.drawRoundRect((RectF) rect, radius, radius, paint);
        }
    }

    public void addAmplitude(float amp) {
        float normal = Math.min(amp / 7, 400);
        amplitudes.add(normal);
        peaks.clear();
        maxPeaks = (int) (displayWidth / (widths + diverse));
        if (amplitudes.size() > maxPeaks) {
            divider = divider + 0.1f;
            widths = widths / divider;
            diverse = diverse / divider;
            for (int i = 0; i < ampsDisplay.size(); i++) {
                float left = 0 + i * (widths + diverse);
                float top = displayHeight / 2 - (float) ampsDisplay.get(i) / 2;
                float right = left + widths;
                float bottom = top + (float) ampsDisplay.get(i) + 3f;

                peaks.add(new RectF(left, top, right, bottom));
            }
        } else {
            ampsDisplay = amplitudes;
            for (int i = 0; i < ampsDisplay.size(); i++) {
                float left = 0 + i * (widths + diverse);
                float top = displayHeight / 2 - (float) ampsDisplay.get(i) / 2;
                float right = left + widths;
                float bottom = top + (float) ampsDisplay.get(i) + 3f;

                peaks.add(new RectF(left, top, right, bottom));
            }
        }
        invalidate();
    }

    public void cleanAll() {
        amplitudes.clear();
        ampsDisplay.clear();
        peaks.clear();
    }

    public float getWidths() {
        return widths;
    }

    public void setWidth(float widths) {
        this.widths = widths;
    }

    public float getDiverse() {
        return diverse;
    }

    public void setDiverse(float diverse) {
        this.diverse = diverse;
    }

    public List getAmplitudes() {
        return amplitudes;
    }

    public void setAmplitudes(List amplitudes) {
        this.amplitudes = amplitudes;
    }
}

