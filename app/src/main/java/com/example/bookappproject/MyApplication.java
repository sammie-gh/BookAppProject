package com.example.bookappproject;

import android.app.Application;
import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Locale;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    //created a static method to convert timestamp tp proper date formart so we can use it everywhere in project no need to rewrite again
    public static final String formartTimestamp(long timestamp) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp);
        String date = DateFormat.format("dd//MM/yyyy", cal).toString();
        return date;
    }
}
