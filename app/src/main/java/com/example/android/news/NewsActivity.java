package com.example.android.news;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class NewsActivity extends AppCompatActivity {

    String aString;

    @Override
    protected void onPause() {
        super.onPause();

        // Save the book values
        SharedPreferences sharedPref = NewsActivity.this.getSharedPreferences("News", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("A String", aString);
        editor.apply();

    } // onPause


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Restore saved values
        SharedPreferences sharedPref = NewsActivity.this.getSharedPreferences("News", Context.MODE_PRIVATE);
        aString = sharedPref.getString("aString", "");

    } // onResume

    /**
     * Will check for null result which mean no interface is online
     */
    private boolean networkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}