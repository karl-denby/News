/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.news;

        import android.util.Log;
        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;
        import java.util.ArrayList;

/**
 * Helper methods related to requesting and receiving Story data from the Guardian API.
 */
public final class QueryUtils {

    // Sample JSON response for a Guardian API query for testing without going out to internet
    // http://content.guardianapis.com/search?order-by=newest&show-fields=headline%2Cbyline&page=1&page-size=10&q=football&api-key=test
    private static final String SAMPLE_JSON_RESPONSE =
            "{\"response\":{\"status\":\"ok\",\"userTier\":\"developer\",\"total\":180380,\"startIndex\":1,\"pageSize\":10,\"currentPage\":1,\"pages\":18038,\"orderBy\":\"newest\",\"results\":[{\"id\":\"football/2016/aug/01/leroy-sane-manchester-city-schalke\",\"type\":\"article\",\"sectionId\":\"football\",\"sectionName\":\"Football\",\"webPublicationDate\":\"2016-08-01T12:32:00Z\",\"webTitle\":\"Leroy Sané in Manchester for expected £35m move to City\",\"webUrl\":\"https://www.theguardian.com/football/2016/aug/01/leroy-sane-manchester-city-schalke\",\"apiUrl\":\"https://content.guardianapis.com/football/2016/aug/01/leroy-sane-manchester-city-schalke\",\"fields\":{\"headline\":\"Leroy Sané in Manchester for expected £35m move to City\",\"byline\":\"Guardian sport\"},\"isHosted\":false},{\"id\":\"football/blog/2016/aug/01/premier-league-preview-bournemouth\",\"type\":\"article\",\"sectionId\":\"football\",\"sectionName\":\"Football\",\"webPublicationDate\":\"2016-08-01T12:00:03Z\",\"webTitle\":\"Premier League 2016-17 preview No2: Bournemouth | Ben Fisher\",\"webUrl\":\"https://www.theguardian.com/football/blog/2016/aug/01/premier-league-preview-bournemouth\",\"apiUrl\":\"https://content.guardianapis.com/football/blog/2016/aug/01/premier-league-preview-bournemouth\",\"fields\":{\"headline\":\"Premier League 2016-17 preview No2: Bournemouth\",\"byline\":\"Ben Fisher\"},\"isHosted\":false},{\"id\":\"football/2016/aug/01/football-transfer-rumours-jonny-evans-and-mauro-icardi-to-arsenal\",\"type\":\"article\",\"sectionId\":\"football\",\"sectionName\":\"Football\",\"webPublicationDate\":\"2016-08-01T07:25:52Z\",\"webTitle\":\"Football transfer rumours: Jonny Evans and Mauro Icardi to Arsenal?\",\"webUrl\":\"https://www.theguardian.com/football/2016/aug/01/football-transfer-rumours-jonny-evans-and-mauro-icardi-to-arsenal\",\"apiUrl\":\"https://content.guardianapis.com/football/2016/aug/01/football-transfer-rumours-jonny-evans-and-mauro-icardi-to-arsenal\",\"fields\":{\"headline\":\"Football transfer rumours: Jonny Evans and Mauro Icardi to Arsenal?\",\"byline\":\"Gregg Bakowski\"},\"isHosted\":false},{\"id\":\"football/blog/2016/aug/01/premier-league-2016-17-preview-arsenal\",\"type\":\"article\",\"sectionId\":\"football\",\"sectionName\":\"Football\",\"webPublicationDate\":\"2016-08-01T07:00:56Z\",\"webTitle\":\"Premier League 2016-17 preview No1: Arsenal | David Hytner\",\"webUrl\":\"https://www.theguardian.com/football/blog/2016/aug/01/premier-league-2016-17-preview-arsenal\",\"apiUrl\":\"https://content.guardianapis.com/football/blog/2016/aug/01/premier-league-2016-17-preview-arsenal\",\"fields\":{\"headline\":\"Premier League 2016-17 preview No1: Arsenal\",\"byline\":\"David Hytner\"},\"isHosted\":false},{\"id\":\"media/mind-your-language/2016/aug/01/capital-letters-out-swearwords-in-one-journalists-legacy\",\"type\":\"article\",\"sectionId\":\"media\",\"sectionName\":\"Media\",\"webPublicationDate\":\"2016-08-01T06:00:24Z\",\"webTitle\":\"Capital letters out, swearwords in: one journalist's legacy\",\"webUrl\":\"https://www.theguardian.com/media/mind-your-language/2016/aug/01/capital-letters-out-swearwords-in-one-journalists-legacy\",\"apiUrl\":\"https://content.guardianapis.com/media/mind-your-language/2016/aug/01/capital-letters-out-swearwords-in-one-journalists-legacy\",\"fields\":{\"headline\":\"Capital letters out, swearwords in: one journalist's legacy\",\"byline\":\"David Marsh\"},\"isHosted\":false},{\"id\":\"education/2016/aug/01/summer-holiday-fun-without-paying-a-fortune\",\"type\":\"article\",\"sectionId\":\"money\",\"sectionName\":\"Money\",\"webPublicationDate\":\"2016-08-01T06:00:24Z\",\"webTitle\":\"Family fun in the long summer hols without it costing you a fortune\",\"webUrl\":\"https://www.theguardian.com/education/2016/aug/01/summer-holiday-fun-without-paying-a-fortune\",\"apiUrl\":\"https://content.guardianapis.com/education/2016/aug/01/summer-holiday-fun-without-paying-a-fortune\",\"fields\":{\"headline\":\"Family fun in the long summer hols without it costing you a fortune\",\"byline\":\"Esther Shaw\"},\"isHosted\":false},{\"id\":\"football/2016/jul/31/wayne-rooney-manchester-united-premier-league\",\"type\":\"article\",\"sectionId\":\"football\",\"sectionName\":\"Football\",\"webPublicationDate\":\"2016-07-31T22:53:18Z\",\"webTitle\":\"Wayne Rooney detects return of the ‘old’ Manchester United this season\",\"webUrl\":\"https://www.theguardian.com/football/2016/jul/31/wayne-rooney-manchester-united-premier-league\",\"apiUrl\":\"https://content.guardianapis.com/football/2016/jul/31/wayne-rooney-manchester-united-premier-league\",\"fields\":{\"headline\":\"Wayne Rooney detects return of the ‘old’ Manchester United this season\",\"byline\":\"Guardian sport\"},\"isHosted\":false},{\"id\":\"sport/2016/aug/01/forget-about-great-stories-and-prevailing-public-sentiment-hawthorn-dont-care\",\"type\":\"article\",\"sectionId\":\"sport\",\"sectionName\":\"Sport\",\"webPublicationDate\":\"2016-07-31T20:30:13Z\",\"webTitle\":\"Forget about great stories and prevailing public sentiment – Hawthorn don't care | Jonathan Horn\",\"webUrl\":\"https://www.theguardian.com/sport/2016/aug/01/forget-about-great-stories-and-prevailing-public-sentiment-hawthorn-dont-care\",\"apiUrl\":\"https://content.guardianapis.com/sport/2016/aug/01/forget-about-great-stories-and-prevailing-public-sentiment-hawthorn-dont-care\",\"fields\":{\"headline\":\"Forget about great stories and prevailing public sentiment – Hawthorn don't care\",\"byline\":\"Jonathan Horn\"},\"isHosted\":false},{\"id\":\"sport/2016/aug/01/the-joy-of-six-olympic-demonstration-sports\",\"type\":\"article\",\"sectionId\":\"sport\",\"sectionName\":\"Sport\",\"webPublicationDate\":\"2016-07-31T20:30:12Z\",\"webTitle\":\"The Joy of Six: Olympic demonstration sports | Paul Connolly\",\"webUrl\":\"https://www.theguardian.com/sport/2016/aug/01/the-joy-of-six-olympic-demonstration-sports\",\"apiUrl\":\"https://content.guardianapis.com/sport/2016/aug/01/the-joy-of-six-olympic-demonstration-sports\",\"fields\":{\"headline\":\"The Joy of Six: Olympic demonstration sports\",\"byline\":\"Paul Connolly\"},\"isHosted\":false},{\"id\":\"sport/2016/jul/31/draymond-green-penis-picture-snapchat-basketball-olympics\",\"type\":\"article\",\"sectionId\":\"sport\",\"sectionName\":\"Sport\",\"webPublicationDate\":\"2016-07-31T18:27:41Z\",\"webTitle\":\"Draymond Green's woes continue after posting penis picture to Snapchat\",\"webUrl\":\"https://www.theguardian.com/sport/2016/jul/31/draymond-green-penis-picture-snapchat-basketball-olympics\",\"apiUrl\":\"https://content.guardianapis.com/sport/2016/jul/31/draymond-green-penis-picture-snapchat-basketball-olympics\",\"fields\":{\"headline\":\"Draymond Green's woes continue after posting penis picture to Snapchat\",\"byline\":\"Guardian sport\"},\"isHosted\":false}]}}";

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link Story} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<Story> extractStories(String query_results) {

        // DEBUG: used only for testing with known input
        query_results = SAMPLE_JSON_RESPONSE;

        // Create an empty ArrayList that we can start adding stories to
        ArrayList<Story> stories = new ArrayList<>();

        try {
            // Create a JSONObject from the SAMPLE_JSON_RESPONSE string
            JSONObject baseJsonResponse = new JSONObject(query_results);


            // Extract the JSONArray associated with the key called "results",
            JSONObject responseJsonResponse = baseJsonResponse.getJSONObject("response");
            JSONArray storyArray = responseJsonResponse.getJSONArray("results");

            // For each Story in the response, create an {@link Story} object
            for (int i = 0; i < storyArray.length(); i++) {

                // Get a single book at position i within the list of books
                JSONObject currentStory = storyArray.getJSONObject(i);

                String url = currentStory.getString("webUrl");
                // For a given book, extract the JSONObject associated with the
                // key called "properties", which represents a list of all properties
                // for that book.
                JSONObject fields = currentStory.getJSONObject("fields");

                // Extract the value for the key called "headline"
                String headline = fields.getString("headline");

                // Extract array of authors and turn it into a single string...
                String author = fields.getString("byline");

                // Create a new {@link Book} object with the title and authors from the response.
                Story story = new Story(headline, author, url);

                // Add the new {@link Book} to the list of books.
                stories.add(story);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the book JSON results", e);
        }

        // Return the list of books
        return stories;
    }
}
