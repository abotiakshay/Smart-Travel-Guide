package com.akshay.stg;

import android.os.Bundle;

public class Params {
    public static String url = "https://techkhajana.000webhostapp.com/";
    public static String date = null;
    public static String time = null;
    public static Bundle place = null;
    public static Boolean place_value = false;
    public static Boolean delete = false;
    public static String addZero(int n){
        if(n <= 9){
            return "0"+n;
        }
        else{
            return n+"";
        }
    }
}

