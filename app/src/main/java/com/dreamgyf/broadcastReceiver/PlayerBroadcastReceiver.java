package com.dreamgyf.broadcastReceiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dreamgyf.R;

public class PlayerBroadcastReceiver extends BroadcastReceiver {

    private Activity activity;

    private Resources resources;

    private Toolbar toolbar;

    private ImageView playButton;

    private TextView currentTime;

    private TextView duration;

    private SeekBar seekBar;

    public PlayerBroadcastReceiver(Activity activity) {
        super();
        this.activity = activity;
        resources = activity.getResources();
        toolbar = activity.findViewById(R.id.toolbar);
        playButton = activity.findViewById(R.id.play_image_view);
        currentTime = activity.findViewById(R.id.current_time);
        duration = activity.findViewById(R.id.duration);
        seekBar = activity.findViewById(R.id.seek_bar);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int status = intent.getIntExtra("status",-1);
        int change = intent.getIntExtra("change",-1);
        int currentPosition = intent.getIntExtra("currentPosition",-1);
        switch(status){
            case 0:
                playButton.setImageDrawable(resources.getDrawable(R.drawable.play_icon));
                break;
            case 1:
                playButton.setImageDrawable(resources.getDrawable(R.drawable.pause_icon));
                break;
        }
        if(change == 1){
            String title = intent.getStringExtra("title");
            String subtitle = intent.getStringExtra("subtitle");
            byte[] bytes = intent.getByteArrayExtra("songPicByte");
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            int durationMs = intent.getIntExtra("duration",-1);
            intent.getIntExtra("duration",-1);
            toolbar.setTitle(title);
            toolbar.setSubtitle(subtitle);
            String durationSec = String.valueOf(durationMs / 1000 % 60);
            String durationMin = String.valueOf(durationMs / 1000 / 60);
            duration.setText(durationMin + ":" + durationSec);
            seekBar.setMax(durationMs);
        }
        if(currentPosition != -1){
            seekBar.setProgress(currentPosition);
            String currentPositionSec = String.valueOf(currentPosition / 1000 % 60);
            String currentPositionMin = String.valueOf(currentPosition / 1000 / 60);
            currentTime.setText(currentPositionMin + ":" + currentPositionSec);
        }
    }
}