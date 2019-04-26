package com.dreamgyf.adapter.viewPager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.dreamgyf.service.PlayMusicPrepareIntentService;
import com.dreamgyf.view.DiscViewPagerItem;

import java.util.ArrayList;
import java.util.List;

public class DiscViewPagerAdapter extends PagerAdapter {

    private Context context;

    private ViewPager parent;

    private List<DiscViewPagerItem> viewList;

    public DiscViewPagerAdapter(Context context,ViewPager parent) {
        super();
        this.context = context;
        this.parent = parent;
        viewList = new ArrayList<>();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        DiscViewPagerItem view = new DiscViewPagerItem(context,parent,position);
        viewList.add(view);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(viewList.get(position));
    }

    @Override
    public int getCount() {
        return PlayMusicPrepareIntentService.songList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
