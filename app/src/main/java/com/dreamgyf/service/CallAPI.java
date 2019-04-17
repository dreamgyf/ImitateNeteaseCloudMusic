package com.dreamgyf.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CallAPI {

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

        public String search(String keywords) throws IOException {
            URL url = new URL(DOMAIN + "/search?keywords=" + keywords);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            if(httpURLConnection.getResponseCode() != 200)
                throw new RuntimeException(httpURLConnection.getResponseMessage());
            InputStream in = httpURLConnection.getInputStream();
            return read(in).toString();
        }

        public String getSong(int id) throws IOException {
            URL url = new URL(DOMAIN + "/song/url?id=" + id);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            if(httpURLConnection.getResponseCode() != 200)
                throw new RuntimeException(httpURLConnection.getResponseMessage());
            InputStream in = httpURLConnection.getInputStream();
            return read(in).toString();
        }
    }
}
