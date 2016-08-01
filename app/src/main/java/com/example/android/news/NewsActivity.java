package com.example.android.news;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class NewsActivity extends AppCompatActivity {

    protected final String LOG_TAG = "NewsActivity";
    String aString;

    @Override
    protected void onPause() {
        super.onPause();

        // Save the book values
        SharedPreferences sharedPref = NewsActivity.this.getSharedPreferences("News", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("A String", aString);
        editor.apply();

        // Clear out emptyView, as when app resumes we will need to decide what to display
        ListView lvStories = (ListView) findViewById(R.id.list_item);
        TextView tvNoInternet = (TextView) findViewById(R.id.no_internet);
        TextView tvNoContent = (TextView) findViewById(R.id.no_content);

        lvStories.setEmptyView(null);
        tvNoInternet.setVisibility(View.GONE);
        tvNoContent.setVisibility(View.GONE);

    } // onPause


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        updateUI();

    } // onCreate

    @Override
    protected void onResume() {
        super.onResume();

        // Restore saved values
        SharedPreferences sharedPref = NewsActivity.this.getSharedPreferences("News", Context.MODE_PRIVATE);
        aString = sharedPref.getString("aString", "");

        updateUI();

    } // onResume

    /**
     * If no internet, set emptyView to "no internet"
     * else no content to display, set to "no content"
     */
    private void updateUI() {
        // Check for internet connection and update listView if no internet
        final ListView lvStories = (ListView) findViewById(R.id.list_item);
        TextView tvNoInternet = (TextView) findViewById(R.id.no_internet);
        TextView tvNoContent = (TextView) findViewById(R.id.no_content);

        // ArrayList >> Adapter >> ListView
        final ArrayList<Story> arrayOfStories = QueryUtils.extractStories("");
        final StoryAdapter storyAdapter = new StoryAdapter(this, arrayOfStories);
        lvStories.setAdapter(storyAdapter);

        // Either show list, show no internet or show no content
        if (!networkAvailable()) {
            lvStories.setEmptyView(tvNoInternet);
            tvNoInternet.setVisibility(View.VISIBLE);
            tvNoContent.setVisibility(View.GONE);
        } else {
            lvStories.setEmptyView(tvNoContent);
            tvNoContent.setVisibility(View.VISIBLE);
            tvNoInternet.setVisibility(View.GONE);
        }

        lvStories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String url = "http://www.google.com";

                Uri webPage = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

    }

    /**
     * Will check for null result which mean no interface is online
     */
    private boolean networkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}