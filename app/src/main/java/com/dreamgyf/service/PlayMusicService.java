package com.dreamgyf.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.dreamgyf.entity.SongData;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayMusicService extends Service {

    private ExecutorService executorService;

    private MediaPlayer mediaPlayer;

    private SongData songData;

    private int mode;

    public PlayMusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        executorService = Executors.newFixedThreadPool(5);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(mediaPlayer != null)
            mediaPlayer.reset();
        //默认为网络播放模式
        mode = intent.getIntExtra("mode",1);
        if(mode == 1){
            final int songId = intent.getIntExtra("songId",-1);
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

    public class MyBinder extends Binder {
        public void play(){
            if(mediaPlayer.isPlaying())
                mediaPlayer.pause();
            else
                mediaPlayer.start();
        }
    }
}
