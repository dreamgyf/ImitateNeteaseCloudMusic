package com.dreamgyf.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.dreamgyf.activity.MainActivity;

import java.io.IOException;

public class PlayMusicService extends Service {

    private MediaPlayer mediaPlayer;

    public static volatile String MODE = "ORDER";

    private MyReceiver myReceiver;

    public final static String PLAY_ACTION = "com.dreamgyf.action.PLAY_ACTION";

    public final static String SET_MODE_ACTION = "com.dreamgyf.action.SET_MODE_ACTION";

    public final static String GET_INFO_ACTION = "com.dreamgyf.action.GET_INFO_ACTION";

    public final static String GET_CURRENT_POSITION_ACTION = "com.dreamgyf.action.GET_CURRENT_POSITION_ACTION";

    public final static String SET_SEEK_ACTION = "com.dreamgyf.action.SET_SEEK_ACTION";

    public final static String UPDATE_PLAY_BUTTON_ACTION = "com.dreamgyf.action.UPDATE_PLAY_BUTTON_ACTION";

    public final static String UPDATE_MODE_UI_ACTION = "com.dreamgyf.action.UPDATE_MODE_UI_ACTION";

    public final static String UPDATE_PLAYER_UI_ACTION = "com.dreamgyf.action.UPDATE_PLAYER_UI_ACTION";

    public final static String UPDATE_CURRENT_POSITION_ACTION = "com.dreamgyf.action.UPDATE_CURRENT_POSITION_ACTION";

    public final static String UPDATE_PLAY_LIST_UI_ACTION = "com.dreamgyf.action.UPDATE_PLAY_LIST_UI_ACTION";

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
    public int onStartCommand(final Intent intent, final int flags, int startId) {

        Notification.Builder builder = new Notification.Builder(this);
        Intent nIntent = new Intent(this, MainActivity.class);
        builder.setContentIntent(PendingIntent.getActivity(this,0,nIntent,0));
        Notification notification = builder.build();
        startForeground(1,notification);

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
                            intentFilter.addAction(SET_MODE_ACTION);
                            intentFilter.addAction(GET_INFO_ACTION);
                            intentFilter.addAction(GET_CURRENT_POSITION_ACTION);
                            intentFilter.addAction(SET_SEEK_ACTION);
                            LocalBroadcastManager.getInstance(PlayMusicService.this).registerReceiver(myReceiver,intentFilter);
                    }
                    Intent sendInfo = new Intent(UPDATE_PLAYER_UI_ACTION);
                    sendInfo.putExtra("title",songName);
                    sendInfo.putExtra("subtitle",artists);
                    sendInfo.putExtra("songPicId",songPicId);
                    sendInfo.putExtra("duration",mediaPlayer.getDuration());
                    LocalBroadcastManager.getInstance(PlayMusicService.this).sendBroadcast(sendInfo);
                    Intent updatePlayButton = new Intent(UPDATE_PLAY_BUTTON_ACTION);
                    updatePlayButton.putExtra("PLAY_STATUS",1);
                    LocalBroadcastManager.getInstance(PlayMusicService.this).sendBroadcast(updatePlayButton);
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
                            else {
                                Intent updatePlayButton = new Intent(UPDATE_PLAY_BUTTON_ACTION);
                                updatePlayButton.putExtra("PLAY_STATUS",0);
                                LocalBroadcastManager.getInstance(PlayMusicService.this).sendBroadcast(updatePlayButton);
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
            String action = intent.getAction();
            //设置播放或暂停
            if(action.equals(PLAY_ACTION)){
                Intent playStatus = new Intent(UPDATE_PLAY_BUTTON_ACTION);
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    playStatus.putExtra("PLAY_STATUS",0);
                }
                else {
                    mediaPlayer.start();
                    playStatus.putExtra("PLAY_STATUS",1);
                }
                LocalBroadcastManager.getInstance(PlayMusicService.this).sendBroadcast(playStatus);
            }
            //设置播放模式
            if(action.equals(SET_MODE_ACTION)){
                MODE = intent.getStringExtra("SET_MODE");
                Intent updateModeUI = new Intent(UPDATE_MODE_UI_ACTION);
                updateModeUI.putExtra("UPDATE_MODE",MODE);
                LocalBroadcastManager.getInstance(PlayMusicService.this).sendBroadcast(updateModeUI);
            }
            //发送音乐信息
            if(action.equals(GET_INFO_ACTION)){
                Intent sendInfo = new Intent(UPDATE_PLAYER_UI_ACTION);
                sendInfo.putExtra("title",songName);
                sendInfo.putExtra("subtitle",artists);
                sendInfo.putExtra("songPicId",songPicId);
                sendInfo.putExtra("duration",mediaPlayer.getDuration());
                LocalBroadcastManager.getInstance(PlayMusicService.this).sendBroadcast(sendInfo);
                Intent updatePlayButton = new Intent(UPDATE_PLAY_BUTTON_ACTION);
                updatePlayButton.putExtra("PLAY_STATUS",1);
                LocalBroadcastManager.getInstance(PlayMusicService.this).sendBroadcast(updatePlayButton);
            }
            //更新进度条
            if(action.equals(GET_CURRENT_POSITION_ACTION)){
                Intent updateCurrentPosition = new Intent(UPDATE_CURRENT_POSITION_ACTION);
                updateCurrentPosition.putExtra("currentPosition",mediaPlayer.getCurrentPosition());
                LocalBroadcastManager.getInstance(PlayMusicService.this).sendBroadcast(updateCurrentPosition);
            }
            //歌曲时间定位
            if(action.equals(SET_SEEK_ACTION)){
                int time = intent.getIntExtra("time",-1);
                mediaPlayer.seekTo(time);
            }
        }
    }

    @Override
    public void onDestroy() {
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;
        stopForeground(true);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        super.onDestroy();
    }

}
