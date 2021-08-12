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
    private List ampsDisplay = new ArrayList<Float>();;
    private final float radius = 6f;
    private final float width = 8f;
    private final float diverse = 2f;
    private int maxPeaks;

    private float sw;
    private float sh = 400f;

    public RecorderVisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        paint = new Paint();
        paint.setColor(Color.rgb(0, 250, 120));
        sw = (float) getResources().getDisplayMetrics().widthPixels;
        maxPeaks = (int) (sw / (width + diverse));
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
        if(amplitudes.size()> maxPeaks) {
            ampsDisplay = amplitudes.subList(amplitudes.size()- maxPeaks, amplitudes.size());
        }
        else {
            ampsDisplay = amplitudes;
        }
        for (int i = 0; i < ampsDisplay.size(); i++) {
            float left = sw - i*(width + diverse);
            float top = sh/2 - (float) ampsDisplay.get(i)/2;
            float right = left + width;
            float bottom = top + (float) ampsDisplay.get(i) +3f;

            peaks.add(new RectF(left, top, right, bottom));
        }
        invalidate();
    }

    public void cleanAll(){
        amplitudes.clear();
        ampsDisplay.clear();
        peaks.clear();
    }




//    public void timerEvent(float amp) {
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                addAmplitude(amp);
//            }
//        }, 0, 100);
//    }



//    public void setZeroPoint(float x) {
//        xV = 0;
//        this.x = x / 300;
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        x = x + (xV /300);
//        canvas.drawCircle(x, 300, 15, paint);
//    }


//    private byte[] mBytes;
//    private float[] mPoints;
//    private Rect mRect = new Rect();
//
//    private Paint mForePaint = new Paint();
//
//    public PlayerVisualizerView(Context context) {
//        super(context);
//        init();
//    }
//
//    public PlayerVisualizerView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        init();
//    }
//
//    public PlayerVisualizerView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        init();
//    }
//
//    private void init() {
//        mBytes = null;
//
//        mForePaint.setStrokeWidth(1f);
//        mForePaint.setAntiAlias(true);
//        mForePaint.setColor(Color.GREEN);
//    }
//
//    public void updateVisualizer(byte[] bytes) {
//        mBytes = bytes;
//        invalidate();
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
//        if (mBytes == null) {
//            return;
//        }
//
//        if (mPoints == null || mPoints.length < mBytes.length * 4) {
//            mPoints = new float[mBytes.length * 4];
//        }
//
//        mRect.set(0, 0, getWidth(), getHeight());
//
//        for (int i = 0; i < mBytes.length - 1; i++) {
//            mPoints[i * 4] = mRect.width() * i / (mBytes.length - 1);
//            mPoints[i * 4 + 1] = mRect.height() / 2
//                    + ((byte) (mBytes[i] + 128)) * (mRect.height() / 2) / 128;
//            mPoints[i * 4 + 2] = mRect.width() * (i + 1) / (mBytes.length - 1);
//            mPoints[i * 4 + 3] = mRect.height() / 2
//                    + ((byte) (mBytes[i + 1] + 128)) * (mRect.height() / 2)
//                    / 128;
//        }
//
//        canvas.drawLines(mPoints, mForePaint);
//    }
}

