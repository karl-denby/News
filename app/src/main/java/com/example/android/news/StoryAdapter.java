package com.example.android.news;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class StoryAdapter extends ArrayAdapter<Story> {

    public StoryAdapter(Context context, ArrayList<Story> Stories) {
        super(context, 0, Stories);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Story story = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.news_story, parent, false);
        }
        // Lookup view for data population
        TextView tvTitle  = (TextView) convertView.findViewById(R.id.text1);
        TextView tvAuthor = (TextView) convertView.findViewById(R.id.text2);

        // Populate the data into the template view using the data object
        tvTitle.setText(story.getTitle());
        tvAuthor.setText(story.getAuthor());

        // Return the completed view to render on screen
        return convertView;
    }

}