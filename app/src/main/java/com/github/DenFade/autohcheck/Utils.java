package com.github.DenFade.autohcheck;

import android.os.Build;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Utils {

    private Utils(){

    }

    public static String cookieParser(List<String> list){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return list.stream().map(v -> v.split(";")[0]).collect(Collectors.joining(";"));
        } else {
            StringBuilder sb = new StringBuilder();
            for(String o : list){
                sb.append(";").append(o.split(";")[0]);
            }
            return sb.substring(1);
        }
    }

    public static String bodyParser(Map<String, String> map){
        StringBuilder sb = new StringBuilder();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            map.forEach((k, v) -> sb.append("&").append(k).append("=").append(v));
        } else {
            for(String k : map.keySet()){
                sb.append("&").append(k).append("=").append(map.get(k));
            }
        }
        return sb.substring(1);
    }

    public static String encodeURI(String str){
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return str;
        }
    }
}

