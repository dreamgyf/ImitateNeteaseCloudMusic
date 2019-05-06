package com.dreamgyf.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;
import com.dreamgyf.entity.Song;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataManage {

    public static void saveAccountInfo(Context context,String phone, String password){
        SharedPreferences sharedPreferences = context.getSharedPreferences("accountInfo",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("phone",phone);
        editor.putString("password",password);
        editor.apply();
    }

    public static Map<String,String> getAccountInfo(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("accountInfo",Context.MODE_PRIVATE);
        Map<String,String> map = new HashMap<>();
        String phone = sharedPreferences.getString("phone",null);
        String password = sharedPreferences.getString("password",null);
        if(phone != null)
            map.put("phone",phone);
        if(password != null)
            map.put("password",password);
        return map;
    }

    public static void addPlayList(Context context, Song song){
        List<Song> songList = new ArrayList<>();
        songList.add(song);
        addPlayList(context,songList);
    }

    public static void addPlayList(Context context, List<Song> songList){
        SharedPreferences sharedPreferences = context.getSharedPreferences("playList",Context.MODE_PRIVATE);
        int size = sharedPreferences.getAll().size();
        int position = sharedPreferences.getInt("position",-1);
        if(position != -1)
            --size;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for(int i = 0;i < songList.size();i++){
            editor.putString(String.valueOf(size + i),JSON.toJSONString(songList.get(i)));
        }
        editor.apply();
    }

    public static List<Song> getPlayList(Context context){
        List<Song> songList = new ArrayList<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences("playList",Context.MODE_PRIVATE);
        int size = sharedPreferences.getAll().size();
        int position = sharedPreferences.getInt("position",-1);
        if(position != -1)
            --size;
        for(int i = 0;i < size;i++){
            String temp = sharedPreferences.getString(String.valueOf(i),null);
            if(temp != null){
                songList.add(JSON.parseObject(temp,Song.class));
            }
        }
        return songList;
    }

    public static void clearPlayList(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("playList",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public static void setSongPosition(Context context,int position){
        SharedPreferences sharedPreferences = context.getSharedPreferences("playList",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("position",position);
        editor.apply();
    }

    public static int getSongPosition(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("playList",Context.MODE_PRIVATE);
        return sharedPreferences.getInt("position",-1);
    }
}
