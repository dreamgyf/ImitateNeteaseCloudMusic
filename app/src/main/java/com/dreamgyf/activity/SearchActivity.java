package com.dreamgyf.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
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
import android.widget.Toast;

import com.dreamgyf.R;
import com.dreamgyf.adapter.recyclerView.SearchSingleResultAdapter;
import com.dreamgyf.adapter.viewPager.SearchSingleViewPagerAdapter;
import com.dreamgyf.broadcastReceiver.PlayerBarBroadcastReceiver;
import com.dreamgyf.entity.Song;
import com.dreamgyf.service.CallAPI;
import com.dreamgyf.service.PlayMusicService;
import com.dreamgyf.service.ResponseProcessing;

import org.json.JSONException;

import java.io.IOException;
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

    private View playerBar;

    private ImageView playerBarImageView;

    private TextView playerBarTitleTextView;

    private TextView playerBarSubtitleTextView;

    private ImageView playerBarPlayButton;

    private PlayerBarBroadcastReceiver playerBarBroadcastReceiver;

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

        playMusicService = new Intent(this, PlayMusicService.class);

        playerBarBroadcastReceiver = new PlayerBarBroadcastReceiver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PlayMusicService.UPDATE_PLAYER_ACTION);
        registerReceiver(playerBarBroadcastReceiver,intentFilter);
        Intent broadcastIntent = new Intent(PlayMusicService.PLAY_ACTION);
        broadcastIntent.putExtra("getInfo",1);
        sendBroadcast(broadcastIntent);
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
                final String keywords = s;
                Thread thread = new Thread(new Runnable(){
                    @Override
                    public void run() {
                        try {
                            final String response = CallAPI.get().search(keywords);
                            final List<Song> songs = ResponseProcessing.get().search(response);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if(viewPager == null)
                                    {
                                        initViewPager();
                                        tabLayout.setVisibility(View.VISIBLE);
                                        initTabLayout();
                                    }
                                    SearchSingleResultAdapter adapter = new SearchSingleResultAdapter(songs);
                                    adapter.addOnItemClickListener(new SearchSingleResultAdapter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(RecyclerView recyclerView, View view, int position, Song song) {
                                            Toast.makeText(SearchActivity.this,song.getName(),Toast.LENGTH_SHORT).show();
                                            playMusicService.putExtra("song",song);
                                            startService(playMusicService);
                                        }
                                    });
                                    searchSingleViewPageRecyclerView.setAdapter(adapter);

                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                executorService.execute(thread);
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
    }

    void initTabLayout(){

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("单曲");
    }

    private void initPlayerBar(){
        playerBar = findViewById(R.id.player_bar);
        playerBarImageView = findViewById(R.id.player_bar_image_view);
        playerBarTitleTextView = findViewById(R.id.player_bar_title_text_view);
        playerBarSubtitleTextView = findViewById(R.id.player_bar_subtitle_text_view);
        playerBarPlayButton = findViewById(R.id.player_bar_play_button);

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
                intent.putExtra("playOrPause",1);
                sendBroadcast(intent);
            }
        });
    }
}
