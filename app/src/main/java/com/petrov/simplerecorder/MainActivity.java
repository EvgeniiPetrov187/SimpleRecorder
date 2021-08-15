package com.petrov.simplerecorder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnTickListener {

    private int PERMISSION_CODE = 1;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String fileName;
    private TextView clock;
    private ImageView recordButton;
    private ImageView playButton;
    private ImageView repeatButton;
    private ImageView pauseButton;
    private ImageView stopButton;
    private Toast toast;
    private boolean recordPressed;
    private boolean pausePressed;
    private boolean playPressed;
    private boolean repeatPressed;
    private RecorderVisualizerView recorderVisualizerView;
    private PlayerVisualizerView playerVisualizerView;
    private List<Float> events = new ArrayList<>();
    private float timeEvent;
    private Timer timer;
    private float x;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this, "Permissions granted", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();
        }

        mediaRecorder = new MediaRecorder();
        mediaPlayer = new MediaPlayer();

        pausePressed = false;
        recordPressed = false;
        playPressed = false;
        repeatPressed = false;


        fileName = Environment.getExternalStorageDirectory() + "/record.mp3";

        recorderVisualizerView = findViewById(R.id.visualizer);
        playerVisualizerView = findViewById(R.id.play_visualizer);
        clock = findViewById(R.id.clock);
        recordButton = findViewById(R.id.record);
        playButton = findViewById(R.id.play);
        repeatButton = findViewById(R.id.repeat);
        pauseButton = findViewById(R.id.pause);
        stopButton = findViewById(R.id.stop);

        toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER, Gravity.CENTER, Gravity.TOP);

        timer = new Timer(this);

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!recordPressed && !playPressed) {
                    recordStart();
                } else {
                    recordStop();
                }
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!playPressed && !recordPressed)
                    playStart();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pausePressed) {
                    resumePlay();
                } else {
                    pausePlay();
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playStop();
            }
        });

        //TODO
        playerVisualizerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                x = event.getX();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    playerVisualizerView.setRectF(x);
                }
                return true;
            }
        });
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Need Permissions")
                    .setMessage("You have no permissions")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.RECORD_AUDIO}, PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO}, PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE)
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
    }

    public void recordStart() {
        if (!mediaPlayer.isPlaying()) {
            try {
                recorderVisualizerView.cleanAll();
                mediaRecorder = new MediaRecorder();

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
                recordPressed = true;
                recordButton.setImageResource(R.drawable.record_active);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void recordStop() {
        if (!mediaPlayer.isPlaying()) {
            mediaRecorder.stop();
            timer.stop();
            mediaRecorder.release();
            recordPressed = false;
            recordButton.setImageResource(R.drawable.record_inactive);
        }
    }


    public void playStop() {
        if (!recordPressed) {
            mediaPlayer.stop();
            timer.stop();
            playPressed = false;
            playButton.setImageResource(R.drawable.play_inactive);
            pauseButton.setImageResource(R.drawable.pause_inactive);
            repeatButton.setImageResource(R.drawable.repeat_inactive);
            mediaPlayer.setLooping(false);
            repeatPressed = false;
            events.clear();
            playerVisualizerView.cleanAmp();
            toast = Toast.makeText(getApplicationContext(),
                    "Stop", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    public void playStart() {
        if (!recordPressed) {
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(fileName);
                mediaPlayer.prepare();
                events.clear();
                mediaPlayer.start();
                timer.start();
                playPressed = true;
                playButton.setImageResource(R.drawable.play_active);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void rewind(View v) {
        if (playPressed) {
            playStop();
            playStart();
        } else {
            playStop();
        }
    }

    public void pausePlay() {
        if (playPressed) {
            mediaPlayer.pause();
            timer.pause();
            pausePressed = true;
            pauseButton.setImageResource(R.drawable.pause_active);
        }
    }

    public void resumePlay() {
        if (playPressed) {
            if (mediaPlayer.getCurrentPosition() != 0
                    && mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition() >= 500) {
                mediaPlayer.start();
                timer.start();
                pausePressed = false;
                pauseButton.setImageResource(R.drawable.pause_inactive);
            }
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

    public void repeat(View v) {
        if (playPressed) {
            if (!mediaPlayer.isLooping()) {
                mediaPlayer.setLooping(true);
                repeatPressed = true;
                repeatButton.setImageResource(R.drawable.repeat_active);
            } else {
                mediaPlayer.setLooping(false);
                repeatPressed = false;
                repeatButton.setImageResource(R.drawable.repeat_inactive);
            }
        } else {
            toast = Toast.makeText(getApplicationContext(),
                    "First press play", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void OnTimerEvent(String duration) {
        clock.setText(duration);
        if (recordPressed) {
            recorderVisualizerView.addAmplitude(mediaRecorder.getMaxAmplitude());
        }
        if (playPressed) {
            timeEvent = mediaPlayer.getCurrentPosition();
            events.add(timeEvent);
            playerVisualizerView.addAmplitude(recorderVisualizerView.getWidths(), recorderVisualizerView.getDiverse(), events.size());

            if (repeatPressed && !recordPressed && mediaPlayer.getDuration() - events.get(events.size() - 1) <= 200) {
                events.clear();
                playerVisualizerView.cleanAmp();
            } else if (!repeatPressed && !recordPressed && mediaPlayer.getDuration() - events.get(events.size() - 1) <= 200) {
                playStop();

            }
        }
    }
}





