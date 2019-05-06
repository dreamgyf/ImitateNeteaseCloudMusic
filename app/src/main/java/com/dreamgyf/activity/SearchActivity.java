package com.dreamgyf.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dreamgyf.R;
import com.dreamgyf.adapter.recyclerView.PlayListAdapter;
import com.dreamgyf.adapter.recyclerView.SearchSingleResultAdapter;
import com.dreamgyf.adapter.viewPager.SearchSingleViewPagerAdapter;
import com.dreamgyf.bottomSheetDialog.PlayListBottomSheetDialog;
import com.dreamgyf.broadcastReceiver.PlayerBarBroadcastReceiver;
import com.dreamgyf.entity.Song;
import com.dreamgyf.service.PlayMusicPrepareIntentService;
import com.dreamgyf.service.PlayMusicService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchActivity extends AppCompatActivity {

    ExecutorService executorService = Executors.newFixedThreadPool(5);

    private Handler handler = new Handler();

    private Resources resources;

    Intent playMusicService;

    private Toolbar toolbar;

    private TabLayout tabLayout;

    private ViewPager viewPager;

    private List<View> viewList = new ArrayList<>();

    private RecyclerView searchSingleViewPageRecyclerView;

    private SearchSingleResultAdapter searchSingleResultAdapter;

    private View playerBar;

    private ImageView playerBarImageView;

    private TextView playerBarTitleTextView;

    private TextView playerBarSubtitleTextView;

    private ImageView playerBarPlayButton;

    private ImageView playerBarPlayListButton;

    private PlayerBarBroadcastReceiver playerBarBroadcastReceiver;

    private PlayListBottomSheetDialog playListBottomSheetDialog;

    private Intent toPlayerIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        resources = getResources();
        initToolbar();
        initPlayerBar();
        //隐藏标签栏
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setVisibility(View.GONE);
        //初始化广播接收器，向service发一条广播来获得歌曲信息
        initBroadcastReceiver();
    }

    private void initToolbar()
    {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_toolbar,menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.onActionViewExpanded();
        searchView.setQueryHint("搜索");
        SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(R.id.search_src_text);
        searchAutoComplete.setHintTextColor(resources.getColor(R.color.searchViewHintFont));
        searchAutoComplete.setTextColor(resources.getColor(R.color.searchViewFont));
        searchAutoComplete.setTextSize(17);
        View underline = searchView.findViewById(R.id.search_plate);
        underline.setBackground(resources.getDrawable(R.drawable.search_underline_background));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(viewPager == null)
                {
                    initViewPager();
                    tabLayout.setVisibility(View.VISIBLE);
                    initTabLayout();
                }
                searchSingleResultAdapter.clear();
                searchSingleResultAdapter.addSongs(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    void initViewPager(){
        viewPager = findViewById(R.id.view_pager);
        viewList.add(getLayoutInflater().inflate(R.layout.viewpager_search_single,null));
        viewPager.setAdapter(new SearchSingleViewPagerAdapter(viewList));
        //设置
        searchSingleViewPageRecyclerView = viewList.get(0).findViewById(R.id.recycler_view);
        searchSingleViewPageRecyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        searchSingleViewPageRecyclerView.addItemDecoration(new DividerItemDecoration(SearchActivity.this,DividerItemDecoration.VERTICAL));
        searchSingleResultAdapter = new SearchSingleResultAdapter();
        searchSingleResultAdapter.addOnItemClickListener(new SearchSingleResultAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View view, int position, Song song) {
                if(toPlayerIntent == null)
                    toPlayerIntent = new Intent(SearchActivity.this,PlayerActivity.class);
                startActivity(toPlayerIntent);
                overridePendingTransition(R.anim.push_up_in,R.anim.no_action);
                Intent toPrepareIntentService = new Intent(SearchActivity.this, PlayMusicPrepareIntentService.class);
                toPrepareIntentService.putExtra("song",song);
                startService(toPrepareIntentService);
            }
        });
        searchSingleViewPageRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE && !recyclerView.canScrollVertically(1)){
                    searchSingleResultAdapter.addSongs();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        searchSingleViewPageRecyclerView.setAdapter(searchSingleResultAdapter);
    }

    void initTabLayout(){

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("单曲");
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
                Intent intent = new Intent(SearchActivity.this,PlayerActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_up_in,R.anim.no_action);
            }
        });

        playerBarImageView.setImageDrawable(resources.getDrawable(R.drawable.default_album_pic));
        playerBarTitleTextView.setText("未知");
        playerBarSubtitleTextView.setText("未知");
        playerBarPlayButton.setImageDrawable(resources.getDrawable(R.drawable.playbar_play_icon));

        playerBarPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayMusicService.PLAY_ACTION);
                LocalBroadcastManager.getInstance(SearchActivity.this).sendBroadcast(intent);
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
                Intent toPlayerActivity = new Intent(SearchActivity.this,PlayerActivity.class);
                startActivity(toPlayerActivity);
                overridePendingTransition(R.anim.push_up_in,R.anim.no_action);
                Intent toPrepareIntentService = new Intent(SearchActivity.this, PlayMusicPrepareIntentService.class);
                toPrepareIntentService.putExtra("song",song);
                startService(toPrepareIntentService);
            }
        });
        playListBottomSheetDialog = new PlayListBottomSheetDialog(this,playListAdapter);
    }

    private void initBroadcastReceiver(){
        playerBarBroadcastReceiver = new PlayerBarBroadcastReceiver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PlayMusicService.UPDATE_PLAYER_UI_ACTION);
        intentFilter.addAction(PlayMusicService.UPDATE_PLAY_BUTTON_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(playerBarBroadcastReceiver,intentFilter);
        //初次加载时获取播放信息
        Intent broadcastIntent = new Intent(PlayMusicService.GET_INFO_ACTION);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(playerBarBroadcastReceiver);
        playListBottomSheetDialog.unregisterReceiver();
        super.onDestroy();
    }

}
