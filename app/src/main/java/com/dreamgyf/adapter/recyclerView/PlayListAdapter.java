package com.dreamgyf.adapter.recyclerView;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dreamgyf.R;
import com.dreamgyf.activity.MainActivity;
import com.dreamgyf.entity.Song;
import com.dreamgyf.service.PlayMusicPrepareIntentService;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.PlayListViewHolder> implements View.OnClickListener {

    private RecyclerView recyclerView;

    private OnItemClickListener onItemClickListener = null;

    public class PlayListViewHolder extends RecyclerView.ViewHolder {

        private ImageView trumpet;

        private TextView title;

        private TextView subtitle;

        public PlayListViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.subtitle);
            trumpet = itemView.findViewById(R.id.trumpet);
        }
    }

    @NonNull
    @Override
    public PlayListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.playlist_recyclerview_item,viewGroup,false);
        PlayListViewHolder playListViewHolder = new PlayListViewHolder(view);
        view.setOnClickListener(this);
        return playListViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlayListViewHolder playListViewHolder, int i) {
        playListViewHolder.trumpet.setVisibility(View.GONE);
        playListViewHolder.title.setText(PlayMusicPrepareIntentService.songList.get(i).getName());
        String artists = "";
        for(int j = 0;j < PlayMusicPrepareIntentService.songList.get(i).getArtists().size();j++)
        {
            if(j == PlayMusicPrepareIntentService.songList.get(i).getArtists().size() - 1)
                artists += PlayMusicPrepareIntentService.songList.get(i).getArtists().get(j).getName();
            else
                artists += PlayMusicPrepareIntentService.songList.get(i).getArtists().get(j).getName() + "/";
        }
        playListViewHolder.subtitle.setText(" - " + artists);
        if(PlayMusicPrepareIntentService.songPosition == i){
            playListViewHolder.trumpet.setVisibility(View.VISIBLE);
            playListViewHolder.title.setTextColor(MainActivity.resources.getColor(R.color.colorNeteaseRed));
            playListViewHolder.subtitle.setTextColor(MainActivity.resources.getColor(R.color.colorNeteaseRed));
        }
    }

    @Override
    public int getItemCount() {
        return PlayMusicPrepareIntentService.songList.size();
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

    public interface OnItemClickListener{
        void onItemClick(RecyclerView recyclerView, View view, int position, Song song);
    }

    public void addOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View v) {
        int position = recyclerView.getChildAdapterPosition(v);
        if(onItemClickListener != null){
            onItemClickListener.onItemClick(recyclerView,v,position,PlayMusicPrepareIntentService.songList.get(position));
        }
    }
}