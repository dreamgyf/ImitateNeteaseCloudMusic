package com.dreamgyf.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import java.io.IOException;

public class PlayMusicService extends Service {

    private MediaPlayer mediaPlayer;

    public static volatile String MODE = "ORDER";

    private MyReceiver myReceiver;

    public final static String PLAY_ACTION = "com.dreamgyf.action.PLAY_ACTION";

    public final static String UPDATE_PLAYER_ACTION = "com.dreamgyf.action.UPDATE_PLAYER_ACTION";

    private String songName;

    private String artists;

    private int songPicId;

    public PlayMusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, final int flags, int startId) {
        songName = intent.getStringExtra("songName");
        artists = intent.getStringExtra("artists");
        songPicId =intent.getIntExtra("songPicId",-1);
        String dataSource = intent.getStringExtra("dataSource");
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(dataSource);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    if(myReceiver == null){
                            //绑定广播事件
                            myReceiver = new MyReceiver();
                            IntentFilter intentFilter = new IntentFilter();
                            intentFilter.addAction(PLAY_ACTION);
                            LocalBroadcastManager.getInstance(PlayMusicService.this).registerReceiver(myReceiver,intentFilter);
                    }
                    Intent broadcastIntent = new Intent(UPDATE_PLAYER_ACTION);
                    broadcastIntent.putExtra("status",1);
                    broadcastIntent.putExtra("change",1);
                    broadcastIntent.putExtra("title",songName);
                    broadcastIntent.putExtra("subtitle",artists);
                    broadcastIntent.putExtra("songPicId",songPicId);
                    broadcastIntent.putExtra("duration",mp.getDuration());
                    LocalBroadcastManager.getInstance(PlayMusicService.this).sendBroadcast(broadcastIntent);
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    switch (MODE){
                        case "ORDER":
                            if(PlayMusicPrepareIntentService.songPosition < PlayMusicPrepareIntentService.songList.size() - 1){
                                Intent playNext = new Intent(PlayMusicService.this,PlayMusicPrepareIntentService.class);
                                playNext.putExtra("song",PlayMusicPrepareIntentService.songList.get(PlayMusicPrepareIntentService.songPosition + 1));
                                startService(playNext);
                            }
                            break;
                        case "LIST_LOOP":
                            if(PlayMusicPrepareIntentService.songPosition < PlayMusicPrepareIntentService.songList.size() - 1){
                                Intent playNext = new Intent(PlayMusicService.this,PlayMusicPrepareIntentService.class);
                                playNext.putExtra("song",PlayMusicPrepareIntentService.songList.get(PlayMusicPrepareIntentService.songPosition + 1));
                                startService(playNext);
                            } else {
                                Intent playNext = new Intent(PlayMusicService.this,PlayMusicPrepareIntentService.class);
                                playNext.putExtra("song",PlayMusicPrepareIntentService.songList.get(0));
                                startService(playNext);
                            }
                            break;
                        case "SINGLE_LOOP":
                            mediaPlayer.seekTo(0);
                            mediaPlayer.start();
                            break;
                        case "RANDOM":
                            break;
                    }
                }
            });
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            int playOrPause = intent.getIntExtra("playOrPause",-1);
            int getInfo = intent.getIntExtra("getInfo",-1);
            int updateUIProgress = intent.getIntExtra("updateUIProgress",-1);
            int updateProgress = intent.getIntExtra("updateProgress",-1);
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
                LocalBroadcastManager.getInstance(PlayMusicService.this).sendBroadcast(broadcastIntent);
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
                broadcastIntent.putExtra("title",songName);
                broadcastIntent.putExtra("subtitle",artists);
                broadcastIntent.putExtra("songPicId",songPicId);
                broadcastIntent.putExtra("duration",mediaPlayer.getDuration());
                LocalBroadcastManager.getInstance(PlayMusicService.this).sendBroadcast(broadcastIntent);
            }
            if(updateUIProgress == 1 && mediaPlayer.isPlaying()){
                int currentPosition = mediaPlayer.getCurrentPosition();
                Intent broadcastIntent = new Intent(UPDATE_PLAYER_ACTION);
                broadcastIntent.putExtra("currentPosition",currentPosition);
                LocalBroadcastManager.getInstance(PlayMusicService.this).sendBroadcast(broadcastIntent);
            }
            if(updateProgress != -1){
                mediaPlayer.seekTo(updateProgress);
            }
        }
    }

    @Override
    public void onDestroy() {
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        super.onDestroy();
    }

}
