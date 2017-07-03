package com.example.android.googlebooks;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Book>> {
    public static final String LOG_TAG = MainActivity.class.getName();
    private String googleBooksRequestUrlHeader = "https://www.googleapis.com/books/v1/volumes?q=";
    private String googleBooksRequestUrlFooter = "&maxResults=5";
    private String googleBooksRequestUrl = null;
    ProgressDialog mDialog = null;
    private boolean mLoaderCalled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("BUTTON PRESSED");
                EditText editText = (EditText) findViewById(R.id.editText);
                String searchTerm = editText.getText().toString();
                googleBooksRequestUrl = "" + googleBooksRequestUrlHeader + searchTerm + googleBooksRequestUrlFooter;
                mDialog = new ProgressDialog(MainActivity.this); // this = YourActivity
                mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mDialog.setMessage("Loading. Please wait...");
                mDialog.setIndeterminate(true);
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.show();
                if (getLoaderManager().getLoader(0) == null) {

                    /*
                     * Loader doesn't exist, so create and start it
                     */
                    getLoaderManager().restartLoader(0, null, MainActivity.this); //calls onCreateLoader()
                } else {

                    /*
                     * Loader already exists so all we need to do is initialise it
                     */
                    getLoaderManager().initLoader(0, null, MainActivity.this); //calls onCreateLoader()
                }
            }
        });

    }

    @Override
    public Loader<ArrayList<Book>> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "onCreateLoader Called");
        Log.v(LOG_TAG, "googleBooksRequestUrl: " + googleBooksRequestUrl);
        return new BookLoader(this, googleBooksRequestUrl);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Book>> loader, final ArrayList<Book> data) {
        mDialog.dismiss();
        ListView bookListView = (ListView) findViewById(R.id.list);
        BookAdapter booksAdapter = new BookAdapter(this, data);
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book currentBook = data.get(position);
                String url = currentBook.getmUrl();
                Uri webpage = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
        bookListView.setAdapter(booksAdapter);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Book>> loader) {

    }

    //Removes focus from the EditText if we click anywhere else.
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
