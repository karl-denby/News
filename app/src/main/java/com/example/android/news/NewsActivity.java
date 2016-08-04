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
import android.webkit.URLUtil;
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
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class NewsActivity extends AppCompatActivity {

    protected final String LOG_TAG = "NewsActivity";
    URL queryUrl = makeURL("http://content.guardianapis.com/search?" +
            "order-by=newest" +
            "&show-fields=headline%2Cbyline" +
            "&page=1" +
            "&page-size=20" +
            "&q=sport" +
            "&api-key=test");
    String jsonDocumentAsString = "{}";

    @Override
    protected void onPause() {
        super.onPause();
        // Save the book values
        SharedPreferences sharedPref = NewsActivity.this.getSharedPreferences("News", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("jsonDocumentAsString", jsonDocumentAsString);
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

        Button btn_refresh = (Button) findViewById(R.id.refresh);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check for new data and updateUI (background thread)
                StoryAsyncTask results = new StoryAsyncTask();
                results.execute(queryUrl);
            }
        });

        // Pass to the UI update code which should get data, then update the display
        StoryAsyncTask results = new StoryAsyncTask();
        results.execute(queryUrl);
    } // onCreate

    @Override
    protected void onResume() {
        super.onResume();

        // Restore saved values
        SharedPreferences sharedPref = NewsActivity.this.getSharedPreferences("News", Context.MODE_PRIVATE);
        jsonDocumentAsString = sharedPref.getString("jsonDocumentAsString", "");

        // Check for new data and updateUI (background thread)
        StoryAsyncTask results = new StoryAsyncTask();
        results.execute(queryUrl);
    } // onResume

    /**
     * @param url_string url in a string
     * @return url
     */
    private URL makeURL(String url_string) {
        try {
            return new URL(url_string);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
    }

    /**
     * @param jsonDocumentAsString is a stored string containing a JSON response from the API
     * If no internet, set emptyView to "no internet"
     * else no content to display, set to "no content"
     */
    private void updateUI(String jsonDocumentAsString) {
        // Check for internet connection and update listView if no internet
        final ListView lvStories = (ListView) findViewById(R.id.list_item);
        final TextView tvNoInternet = (TextView) findViewById(R.id.no_internet);
        final TextView tvNoContent = (TextView) findViewById(R.id.no_content);

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
        } else {
            lvStories.setEmptyView(tvNoContent);
            tvNoContent.setVisibility(View.VISIBLE);
            tvNoInternet.setVisibility(View.GONE);
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

    /**
     * Run the Query in a Thread and when it returns store result and call UI update code
     */
    private class StoryAsyncTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {

            // Can't update the UI from here, only thread that made them (main OnCreate) can
            // update them, so just run query, get results and store them somewhere that the
            // main thread can access and use to update the UI.
            try {
                jsonDocumentAsString = makeHttpRequest(urls[0]);
            } catch (IOException e) {
                Log.e(LOG_TAG,"HTTP error", e);
            }
            return jsonDocumentAsString;
        }

        @Override
        protected void onPostExecute(String jsonDocumentAsString) {
            updateUI(jsonDocumentAsString);
        }
    } // BooksAsyncTask

    /**
     * @param url where is our source data
     * @return string with our JSON data as a String (can save this easily)
     * @throws IOException, if something went wrong on the Internet
     */
    private String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "{}";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            jsonResponse = readFromStream(inputStream);
        } catch (IOException e) {
            Log.e(LOG_TAG, "HTTP IOException: ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    } // makeHttpRequest

    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    } //readFromStream

}