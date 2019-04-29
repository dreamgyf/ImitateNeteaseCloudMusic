package com.dreamgyf.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.dreamgyf.entity.Song;
import com.dreamgyf.entity.SongData;
import com.dreamgyf.util.FileUtil;
import com.kingsoft.media.httpcache.KSYProxyService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PlayMusicPrepareIntentService extends IntentService {

    private KSYProxyService ksyProxyService;

    public static volatile List<Song> songList = new ArrayList<>();

    public static volatile List<Boolean> songPicReady = new ArrayList<>();

    public static volatile int songPosition;

    private String path;    //apk包路径

    public PlayMusicPrepareIntentService() {
        super("");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ksyProxyService = new KSYProxyService(this);
        ksyProxyService.startServer();
        path = getExternalFilesDir("").getAbsolutePath();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Intent broadcast = new Intent(PlayMusicService.UPDATE_PLAY_LIST_UI_ACTION);
        if(songPosition >= 0){
            broadcast.putExtra("previousSongPosition",songPosition);
        } else {
            broadcast.putExtra("previousSongPosition",0);
        }
        Song song = (Song) intent.getSerializableExtra("song");
        //加入播放列表
        for(int i = 0;i < songList.size();i++){
            if(songList.get(i).getId() == song.getId()){
                if(songPosition == i)
                    return;
                songPosition = i;
                break;
            }
            if(i == songList.size() - 1){
                songList.add(song);
                songPicReady.add(false);
                songPosition = i + 1;
                break;
            }
        }
        if(songList.isEmpty()){
            songList.add(song);
            songPicReady.add(false);
            songPosition = 0;
        }
        broadcast.putExtra("nextSongPosition",songPosition);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
        String url = null;
        byte[] songPicByte = null;
        try {
            final String filePath = path + "/song/" + song.getId() + ".mp3";
            String picPath = path + "/album_pic/" + song.getAl().getPic() + ".jpg";
            File picFile = new File(picPath);
            if(!picFile.exists()) {
                if (!picFile.getParentFile().exists()) {
                    if (!picFile.getParentFile().mkdirs())
                        throw new RuntimeException("create file error");
                }
                InputStream picStream = FileUtil.getInputStreamFromUrl(song.getAl().getPicUrl());
                writeFile(picStream,picFile);
            }
            songPicReady.set(songPosition,true);
            File file = new File(filePath);
            if(!file.exists()){
                if(!file.getParentFile().exists()){
                    if(!file.getParentFile().mkdirs())
                        throw new RuntimeException("create file error");
                }
                SongData songData = CallAPI.get().getSongData(String.valueOf(song.getId())).get(0);
                url = ksyProxyService.getProxyUrl(songData.getUrl());
                final InputStream in = getSongStream(url);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        writeFile(in,filePath);
                    }
                }).start();
            } else {
                url = file.toURI().toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent toPlayMusicService = new Intent(this,PlayMusicService.class);
        toPlayMusicService.putExtra("songName",song.getName());
        String artists = "";
        for(int j = 0;j < songList.get(songPosition).getAr().size();j++)
        {
            if(j == songList.get(songPosition).getAr().size() - 1)
                artists += songList.get(songPosition).getAr().get(j).getName();
            else
                artists += songList.get(songPosition).getAr().get(j).getName() + "/";
        }
        toPlayMusicService.putExtra("artists",artists);
        toPlayMusicService.putExtra("songPicId",song.getAl().getPic());
        toPlayMusicService.putExtra("dataSource",url);
        toPlayMusicService.putExtra("songPosition",songPosition);
        startService(toPlayMusicService);
    }

    private InputStream getSongStream(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
        if(httpURLConnection.getResponseCode() != 200)
            throw new RuntimeException("error");
        return httpURLConnection.getInputStream();
    }

    private void writeFile(InputStream in,File file){
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

    private void writeFile(InputStream in,String filePath){
        try{
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            int len;
            byte[] buffer = new byte[1024];
            while ((len = in.read(buffer)) > 0){
                fileOutputStream.write(buffer,0,len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
