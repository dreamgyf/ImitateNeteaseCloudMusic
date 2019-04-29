package com.dreamgyf.adapter.recyclerView;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dreamgyf.R;
import com.dreamgyf.activity.MainActivity;
import com.dreamgyf.entity.Song;
import com.dreamgyf.service.CallAPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchSingleResultAdapter extends RecyclerView.Adapter<SearchSingleResultAdapter.SearchResultViewHolder> implements View.OnClickListener {

    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    private Handler handler = new Handler();

    private String keywords;

    private List<Song> songs = new ArrayList<>();

    private RecyclerView recyclerView;

    private OnItemClickListener onItemClickListener = null;

    public static class SearchResultViewHolder extends RecyclerView.ViewHolder{

        private TextView title;

        private TextView subtitle;

        public SearchResultViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.subtitle);
        }
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.song_recycleview_item,viewGroup,false);
        SearchResultViewHolder viewHolder = new SearchResultViewHolder(view);
        view.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder viewHolder, int i) {
        viewHolder.title.setText(songs.get(i).getName());
        String artists = "";
        for(int j = 0;j < songs.get(i).getAr().size();j++)
        {
            if(j == songs.get(i).getAr().size() - 1)
                artists += songs.get(i).getAr().get(j).getName();
            else
                artists += songs.get(i).getAr().get(j).getName() + "/";
        }
        viewHolder.subtitle.setText(artists + "-" + songs.get(i).getAl().getName());
    }

    @Override
    public int getItemCount() {
        return songs.size();
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
            onItemClickListener.onItemClick(recyclerView,v,position,songs.get(position));
        }
    }

    public void addSongs(String keywords){
        this.keywords = keywords;
        addSongs();
    }

    public void addSongs(){
        if(keywords == null)
            throw new RuntimeException("no keywords");
        MainActivity.executorService.execute(new Thread(new Runnable() {
            @Override
            public void run() {
                final String response;
                try {
                    final List<Song> songList = CallAPI.get().search(keywords,1,songs.size());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            songs.addAll(songList);
                            notifyDataSetChanged();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    public void clear(){
        songs.clear();
    }
}
