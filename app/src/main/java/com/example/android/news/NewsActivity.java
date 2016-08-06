package com.example.android.news;



import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends FragmentActivity
        implements android.support.v4.app.LoaderManager.LoaderCallbacks<List<Story>> {

    protected final String LOG_TAG = "NewsActivity";
    StoryAdapter storyAdapter;

    // Loader lifecycle Events
    @Override
    public StoryLoader onCreateLoader(int id, Bundle args) {
        return new StoryLoader(this);
    }

    @Override public void onLoaderReset(Loader<List<Story>> param1) {
        // TODO: Something
    }

    @Override
    public void onLoadFinished(Loader<List<Story>> loader, List<Story> data) {
        // update listView with loaded content
        ListView lvStories = (ListView) findViewById(R.id.list_item);

        Log.v(LOG_TAG, "Loader onLoadFinished");
        // ArrayList >> Adapter >> ListView
        ArrayList<Story> arrayOfStories = QueryUtils.extractStories("{}");
        storyAdapter = new StoryAdapter(this, arrayOfStories);
        lvStories.setAdapter(storyAdapter);
        storyAdapter.notifyDataSetChanged();
    }

    // App lifecycle Events
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

        // Initialize our global storyAdapter
        // -- Setup Loader to populate the storyAdapter
        //storyAdapter = new StoryAdapter(this, new ArrayList<Story>());
        this.getSupportLoaderManager().initLoader(0, null, NewsActivity.this).forceLoad();

        Button btn_refresh = (Button) findViewById(R.id.refresh);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check for new data and updateUI (background thread)
                if (storyAdapter != null) {
                    updateUI(storyAdapter);
                } else {
                    Log.v(LOG_TAG, "storyAdapter was Null");
                }
            }
        });
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
    } // onResume

    /**
     * @param stories a stored collection of Story's in a list adapter
     *                If no internet, set emptyView to "no internet"
     *                else no content to display, set to "no content"
     */
    private void updateUI(StoryAdapter stories) {
        // Check for internet connection and update listView if no internet
        ListView lvStories = (ListView) findViewById(R.id.list_item);
        TextView tvNoInternet = (TextView) findViewById(R.id.no_internet);
        TextView tvNoContent = (TextView) findViewById(R.id.no_content);
        Button btnRefresh = (Button) findViewById(R.id.refresh);

        Log.v(LOG_TAG, "updateUI");
        lvStories.setAdapter(stories);
        stories.notifyDataSetChanged();
        tvNoInternet.setVisibility(View.GONE);
        tvNoContent.setVisibility(View.GONE);
        // Either show no internet
        // or show no content view, then load storyAdapter
/*
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
*/
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