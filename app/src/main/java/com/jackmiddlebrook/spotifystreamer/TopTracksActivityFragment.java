package com.jackmiddlebrook.spotifystreamer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
        final String artistName = intent.getStringExtra("ARTIST_NAME");
        mTrackAdapter = new TrackDataArrayAdapter(
                getActivity(),
                R.id.list_item_track_name_text_view
        );

        getTopTracksFromSpotify(spotifyId);

        ListView listView = (ListView) rootView.findViewById(R.id.tracks_list_view);
        listView.setAdapter(mTrackAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), TrackPlayerActivity.class);
                intent.putExtra("TRACK_DATA", mTrackAdapter.getItem(i));
                intent.putExtra("ARTIST_NAME", artistName);
                startActivity(intent);
            }
        });

        getActionBar().setSubtitle(artistName);

        return rootView;
    }

    private void getTopTracksFromSpotify(String spotifyId) {
        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();
        Map<String, Object> queryMap = new HashMap<>();
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String countryCode = sharedPrefs.getString(
                getString(R.string.country_pref_key),
                getString(R.string.country_pref_default_value)
        );
        queryMap.put("country", countryCode);
        spotify.getArtistTopTrack(spotifyId, queryMap, new Callback<Tracks>() {
            @Override
            public void success(Tracks tracks, Response response) {
                List<Track> trackResults = tracks.tracks;
                final List<TrackData> trackDataResults = new ArrayList<>();
                for (Track track : trackResults) {
                    String trackName = track.name;
                    String previewUrl = track.preview_url;
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
                    trackDataResults.add(new TrackData(trackName, imageUrl, albumName, previewUrl));
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTrackAdapter.clear();
                        if (trackDataResults.size() > 0) {
                            for (TrackData data : trackDataResults) {
                                mTrackAdapter.add(data);
                            }
                        } else {
                            mTrackAdapter.clear();
                            Toast.makeText(getActivity(),
                                    getString(R.string.no_tracks_found_text),
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
                                getString(R.string.no_tracks_found_text),
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
