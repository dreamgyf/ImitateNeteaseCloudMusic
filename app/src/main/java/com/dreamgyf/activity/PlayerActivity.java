package com.dreamgyf.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dreamgyf.R;
import com.dreamgyf.bottomSheetDialog.PlayListBottomSheetDialog;
import com.dreamgyf.broadcastReceiver.PlayerBroadcastReceiver;
import com.dreamgyf.service.PlayMusicPrepareIntentService;
import com.dreamgyf.service.PlayMusicService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayerActivity extends AppCompatActivity {

    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    private Resources resources;

    private Toolbar toolbar;

    private ImageView playMusicButton;

    private ImageView nextMusicButton;

    private ImageView previousMusicButton;

    private ImageView playListButton;

    private ImageView playModeButton;

    private PlayerBroadcastReceiver playerBroadcastReceiver;

    private SeekBar seekBar;

    private Thread updateProgressThread;

    private boolean pauseUpdateProgressThread = false;

    private TextView currentTime;

    private PlayListBottomSheetDialog playListBottomSheetDialog;

    private BottomSheetDialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        resources = getResources();

        initToolbar();
        initButton();
        setUpdateProgress();
        initSeekBar();

        playListBottomSheetDialog = new PlayListBottomSheetDialog(this);

        playerBroadcastReceiver = new PlayerBroadcastReceiver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PlayMusicService.UPDATE_PLAYER_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(playerBroadcastReceiver,intentFilter);

        Intent broadcastIntent = new Intent(PlayMusicService.PLAY_ACTION);
        broadcastIntent.putExtra("getInfo",1);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    private void initToolbar()
    {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.no_action,R.anim.push_up_out);
    }

    private void initButton(){
        playMusicButton = findViewById(R.id.play_image_view);
        nextMusicButton = findViewById(R.id.next_image_view);
        previousMusicButton = findViewById(R.id.previous_image_view);
        playModeButton = findViewById(R.id.play_mode_image_view);
        playListButton = findViewById(R.id.play_list_image_view);
        playMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayMusicService.PLAY_ACTION);
                intent.putExtra("playOrPause",1);
                LocalBroadcastManager.getInstance(PlayerActivity.this).sendBroadcast(intent);
            }
        });
        nextMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PlayMusicPrepareIntentService.songPosition < PlayMusicPrepareIntentService.songList.size() - 1) {
                    Intent intent = new Intent(PlayerActivity.this, PlayMusicPrepareIntentService.class);
                    intent.putExtra("song",PlayMusicPrepareIntentService.songList.get(PlayMusicPrepareIntentService.songPosition + 1));
                    startService(intent);
                } else {
                    Intent intent = new Intent(PlayerActivity.this, PlayMusicPrepareIntentService.class);
                    intent.putExtra("song",PlayMusicPrepareIntentService.songList.get(0));
                    startService(intent);
                }
            }
        });
        previousMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PlayMusicPrepareIntentService.songPosition == 0) {
                    Intent intent = new Intent(PlayerActivity.this, PlayMusicPrepareIntentService.class);
                    intent.putExtra("song",PlayMusicPrepareIntentService.songList.get(PlayMusicPrepareIntentService.songList.size() - 1));
                    startService(intent);
                } else {
                    Intent intent = new Intent(PlayerActivity.this, PlayMusicPrepareIntentService.class);
                    intent.putExtra("song",PlayMusicPrepareIntentService.songList.get(PlayMusicPrepareIntentService.songPosition - 1));
                    startService(intent);
                }
            }
        });
        playModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (PlayMusicService.MODE){
                    case "ORDER":
                        PlayMusicService.MODE = "LIST_LOOP";
                        playModeButton.setImageDrawable(resources.getDrawable(R.drawable.list_loop_play_mode_icon));
                        break;
                    case "LIST_LOOP":
                        PlayMusicService.MODE = "SINGLE_LOOP";
                        playModeButton.setImageDrawable(resources.getDrawable(R.drawable.single_loop_play_mode_icon));
                        break;
                    case "SINGLE_LOOP":
                        PlayMusicService.MODE = "RANDOM";
                        playModeButton.setImageDrawable(resources.getDrawable(R.drawable.random_play_mode_icon));
                        break;
                    case "RANDOM":
                        PlayMusicService.MODE = "ORDER";
                        playModeButton.setImageDrawable(resources.getDrawable(R.drawable.order_play_mode_icon));
                        break;
                }
            }
        });
        playListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playListBottomSheetDialog.show();
            }
        });
    }

    private void initSeekBar(){
        seekBar = findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pauseUpdateProgressThread = true;
                int currentPosition = seekBar.getProgress();
                String currentPositionSec = String.valueOf(currentPosition / 1000 % 60);
                String currentPositionMin = String.valueOf(currentPosition / 1000 / 60);
                if(currentPositionSec.length() < 2)
                    currentPositionSec = "0" + currentPositionSec;
                if(currentPositionMin.length() < 2)
                    currentPositionMin = "0" + currentPositionMin;
                if(currentTime == null){
                    currentTime = findViewById(R.id.current_time);
                }
                currentTime.setText(currentPositionMin + ":" + currentPositionSec);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                pauseUpdateProgressThread = false;
                Intent updateProgressIntent = new Intent(PlayMusicService.PLAY_ACTION);
                updateProgressIntent.putExtra("updateProgress",seekBar.getProgress());
                LocalBroadcastManager.getInstance(PlayerActivity.this).sendBroadcast(updateProgressIntent);
            }
        });
    }

    public void setUpdateProgress(){
        updateProgressThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent updateUIProgressIntent = new Intent(PlayMusicService.PLAY_ACTION);
                updateUIProgressIntent.putExtra("updateUIProgress",1);
                while (true)
                {
                    while (pauseUpdateProgressThread)
                    {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    LocalBroadcastManager.getInstance(PlayerActivity.this).sendBroadcast(updateUIProgressIntent);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        executorService.execute(updateProgressThread);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(playerBroadcastReceiver);
        super.onDestroy();
    }
}
