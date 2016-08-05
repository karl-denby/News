package com.example.android.news;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class NewsActivity extends AppCompatActivity {

    protected final String LOG_TAG = "NewsActivity";

    @Override
    protected void onPause() {
        super.onPause();
        // Save the book values
        //SharedPreferences sharedPref = NewsActivity.this.getSharedPreferences("News", Context.MODE_PRIVATE);
        //SharedPreferences.Editor editor = sharedPref.edit();

        //editor.putString("jsonDocumentAsString", jsonDocumentAsString);
        //editor.apply();

        // Clear out emptyView, as when app resumes we will need to decide what to display
        ListView lvStories = (ListView) findViewById(R.id.list_item);
        TextView tvNoInternet = (TextView) findViewById(R.id.no_internet);
        TextView tvNoContent = (TextView) findViewById(R.id.no_content);
        Button btnRefresh = (Button) findViewById(R.id.refresh);

        lvStories.setEmptyView(null);
        tvNoInternet.setVisibility(View.GONE);
        tvNoContent.setVisibility(View.GONE);
        btnRefresh.setVisibility(View.VISIBLE);
    } // onPause

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        Button btn_refresh = (Button) findViewById(R.id.refresh);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check for new data and updateUI (background thread)
                updateUI("{}");
            }
        });

        updateUI("{}");
    } // onCreate

    @Override
    protected void onResume() {
        super.onResume();

        // Restore saved values
        //SharedPreferences sharedPref = NewsActivity.this.getSharedPreferences("News", Context.MODE_PRIVATE);
        //jsonDocumentAsString = sharedPref.getString("jsonDocumentAsString", "");

        // Check for new data and updateUI (background thread)
        //StoryAsyncTask results = new StoryAsyncTask();
        //results.execute(queryUrl);
        updateUI("{}");
    } // onResume

    /**
     * @param jsonDocumentAsString is a stored string containing a JSON response from the API
     *                             If no internet, set emptyView to "no internet"
     *                             else no content to display, set to "no content"
     */
    private void updateUI(String jsonDocumentAsString) {
        // Check for internet connection and update listView if no internet
        final ListView lvStories = (ListView) findViewById(R.id.list_item);
        final TextView tvNoInternet = (TextView) findViewById(R.id.no_internet);
        final TextView tvNoContent = (TextView) findViewById(R.id.no_content);
        final Button btnRefresh = (Button) findViewById(R.id.refresh);

        // ArrayList >> Adapter >> ListView
        final ArrayList<Story> arrayOfStories = QueryUtils.extractStories(jsonDocumentAsString);
        final StoryAdapter storyAdapter = new StoryAdapter(this, arrayOfStories);
        lvStories.setAdapter(null);

        // Either show no internet
        // or show no content view, then load storyAdapter
        if (!networkAvailable()) {
            lvStories.setEmptyView(tvNoInternet);
            tvNoInternet.setVisibility(View.VISIBLE);
            tvNoContent.setVisibility(View.GONE);
            btnRefresh.setVisibility(View.VISIBLE);
        } else {
            lvStories.setEmptyView(tvNoContent);
            tvNoContent.setVisibility(View.VISIBLE);
            tvNoInternet.setVisibility(View.GONE);
            btnRefresh.setVisibility(View.VISIBLE);
        }

        lvStories.setAdapter(storyAdapter);

        lvStories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String url = view.getTag().toString();
                Uri webPage = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    } // updateUI

    /**
     * Will check for null result which mean no interface is online
     */
    private boolean networkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}