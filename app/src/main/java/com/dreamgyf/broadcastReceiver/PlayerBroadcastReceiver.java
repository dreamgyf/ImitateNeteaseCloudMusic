package com.dreamgyf.broadcastReceiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dreamgyf.R;
import com.dreamgyf.activity.MainActivity;
import com.dreamgyf.activity.PlayerActivity;
import com.dreamgyf.service.PlayMusicService;
import com.dreamgyf.util.ImageUtil;
import com.dreamgyf.view.NoSlidingViewPager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class PlayerBroadcastReceiver extends BroadcastReceiver {

    private Activity activity;

    private Resources resources;

    private Toolbar toolbar;

    private ImageView playButton;

    private ImageView playModeButton;

    private TextView currentTime;

    private TextView duration;

    private SeekBar seekBar;

    private NoSlidingViewPager viewPager;

    private ViewPager discViewPager;

    public PlayerBroadcastReceiver(Activity activity) {
        super();
        this.activity = activity;
        resources = activity.getResources();
        toolbar = activity.findViewById(R.id.toolbar);
        playButton = activity.findViewById(R.id.play_image_view);
        playModeButton = activity.findViewById(R.id.play_mode_image_view);
        currentTime = activity.findViewById(R.id.current_time);
        duration = activity.findViewById(R.id.duration);
        seekBar = activity.findViewById(R.id.seek_bar);
        viewPager = activity.findViewById(R.id.view_pager);
        discViewPager = PlayerActivity.viewList.get(0).findViewById(R.id.view_pager);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        //更新播放状态UI
        if(action.equals(PlayMusicService.UPDATE_PLAY_BUTTON_ACTION)){
            int status = intent.getIntExtra("PLAY_STATUS",-1);
            switch(status){
                case 0:
                    playButton.setImageDrawable(resources.getDrawable(R.drawable.play_icon));
                    break;
                case 1:
                    playButton.setImageDrawable(resources.getDrawable(R.drawable.pause_icon));
                    break;
            }
        }
        //更新模式UI
        if(action.equals(PlayMusicService.UPDATE_MODE_UI_ACTION)){
            String updateMode = intent.getStringExtra("UPDATE_MODE");
            switch (updateMode){
                case "ORDER":
                    playModeButton.setImageDrawable(resources.getDrawable(R.drawable.order_play_mode_icon));
                    break;
                case "LIST_LOOP":
                    playModeButton.setImageDrawable(resources.getDrawable(R.drawable.list_loop_play_mode_icon));
                    break;
                case "SINGLE_LOOP":
                    playModeButton.setImageDrawable(resources.getDrawable(R.drawable.single_loop_play_mode_icon));
                    break;
                case "RANDOM":
                    playModeButton.setImageDrawable(resources.getDrawable(R.drawable.random_play_mode_icon));
                    break;
            }
        }
        //更新音乐信息UI
        if(action.equals(PlayMusicService.UPDATE_PLAYER_UI_ACTION)){
            String title = intent.getStringExtra("title");
            String subtitle = intent.getStringExtra("subtitle");
            int durationMs = intent.getIntExtra("duration",-1);
            intent.getIntExtra("duration",-1);
            toolbar.setTitle(title);
            toolbar.setSubtitle(subtitle);
            String durationSec = String.valueOf(durationMs / 1000 % 60);
            String durationMin = String.valueOf(durationMs / 1000 / 60);
            if(durationSec.length() < 2)
                durationSec = "0" + durationSec;
            if(durationMin.length() < 2)
                durationMin = "0" + durationMin;
            duration.setText(durationMin + ":" + durationSec);
            seekBar.setMax(durationMs);
            int songPosition = intent.getIntExtra("songPosition",-1);
            if(songPosition != -1)
                discViewPager.setCurrentItem(songPosition);
            final int songPicId = intent.getIntExtra("songPicId",-1);
            if(songPicId != -1){
                final Handler handler = new Handler();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            float aspectRatio = (float) MainActivity.RESOURCES.getDisplayMetrics().widthPixels / MainActivity.RESOURCES.getDisplayMetrics().heightPixels;
                            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(MainActivity.PATH + "/pic/" + songPicId + ".jpg"));
                            int cropBitmapWidth;
                            int cropBitmapHeight;
                            if(bitmap.getWidth() >= bitmap.getHeight()){
                                cropBitmapWidth = (int) (bitmap.getWidth() * aspectRatio);
                                cropBitmapHeight = bitmap.getHeight();
                            } else {
                                cropBitmapWidth = bitmap.getWidth();
                                cropBitmapHeight = (int) (bitmap.getHeight() * aspectRatio);
                            }
                            Bitmap cropBitmap = Bitmap.createBitmap(bitmap,
                                    bitmap.getWidth() >= bitmap.getHeight()
                                            ? (bitmap.getWidth() - cropBitmapWidth) / 2
                                            : 0,
                                    bitmap.getWidth() >= bitmap.getHeight()
                                            ? 0
                                            : (bitmap.getHeight() - cropBitmapHeight) / 2,
                                    cropBitmapWidth,cropBitmapHeight);
                            Bitmap zoomOutBitmap = Bitmap.createScaledBitmap(cropBitmap,cropBitmapWidth / 50,cropBitmapHeight / 50,false);
                            Bitmap blurBitmap = ImageUtil.doBlur(zoomOutBitmap,8,true);
                            final Drawable background = new BitmapDrawable(MainActivity.RESOURCES,blurBitmap);
                            background.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    DrawerLayout root = activity.findViewById(R.id.root);
                                    TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{root.getBackground(),background});
                                    root.setBackground(transitionDrawable);
                                    transitionDrawable.startTransition(300);
                                }
                            });


                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
        //更新当前播放时间
        if(action.equals(PlayMusicService.UPDATE_CURRENT_POSITION_ACTION)){
            int currentPosition = intent.getIntExtra("currentPosition",-1);
            if(currentPosition != -1){
                seekBar.setProgress(currentPosition);
                String currentPositionSec = String.valueOf(currentPosition / 1000 % 60);
                String currentPositionMin = String.valueOf(currentPosition / 1000 / 60);
                if(currentPositionSec.length() < 2)
                    currentPositionSec = "0" + currentPositionSec;
                if(currentPositionMin.length() < 2)
                    currentPositionMin = "0" + currentPositionMin;
                currentTime.setText(currentPositionMin + ":" + currentPositionSec);
            }
        }
    }
}
