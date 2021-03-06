package com.dreamgyf.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.dreamgyf.entity.Song;
import com.dreamgyf.entity.SongData;
import com.dreamgyf.entity.SongList;
import com.dreamgyf.entity.UserDetail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class CallAPI {

    public final static int LIMIT = 20;

    private static SingleCallAPI singleCallAPI = new SingleCallAPI();

    public static SingleCallAPI get()
    {
        return singleCallAPI;
    }

    public static class SingleCallAPI{

        private static String DOMAIN = "http://47.100.255.133:8083";

        private OutputStream read(InputStream in) throws IOException {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while((len = in.read(buffer)) > 0)
            {
                out.write(buffer,0,len);
            }
            return out;
        }

        public List<Song> search(String keywords,int type,int offset) throws IOException {
            URL url = null;
            switch(type)
            {
                case 1 :
                    url = new URL(DOMAIN + "/search?type=1&keywords=" + keywords + "&limit=" + LIMIT + "&offset=" + offset);
                    break;
            }

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            if(httpURLConnection.getResponseCode() != 200)
                throw new RuntimeException(httpURLConnection.getResponseMessage());
            InputStream in = httpURLConnection.getInputStream();
            String res = read(in).toString();
            httpURLConnection.disconnect();
            JSONObject jsonObject = JSON.parseObject(res);
            JSONObject resultJson = jsonObject.getJSONObject("result");
            JSONArray songsJson = resultJson.getJSONArray("songs");
            String ids = "";
            for(int i = 0;i < songsJson.size();i++){
                if(i == songsJson.size() - 1){
                    ids += songsJson.getJSONObject(i).getString("id");
                    break;
                }
                ids += songsJson.getJSONObject(i).getString("id") + ",";
            }
            return songDetail(ids);
        }

        public List<SongData> getSongData(String id) throws IOException {
            URL url = new URL(DOMAIN + "/song/url?id=" + id);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            if(httpURLConnection.getResponseCode() != 200)
                throw new RuntimeException(httpURLConnection.getResponseMessage());
            InputStream in = httpURLConnection.getInputStream();
            String res = read(in).toString();
            httpURLConnection.disconnect();
            JSONObject jsonObject = JSON.parseObject(res);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            return JSON.parseObject(jsonArray.toJSONString(),new TypeReference<List<SongData>>(){});
        }

        public List<Song> songDetail(String ids) throws IOException {
            URL url = new URL(DOMAIN + "/song/detail?ids=" + ids);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            if(httpURLConnection.getResponseCode() != 200)
                throw new RuntimeException(httpURLConnection.getResponseMessage());
            InputStream in = httpURLConnection.getInputStream();
            String res = read(in).toString();
            httpURLConnection.disconnect();
            JSONObject jsonObject = JSON.parseObject(res);
            JSONArray songsJson = jsonObject.getJSONArray("songs");
            return JSON.parseObject(songsJson.toJSONString(),new TypeReference<List<Song>>(){});
        }

        public String signIn(String phone,String password) throws IOException {
            URL url = new URL(DOMAIN + "/login/cellphone?phone=" + phone + "&password=" + password);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            if(httpURLConnection.getResponseCode() != 200)
                return String.valueOf(httpURLConnection.getResponseCode());
            InputStream in = httpURLConnection.getInputStream();
            String res = read(in).toString();
            httpURLConnection.disconnect();
            JSONObject jsonObject = JSON.parseObject(res);
            JSONObject accountJson = jsonObject.getJSONObject("account");
            return accountJson.getString("id");
        }

        public UserDetail getUserDetail(String uid) throws IOException {
            URL url = new URL(DOMAIN + "/user/detail?uid=" + uid);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            if(httpURLConnection.getResponseCode() != 200)
                throw new RuntimeException(httpURLConnection.getResponseMessage());
            InputStream in = httpURLConnection.getInputStream();
            String res = read(in).toString();
            httpURLConnection.disconnect();
            return JSON.parseObject(res, UserDetail.class);
        }

        public List<SongList> getSongList(String uid) throws IOException {
            URL url = new URL(DOMAIN + "/user/playlist?uid=" + uid);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            if(httpURLConnection.getResponseCode() != 200)
                throw new RuntimeException(httpURLConnection.getResponseMessage());
            InputStream in = httpURLConnection.getInputStream();
            String res = read(in).toString();
            httpURLConnection.disconnect();
            JSONObject jsonObject = JSON.parseObject(res);
            JSONArray songListJson = jsonObject.getJSONArray("playlist");
            List<SongList> songLists = JSON.parseObject(songListJson.toJSONString(),new TypeReference<List<SongList>>(){});
            return songLists;
        }

        public List<Song> getSongInSongList(String id) throws IOException {
            URL url = new URL(DOMAIN + "/playlist/detail?id=" + id);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            if(httpURLConnection.getResponseCode() != 200)
                throw new RuntimeException(httpURLConnection.getResponseMessage());
            InputStream in = httpURLConnection.getInputStream();
            String res = read(in).toString();
            httpURLConnection.disconnect();
            JSONObject jsonObject = JSON.parseObject(res);
            JSONObject playListJson = jsonObject.getJSONObject("playlist");
            JSONArray trackIdsJson = playListJson.getJSONArray("trackIds");
            String ids = "";
            for(int i = 0;i < trackIdsJson.size();i++){
                if(i == trackIdsJson.size() - 1){
                    ids += trackIdsJson.getJSONObject(i).getString("id");
                    break;
                }
                ids += trackIdsJson.getJSONObject(i).getString("id") + ",";
            }
            return songDetail(ids);
        }
    }
}
