package com.dreamgyf.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dreamgyf.R;
import com.dreamgyf.adapter.viewPager.MainViewPagerAdapter;
import com.dreamgyf.adapter.recyclerView.MusicListViewPagerTopAdapter;
import com.dreamgyf.view.RoundImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Resources resources;

    private List<View> viewList = new ArrayList<>();

    private Toolbar toolbar;

    private ViewPager viewPager;

    private TabLayout tabLayout;

    private DrawerLayout drawerLayout;

    private ActionBarDrawerToggle drawerToggle;

    private NavigationView navigationView;

    private View headerView;

    private RoundImageView headerAvatar;

    private RoundImageView musicListAvatar;

    private RecyclerView musicListViewPagerTopRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resources = getResources();

        initToolbar();
        initDrawerToggle();
        initViewPager();
        initTabLayout();

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
        viewList.add(getLayoutInflater().inflate(R.layout.viewpager_music_list,null));
        viewList.add(getLayoutInflater().inflate(R.layout.viewpager_music_recommend,null));
        viewPager.setAdapter(new MainViewPagerAdapter(viewList));
        //初始化音乐列表页面
        initMusicListViewPager();
    }

    private void initMusicListViewPager()
    {
        //初始化头像
        musicListAvatar = viewList.get(0).findViewById(R.id.avatar);
        //设置默认头像
        musicListAvatar.setImageDrawable(resources.getDrawable(R.drawable.default_avatar));
        //加载上部的RecyclerView
        List<Map<String,Object>> data = initMusicListViewPagerTopRecyclerViewData();
        musicListViewPagerTopRecyclerView = viewList.get(0).findViewById(R.id.top_recycler_view);
        RecyclerView.LayoutManager musicListViewPagerTopRecyclerViewLayoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        musicListViewPagerTopRecyclerView.setLayoutManager(musicListViewPagerTopRecyclerViewLayoutManager);
        musicListViewPagerTopRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        musicListViewPagerTopRecyclerView.setAdapter(new MusicListViewPagerTopAdapter(data));
    }

    private List<Map<String,Object>> initMusicListViewPagerTopRecyclerViewData()
    {
        List<Map<String,Object>> data = new ArrayList<>();
        Map<String,Object> map = new HashMap<>();
        map.put("text","本地音乐");
        map.put("firstImage",resources.getDrawable(R.drawable.local_music_icon));
        data.add(map);
        map = new HashMap<>();
        map.put("text","最近播放");
        map.put("firstImage",resources.getDrawable(R.drawable.recent_play_icon));
        data.add(map);
        map = new HashMap<>();
        map.put("text","下载管理");
        map.put("firstImage",resources.getDrawable(R.drawable.download_manage_icon));
        data.add(map);
        map = new HashMap<>();
        map.put("text","我的收藏");
        map.put("firstImage",resources.getDrawable(R.drawable.my_collect_icon));
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
        headerView.setBackground(resources.getDrawable(R.drawable.test));
        //初始化header里的头像
        headerAvatar = headerView.findViewById(R.id.main_drawer_avatar);
        headerAvatar.setImageDrawable(resources.getDrawable(R.drawable.avatar));

    }


}
