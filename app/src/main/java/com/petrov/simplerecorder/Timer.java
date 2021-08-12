package com.petrov.simplerecorder;

import android.os.Handler;
import android.os.Looper;


public class Timer {

    OnTickListener listener;

    public Timer(OnTickListener listener) {
        this.listener = listener;
        initTimer();
    }


    private Handler handler = new Handler(Looper.getMainLooper());
    Runnable runnable;
    private long duration = 0L;
    private long delay = 100L;


    public void initTimer() {
        runnable = new Runnable() {
            @Override
            public void run() {
                duration += delay;
                handler.postDelayed(runnable, delay);
                listener.OnTimerTick(timerFormat());
            }
        };
    }

    public void start() {
        handler.postDelayed(runnable, delay);
    }

    public void pause() {
        handler.removeCallbacks(runnable);
    }

    public void stop() {
        handler.removeCallbacks(runnable);
        duration = 0L;
    }

    public String timerFormat() {
        long millis = duration % 1000;
        long seconds = (duration / 1000) % 60;
        long minutes = (duration / (1000 * 60)) % 60;
        return String.format("%02d:%02d:%02d", minutes, seconds, millis/10);
    }
}
