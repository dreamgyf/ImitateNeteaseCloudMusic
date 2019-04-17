package com.dreamgyf.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.dreamgyf.entity.Song;
import com.dreamgyf.entity.SongData;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayMusicService extends Service {

    private ExecutorService executorService;

    private MediaPlayer mediaPlayer;

    private SongData songData;

    private Song song;

    private int mode;

    private MyReceiver myReceiver;

    public final static String PLAY_ACTION = "com.dreamgyf.action.PLAY_ACTION";

    public final static String UPDATE_PLAYER_ACTION = "com.dreamgyf.action.UPDATE_PLAYER_ACTION";

    public PlayMusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        executorService = Executors.newFixedThreadPool(5);
        //绑定广播事件
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PLAY_ACTION);
        registerReceiver(myReceiver,intentFilter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(mediaPlayer != null)
            mediaPlayer.reset();
        //默认为网络播放模式
        mode = intent.getIntExtra("mode",1);
        if(mode == 1){
            song = (Song) intent.getSerializableExtra("song");
            final int songId = song.getId();
            Thread getSongFromNetThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        songData = ResponseProcessing.get().getSong(CallAPI.get().getSong(songId)).get(0);
                        mediaPlayer.setDataSource(songData.getUrl());
                        mediaPlayer.prepareAsync();
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mediaPlayer.start();
                                Intent broadcastIntent = new Intent(UPDATE_PLAYER_ACTION);
                                broadcastIntent.putExtra("status",1);
                                broadcastIntent.putExtra("change",1);
                                broadcastIntent.putExtra("title",song.getName());
                                String artists = "";
                                for(int j = 0;j < song.getArtists().size();j++)
                                {
                                    if(j == song.getArtists().size() - 1)
                                        artists += song.getArtists().get(j).getName();
                                    else
                                        artists += song.getArtists().get(j).getName() + "/";
                                }
                                broadcastIntent.putExtra("subtitle",artists);
                                sendBroadcast(broadcastIntent);
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            executorService.execute(getSongFromNetThread);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            int playOrPause = intent.getIntExtra("playOrPause",-1);
            int getInfo = intent.getIntExtra("getInfo",-1);
            if(playOrPause == 1){
                Intent broadcastIntent = new Intent(UPDATE_PLAYER_ACTION);
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    broadcastIntent.putExtra("status",0);
                }
                else {
                    mediaPlayer.start();
                    broadcastIntent.putExtra("status",1);
                }
                sendBroadcast(broadcastIntent);
            }
            if(getInfo == 1){
                Intent broadcastIntent = new Intent(UPDATE_PLAYER_ACTION);
                if(mediaPlayer.isPlaying()){
                    broadcastIntent.putExtra("status",1);
                }
                else {
                    broadcastIntent.putExtra("status",0);
                }
                broadcastIntent.putExtra("change",1);
                broadcastIntent.putExtra("title",song.getName());
                String artists = "";
                for(int j = 0;j < song.getArtists().size();j++)
                {
                    if(j == song.getArtists().size() - 1)
                        artists += song.getArtists().get(j).getName();
                    else
                        artists += song.getArtists().get(j).getName() + "/";
                }
                broadcastIntent.putExtra("subtitle",artists);
                sendBroadcast(broadcastIntent);
            }
        }
    }
}
