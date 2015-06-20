package com.jackmiddlebrook.spotifystreamer;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private final String TAG = MainActivityFragment.class.getSimpleName();

    public ArtistDataArrayAdapter mArtistAdapter;
    public ListView mListView;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.artist_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        final EditText searchEditText = (EditText) rootView.findViewById(R.id.artist_search_edit_text);
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    String searchText = String.valueOf(textView.getText());
                    getArtistsFromSpotify(searchText);
                    handled = true;
                }

                //hide the keyboard
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);

                return handled;
            }
        });

        List<String> artistList = new ArrayList<>();

        mArtistAdapter = new ArtistDataArrayAdapter(
                getActivity(),
                R.id.list_item_artist_textview);

        mListView = (ListView) rootView.findViewById(R.id.artist_listview);
        mListView.setAdapter(mArtistAdapter);

        return rootView;
    }

    private void getArtistsFromSpotify(final String artistName) {

        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();

        spotify.searchArtists(artistName, new Callback<ArtistsPager>() {
            @Override
            public void success(ArtistsPager artistsPager, Response response) {
                List<Artist> artistsResults = artistsPager.artists.items;
                final List<ArtistData> artistInfo = new ArrayList<>();
                for (Artist artist : artistsResults) {
                    String name = artist.name;
                    List<Image> imageList = artist.images;
                    String imageUrl = "";
                    for (int i = 0; i < imageList.size(); i++) {
                        if (i == 0) {
                            Image image = imageList.get(i);
                            imageUrl = image.url;
                        }
                    }
                    String spotifyId = artist.id;
                    artistInfo.add(new ArtistData(name, imageUrl, spotifyId));
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mArtistAdapter.clear();
                        for (ArtistData data : artistInfo) {
                            mArtistAdapter.add(data);
                            Log.v(TAG, data.toString());
                        }
                    }
                });


            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, error.toString());
            }
        });

    }
}
