package com.jackmiddlebrook.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksActivityFragment extends Fragment {

    private final String TAG = TopTracksActivityFragment.class.getSimpleName();

    private TrackDataArrayAdapter mTrackAdapter;

    public TopTracksActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);
        Intent intent = getActivity().getIntent();
        String spotifyId = intent.getStringExtra(Intent.EXTRA_TEXT);
        String artistName = intent.getStringExtra("ARTIST_NAME");
        mTrackAdapter = new TrackDataArrayAdapter(
                getActivity(),
                R.id.list_item_track_name_text_view
        );

        getTopTracksFromSpotify(spotifyId);

        ListView listView = (ListView) rootView.findViewById(R.id.tracks_list_view);
        listView.setAdapter(mTrackAdapter);

        getActionBar().setSubtitle(artistName);

        return rootView;
    }

    private void getTopTracksFromSpotify(String spotifyId) {
        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("country", "US");
        spotify.getArtistTopTrack(spotifyId, queryMap, new Callback<Tracks>() {
            @Override
            public void success(Tracks tracks, Response response) {
                List<Track> trackResults = tracks.tracks;
                final List<TrackData> trackDataResults = new ArrayList<>();
                for (Track track : trackResults) {
                    String trackName = track.name;
                    AlbumSimple albumInfo = track.album;
                    List<Image> imageList = albumInfo.images;
                    String imageUrl = "";
                    for (int i = 0; i < imageList.size(); i++) {
                        if (i == 0) {
                            Image image = imageList.get(i);
                            imageUrl = image.url;
                        }
                    }
                    String albumName = albumInfo.name;
                    trackDataResults.add(new TrackData(trackName, imageUrl, albumName));
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTrackAdapter.clear();
                        if (trackDataResults.size() > 0) {
                            for (TrackData data : trackDataResults) {
                                mTrackAdapter.add(data);
                                Log.v(TAG, data.toString());
                            }
                        } else {
                            mTrackAdapter.clear();
                            Toast.makeText(getActivity(),
                                    "No Top Tracks were found. Try searching again.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, error.toString());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTrackAdapter.clear();
                        Toast.makeText(getActivity(),
                                "No Top Tracks were found. Try searching again.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

}
