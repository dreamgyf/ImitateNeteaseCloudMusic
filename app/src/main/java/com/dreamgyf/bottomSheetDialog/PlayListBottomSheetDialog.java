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

    private MyReceiver myReceiver;

    public PlayListBottomSheetDialog(@NonNull final Context context, PlayListAdapter playListAdapter) {
        super(context);
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.bottomsheetdialog_playlist,null,false);
        modeImage = view.findViewById(R.id.mode_image);
        modeText = view.findViewById(R.id.mode_text);
        switch (PlayMusicService.MODE){
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
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.getLayoutParams().height = context.getResources().getDisplayMetrics().heightPixels / 2;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context,DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(playListAdapter);
        setContentView(view);



        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PlayMusicService.UPDATE_PLAY_LIST_UI_ACTION);
        intentFilter.addAction(PlayMusicService.UPDATE_MODE_UI_ACTION);
        LocalBroadcastManager.getInstance(context).registerReceiver(myReceiver,intentFilter);

//        for(int i = 0;i < recyclerView.getChildCount();i++) {
//            View view = recyclerView.getChildAt(i);
//            ImageView imageView = view.findViewById(R.id.trumpet);
//            TextView title = view.findViewById(R.id.title);
//            TextView subtitle = view.findViewById(R.id.subtitle);
//            if(PlayMusicPrepareIntentService.songPosition == i){
//                imageView.setVisibility(View.VISIBLE);
//                title.setTextColor(MainActivity.RESOURCES.getColor(R.color.colorNeteaseRed));
//                subtitle.setTextColor(MainActivity.RESOURCES.getColor(R.color.colorNeteaseRed));
//            } else {
//                imageView.setVisibility(View.GONE);
//                title.setTextColor(MainActivity.RESOURCES.getColor(R.color.colorFontBlack));
//                subtitle.setTextColor(MainActivity.RESOURCES.getColor(R.color.infoFont));
//            }
//        }
    }

    public class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //正在播放的歌曲标红
            if(action.equals(PlayMusicService.UPDATE_PLAY_LIST_UI_ACTION)){
                int preSongPosition = intent.getIntExtra("preSongPosition",-1);
                int nextSongPosition = intent.getIntExtra("nextSongPosition",-1);
                if(nextSongPosition != -1 && nextSongPosition < recyclerView.getChildCount()){
                    View nextView = recyclerView.getChildAt(nextSongPosition);
                    ImageView nextImageView = nextView.findViewById(R.id.trumpet);
                    TextView nextTitle = nextView.findViewById(R.id.title);
                    TextView nextSubtitle = nextView.findViewById(R.id.subtitle);
                    nextImageView.setVisibility(View.VISIBLE);
                    nextTitle.setTextColor(MainActivity.RESOURCES.getColor(R.color.colorNeteaseRed));
                    nextSubtitle.setTextColor(MainActivity.RESOURCES.getColor(R.color.colorNeteaseRed));
                    if(preSongPosition != -1 && nextSongPosition < recyclerView.getChildCount()){
                        View preView = recyclerView.getChildAt(preSongPosition);
                        ImageView preImageView = preView.findViewById(R.id.trumpet);
                        TextView preTitle = preView.findViewById(R.id.title);
                        TextView preSubtitle = preView.findViewById(R.id.subtitle);
                        preImageView.setVisibility(View.GONE);
                        preTitle.setTextColor(MainActivity.RESOURCES.getColor(R.color.colorFontBlack));
                        preSubtitle.setTextColor(MainActivity.RESOURCES.getColor(R.color.infoFont));
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

    public void unregisterReceiver(){
        LocalBroadcastManager.getInstance(context).unregisterReceiver(myReceiver);
    }
}
