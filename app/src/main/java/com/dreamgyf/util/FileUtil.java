package com.dreamgyf.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileUtil {

    public static InputStream getInputStreamFromUrl(String Url) throws IOException {
        URL url = new URL(Url);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");
        if(httpURLConnection.getResponseCode() != 200)
            throw new RuntimeException(httpURLConnection.getResponseMessage());
        return httpURLConnection.getInputStream();
    }
}
