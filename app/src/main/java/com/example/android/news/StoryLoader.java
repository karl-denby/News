package com.example.android.news;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

public class StoryLoader extends AsyncTaskLoader<List<Story>> {

    // Variables
    protected final String LOG_TAG = "StoryLoader";
    URL queryUrl = makeURL(
            "http://content.guardianapis.com/search" +
            "?order-by=newest" +
            "&show-fields=headline%2Cbyline" +
            "&page=1" +
            "&page-size=20" +
            "&q=sport" +
            "&api-key=test");

    public StoryLoader(Context ctx) {
        super(ctx);
    }

    /****************************************************/
    /** (1) A task that performs the asynchronous load **/
    /****************************************************/

    @Override
    public List<Story> loadInBackground() {
        // This method is called on a background thread and should generate a
        // new set of data to be delivered back to the client.
        String jsonDocumentAsString = "{}";

        // use URL to query API and return STRING(of JSON data)
        try {
            jsonDocumentAsString = makeHttpRequest(queryUrl);
            Log.v(LOG_TAG, "HttpRequest done answer has size " + jsonDocumentAsString.length());
        } catch (IOException e) {
            Log.e(LOG_TAG,"HTTP error", e);
        }

        // use STRING to pull out and DECODE JSON return ArrayList<Story>
        List<Story> listOfStory = QueryUtils.extractStories(jsonDocumentAsString);

        return listOfStory;
    }

    @Override
    protected void onStartLoading() {
        if (takeContentChanged())
            forceLoad();
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

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

}
