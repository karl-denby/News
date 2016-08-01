package com.example.android.news;

public class Story {
    private String mHeadline;
    private String mAuthor;
    private String mURL;

    public Story(String headline, String author, String url) {
        mHeadline= headline;
        mAuthor = author;
        mURL = url;
    }

    public String getTitle() {return mHeadline;}

    public String getAuthor() {return mAuthor;}

    public String getURL() {return mURL;}
};