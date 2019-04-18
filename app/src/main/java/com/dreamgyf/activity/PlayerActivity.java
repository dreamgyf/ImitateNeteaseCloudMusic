package com.dreamgyf.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.dreamgyf.R;
import com.dreamgyf.broadcastReceiver.PlayerBroadcastReceiver;
import com.dreamgyf.service.PlayMusicService;

public class PlayerActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private ImageView playButton;

    private PlayerBroadcastReceiver playerBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        initToolbar();
        initButton();

        playerBroadcastReceiver = new PlayerBroadcastReceiver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PlayMusicService.UPDATE_PLAYER_ACTION);
        registerReceiver(playerBroadcastReceiver,intentFilter);

        Intent broadcastIntent = new Intent(PlayMusicService.PLAY_ACTION);
        broadcastIntent.putExtra("getInfo",1);
        sendBroadcast(broadcastIntent);
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
                sendBroadcast(intent);
            }
        });
    }
}
