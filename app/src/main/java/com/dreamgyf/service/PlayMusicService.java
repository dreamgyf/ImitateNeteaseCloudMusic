package com.dreamgyf.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.dreamgyf.entity.Song;
import com.dreamgyf.entity.SongData;
import com.kingsoft.media.httpcache.KSYProxyService;

import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayMusicService extends Service {

    private ExecutorService executorService;

    private MediaPlayer mediaPlayer;

    private KSYProxyService ksyProxyService;

    public static List<Song> songList;

    private int songPosition;

    private String path;

    private SongData songData;

    private Song song;

    private File file;

    private String proxyURL;

    private byte[] songPicByte;

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
        executorService = Executors.newFixedThreadPool(10);
        path = getExternalFilesDir("").getAbsolutePath();
        ksyProxyService = new KSYProxyService(this);
        ksyProxyService.startServer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, final int flags, int startId) {
        if(mediaPlayer != null)
            mediaPlayer.reset();
        songData = null;
        proxyURL = null;
        songPicByte = null;
        //默认为网络播放模式
        mode = intent.getIntExtra("mode",1);
        if(mode == 1){
            final int songPosition = intent.getIntExtra("songPosition",-1);
            if (songPosition == -1)
                return super.onStartCommand(intent, flags, startId);
            song = songList.get(songPosition);
            Thread getSongFromNetThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        songData = ResponseProcessing.get().getSong(CallAPI.get().getSong(song.getId())).get(0);
                        file = new File(path + "/songs/" + song.getId() + "." + songData.getType());
                        if(!file.exists()){
                            if(!file.getParentFile().exists()){
                                if(!file.getParentFile().mkdirs())
                                    throw new RuntimeException("create file error");
                            }
                            proxyURL = ksyProxyService.getProxyUrl(songData.getUrl());
                            mediaPlayer.setDataSource(proxyURL);
                            URL url = new URL(proxyURL);
                            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                            if(httpURLConnection.getResponseCode() != 200)
                                throw new RuntimeException("error");
                            final InputStream in = httpURLConnection.getInputStream();
                            executorService.execute(new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try{
                                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                                        int len;
                                        byte[] buffer = new byte[1024];
                                        while ((len = in.read(buffer)) > 0){
                                            fileOutputStream.write(buffer,0,len);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }));
                        } else {
                            mediaPlayer.setDataSource(new FileInputStream(file).getFD());
                        }
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                executorService.execute(new Thread(new Runnable() {
                                    @Override
                                    public void run() {
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
                                        while (songPicByte == null);
                                        broadcastIntent.putExtra("songPicByte",songPicByte);
                                        broadcastIntent.putExtra("duration",mediaPlayer.getDuration());
                                        LocalBroadcastManager.getInstance(PlayMusicService.this).sendBroadcast(broadcastIntent);
                                        mediaPlayer.start();
                                    }
                                }));
                            }
                        });
                        mediaPlayer.prepareAsync();
                        songPicByte = ResponseProcessing.get().songPicByte(song.getId());
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
                broadcastIntent.putExtra("songPicByte",songPicByte);
                broadcastIntent.putExtra("duration",mediaPlayer.getDuration());
                LocalBroadcastManager.getInstance(PlayMusicService.this).sendBroadcast(broadcastIntent);
            }
            if(updateUIProgress == 1){
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        super.onDestroy();
    }
}
