package com.akshay.stg;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        Log.d("zxcvbnm","Alarm...");

        String place        = intent.getStringExtra("place");
        String notes        = intent.getStringExtra("note");
        String userId       = intent.getStringExtra("userId");
        String requestCode  = String.valueOf(intent.getIntExtra("requestCode",0));
        Log.d("asdf", "\nplace: "+place+"\nnotes :"+notes+"\nuserId :"+userId+"\ncode :"+requestCode);
        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel("my_channel_01", "hello", NotificationManager.IMPORTANCE_HIGH);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "my_channel_01");
        builder.setContentTitle("" + place);
        builder.setContentText(notes);
        builder.setSmallIcon(R.drawable.stgstg);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setAutoCancel(true);
        builder.setGroup("group-1");

        Intent iii = new Intent(context, Home.class);
        iii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, iii, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_MUTABLE);
        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(1, builder.build());
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Params.url + "/stg/deleteReminder.php",
                response -> {
                    if (response.equals("200")) {
                        Log.d("asdf", "Data deleted Successfully");

                    } else {
                        Log.d("asdf", "Error");
                    }
                }, error -> {
            Log.d("zxcvbnm", "ERROR : " + error);
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("requestCode", requestCode + "");
                params.put("userId", (userId != null) ? userId : "");
                return params;
            }
        };
        requestQueue.add(request);



//        Toast.makeText(context, "Alarm....", Toast.LENGTH_LONG).show();
    }
}