package com.dreamgyf.adapter.recyclerView;

import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dreamgyf.R;
import com.dreamgyf.activity.MainActivity;
import com.dreamgyf.entity.SongList;
import com.dreamgyf.util.ImageUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.SongListViewHolder> implements View.OnClickListener {

    private List<SongList> songLists = new ArrayList<>();

    private Handler handler = new Handler();

    private RecyclerView recyclerView;

    private OnItemClickListener onItemClickListener = null;

    public static class SongListViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        private TextView name;

        private TextView count;

        public SongListViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            count = itemView.findViewById(R.id.count);
        }
    }

    @NonNull
    @Override
    public SongListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.songlist_recyclerview_item,viewGroup,false);
        SongListViewHolder viewHolder = new SongListViewHolder(view);
        view.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final SongListViewHolder songListViewHolder, int i) {
        final int position = i;
        MainActivity.executorService.execute(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Bitmap bitmap = ImageUtil.createBitmapFromUrl(songLists.get(position).getCoverImgUrl());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            songListViewHolder.imageView.setImageBitmap(bitmap);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }));
        songListViewHolder.name.setText(songLists.get(i).getName());
        songListViewHolder.count.setText(songLists.get(i).getTrackCount() + "首");
    }

    @Override
    public int getItemCount() {
        return songLists.size();
    }

    public interface OnItemClickListener{
        void onClick(RecyclerView recyclerView,View view,int position,SongList songList);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }

    public void addOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View v) {
        int position = recyclerView.getChildAdapterPosition(v);
        if(onItemClickListener != null){
            onItemClickListener.onClick(recyclerView,v,position,songLists.get(position));
        }
    }

    public void add(SongList songList){
        songLists.add(songList);
    }

    public void add(List<SongList> songLists){
        this.songLists.addAll(songLists);
    }

    public void clear(){
        songLists.clear();
    }
}
