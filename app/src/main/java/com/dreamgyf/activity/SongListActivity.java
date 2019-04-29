package com.dreamgyf.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dreamgyf.R;
import com.dreamgyf.adapter.recyclerView.PlayListAdapter;
import com.dreamgyf.adapter.recyclerView.SongInListAdapter;
import com.dreamgyf.bottomSheetDialog.PlayListBottomSheetDialog;
import com.dreamgyf.broadcastReceiver.PlayerBarBroadcastReceiver;
import com.dreamgyf.entity.Song;
import com.dreamgyf.entity.SongList;
import com.dreamgyf.service.CallAPI;
import com.dreamgyf.service.PlayMusicPrepareIntentService;
import com.dreamgyf.service.PlayMusicService;

import java.io.IOException;
import java.util.List;

public class SongListActivity extends AppCompatActivity {

    private SongList songList;

    private PlayerBarBroadcastReceiver playerBarBroadcastReceiver;

    private PlayListBottomSheetDialog playListBottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
        songList = (SongList) getIntent().getSerializableExtra("songList");
        initToolbar();
        initInfo();
        initPlayerBar();
        initBroadcastReceiver();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar()
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initInfo(){
        MainActivity.executorService.execute(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<Song> songs = CallAPI.get().getSongInSongList(String.valueOf(songList.getId()));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RecyclerView recyclerView = SongListActivity.this.findViewById(R.id.song_recyclerview);
                            recyclerView.setLayoutManager(new LinearLayoutManager(SongListActivity.this));
                            recyclerView.addItemDecoration(new DividerItemDecoration(SongListActivity.this,DividerItemDecoration.VERTICAL));
                            SongInListAdapter songInListAdapter = new SongInListAdapter(songs);
                            songInListAdapter.addOnItemClickListener(new SongInListAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(RecyclerView recyclerView, View view, int position, Song song) {
                                    Intent toPrepareIntentService = new Intent(SongListActivity.this, PlayMusicPrepareIntentService.class);
                                    toPrepareIntentService.putExtra("song",song);
                                    startService(toPrepareIntentService);
                                }
                            });
                            recyclerView.setAdapter(songInListAdapter);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    private void initPlayerBar(){
        initPlayList();
        View playerBar = findViewById(R.id.player_bar);
        ImageView playerBarImageView = findViewById(R.id.player_bar_image_view);
        TextView playerBarTitleTextView = findViewById(R.id.player_bar_title_text_view);
        TextView playerBarSubtitleTextView = findViewById(R.id.player_bar_subtitle_text_view);
        ImageView playerBarPlayButton = findViewById(R.id.player_bar_play_button);
        ImageView playerBarPlayListButton = findViewById(R.id.playlist_button);
        playerBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SongListActivity.this,PlayerActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_up_in,R.anim.no_action);
            }
        });

        playerBarImageView.setImageDrawable(MainActivity.RESOURCES.getDrawable(R.drawable.default_album_pic));
        playerBarTitleTextView.setText("未知");
        playerBarSubtitleTextView.setText("未知");
        playerBarPlayButton.setImageDrawable(MainActivity.RESOURCES.getDrawable(R.drawable.playbar_play_icon));

        playerBarPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayMusicService.PLAY_ACTION);
                LocalBroadcastManager.getInstance(SongListActivity.this).sendBroadcast(intent);
            }
        });
        playerBarPlayListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playListBottomSheetDialog.show();
            }
        });
    }

    private void initPlayList(){
        //加载播放列表
        PlayListAdapter playListAdapter = new PlayListAdapter();
        playListAdapter.addOnItemClickListener(new PlayListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View view, int position, Song song) {
                Intent toPlayerActivity = new Intent(SongListActivity.this,PlayerActivity.class);
                startActivity(toPlayerActivity);
                overridePendingTransition(R.anim.push_up_in,R.anim.no_action);
                Intent toPrepareIntentService = new Intent(SongListActivity.this, PlayMusicPrepareIntentService.class);
                toPrepareIntentService.putExtra("song",song);
                startService(toPrepareIntentService);
            }
        });
        playListBottomSheetDialog = new PlayListBottomSheetDialog(this,playListAdapter);
    }

    private void initBroadcastReceiver(){
        //注册广播接收器
        playerBarBroadcastReceiver = new PlayerBarBroadcastReceiver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PlayMusicService.UPDATE_PLAYER_UI_ACTION);
        intentFilter.addAction(PlayMusicService.UPDATE_PLAY_BUTTON_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(playerBarBroadcastReceiver,intentFilter);
        //初次加载时获取播放信息
        Intent broadcastIntent = new Intent(PlayMusicService.GET_INFO_ACTION);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(playerBarBroadcastReceiver);
        super.onDestroy();
    }
}
