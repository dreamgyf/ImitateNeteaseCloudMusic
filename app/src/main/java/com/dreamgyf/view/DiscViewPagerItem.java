package com.dreamgyf.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dreamgyf.R;
import com.dreamgyf.activity.MainActivity;
import com.dreamgyf.service.PlayMusicPrepareIntentService;
import com.dreamgyf.service.PlayMusicService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class DiscViewPagerItem extends ConstraintLayout {

    private int position;

    private RoundImageView songPic;

    public DiscViewPagerItem(final Context context, final ViewPager parent,final int position) {
        super(context);
        this.position = position;
        //设置根布局的样式
        ConstraintLayout.LayoutParams discLayoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(discLayoutParams);
        //设置唱片盘
        ImageView discImage = new ImageView(context);
        discImage.setId(10086);
        //设置专辑图片
        songPic = new RoundImageView(context);
        songPic.setId(10087);
        //将子布局加入根布局
        addView(discImage);
        addView(songPic);
        //设置样式
        ConstraintSet set = new ConstraintSet();
        set.clone(this);
        set.centerHorizontally(discImage.getId(),ConstraintSet.PARENT_ID);
        set.centerVertically(discImage.getId(),ConstraintSet.PARENT_ID);
        set.constrainWidth(discImage.getId(),MainActivity.resources.getDisplayMetrics().widthPixels / 4 * 3);
        set.constrainHeight(discImage.getId(),MainActivity.resources.getDisplayMetrics().widthPixels / 4 * 3);
        set.centerHorizontally(songPic.getId(),ConstraintSet.PARENT_ID);
        set.centerVertically(songPic.getId(),ConstraintSet.PARENT_ID);
        set.constrainWidth(songPic.getId(),MainActivity.resources.getDisplayMetrics().widthPixels / 2);
        set.constrainHeight(songPic.getId(),MainActivity.resources.getDisplayMetrics().widthPixels / 2);
        set.applyTo(this);
        //设置图片
        discImage.setImageDrawable(MainActivity.resources.getDrawable(R.drawable.disc));
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.setCurrentItem(1);
            }
        });
        discImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.setCurrentItem(1);
            }
        });
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int songPicId = PlayMusicPrepareIntentService.songList.get(position).getAlbum().getId();
                    File file = new File(MainActivity.PATH + "/pic/" + songPicId + ".jpg");
                    while(position >= PlayMusicPrepareIntentService.songPicReady.size() || PlayMusicPrepareIntentService.songPicReady.get(position).equals(false));
                    final Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            songPic.setImageBitmap(bitmap);
                        }
                    });
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
