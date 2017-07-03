package com.example.android.googlebooks;

import android.graphics.Bitmap;

/**
 * Created by Nissan on 6/6/2017.
 */

public class Book {
    private Bitmap mThumbnail;
    private String mTitle;
    private String mAuthor;
    private String mPublishedDate;
    private String mDescription;

    private String mUrl;

    Book(Bitmap thumbnail, String title, String author, String publishedDate, String description,String url) {
        this.mThumbnail = thumbnail;
        this.mTitle = title;
        this.mAuthor = author;
        this.mPublishedDate = publishedDate;
        this.mDescription = description;
        this.mUrl = url;
    }

    public Bitmap getmThumbnail() {
        return mThumbnail;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public String getmPublishedDate() {
        return mPublishedDate;
    }

    public String getmDescription() {
        return mDescription;
    }

    public String getmUrl() {
        return mUrl;
    }
}
