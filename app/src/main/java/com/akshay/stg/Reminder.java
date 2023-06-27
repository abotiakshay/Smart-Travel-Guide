package com.akshay.stg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Reminder extends Fragment {
    private RecyclerView recycler;
    private SearchView remindersearch;
    private FloatingActionButton Add_Reminder;
    Context context;
    View reminder_view;
    ReminderRecyclerAdapter reminderadapter;
    Calendar calendar;
    String userId;
    Home abc;
    List<ReminderDetails> reminders = new ArrayList<>();
    private static String TAG = "com.akshay.stg.Reminder.Tag";


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void assignId() {
        recycler = reminder_view.findViewById(R.id.reminder_recycler);
        Add_Reminder = reminder_view.findViewById(R.id.add_reminder);
    }

    private void display() {
//        reminders.add(new ReminderDetails(1, "4:00", "10-2-2021", "4", "This is my trip"));
//        reminders.add(new ReminderDetails(2, "4:00", "10-2-2021", "4", "This is my trip"));
//        reminders.add(new ReminderDetails(3, "4:00", "10-2-2021", "4", "This is my trip"));

        String url = Params.url + "/stg/getReminder.php"; // php string to get liked place
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Log.d("zxcvbnm", "respons :" + response);


                try {
                    JSONObject jsonObject = new JSONObject(response);
//                    Log.d("asdf", response);
                    if (jsonObject.getString("success").equals("true")) {
//                        Log.d("asdf", "TRUE");
                        JSONArray array = jsonObject.getJSONArray("data");
                        if(array.length() == 0){
                            return;
                        }
                        getActivity().findViewById(R.id.textViewNoReminder).setVisibility(View.INVISIBLE);
                        calendar = Calendar.getInstance();
                        String daay, time, place;
                        int requestCode;
                        reminders.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject ob = array.getJSONObject(i);
                            calendar.setTimeInMillis(Long.parseLong(ob.getString("time")));
                            daay = Params.addZero(calendar.get(Calendar.DAY_OF_MONTH)) + "-" + Params.addZero(calendar.get(Calendar.MONTH)) + "-" + Params.addZero(calendar.get(Calendar.YEAR));
                            String am_pm = ((calendar.get(Calendar.AM_PM))==0)?"AM":"PM";
                            time = Params.addZero(calendar.get(Calendar.HOUR)) + ":" + Params.addZero(calendar.get(Calendar.MINUTE)) +am_pm;
                            requestCode = ob.getInt("requestCode");
                            place = "";
                            if (!ob.getString("city").equals("")) {
                                place += ob.getString("city") + ", ";
                            }
                            if (!ob.getString("state").equals("")) {
                                place += ob.getString("state") + ", ";
                            }
                            if (!ob.getString("zip").equals("")) {
                                place += ob.getString("zip") + ", ";
                            }
                            if (!ob.getString("country").equals("")) {
                                place += ob.getString("country");
                            }
                            ReminderDetails list = new ReminderDetails(
                                    requestCode,
                                    time,
                                    daay,
                                    ob.getString("notes"),
                                    place
                            );
//                        Log.d("zxcvbnm", "Time:" + time + "  Day:" + daay + "   place" + place);
                            reminders.add(list); // set data into places list
                        }
//                    Log.d("zxcvbnm", "LIST: " + reminders.get(0));

                        reminderadapter = new ReminderRecyclerAdapter(reminders, context);
                        recycler.setAdapter(reminderadapter);
                        reminderadapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parms = new HashMap<String, String>();
                parms.put("userId", userId);  // pass logged in user
                return parms;
            }
        };
        reminderadapter = new ReminderRecyclerAdapter(reminders, context);
        recycler.setAdapter(reminderadapter);
        queue.add(request);

    }

//    public void setA() {
//        reminderadapter = new ReminderRecyclerAdapter(reminders, context);
//        recycler.setAdapter(reminderadapter);
//        reminderadapter.notifyDataSetChanged();
//    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        reminder_view = inflater.inflate(R.layout.fragment_reminder, container, false);
        Toolbar tt = getActivity().findViewById(R.id.tthome);
        SharedPreferences sh = context.getSharedPreferences("loginuser", Context.MODE_PRIVATE);  // get logged in user
        tt.setTitle("Travel Reminder");
        assignId();
        recycler.setLayoutManager(new LinearLayoutManager(context));


//        Log.d("zxcvbnm", "reminders :"+reminders.get(0));
        userId = sh.getString("Username", null);
        Add_Reminder.setOnClickListener(view -> {
            startActivity(new Intent(context, AddReminder.class));
        });
        return reminder_view;
    }

    @Override
    public void onStart() {
        super.onStart();
        display();
        Log.d(TAG, "onStart: ");
    }
}