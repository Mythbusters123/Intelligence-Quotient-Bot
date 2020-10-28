package com.mythbusters123.IQXP.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.stream.Collectors;

public class DataUtil {
    public String getData(String URL) {
        try {
            URLConnection url = new URL(URL).openConnection();
            url.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:19.0) Gecko/20100101 Firefox/19.0");
            url.connect();
            BufferedReader serverResponse = new BufferedReader(new InputStreamReader(url.getInputStream()));
            String response = serverResponse.lines().collect(Collectors.joining());
            serverResponse.close();
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
