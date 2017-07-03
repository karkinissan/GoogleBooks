package com.example.android.googlebooks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static com.example.android.googlebooks.MainActivity.LOG_TAG;

/**
 * Created by Nissan on 6/6/2017.
 */

public final class QueryUtils {

    private QueryUtils() {
    }

    public static ArrayList<Book> fetchBookData(String requestURL) {
        Log.v(LOG_TAG, "QueryUtils' fetchBookData called");
        if (requestURL == null) {
            Log.v(LOG_TAG, "fetchBookData Nothing in requestURL");
            return null;
        }
        String jsonResponse = null;
        URL url = getURL(requestURL);
        try {

            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
            e.printStackTrace();
        }
        ArrayList<Book> books = extractBooks(jsonResponse);
        return books;


    }

    private static String makeHttpRequest(URL url) throws IOException {
        Log.v(LOG_TAG, "QueryUtils' makeHttpRequest called");
        String jsonResponse = "";
        if (url == null) {
            Log.e(LOG_TAG, "URL Null. Returning blank JSON Response");
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(1500);
            urlConnection.setReadTimeout(1000);
            urlConnection.connect();
            Log.v(LOG_TAG, "URL RESPONSE CODE: " + urlConnection.getResponseCode());
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseMessage());
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        Log.v(LOG_TAG, "makeHttpRequest returning JsonResponse");
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        Log.v(LOG_TAG, "QueryUtils' readFromFromStream called");
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();
            }

        }
        Log.v(LOG_TAG, "readFromStream returning output String");
        return output.toString();
    }

    private static URL getURL(String urlInString) {
        Log.v(LOG_TAG, "QueryUtils' getURL called");
        URL url = null;
        try {
            url = new URL(urlInString);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error creating URl", e);
            e.printStackTrace();
        }
        return url;
    }

    public static ArrayList<Book> extractBooks(String jsonResponse) {
        Log.v(LOG_TAG, "QueryUtils' extractBooks Called");
        ArrayList<Book> books = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);

            JSONArray itemsArray = baseJsonResponse.getJSONArray("items");
            Log.v(LOG_TAG, "itemsArray Length: " + itemsArray.length());

            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject currentBook = itemsArray.getJSONObject(i);
                JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");
                String title = volumeInfo.getString("title");
                String authors = null;
                if (volumeInfo.has("authors")) {
                    JSONArray authorsArray = volumeInfo.getJSONArray("authors");
                    authors = extractAuthors(authorsArray);
                } else authors = "No authors listed";

                String fullPublishedDate = null;
                String yearPublishedDate = null;
                if (volumeInfo.has("publishedDate")) {
                    fullPublishedDate = volumeInfo.getString("publishedDate");
                    yearPublishedDate = fullPublishedDate.substring(0, 4);
                } else {
                    yearPublishedDate = "";
                }
                String fullDescription = null;
                if (volumeInfo.has("description")) {
                    fullDescription = volumeInfo.getString("description");
                } else {
                    fullDescription = "No description available.";
                    Log.v(LOG_TAG, "No Description Found For: " + title);
                }
                Bitmap thumbnailBitmap = null;
                if (volumeInfo.has("imageLinks")) {
                    JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                    String smallThumbnailURL = imageLinks.getString("smallThumbnail");

                    thumbnailBitmap = getBitmapFromURL(smallThumbnailURL);
                }
                String infoLink = volumeInfo.getString("infoLink");
//                System.out.println("Book Info:");
//                System.out.println("Title: "+title);
//                System.out.println("Authors: "+authors);
//                System.out.println("Published Date: "+yearPublishedDate);
//                System.out.println("Descripton: "+fullDescription);
                books.add(new Book(thumbnailBitmap, title, authors, yearPublishedDate, fullDescription, infoLink));
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return books;
    }

    private static String getShortDescription(String fullDescription) {
        String[] words = fullDescription.split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= 25; i++) {
            System.out.print(words[i] + " ");
            sb.append(words[i]).append(" ");
        }
        sb.delete(sb.length() - 2, sb.length() - 1);
        sb.append("...");
        return sb.toString();
    }


    public static Bitmap getBitmapFromURL(String src) {
        Log.v(LOG_TAG, "QueryUtils' getBitmapFromURL Called");

        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.v(LOG_TAG, "getBitmapFromURL returning Bitmap");
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    private static String extractAuthors(JSONArray authorsArray) {
        StringBuilder authors = new StringBuilder();
        for (int i = 0; i < authorsArray.length(); i++) {
            try {
                authors.append(authorsArray.getString(i));
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            if (authorsArray.length() != 1) {
                authors.append(",\n");
            }
        }
        authors.delete(authors.length() - 1, authors.length());
        if (authorsArray.length() != 1) {
            authors.delete(authors.length() - 1, authors.length());
        }
        return authors.toString();
    }
}
