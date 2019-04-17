package com.dreamgyf.activity;

import android.content.Intent;
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
import android.widget.Toast;

import com.dreamgyf.R;
import com.dreamgyf.adapter.recyclerView.SearchSingleResultAdapter;
import com.dreamgyf.adapter.viewPager.SearchSingleViewPagerAdapter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        resources = getResources();
        initToolbar();
        //隐藏标签栏
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setVisibility(View.GONE);

        playMusicService = new Intent(this, PlayMusicService.class);

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
                                            playMusicService.putExtra("songId",song.getId());
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
}
