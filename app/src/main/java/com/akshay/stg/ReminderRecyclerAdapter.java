package com.akshay.stg;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReminderRecyclerAdapter extends RecyclerView.Adapter<ReminderRecyclerAdapter.ViewHolder> implements Filterable {    // place recycler

    int requestCode;
    String userId;
    List<ReminderDetails> reminders;
    Context context;
    List<ReminderDetails> backup;

    public ReminderRecyclerAdapter(List<ReminderDetails> reminders, Context context) {
        this.reminders = reminders;
        this.context = context;
        backup = new ArrayList<>(reminders);
    }

    @NonNull
    @Override
    public ReminderRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.designforreminder, parent, false);
        ViewHolder reminder_view = new ViewHolder(v);    // view creation in which data will bind at runtime
        return reminder_view;
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderRecyclerAdapter.ViewHolder holder, int position) {
//        Log.d("asdf", "onBindViewHolder --- Reminders : " + reminders);
        holder.index.setText((position + 1) + "");

//        holder.time.setText(reminders.get(position).getTime());
//        holder.date.setText(reminders.get(position).getDate());
//        holder.message.setText(reminders.get(position).getMessage());
//        holder.placename.setText(reminders.get(position).getPlace());

        String stringDateAndTime = "On " + reminders.get(position).getDate() + ", " + reminders.get(position).getTime();
        holder.dateAndTime.setText(stringDateAndTime);
        holder.details.setText(reminders.get(position).getPlace());
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                holder.details.setSelected(true);
                h.postDelayed(this,2000);
            }
        }, 2000);

        holder.close.setOnClickListener(v -> {
//            Reminder.reminders.remove(position);
//            Reminder.reminderadapter.notifyDataSetChanged();
//            ReminderRecyclerAdapter reminderadapter = this.getClass();
//            reminderadapter.notifyDataSetChanged();
            SharedPreferences sh = context.getSharedPreferences("loginuser", context.MODE_PRIVATE);  // get logged in user
            userId = sh.getString("Username", null);
            requestCode = reminders.get(position).getRequestCode();
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            StringRequest request = new StringRequest(
                    Request.Method.POST,
                    Params.url + "/stg/deleteReminder.php",
                    response -> {
                        if (response.equals("200")) {
                            //  remove from list
//                            Log.d("asdf", "Remionder list : " + reminders + " \n\nnew list " + newlist);
                            reminders.remove(position);
                            this.notifyDataSetChanged();
                            //  remove from AlarmManger
                            Intent intent = new Intent(context, MyBroadcastReceiver.class);
                            PendingIntent intent1 = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
                            AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
                            alarmManager.cancel(intent1);
//                            Params.delete =true;
//                            Home abc = new Home();
//                            Fragment currentFragment=new Reminder();
//                            abc.getSupportFragmentManager().beginTransaction().detach(currentFragment).attach(currentFragment).commit();
                            Toast.makeText(context, "Reminder removed.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "Error ", Toast.LENGTH_LONG).show();
//                            Log.d("zxcvbnm", "\nuserId: " + userId + "\nrCode: " + requestCode);
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
        });


        holder.mainRecycler.setOnClickListener(v -> {
            Intent i = new Intent(context, ReminderModify.class);
            i.putExtra("time", String.valueOf(reminders.get(position).getTime()));
            i.putExtra("date", String.valueOf(reminders.get(position).getDate()));
            i.putExtra("msg", String.valueOf(reminders.get(position).getMessage()));
            i.putExtra("place", String.valueOf(reminders.get(position).getPlace()));
            context.startActivity(i);
        });


    }

    @Override
    public Filter getFilter() {
        return reminderfilter;
    }

    Filter reminderfilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults result = new FilterResults();

            if (constraint == null || constraint.length() == 0) {
                result.values = backup;
                result.count = backup.size();
            } else {
                ArrayList<ReminderDetails> temp = new ArrayList<>();
                for (ReminderDetails p : backup) {
                    if (p.getPlace().toLowerCase().contains(constraint.toString().toLowerCase()) || p.getDate().contains(constraint.toString().toLowerCase())) {
                        temp.add(p);
                    }
                }
                result.count = temp.size();
                result.values = temp;


            }
            return result;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            reminders = (ArrayList<ReminderDetails>) results.values;
            notifyDataSetChanged();
        }
    };

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView mainRecycler;

        TextView details, dateAndTime, index;
        private final ImageView close;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            time = itemView.findViewById(R.id.timee);
//            date = itemView.findViewById(R.id.datee);
//            message = itemView.findViewById(R.id.custome_message);
//            placename = itemView.findViewById(R.id.place_namee);


            mainRecycler = itemView.findViewById(R.id.main_recycler);
            index = itemView.findViewById(R.id.index);
            details = itemView.findViewById(R.id.reminder_detail);
            dateAndTime = itemView.findViewById(R.id.reminder_date_time);
            close = itemView.findViewById(R.id.reminder_remove_btn);
        }
    }
}
