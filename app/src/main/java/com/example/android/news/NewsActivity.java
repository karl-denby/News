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
        updateUI();
    }

    @Override
    public void onLoadFinished(Loader<List<Story>> loader, List<Story> data) {
        /**
         * turn "List<Story> data" into "ArrayList<Story> arrayOfStories"
         * attach data to listView
         * updateUI makes sure extra views are not visible
         */
        ArrayList<Story> arrayOfStories;
        arrayOfStories = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            arrayOfStories.add(i, data.get(i));
        }
        storyAdapter = new StoryAdapter(this, arrayOfStories);

        // attach data to listView and tell it to refresh
        ListView lvStories = (ListView) findViewById(R.id.list_item);
        lvStories.setAdapter(storyAdapter);
        storyAdapter.notifyDataSetChanged();
        updateUI();
    }

    // App lifecycle Events
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        getSupportLoaderManager().initLoader(0, null, NewsActivity.this).forceLoad();

        // When user clicks a story open it with the browser
        ListView lvStories = (ListView) findViewById(R.id.list_item);
        lvStories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String url = view.getTag().toString();
                Log.v(LOG_TAG,"Click url is " + url);
                Uri webPage = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    } // onCreate

    /**
     * Set the correct EmptyView and make the other one disappear
     */
    private void updateUI() {
        ListView lvStories = (ListView) findViewById(R.id.list_item);
        TextView tvNoInternet = (TextView) findViewById(R.id.no_internet);
        TextView tvNoContent = (TextView) findViewById(R.id.no_content);

        lvStories.setVisibility(View.VISIBLE);
        tvNoInternet.setVisibility(View.GONE);
        tvNoContent.setVisibility(View.GONE);

        // Either show no internet
        // or show no content view, then load storyAdapter
        if (!networkAvailable()) {
            lvStories.setEmptyView(tvNoInternet);
            tvNoInternet.setVisibility(View.VISIBLE);
        } else {
            lvStories.setEmptyView(tvNoContent);
            tvNoContent.setVisibility(View.VISIBLE);
        }
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