package com.dreamgyf.adapter.recyclerView;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dreamgyf.R;
import com.dreamgyf.entity.Song;

import java.util.List;

public class SongInListAdapter extends  RecyclerView.Adapter<SongInListAdapter.SongInListViewHolder> implements View.OnClickListener{

    private RecyclerView recyclerView;

    private OnItemClickListener onItemClickListener = null;

    private List<Song> songList;

    public static class SongInListViewHolder extends RecyclerView.ViewHolder{

        private TextView title;

        private TextView subtitle;

        public SongInListViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.subtitle);
        }
    }

    public SongInListAdapter(List<Song> songList) {
        super();
        this.songList = songList;
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

    @NonNull
    @Override
    public SongInListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.song_recycleview_item,viewGroup,false);
        SongInListViewHolder viewHolder = new SongInListViewHolder(view);
        view.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SongInListViewHolder viewHolder, int i) {
        viewHolder.title.setText(songList.get(i).getName());
        String artists = "";
        for(int j = 0;j < songList.get(i).getAr().size();j++)
        {
            if(j == songList.get(i).getAr().size() - 1)
                artists += songList.get(i).getAr().get(j).getName();
            else
                artists += songList.get(i).getAr().get(j).getName() + "/";
        }
        viewHolder.subtitle.setText(artists + "-" + songList.get(i).getAl().getName());
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public interface OnItemClickListener{
        void onItemClick(RecyclerView recyclerView,View view,int position,Song song);
    }

    public void addOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View v) {
        int position = recyclerView.getChildAdapterPosition(v);
        if(onItemClickListener != null)
        {
            onItemClickListener.onItemClick(recyclerView,v,position,songList.get(position));
        }
    }


}
