package com.petrov.simplerecorder;

import android.app.Activity;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;

public class MainActivity extends Activity {

    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String fileName;
    private SeekBar seekBar;
    private final Handler handler = new Handler();
    private ImageView recordButton;
    private ImageView playButton;
    private TextView clock;
    private ImageView repeatButton;
    private ImageView pauseButton;
    private Toast toast;
    private boolean recordPressed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mediaRecorder = new MediaRecorder();
        mediaPlayer = new MediaPlayer();


        recordPressed = false;
        fileName = Environment.getExternalStorageDirectory() + "/record.3gpp";

        clock = (TextView) findViewById(R.id.clock);
        recordButton = (ImageView) findViewById(R.id.record);
        playButton = (ImageView) findViewById(R.id.play);
        repeatButton = (ImageView) findViewById(R.id.repeat);
        pauseButton = (ImageView) findViewById(R.id.pause);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER, Gravity.CENTER, Gravity.TOP);
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                seekChange(v);

                return false;

            }
        });
    }

    public void recordStart(View v) {
        try {
            if (!recordPressed) {
                recordPressed = true;
                mediaRecorder = new MediaRecorder();
                releaseRecorder();
                recordButton.setImageResource(R.drawable.record_active);
                File outFile = new File(fileName);
                if (outFile.exists()) {
                    outFile.delete();
                }

                mediaRecorder = new MediaRecorder();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mediaRecorder.setAudioChannels(2);
                mediaRecorder.setMaxDuration(900000);
                mediaRecorder.setOutputFile(fileName);
                mediaRecorder.setAudioEncodingBitRate(128000);
                mediaRecorder.prepare();
                mediaRecorder.start();
                toast = Toast.makeText(getApplicationContext(),
                        "Record", Toast.LENGTH_SHORT);
                toast.show();

            } else {
                recordPressed = false;
                if (mediaRecorder != null) {
                    mediaRecorder.stop();
                    recordButton.setImageResource(R.drawable.record_inactive);
                    toast = Toast.makeText(getApplicationContext(),
                            "Stop record", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void seekChange(View v) {
        if (mediaPlayer.isPlaying()) {
            SeekBar sb = (SeekBar) v;
            mediaPlayer.seekTo(sb.getProgress());
        }
    }


    public void playStop(View v) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            pauseButton.setImageResource(R.drawable.pause_inactive);
            seekBar.setProgress(0);
            clock.setText("0 seconds");
            toast = Toast.makeText(getApplicationContext(),
                    "Stop", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void playStart(View v) {
        try {

            releasePlayer();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(fileName);
            mediaPlayer.prepare();
            seekBar.setMax(mediaPlayer.getDuration());
            mediaPlayer.start();
            startPlayProgressUpdater();
            toast = Toast.makeText(getApplicationContext(),
                    "Play", Toast.LENGTH_SHORT);
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void rewind(View v) {
        try {
            seekBar.setProgress(0);
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                playStart(v);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pause(View v) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            pauseButton.setImageResource(R.drawable.pause_active);
            toast = Toast.makeText(getApplicationContext(),
                    "Pause", Toast.LENGTH_SHORT);
            toast.show();
        } else if (!mediaPlayer.isPlaying()
                && mediaPlayer.getCurrentPosition() != 0
                && mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition() > 500) {
            pauseButton.setImageResource(R.drawable.pause_inactive);
            mediaPlayer.start();
            startPlayProgressUpdater();
            toast = Toast.makeText(getApplicationContext(),
                    "Play", Toast.LENGTH_SHORT);
            toast.show();
        }
        if (mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition() < 500) {
            mediaPlayer.stop();
            toast = Toast.makeText(getApplicationContext(),
                    "Stop", Toast.LENGTH_SHORT);
            toast.show();
            pauseButton.setImageResource(R.drawable.pause_inactive);
        }
    }

    private void releaseRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    private void releasePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
        releaseRecorder();
    }

    public void startPlayProgressUpdater() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        clock.setText((mediaPlayer.getCurrentPosition() / 1000) + " seconds");
        playButton.setImageResource(R.drawable.play_active);
        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    startPlayProgressUpdater();
                }
            };
            handler.postDelayed(notification, 10);
        } else {
            clock.setText((mediaPlayer.getCurrentPosition() / 1000) + " seconds");
            playButton.setImageResource(R.drawable.play_inactive);
        }
    }

    public void repeat(View v) {
        if (mediaPlayer.isPlaying()) {
            if (!mediaPlayer.isLooping()) {
                mediaPlayer.setLooping(true);
                repeatButton.setImageResource(R.drawable.repeat_active);
                toast = Toast.makeText(getApplicationContext(),
                        "Repeat ON", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                mediaPlayer.setLooping(false);
                repeatButton.setImageResource(R.drawable.repeat_inactive);
                toast = Toast.makeText(getApplicationContext(),
                        "Repeat OFF", Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            toast = Toast.makeText(getApplicationContext(),
                    "First press play", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}



