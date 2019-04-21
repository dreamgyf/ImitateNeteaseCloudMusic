package com.dreamgyf.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dreamgyf.R;
import com.dreamgyf.broadcastReceiver.PlayerBroadcastReceiver;
import com.dreamgyf.service.PlayMusicService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayerActivity extends AppCompatActivity {

    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    private Toolbar toolbar;

    private ImageView playButton;

    private PlayerBroadcastReceiver playerBroadcastReceiver;

    private SeekBar seekBar;

    private Thread updateProgressThread;

    private boolean pauseUpdateProgressThread = false;

    private TextView currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        initToolbar();
        initButton();
        setUpdateProgress();
        initSeekBar();


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
        playButton = findViewById(R.id.play_image_view);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayMusicService.PLAY_ACTION);
                intent.putExtra("playOrPause",1);
                LocalBroadcastManager.getInstance(PlayerActivity.this).sendBroadcast(intent);
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
