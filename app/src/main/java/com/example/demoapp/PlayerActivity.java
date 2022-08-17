package com.example.demoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    Button btnplay, btnnext, btnprev;
    TextView textsngname, textsngstart, textsngend;
    SeekBar seekmusic;

    String sngname;
    public static final String EXTRA_NAME = "song_name";
    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> mySong;
    Thread update_seek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        getSupportActionBar().hide();

        btnplay = findViewById(R.id.play_btn);
        btnnext = findViewById(R.id.skip_next_btn);
        btnprev = findViewById(R.id.skip_prev_btn);

        textsngname = (TextView)  findViewById(R.id.song_name_display);
        textsngstart = findViewById(R.id.song_start);
        textsngend = findViewById(R.id.song_end);

        seekmusic = findViewById(R.id.seek_bar);

        if (mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent in = getIntent();
        Bundle bundle = in.getExtras();

        mySong = (ArrayList) bundle.getParcelableArrayList("songs");
        position = bundle.getInt("pos");
        Uri uri = Uri.parse(mySong.get(position).toString());
        sngname = mySong.get(position).getName().replace(".mp3","");
        textsngname.setText(sngname);

        mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();

        textsngend.setText(createTime(mediaPlayer.getDuration()));

        btnplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    btnplay.setBackgroundResource(R.drawable.ic_play);
                    mediaPlayer.pause();
                }
                else{
                    btnplay.setBackgroundResource(R.drawable.ic_pause_btn);
                    mediaPlayer.start();
                }
            }
        });

        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position+1)%mySong.size());
                Uri u = Uri.parse(mySong.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),u);
                sngname = mySong.get(position).getName().replace(".mp3","");
                textsngname.setText(sngname);
                mediaPlayer.start();
                textsngend.setText(createTime(mediaPlayer.getDuration()));
                thread_seek();
                btnplay.setBackgroundResource(R.drawable.ic_pause_btn);
                auto_play();
            }
        });

        btnprev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = (((position-1)<0)?(mySong.size()-1):(position-1));
                Uri u = Uri.parse(mySong.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),u);
                sngname = mySong.get(position).getName().replace(".mp3","");
                textsngname.setText(sngname);
                mediaPlayer.start();
                textsngend.setText(createTime(mediaPlayer.getDuration()));
                thread_seek();
                btnplay.setBackgroundResource(R.drawable.ic_pause_btn);
                auto_play();
            }
        });

        thread_seek();

        auto_play();

        final Handler handler = new Handler();
        final int delay = 1000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                textsngstart.setText(createTime(mediaPlayer.getCurrentPosition()));
                handler.postDelayed(this,delay);
            }
        },delay);

    }

    public String createTime(int duration){
        String time = "";
        int min = duration/1000/60;
        int sec = duration/1000%60;

        time += (min+":");

        if (sec<10){
            time+=("0"+sec);
        }
        else{
            time+=sec;
        }
        return time;
    }

    public void thread_seek(){
        update_seek = new Thread(){
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;
                while (currentPosition < totalDuration){
                    try{
                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekmusic.setProgress(currentPosition);
                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
        };

        seekmusic.setMax(mediaPlayer.getDuration());
        update_seek.start();
        seekmusic.getProgressDrawable()
                .setColorFilter(getResources()
                        .getColor(R.color.purple_500), PorterDuff.Mode.MULTIPLY);
        seekmusic.getThumb()
                .setColorFilter(getResources()
                        .getColor(R.color.teal_200),PorterDuff.Mode.SRC_IN);

        seekmusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
    }

    public void auto_play(){
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                btnnext.performClick();
            }
        });
    }

}