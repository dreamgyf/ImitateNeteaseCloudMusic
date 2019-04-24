package com.dreamgyf.bottomSheetDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dreamgyf.R;
import com.dreamgyf.activity.MainActivity;
import com.dreamgyf.adapter.recyclerView.PlayListAdapter;
import com.dreamgyf.service.PlayMusicService;

public class PlayListBottomSheetDialog extends BottomSheetDialog {

    private RecyclerView recyclerView;

    private View view;

    private Context context;

    private ImageView modeImage;

    private TextView modeText;

    public PlayListBottomSheetDialog(@NonNull final Context context, PlayListAdapter playListAdapter) {
        super(context);
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.bottomsheetdialog_playlist,null,false);
        modeImage = view.findViewById(R.id.mode_image);
        modeText = view.findViewById(R.id.mode_text);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.getLayoutParams().height = context.getResources().getDisplayMetrics().heightPixels / 2;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context,DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(playListAdapter);
        setContentView(view);



        MyReceiver myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PlayMusicService.UPDATE_PLAY_LIST_UI_ACTION);
        intentFilter.addAction(PlayMusicService.UPDATE_MODE_UI_ACTION);
        LocalBroadcastManager.getInstance(context).registerReceiver(myReceiver,intentFilter);
    }

    public class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //正在播放的歌曲标红
            if(action.equals(PlayMusicService.UPDATE_PLAY_LIST_UI_ACTION)){
                int previousSongPosition = intent.getIntExtra("previousSongPosition",-1);
                int nextSongPosition = intent.getIntExtra("nextSongPosition",-1);
                if(previousSongPosition != -1 && nextSongPosition != -1){
                    if(previousSongPosition < recyclerView.getChildCount()){
                        View previousView = recyclerView.getChildAt(previousSongPosition);
                        ImageView previousImageView = previousView.findViewById(R.id.trumpet);
                        TextView previousTitle = previousView.findViewById(R.id.title);
                        TextView previousSubtitle = previousView.findViewById(R.id.subtitle);
                        previousImageView.setVisibility(View.GONE);
                        previousTitle.setTextColor(MainActivity.resources.getColor(R.color.colorFontBlack));
                        previousSubtitle.setTextColor(MainActivity.resources.getColor(R.color.infoFont));
                    }
                    if(nextSongPosition < recyclerView.getChildCount()){
                        View nextView = recyclerView.getChildAt(nextSongPosition);
                        ImageView nextImageView = nextView.findViewById(R.id.trumpet);
                        TextView nextTitle = nextView.findViewById(R.id.title);
                        TextView nextSubtitle = nextView.findViewById(R.id.subtitle);
                        nextImageView.setVisibility(View.VISIBLE);
                        nextTitle.setTextColor(MainActivity.resources.getColor(R.color.colorNeteaseRed));
                        nextSubtitle.setTextColor(MainActivity.resources.getColor(R.color.colorNeteaseRed));
                    }
                }
            }
            //更新模式UI
            if(action.equals(PlayMusicService.UPDATE_MODE_UI_ACTION)){
                String updateMode = intent.getStringExtra("UPDATE_MODE");
                switch (updateMode){
                    case "ORDER":
                        modeText.setText("顺序播放");
                        break;
                    case "LIST_LOOP":
                        modeText.setText("列表循环");
                        break;
                    case "SINGLE_LOOP":
                        modeText.setText("单曲循环");
                        break;
                    case "RANDOM":
                        modeText.setText("随机播放");
                        break;
                }
            }
        }
    }
}
