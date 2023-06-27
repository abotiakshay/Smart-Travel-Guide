package com.akshay.stg;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddReminder extends AppCompatActivity {

    TextView inp_date, inp_time, inp_place;
    EditText inp_notes;
    Calendar calendar = Calendar.getInstance();

    String datee, timee, placee;
    Button add_reminder;
    int requestCode;
    String address = null, knownName = null, city1 = null, state1 = null, zip = null, country1 = null, userId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        inp_date = findViewById(R.id.inp_date);
        inp_time = findViewById(R.id.inp_time);
        inp_place = findViewById(R.id.inp_place);
        inp_notes = findViewById(R.id.inp_notes);
        add_reminder = findViewById(R.id.add_reminder_btn);
//        inp_date.setText("Click Here to choose Date");
//        inp_time.setText("Click Here to choose Time");
//        inp_place.setText("Click Here to choose Place");
        SharedPreferences sh = getSharedPreferences("loginuser", MODE_PRIVATE);  // get logged in user
        userId = sh.getString("Username", null);

        if (Params.place_value) {
            Bundle bundle = Params.place;
            LatLng fromPosition = bundle.getParcelable("from_position");  // get lattitude and longitude from Map
            Geocoder geocoder = new Geocoder(AddReminder.this);
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(fromPosition.latitude, fromPosition.longitude, 1); // get address from lattitude and logitude
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                city1 = addresses.get(0).getLocality();
                state1 = addresses.get(0).getAdminArea();
                zip = addresses.get(0).getPostalCode();
                country1 = addresses.get(0).getCountryName();
                knownName = addresses.get(0).getFeatureName();
                placee = "";
                if (city1 != null) {
                    placee += city1 + ", ";
                }
                if (state1 != null) {
                    placee += state1 + ", ";
                }
                if (zip != null) {
                    placee += zip + ", ";
                }
                if (country1 != null) {
                    placee += country1;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            inp_place.setText(placee);
        }
        inp_date.setOnClickListener(v -> showDateDialog(inp_date));
        inp_time.setOnClickListener(v -> showTimeDialog(inp_time));
        inp_place.setOnClickListener(v -> {
            Intent intent = new Intent(AddReminder.this, PlacePicker.class);
            intent.putExtra("classname", "addreminder");
            startActivity(intent);
        });
        add_reminder.setOnClickListener(v -> {
            if (Params.date != null && Params.time != null && Params.place != null) {
//                Log.d("zxcvbnm", "calendar : " + calendar.getTime() + " ... " + calendar.getTimeInMillis());
//                Toast.makeText(AddReminder.this, "data sending...", Toast.LENGTH_LONG).show();
                requestCode = (int) (Math.random() * 2000000000);
//                Log.d("zxcvbnm", "calender :" + calendar.getTime());
                RequestQueue requestQueue = Volley.newRequestQueue(AddReminder.this);
                StringRequest request = new StringRequest(
                        Request.Method.POST,
                        Params.url + "/stg/addReminder.php",
                        response -> {
//                            Log.d("asdf", "add reminder : "+response);
                            if (response.equals("200")) {
                                Intent intent = new Intent(AddReminder.this, MyBroadcastReceiver.class);

                                Log.d("asdf", "\nplace: "+placee+"\nnotes :"+(inp_notes.getText())+"\nuserId :"+userId+"\ncode :"+requestCode);
                                intent.putExtra("userId", userId);
                                intent.putExtra("requestCode", requestCode);
                                intent.putExtra("place", placee);
                                intent.putExtra("note", String.valueOf(inp_notes.getText()));

                                PendingIntent intent1 = PendingIntent.getBroadcast(AddReminder.this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_MUTABLE);
                                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);
                                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), intent1);
//                                Log.d("zxcvbnm", "Data Added Successfully");
//                                Toast.makeText(AddReminder.this, "Data added Succesefully " + response, Toast.LENGTH_LONG).show();
//                                Intent i = new Intent(AddReminder.this, Home.class);
//                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                i.putExtra("name", "AddReminder");
//                                startActivity(i);
                                finish();
                            } else {
//                                Log.d("zxcvbnm", "Something went wrong! Please Try Again later.");
                                Toast.makeText(AddReminder.this, "Something went wrong! Please Try Again later.", Toast.LENGTH_LONG).show();
                            }
                        }, error -> {
                    Log.d("zxcvbnm", "ERROR : " + error);
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("time", String.valueOf(calendar.getTimeInMillis()));
                        params.put("notes", String.valueOf(inp_notes.getText()));
                        params.put("knownName", (knownName != null) ? knownName : "");
                        params.put("city", (city1 != null) ? city1 : "");
                        params.put("state", (state1 != null) ? state1 : "");
                        params.put("zip", (zip != null) ? zip : "");
                        params.put("country", (country1 != null) ? country1 : "");
                        params.put("requestCode", requestCode + "");
                        params.put("userId", (userId != null) ? userId : "");
                        return params;
                    }
                };
                requestQueue.add(request);
            } else {
                Toast.makeText(this, "Please Enter All Details", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showTimeDialog(TextView time) {
        TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
            Params.time = simpleDateFormat.format(calendar.getTime());
            time.setText(simpleDateFormat.format(calendar.getTime()));
            timee = calendar.getTime().toString();
        };
        new TimePickerDialog(AddReminder.this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
    }

    private void showDateDialog(TextView date) {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Params.date = simpleDateFormat.format(calendar.getTime());
            date.setText(simpleDateFormat.format(calendar.getTime()));
            datee = calendar.getTime().toString();
        };
        new DatePickerDialog(AddReminder.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}