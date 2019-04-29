package com.dreamgyf.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamgyf.R;
import com.dreamgyf.adapter.recyclerView.IndexViewPagerTopAdapter;
import com.dreamgyf.adapter.recyclerView.PlayListAdapter;
import com.dreamgyf.adapter.recyclerView.SongListAdapter;
import com.dreamgyf.adapter.viewPager.MainViewPagerAdapter;
import com.dreamgyf.bottomSheetDialog.PlayListBottomSheetDialog;
import com.dreamgyf.broadcastReceiver.PlayerBarBroadcastReceiver;
import com.dreamgyf.entity.Song;
import com.dreamgyf.entity.SongList;
import com.dreamgyf.entity.UserDetail;
import com.dreamgyf.service.CallAPI;
import com.dreamgyf.service.PlayMusicPrepareIntentService;
import com.dreamgyf.service.PlayMusicService;
import com.dreamgyf.util.ImageUtil;
import com.dreamgyf.view.RoundImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    public static Resources RESOURCES;

    public static String PATH;

    public static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static int LOGIN_STATUS = 0;     //0未知，-1未登录，1已登录

    public static String accountId = "-1";

    private List<View> viewList = new ArrayList<>();

    private Toolbar toolbar;

    private ViewPager viewPager;

    private TabLayout tabLayout;

    private DrawerLayout drawerLayout;

    private ActionBarDrawerToggle drawerToggle;

    private NavigationView navigationView;

    private View headerView;

    private RoundImageView headerAvatar;

    private RoundImageView indexAvatar;

    private RecyclerView indexViewPagerTopRecyclerView;

    private View playerBar;

    private ImageView playerBarImageView;

    private TextView playerBarTitleTextView;

    private TextView playerBarSubtitleTextView;

    private ImageView playerBarPlayButton;

    private ImageView playerBarPlayListButton;

    private PlayerBarBroadcastReceiver playerBarBroadcastReceiver;

    private PlayListBottomSheetDialog playListBottomSheetDialog;

    private SongListAdapter createdSongListAdapter;

    private SongListAdapter collectSongListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RESOURCES = getResources();
        PATH = getExternalFilesDir("").getAbsolutePath();

        initToolbar();
        initDrawerToggle();
        initViewPager();
        initTabLayout();
        initSongList();
        initPlayerBar();
        if(!accountId.equals("-1"))
            updateUserInfo();
        //初始化广播接收器，向service发一条广播来获得歌曲信息
        initBroadcastReceiver();


    }

    private void initToolbar()
    {
        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.search :
                Intent intent = new Intent(this,SearchActivity.class);
                startActivity(intent);
                break;
            default :
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViewPager()
    {
        viewPager = findViewById(R.id.main_viewPager);
        viewList.add(getLayoutInflater().inflate(R.layout.viewpager_index,null));
        viewList.add(getLayoutInflater().inflate(R.layout.viewpager_music_recommend,null));
        viewPager.setAdapter(new MainViewPagerAdapter(viewList));
        //初始化音乐列表页面
        initIndexViewPager();
    }

    private void initIndexViewPager()
    {
        //初始化头像
        indexAvatar = viewList.get(0).findViewById(R.id.avatar);
        indexAvatar.setImageDrawable(RESOURCES.getDrawable(R.drawable.default_avatar));
        indexAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        //加载上部的RecyclerView
        List<Map<String,Object>> data = initIndexViewPagerTopRecyclerViewData();
        indexViewPagerTopRecyclerView = viewList.get(0).findViewById(R.id.top_recycler_view);
        indexViewPagerTopRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        indexViewPagerTopRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        indexViewPagerTopRecyclerView.setAdapter(new IndexViewPagerTopAdapter(data));
    }

    private List<Map<String,Object>> initIndexViewPagerTopRecyclerViewData()
    {
        List<Map<String,Object>> data = new ArrayList<>();
        Map<String,Object> map = new HashMap<>();
        map.put("text","本地音乐");
        map.put("firstImage", RESOURCES.getDrawable(R.drawable.local_music_icon));
        data.add(map);
        map = new HashMap<>();
        map.put("text","最近播放");
        map.put("firstImage", RESOURCES.getDrawable(R.drawable.recent_play_icon));
        data.add(map);
        map = new HashMap<>();
        map.put("text","下载管理");
        map.put("firstImage", RESOURCES.getDrawable(R.drawable.download_manage_icon));
        data.add(map);
        map = new HashMap<>();
        map.put("text","我的收藏");
        map.put("firstImage", RESOURCES.getDrawable(R.drawable.my_collect_icon));
        data.add(map);
        return data;
    }

    private void initTabLayout()
    {
        tabLayout = findViewById(R.id.main_tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.icon1);
        tabLayout.getTabAt(1).setIcon(R.drawable.icon2);
        tabLayout.getTabAt(0).getIcon().setAlpha(255);
        tabLayout.getTabAt(1).getIcon().setAlpha(100);
        tabLayout.setSelectedTabIndicatorColor(0);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setAlpha(255);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setAlpha(100);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initSongList(){
        RecyclerView createdSongLists = viewList.get(0).findViewById(R.id.created_songlist);
        createdSongLists.setLayoutManager(new LinearLayoutManager(this));
        createdSongLists.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        createdSongListAdapter = new SongListAdapter();
        createdSongLists.setAdapter(createdSongListAdapter);
        createdSongListAdapter.addOnItemClickListener(new SongListAdapter.OnItemClickListener() {
            @Override
            public void onClick(RecyclerView recyclerView, View view, int position, SongList songList) {
                Intent intent = new Intent(MainActivity.this,SongListActivity.class);
                intent.putExtra("songList",songList);
                startActivity(intent);
            }
        });
        RecyclerView collectSongLists = viewList.get(0).findViewById(R.id.collect_songlist);
        collectSongLists.setLayoutManager(new LinearLayoutManager(this));
        collectSongLists.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        collectSongListAdapter = new SongListAdapter();
        collectSongLists.setAdapter(collectSongListAdapter);
        collectSongListAdapter.addOnItemClickListener(new SongListAdapter.OnItemClickListener() {
            @Override
            public void onClick(RecyclerView recyclerView, View view, int position, SongList songList) {
                Intent intent = new Intent(MainActivity.this,SongListActivity.class);
                intent.putExtra("songList",songList);
                startActivity(intent);
            }
        });
    }

    private void initDrawerToggle()
    {
        drawerLayout = findViewById(R.id.main_drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.openDrawerToggle,R.string.closeDrawerToggle);
        drawerToggle.syncState();
        drawerLayout.addDrawerListener(drawerToggle);
        //初始化抽屉里的控件
        navigationView = findViewById(R.id.main_navigation_view);
        //初始化header
        headerView = navigationView.getHeaderView(0);
        //初始化header背景
        headerView.setBackground(RESOURCES.getDrawable(R.drawable.test));
        //初始化header里的头像
        headerAvatar = headerView.findViewById(R.id.main_drawer_avatar);
        headerAvatar.setImageDrawable(RESOURCES.getDrawable(R.drawable.avatar));

    }

    private void initPlayerBar(){
        initPlayList();
        playerBar = findViewById(R.id.player_bar);
        playerBarImageView = findViewById(R.id.player_bar_image_view);
        playerBarTitleTextView = findViewById(R.id.player_bar_title_text_view);
        playerBarSubtitleTextView = findViewById(R.id.player_bar_subtitle_text_view);
        playerBarPlayButton = findViewById(R.id.player_bar_play_button);
        playerBarPlayListButton = findViewById(R.id.playlist_button);
        playerBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,PlayerActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_up_in,R.anim.no_action);
            }
        });

        playerBarImageView.setImageDrawable(RESOURCES.getDrawable(R.drawable.default_album_pic));
        playerBarTitleTextView.setText("未知");
        playerBarSubtitleTextView.setText("未知");
        playerBarPlayButton.setImageDrawable(RESOURCES.getDrawable(R.drawable.playbar_play_icon));

        playerBarPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayMusicService.PLAY_ACTION);
                LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
            }
        });
        playerBarPlayListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playListBottomSheetDialog.show();
            }
        });
    }

    private void initPlayList(){
        //加载播放列表
        PlayListAdapter playListAdapter = new PlayListAdapter();
        playListAdapter.addOnItemClickListener(new PlayListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View view, int position, Song song) {
                Intent toPlayerActivity = new Intent(MainActivity.this,PlayerActivity.class);
                startActivity(toPlayerActivity);
                overridePendingTransition(R.anim.push_up_in,R.anim.no_action);
                Intent toPrepareIntentService = new Intent(MainActivity.this, PlayMusicPrepareIntentService.class);
                toPrepareIntentService.putExtra("song",song);
                startService(toPrepareIntentService);
            }
        });
        playListBottomSheetDialog = new PlayListBottomSheetDialog(this,playListAdapter);
    }

    private void initBroadcastReceiver(){
        //注册广播接收器
        playerBarBroadcastReceiver = new PlayerBarBroadcastReceiver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PlayMusicService.UPDATE_PLAYER_UI_ACTION);
        intentFilter.addAction(PlayMusicService.UPDATE_PLAY_BUTTON_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(playerBarBroadcastReceiver,intentFilter);
        //初次加载时获取播放信息
        Intent broadcastIntent = new Intent(PlayMusicService.GET_INFO_ACTION);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    private void updateUserInfo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final UserDetail userDetail = CallAPI.get().getUserDetail(accountId);
                    final Bitmap avatarBitmap = ImageUtil.createBitmapFromUrl(userDetail.getProfile().getAvatarUrl());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            indexAvatar.setImageBitmap(avatarBitmap);
                            headerAvatar.setImageBitmap(avatarBitmap);
                            TextView nickname = viewList.get(0).findViewById(R.id.nickname);
                            nickname.setText(userDetail.getProfile().getNickname());
                        }
                    });
                    List<SongList> songLists = CallAPI.get().getSongList(accountId);
                    final List<SongList> createdSongLists = new ArrayList<>();
                    final List<SongList> collectSongLists = new ArrayList<>();
                    for(SongList songList : songLists){
                        if(songList.getCreator().getUserId() == Long.parseLong(accountId))
                            createdSongLists.add(songList);
                        else
                            collectSongLists.add(songList);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            createdSongListAdapter.clear();
                            createdSongListAdapter.add(createdSongLists);
                            createdSongListAdapter.notifyDataSetChanged();
                            collectSongListAdapter.clear();
                            collectSongListAdapter.add(collectSongLists);
                            collectSongListAdapter.notifyDataSetChanged();
                        }
                    });


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Toast.makeText(this,"正在登录",Toast.LENGTH_LONG).show();
        updateUserInfo();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(playerBarBroadcastReceiver);
        super.onDestroy();
        System.exit(0);
    }

}
