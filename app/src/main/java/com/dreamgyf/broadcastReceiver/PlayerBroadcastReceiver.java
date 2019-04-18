package com.dreamgyf.broadcastReceiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.dreamgyf.R;

public class PlayerBroadcastReceiver extends BroadcastReceiver {

    private Activity activity;

    private Resources resources;

    private Toolbar toolbar;

    private ImageView playButton;

    public PlayerBroadcastReceiver(Activity activity) {
        super();
        this.activity = activity;
        resources = activity.getResources();
        toolbar = activity.findViewById(R.id.toolbar);
        playButton = activity.findViewById(R.id.play_image_view);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int status = intent.getIntExtra("status",-1);
        int change = intent.getIntExtra("change",-1);
        int getInfo = intent.getIntExtra("getInfo",-1);
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
            toolbar.setTitle(title);
            toolbar.setSubtitle(subtitle);
        }
        if(getInfo == 1){
            String title = intent.getStringExtra("title");
            String subtitle = intent.getStringExtra("subtitle");
            toolbar.setTitle(title);
            toolbar.setSubtitle(subtitle);
        }
    }
}
