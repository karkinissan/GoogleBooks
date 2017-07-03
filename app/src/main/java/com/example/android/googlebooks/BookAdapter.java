package com.example.android.googlebooks;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Nissan on 6/6/2017.
 */

public class BookAdapter extends ArrayAdapter<Book> {
    public BookAdapter(@NonNull Context context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }
        Book book = getItem(position);

        ImageView thumbnailView = (ImageView) listItemView.findViewById(R.id.thumbnail);
        thumbnailView.setImageBitmap(book.getmThumbnail());

        TextView titleView = (TextView) listItemView.findViewById(R.id.book_title);
        titleView.setText(book.getmTitle());

        TextView authorView = (TextView) listItemView.findViewById(R.id.authors_name);
        authorView.setText(book.getmAuthor());

        TextView publishedDateView = (TextView) listItemView.findViewById(R.id.published_date);
        publishedDateView.setText(book.getmPublishedDate());

        TextView descriptionView = (TextView) listItemView.findViewById(R.id.description);
        descriptionView.setText(book.getmDescription());
        String text = (String) descriptionView.getText();

        return listItemView;
    }

}
