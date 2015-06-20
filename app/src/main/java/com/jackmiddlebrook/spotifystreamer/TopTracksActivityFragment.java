package com.jackmiddlebrook.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksActivityFragment extends Fragment {

    public TopTracksActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);
        Intent intent = getActivity().getIntent();
        String spotifyId = intent.getStringExtra(Intent.EXTRA_TEXT);
        TextView spotifyIdTextView = (TextView) rootView.findViewById(R.id.spotify_id_text_view);
        spotifyIdTextView.setText(spotifyId);
        return rootView;
    }

}
