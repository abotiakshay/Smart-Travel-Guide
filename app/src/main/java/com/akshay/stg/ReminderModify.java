package com.akshay.stg;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ReminderModify extends AppCompatActivity {

    String time, date, place, msg;
    TextView tw_time, tw_date, tw_place;
    TextView et_notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_modify);
        tw_time = findViewById(R.id.textview_time);
        tw_date = findViewById(R.id.textview_date);
        tw_place = findViewById(R.id.textview_place);
        et_notes = findViewById(R.id.edit_text_notes);
        Intent i = getIntent();
        time = i.getStringExtra("time");
        date = i.getStringExtra("date");
        place = i.getStringExtra("place");
        msg = i.getStringExtra("msg");

        tw_time.setText(time);
        tw_date.setText(date);
        tw_place.setText(place);
        et_notes.setText(msg);
        Toolbar tt = findViewById(R.id.tttt);
        tt.setTitle("Reminder");

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}