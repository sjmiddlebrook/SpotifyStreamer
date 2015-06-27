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
 * Created by jackmiddlebrook on 6/20/15.
 * Custom ArrayAdapter for TrackData from Spotify
 */
public class TrackDataArrayAdapter extends ArrayAdapter<TrackData> {

    public TrackDataArrayAdapter(Context context, int resource) {
        super(context, resource);
    }

    private final static String TAG = TrackDataArrayAdapter.class.getSimpleName();

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TrackData data = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_tracks,
                    parent,
                    false);
        }

        ImageView albumImage = (ImageView) convertView.findViewById(R.id.list_item_track_image_view);
        TextView trackName = (TextView) convertView.findViewById(R.id.list_item_track_name_text_view);
        TextView albumName = (TextView) convertView.findViewById(R.id.list_item_album_name_text_view);

        if (!data.getAlbumImageUrl().equals("")) {
            Picasso.with(getContext())
                    .load(data.getAlbumImageUrl())
                    .resize(200, 200)
                    .into(albumImage);
        }

        trackName.setText(data.getTrackName());
        albumName.setText(data.getAlbumName());

        return convertView;
    }

}
