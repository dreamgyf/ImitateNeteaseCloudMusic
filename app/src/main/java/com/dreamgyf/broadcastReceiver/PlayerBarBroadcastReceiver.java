package com.dreamgyf.broadcastReceiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.TextView;

import com.dreamgyf.R;
import com.dreamgyf.activity.MainActivity;
import com.dreamgyf.service.PlayMusicService;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class PlayerBarBroadcastReceiver extends BroadcastReceiver {

    private Activity activity;

    private Resources resources;

    private ImageView playerBarImageView;

    private TextView playerBarTitleTextView;

    private TextView playerBarSubtitleTextView;

    private ImageView playerBarPlayButton;

    public PlayerBarBroadcastReceiver(Activity activity) {
        super();
        this.activity = activity;
        resources = activity.getResources();
        playerBarImageView = activity.findViewById(R.id.player_bar_image_view);
        playerBarTitleTextView = activity.findViewById(R.id.player_bar_title_text_view);
        playerBarSubtitleTextView = activity.findViewById(R.id.player_bar_subtitle_text_view);
        playerBarPlayButton = activity.findViewById(R.id.player_bar_play_button);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        //更新播放状态UI
        if(action.equals(PlayMusicService.UPDATE_PLAY_BUTTON_ACTION)){
            int status = intent.getIntExtra("PLAY_STATUS",-1);
            switch(status){
                case 0:
                    playerBarPlayButton.setImageDrawable(resources.getDrawable(R.drawable.playbar_play_icon));
                    break;
                case 1:
                    playerBarPlayButton.setImageDrawable(resources.getDrawable(R.drawable.playbar_pause_icon));
                    break;
            }
        }
        //更新音乐信息UI
        if(action.equals(PlayMusicService.UPDATE_PLAYER_UI_ACTION)){
            String title = intent.getStringExtra("title");
            String subtitle = intent.getStringExtra("subtitle");
            long songPicId = intent.getLongExtra("songPicId",-1);
            Bitmap bitmap = null;
            if(songPicId != -1){
                try {
                    bitmap = BitmapFactory.decodeStream(new FileInputStream(MainActivity.PATH + "/album_pic/" + songPicId + ".jpg"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            playerBarTitleTextView.setText(title);
            playerBarSubtitleTextView.setText(subtitle);
            if(bitmap != null)
                playerBarImageView.setImageBitmap(bitmap);
        }
    }
}
