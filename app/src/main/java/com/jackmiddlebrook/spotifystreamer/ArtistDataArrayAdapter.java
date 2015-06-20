package com.jackmiddlebrook.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by jackmiddlebrook on 6/19/15.
 * Custom ArrayAdapter for ArtistData from Spotify
 */
public class ArtistDataArrayAdapter extends ArrayAdapter<ArtistData> {

    public ArtistDataArrayAdapter(Context context, int resource) {
        super(context, resource);
    }

    private final static String TAG = ArtistDataArrayAdapter.class.getSimpleName();

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ArtistData data = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_artist,
                    parent,
                    false);
        }

        ImageView artistImage = (ImageView) convertView.findViewById(R.id.list_item_artist_image_view);
        TextView artistName = (TextView) convertView.findViewById(R.id.list_item_artist_textview);

        if (!data.mImageUrl.equals("")) {
            Picasso.with(getContext())
                    .load(data.mImageUrl)
                    .resize(200, 200)
                    .into(artistImage);
        }

        artistName.setText(data.mName);

        return convertView;
    }
}
