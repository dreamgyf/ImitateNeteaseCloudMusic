package com.dreamgyf.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dreamgyf.R;
import com.dreamgyf.adapter.recyclerView.PlayListAdapter;
import com.dreamgyf.adapter.viewPager.DiscViewPagerAdapter;
import com.dreamgyf.adapter.viewPager.PlayerViewPagerAdapter;
import com.dreamgyf.anim.FadeOutPageTransformer;
import com.dreamgyf.anim.ViewPagerScroller;
import com.dreamgyf.bottomSheetDialog.PlayListBottomSheetDialog;
import com.dreamgyf.broadcastReceiver.PlayerBroadcastReceiver;
import com.dreamgyf.entity.Song;
import com.dreamgyf.service.PlayMusicPrepareIntentService;
import com.dreamgyf.service.PlayMusicService;
import com.dreamgyf.view.NoSlidingViewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayerActivity extends AppCompatActivity {

    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    private Resources resources;

    private Toolbar toolbar;

    private NoSlidingViewPager viewPager;

    public static List<View> viewList = new ArrayList<>();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        resources = getResources();

        initToolbar();
        initPlayList();
        initViewPager();
        initButton();
        setUpdateProgress();
        initSeekBar();
        //初始化广播接收器，向service发一条广播来获得歌曲信息
        initBroadcastReceiver();
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

    private void initPlayList(){
        //加载播放列表
        PlayListAdapter playListAdapter = new PlayListAdapter();
        playListAdapter.addOnItemClickListener(new PlayListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View view, int position, Song song) {
                Intent toPrepareIntentService = new Intent(PlayerActivity.this, PlayMusicPrepareIntentService.class);
                toPrepareIntentService.putExtra("song",song);
                startService(toPrepareIntentService);
            }
        });
        playListBottomSheetDialog = new PlayListBottomSheetDialog(this,playListAdapter);
    }

    private void initViewPager(){
        viewPager = findViewById(R.id.view_pager);
        viewList.add(getLayoutInflater().inflate(R.layout.viewpager_disc,null));
        viewList.add(getLayoutInflater().inflate(R.layout.viewpager_lyric,null));
        viewPager.setAdapter(new PlayerViewPagerAdapter(viewList));
        ViewPagerScroller viewPagerScroller = new ViewPagerScroller(this);
        viewPagerScroller.setDuration(500);     //设置切换动画时间
        viewPagerScroller.setViewPager(viewPager);
        viewPager.setPageTransformer(true,new FadeOutPageTransformer());    //设置切换动画
        initDiscViewPager();
        viewList.get(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
            }
        });
    }

    void initDiscViewPager(){
        viewList.get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
            }
        });
        ImageView discBackground = viewList.get(0).findViewById(R.id.disc_background);
        discBackground.getLayoutParams().width = (int)(resources.getDisplayMetrics().widthPixels * 1.04);
        discBackground.getLayoutParams().height = discBackground.getLayoutParams().width;
        ImageView needle = viewList.get(0).findViewById(R.id.needle);
        ViewGroup.LayoutParams needleLayout = needle.getLayoutParams();
        needleLayout.width = resources.getDisplayMetrics().widthPixels / 5;
        needleLayout.height = resources.getDisplayMetrics().heightPixels;
        needle.setLayoutParams(needleLayout);

        ViewPager discViewPager = viewList.get(0).findViewById(R.id.view_pager);
        discViewPager.setAdapter(new DiscViewPagerAdapter(this,viewPager));
        discViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                Intent intent = new Intent(PlayerActivity.this,PlayMusicPrepareIntentService.class);
                intent.putExtra("song",PlayMusicPrepareIntentService.songList.get(i));
                startService(intent);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

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
        switch (PlayMusicService.MODE){
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
        playModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent setMode = new Intent(PlayMusicService.SET_MODE_ACTION);
                switch (PlayMusicService.MODE){
                    case "ORDER":
                        setMode.putExtra("SET_MODE","LIST_LOOP");
                        break;
                    case "LIST_LOOP":
                        setMode.putExtra("SET_MODE","SINGLE_LOOP");
                        break;
                    case "SINGLE_LOOP":
                        setMode.putExtra("SET_MODE","RANDOM");
                        break;
                    case "RANDOM":
                        setMode.putExtra("SET_MODE","ORDER");
                        break;
                }
                LocalBroadcastManager.getInstance(PlayerActivity.this).sendBroadcast(setMode);
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
            public void onStartTrackingTouch(SeekBar seekBar) {
                pauseUpdateProgressThread = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                pauseUpdateProgressThread = false;
                Intent updateProgressIntent = new Intent(PlayMusicService.SET_SEEK_ACTION);
                updateProgressIntent.putExtra("time",seekBar.getProgress());
                LocalBroadcastManager.getInstance(PlayerActivity.this).sendBroadcast(updateProgressIntent);
            }
        });
    }

    public void setUpdateProgress(){
        updateProgressThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent updateUIProgressIntent = new Intent(PlayMusicService.GET_CURRENT_POSITION_ACTION);
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

    private void initBroadcastReceiver(){
        //注册广播接收器
        playerBroadcastReceiver = new PlayerBroadcastReceiver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PlayMusicService.UPDATE_PLAYER_UI_ACTION);
        intentFilter.addAction(PlayMusicService.UPDATE_MODE_UI_ACTION);
        intentFilter.addAction(PlayMusicService.UPDATE_PLAY_BUTTON_ACTION);
        intentFilter.addAction(PlayMusicService.UPDATE_CURRENT_POSITION_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(playerBroadcastReceiver,intentFilter);
        //初次加载时获取播放信息
        Intent broadcastIntent = new Intent(PlayMusicService.GET_INFO_ACTION);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(playerBroadcastReceiver);
        playListBottomSheetDialog.unregisterReceiver();
        super.onDestroy();
    }

}
