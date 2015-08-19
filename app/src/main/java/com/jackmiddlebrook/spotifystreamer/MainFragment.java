package com.jackmiddlebrook.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
public class MainFragment extends Fragment {

    private final String TAG = MainFragment.class.getSimpleName();

    private ArtistDataArrayAdapter mArtistAdapter;
    private List<ArtistData> mArtistDataList;
    private final String DATA_VALUE_KEY = "ARTIST_DATA_LIST";
    private final String DATA_BUNDLE_KEY = "ARTIST_DATA_BUNDLE";


    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface TrackCallback {
        /**
         * TopTracksFragmentCallback for when an item has been selected.
         */
        void onItemSelected(String artistName, String spotifyId);
    }

    public MainFragment() {
    }

    public void setData(List<ArtistData> dataList) {
        mArtistDataList = dataList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        final EditText searchEditText = (EditText) rootView.findViewById(R.id.artist_search_edit_text);
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    String searchText = String.valueOf(textView.getText());

                    ListView listView = (ListView) getActivity().findViewById(R.id.tracks_list_view);
                    if (listView != null) {
                        listView.setAdapter(null);
                    }
                    getArtistsFromSpotify(searchText);
                    handled = true;
                }

                hideKeyboard(getActivity(), searchEditText.getWindowToken());

                return handled;
            }
        });

        mArtistAdapter = new ArtistDataArrayAdapter(
                getActivity(),
                R.id.list_item_artist_textview);

        if (savedInstanceState != null) {
            Bundle bundle = savedInstanceState.getBundle(DATA_BUNDLE_KEY);
            mArtistDataList = bundle.getParcelableArrayList(DATA_VALUE_KEY);
            if (mArtistDataList != null) {
                for (ArtistData data : mArtistDataList) {
                    mArtistAdapter.add(data);
                }
            }
        }

        ListView listView = (ListView) rootView.findViewById(R.id.artist_listview);
        listView.setAdapter(mArtistAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ArtistData data = mArtistAdapter.getItem(i);
                String artistId = data.getSpotifyId();
                String artistName = data.getName();

                ((TrackCallback) getActivity())
                        .onItemSelected(artistName, artistId);

            }
        });

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
                setData(artistInfo);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mArtistAdapter.clear();
                        if (artistInfo.size() > 0) {
                            for (ArtistData data : artistInfo) {
                                mArtistAdapter.add(data);
                            }
                        } else {
                            mArtistAdapter.clear();
                            Toast.makeText(getActivity(),
                                    "No Artist were found. Try searching again.",
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
                        setData(null);
                        mArtistAdapter.clear();
                        Toast.makeText(getActivity(),
                                "No Artist were found. Try searching again.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public Bundle getBundledData() {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(DATA_VALUE_KEY, (ArrayList<? extends Parcelable>) mArtistDataList);
        return bundle;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(DATA_BUNDLE_KEY, getBundledData());
    }

    public static void hideKeyboard(Activity activity,
                                    IBinder windowToken) {
        InputMethodManager mgr =
                (InputMethodManager) activity.getSystemService
                        (Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken,
                0);
    }
}
