package com.example.flirtandroid.Utils;

import android.content.Context;

public class GetApiParameters {

    //Beta testing url
//    public static String url = "http://10.1.1.188:9080/api/";
//    public  static  String newUrl= "http://10.1.1.188:9080/";
//    public static String baseUrl = "";

    //Live testing url
    public static String url = "https://flirt.witorbit.com/api/";
    public  static  String newUrl= "https://flirt.witorbit.com/";
    public static String baseUrl = "";


    public GetApiParameters(Context context) {

    }

    public static String getUrl() {
        return url;
    }

    public static String getNewUrl() {
        return newUrl;
    }

    public static String getBaseUrl() {
        return baseUrl;
    }
}
