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
        int status = intent.getIntExtra("status",-1);
        int change = intent.getIntExtra("change",-1);
        switch(status){
            case 0:
                playerBarPlayButton.setImageDrawable(resources.getDrawable(R.drawable.playbar_play_icon));
                break;
            case 1:
                playerBarPlayButton.setImageDrawable(resources.getDrawable(R.drawable.playbar_pause_icon));
                break;
        }
        if(change == 1){
            String title = intent.getStringExtra("title");
            String subtitle = intent.getStringExtra("subtitle");
            byte[] bytes = intent.getByteArrayExtra("songPicByte");
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            playerBarTitleTextView.setText(title);
            playerBarSubtitleTextView.setText(subtitle);
            playerBarImageView.setImageBitmap(bitmap);

        }
    }
}
