package com.dreamgyf.adapter.recyclerView;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dreamgyf.R;

import java.util.List;
import java.util.Map;

public class IndexViewPagerTopAdapter extends RecyclerView.Adapter<IndexViewPagerTopAdapter.MusicListViewPagerTopViewHolder> {

    private List<Map<String,Object>> data;

    public static class MusicListViewPagerTopViewHolder extends RecyclerView.ViewHolder{

        TextView textView;
        ImageView firstImageView;
        ImageView secondImageView;

        public MusicListViewPagerTopViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_view_item);
            firstImageView = itemView.findViewById(R.id.first_image_view_item);
            secondImageView = itemView.findViewById(R.id.second_image_view_item);
        }
    }

    public IndexViewPagerTopAdapter(List<Map<String,Object>> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public MusicListViewPagerTopViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.index_viewpager_top_recyclerview_item,viewGroup,false);
        MusicListViewPagerTopViewHolder viewHolder = new MusicListViewPagerTopViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MusicListViewPagerTopViewHolder viewHolder, int i) {
        viewHolder.textView.setText(data.get(i).get("text").toString());
        viewHolder.firstImageView.setImageDrawable((Drawable) data.get(i).get("firstImage"));
        viewHolder.secondImageView.setImageDrawable((Drawable) data.get(i).get("secondImage"));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
