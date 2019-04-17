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

public class SearchSingleResultAdapter extends RecyclerView.Adapter<SearchSingleResultAdapter.SearchResultViewHolder> implements View.OnClickListener {

    private List<Song> songs;

    private RecyclerView recyclerView;

    private OnItemClickListener onItemClickListener;

    public static class SearchResultViewHolder extends RecyclerView.ViewHolder{

        private TextView nameTextView;

        private TextView infoTextView;

        public SearchResultViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name_textView);
            infoTextView = itemView.findViewById(R.id.info_textView);
        }
    }

    public SearchSingleResultAdapter(List<Song> songs) {
        this.songs = songs;
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_single_viewpage_recycleview_item,viewGroup,false);
        SearchResultViewHolder viewHolder = new SearchResultViewHolder(view);
        view.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder viewHolder, int i) {
        viewHolder.nameTextView.setText(songs.get(i).getName());
        String artists = "";
        for(int j = 0;j < songs.get(i).getArtists().size();j++)
        {
            if(j == songs.get(i).getArtists().size() - 1)
                artists += songs.get(i).getArtists().get(j).getName();
            else
                artists += songs.get(i).getArtists().get(j).getName() + "/";
        }
        viewHolder.infoTextView.setText(artists + "-" + songs.get(i).getAlbum().getName());
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
}
