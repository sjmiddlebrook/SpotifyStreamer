package com.jackmiddlebrook.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * A placeholder fragment containing a simple view.
 */
public class TrackPlayerActivityFragment extends Fragment {

    private final String TAG = TrackPlayerActivityFragment.class.getSimpleName();

    public TrackPlayerActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_track_player, container, false);
        Intent intent = getActivity().getIntent();
        String artistName = intent.getStringExtra("ARTIST_NAME");
        TrackData trackData = intent.getExtras().getParcelable("TRACK_DATA");
        Log.v(TAG, "Track: " + trackData.toString());
        Log.v(TAG, "Artist Name: " + artistName);

        TextView artistNameTextView = (TextView) rootView.findViewById(R.id.player_artist_name);
        TextView albumNameTextView = (TextView) rootView.findViewById(R.id.player_album_name);
        TextView trackNameTextView = (TextView) rootView.findViewById(R.id.player_track_name);
        ImageView albumImageView = (ImageView) rootView.findViewById(R.id.player_album_image);

        artistNameTextView.setText(artistName);
        albumNameTextView.setText(trackData.getAlbumName());
        trackNameTextView.setText(trackData.getTrackName());

        if (!trackData.getAlbumImageUrl().equals("")) {
            Picasso.with(getActivity().getApplicationContext())
                    .load(trackData.getAlbumImageUrl())
                    .resize(700, 700)
                    .into(albumImageView);
        }

        return rootView;
    }
}
