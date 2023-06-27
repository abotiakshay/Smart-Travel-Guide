package com.akshay.stg;

public class ReminderDetails {
    String time, date, message, place;
    public int  requestCode;

    public ReminderDetails(int requestCode, String time, String date , String message, String place ) {
        this.requestCode = requestCode;
        this.time = time;
        this.date = date;
        this.message = message;
        this.place = place;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}


