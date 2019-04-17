package com.dreamgyf.service;

import com.dreamgyf.entity.Album;
import com.dreamgyf.entity.Artist;
import com.dreamgyf.entity.Song;
import com.dreamgyf.entity.SongData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ResponseProcessing {
    private static SingleResponseProcessing singleResponseProcessing = new SingleResponseProcessing();

    public static SingleResponseProcessing get()
    {
        return singleResponseProcessing;
    }

    public static class SingleResponseProcessing{

        private Artist json2Artist(JSONObject jsonObject) throws JSONException {
            Artist artist = new Artist();
            artist.setId(jsonObject.getInt("id"));
            artist.setName(jsonObject.getString("name"));
            artist.setPicUrl(jsonObject.getString("picUrl"));
            JSONArray jsonAlias = jsonObject.getJSONArray("alias");
            List<String> alias = new ArrayList<>();
            for(int i = 0;i < jsonAlias.length();i++)
            {
                alias.add(jsonAlias.getString(i));
            }
            artist.setAlias(alias);
            artist.setAlbumSize(jsonObject.getInt("albumSize"));
            artist.setPicId(jsonObject.getInt("picId"));
            artist.setPicUrl(jsonObject.getString("img1v1Url"));
            artist.setImg1v1(jsonObject.getInt("img1v1"));
            artist.setTrans(jsonObject.getString("trans"));
            return artist;
        }

        private Album json2Album(JSONObject jsonObject) throws JSONException {
            Album album = new Album();
            album.setId(jsonObject.getInt("id"));
            album.setName(jsonObject.getString("name"));
            JSONObject jsonArtist = jsonObject.getJSONObject("artist");
            album.setArtist(json2Artist(jsonArtist));
            album.setPublishTime(jsonObject.getInt("publishTime"));
            album.setSize(jsonObject.getInt("size"));
            album.setCopyrightId(jsonObject.getInt("copyrightId"));
            album.setStatus(jsonObject.getInt("status"));
            album.setPicId(jsonObject.getInt("picId"));
            return album;
        }

        private Song json2Song(JSONObject jsonObject) throws JSONException {
            Song song = new Song();
            song.setId(jsonObject.getInt("id"));
            song.setName(jsonObject.getString("name"));
            JSONArray jsonArtists = jsonObject.getJSONArray("artists");
            List<Artist> artists = new ArrayList<>();
            for(int i = 0;i < jsonArtists.length();i++)
            {
                JSONObject jsonArtist = jsonArtists.getJSONObject(i);
                artists.add(json2Artist(jsonArtist));
            }
            song.setArtists(artists);
            JSONObject jsonAlbum = jsonObject.getJSONObject("album");
            song.setAlbum(json2Album(jsonAlbum));
            song.setDuration(jsonObject.getInt("duration"));
            song.setCopyrightId(jsonObject.getInt("copyrightId"));
            song.setStatus(jsonObject.getInt("status"));
            JSONArray jsonAlias = jsonObject.getJSONArray("alias");
            List<String> alias = new ArrayList<>();
            for(int i = 0;i < jsonAlias.length();i++)
            {
                alias.add(jsonAlias.getString(i));
            }
            song.setAlias(alias);
            song.setRtype(jsonObject.getInt("rtype"));
            song.setFtype(jsonObject.getInt("ftype"));
            song.setMvid(jsonObject.getInt("mvid"));
            song.setFee(jsonObject.getInt("fee"));
            song.setrUrl(jsonObject.getString("rUrl"));
            return song;
        }

        public SongData json2SongData(JSONObject jsonObject) throws JSONException {
            SongData songData = new SongData();
            songData.setId(jsonObject.getInt("id"));
            songData.setUrl(jsonObject.getString("url"));
            songData.setBr(jsonObject.getInt("br"));
            songData.setSize(jsonObject.getInt("size"));
            songData.setMd5(jsonObject.getString("md5"));
            songData.setCode(jsonObject.getInt("code"));
            songData.setExpi(jsonObject.getInt("expi"));
            songData.setType(jsonObject.getString("type"));
            songData.setGain(jsonObject.getDouble("gain"));
            songData.setFee(jsonObject.getInt("fee"));
            songData.setUf(jsonObject.getString("uf"));
            songData.setPayed(jsonObject.getInt("payed"));
            songData.setFlag(jsonObject.getInt("flag"));
            songData.setCanExtend(jsonObject.getBoolean("canExtend"));
            songData.setFreeTrialInfo(jsonObject.getString("freeTrialInfo"));
            songData.setLevel(jsonObject.getString("level"));
            songData.setEncodeType(jsonObject.getString("encodeType"));
            return songData;
        }

        public List<Song> search(String stringJson) throws JSONException {
            List<Song> songs = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(stringJson);
            JSONObject jsonResult = jsonObject.getJSONObject("result");
            JSONArray jsonSongs = jsonResult.getJSONArray("songs");
            for(int i = 0;i < jsonSongs.length();i++)
            {
                JSONObject jsonSong = jsonSongs.getJSONObject(i);
                songs.add(json2Song(jsonSong));
            }
            return songs;
        }

        public List<SongData> getSong(String stringJson) throws JSONException {
            List<SongData> songDataList = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(stringJson);
            JSONArray jsonDatas = jsonObject.getJSONArray("data");
            for(int i = 0;i < jsonDatas.length();i++)
            {
                JSONObject jsonData = jsonDatas.getJSONObject(i);
                songDataList.add(json2SongData(jsonData));
            }
            return songDataList;
        }
    }
}
