package com.petrov.simplerecorder;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
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


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements OnTickListener {

    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String fileName;
    private SeekBar seekBar;
    private final Handler handlerRecord = new Handler();
    private final Handler handlerPlay = new Handler();
    private ImageView recordButton;
    private ImageView playButton;
    private TextView clock;
    private ImageView repeatButton;
    private ImageView pauseButton;
    private Toast toast;
    private boolean recordPressed;
    private RecorderVisualizerView recorderVisualizerView;
    private Timer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mediaRecorder = new MediaRecorder();
        mediaPlayer = new MediaPlayer();
        recorderVisualizerView = findViewById(R.id.visualizer);

        recordPressed = false;
        fileName = Environment.getExternalStorageDirectory() + "/record.mp3";

        clock = findViewById(R.id.clock);
        recordButton = findViewById(R.id.record);
        playButton = findViewById(R.id.play);
        repeatButton = findViewById(R.id.repeat);
        pauseButton = findViewById(R.id.pause);
        seekBar = findViewById(R.id.seekBar);
        toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER, Gravity.CENTER, Gravity.TOP);
        timer = new Timer(this);

//        recordButton.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.N)
//            @Override
//            public void onClick(View v) {
//                if(recordPressed)
//                    recordPause();
//                if(!recordPressed)
//                    recordResume();
//                else
//                    recordStart(v);
//            }
//        };
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                seekChange(v);

                return false;

            }
        });
    }

//    @RequiresApi(api = Build.VERSION_CODES.N)
//    public void recordPause(){
//        mediaRecorder.pause();
//        recordPressed = false;
//        recordButton.setImageResource(R.drawable.record_inactive);
//        toast = Toast.makeText(getApplicationContext(),
//                "Record paused", Toast.LENGTH_SHORT);
//        toast.show();
//    }
//
//
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    public void recordResume(){
//        mediaRecorder.resume();
//        recordPressed = true;
//        recordButton.setImageResource(R.drawable.record_active);
//        toast = Toast.makeText(getApplicationContext(),
//                "Record resumed", Toast.LENGTH_SHORT);
//        toast.show();
//    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void recordStart(View v) {
        try {
            if (!recordPressed) {
                recorderVisualizerView.cleanAll();
                recordPressed = true;
                mediaRecorder = new MediaRecorder();
                releaseRecorder();
                File outFile = new File(fileName);
                if (outFile.exists()) {
                    outFile.delete();
                }

                mediaRecorder = new MediaRecorder();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                mediaRecorder.setAudioChannels(2);
                mediaRecorder.setMaxDuration(900000);
                mediaRecorder.setOutputFile(fileName);
                mediaRecorder.setAudioEncodingBitRate(128000);
                mediaRecorder.prepare();
                mediaRecorder.start();
                timer.start();
                recordButton.setImageResource(R.drawable.record_active);
                //startRecordProgressUpdater();
                toast = Toast.makeText(getApplicationContext(),
                        "Record", Toast.LENGTH_SHORT);
                toast.show();

            } else {
                recordPressed = false;
                if (mediaRecorder != null) {
                    mediaRecorder.stop();
                    timer.stop();
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
            timer.stop();
            playButton.setImageResource(R.drawable.play_inactive);
            pauseButton.setImageResource(R.drawable.pause_inactive);
            seekBar.setProgress(0);
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
            timer.start();
            playButton.setImageResource(R.drawable.play_active);
            // playProgressUpdater();
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
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            timer.pause();
            pauseButton.setImageResource(R.drawable.pause_active);
            toast = Toast.makeText(getApplicationContext(),
                    "Pause", Toast.LENGTH_SHORT);
            toast.show();
        } else if (!mediaPlayer.isPlaying()
                && mediaPlayer.getCurrentPosition() != 0
                && mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition() > 500) {
            pauseButton.setImageResource(R.drawable.pause_inactive);
            mediaPlayer.start();
            timer.start();
            //startRecordProgressUpdater();
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



//    public void startRecordProgressUpdater() {
//        if (recordPressed) {
//            Runnable notification = new Runnable() {
//                public void run() {
//                    //startRecordProgressUpdater();
//                    recorderVisualizerView.addAmplitude(mediaRecorder.getMaxAmplitude());
//                    ;
//                }
//            };
//            handlerRecord.postDelayed(notification, 100);
//        }
//    }

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

    @Override
    public void OnTimerTick(String duration) {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        clock.setText(duration);
        if (mediaPlayer.isPlaying()) {
            playButton.setImageResource(R.drawable.play_active);
        }
        if (recordPressed && !mediaPlayer.isPlaying()) {
            recorderVisualizerView.addAmplitude(mediaRecorder.getMaxAmplitude());
        } else if (!mediaPlayer.isPlaying()) {
            playButton.setImageResource(R.drawable.play_inactive);
            timer.stop();
        }
    }


//    public void setVisualiser() {
//        Runnable visual = new Runnable() {
//            @Override
//            public void run() {
//                visualizer = new Visualizer(mediaPlayer.getAudioSessionId());
//                visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[2]);
//                visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
//                    @Override
//                    public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
//                        playerVisualizerView.update(waveform);
//                    }
//
//                    @Override
//                    public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
//                    }
//                }, Visualizer.getMaxCaptureRate() / 2, true, false);
//            }
//        };
//    }
}




