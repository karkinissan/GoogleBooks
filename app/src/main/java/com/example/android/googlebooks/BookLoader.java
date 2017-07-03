package com.example.android.googlebooks;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import static com.example.android.googlebooks.MainActivity.LOG_TAG;

/**
 * Created by Nissan on 6/6/2017.
 */

public class BookLoader extends AsyncTaskLoader<ArrayList<Book>> {
    private String mURL;
    public BookLoader(Context context, String url) {
        super(context);
        mURL = url;
    }


    @Override
    public ArrayList<Book> loadInBackground() {
        Log.v(LOG_TAG,"BookLoader's loadinBackground() called");
        ArrayList<Book> books = QueryUtils.fetchBookData(mURL);
        Log.v(LOG_TAG,"Bookloader's loadInBackgrond() returning books");
        return books;
    }

    @Override
    protected void onStartLoading() {
        Log.v(LOG_TAG,"BookLoader's onStartLoading() called");
        forceLoad();
    }
}
