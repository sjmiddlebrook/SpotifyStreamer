package com.jackmiddlebrook.spotifystreamer;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

    public ArrayAdapter<String> mArtistAdapter;
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
            new FetchArtistTask().execute("");
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
//                    new FetchArtistTask().execute(searchText);
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

        mArtistAdapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.list_item_artist,
                R.id.list_item_artist_textview,
                artistList
        );

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
                final List<Artist> artistsResults = artistsPager.artists.items;
                final List<ArtistData> artistInfo = new ArrayList<>();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mArtistAdapter.clear();
                        for (Artist artist : artistsResults) {
                            mArtistAdapter.add(artist.name);
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
                        for (ArtistData data : artistInfo) {
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


    private class FetchArtistTask extends AsyncTask<String, Void, String[]> {

        private final String TAG = FetchArtistTask.class.getSimpleName();


        private String[] getArtistDataFromJson(String artistSearchJsonStr, int numOfArtist) throws JSONException{

            // These are the names of the JSON objects that need to be extracted.
            final String SPOTIFY_ARTISTS = "artists";
            final String SPOTIFY_ITEMS = "items";
            final String SPOTIFY_NAME = "name";

            JSONObject artistSearch = new JSONObject(artistSearchJsonStr);
            JSONObject artistsObject = artistSearch.getJSONObject(SPOTIFY_ARTISTS);
            JSONArray itemsArray = artistsObject.getJSONArray(SPOTIFY_ITEMS);

            int resultsLength = itemsArray.length();
            Log.d(TAG, "number of results: " + resultsLength);

            String[] resultStrs = new String[resultsLength];
            for(int i = 0; i < itemsArray.length(); i++) {
                // just get the artist from the JSON
                String artist;

                // Get the JSON object representing the item
                JSONObject artistItem = itemsArray.getJSONObject(i);

                artist = artistItem.getString(SPOTIFY_NAME);

                resultStrs[i] = artist;
            }

            return resultStrs;
        }

        @Override
        protected String[] doInBackground(String... strings) {

            if (strings.length == 0) {
                return null;
            }
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String artistJsonStr = null;

            String type = "artist";
            // default limit for number of objects returned
            int limit = 20;

            try {
                // Construct the URL for the Spotify query
                // Possible parameters are avaiable at Spotify's forecast API page, at
                // https://developer.spotify.com/web-api/user-guide/
                final String SPOTIFY_BASE_URL = "https://api.spotify.com/v1/search?";
                final String SEARCH_PARAM = "q";
                final String TYPE_PARAM = "type";

                Uri builtUri = Uri.parse(SPOTIFY_BASE_URL).buildUpon()
                        .appendQueryParameter(SEARCH_PARAM, strings[0])
                        .appendQueryParameter(TYPE_PARAM, type)
                        .build();


                URL url = new URL(builtUri.toString());

                Log.v(TAG, "built url " + builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                artistJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }

            int numArtists = 20;

            String[] artistArray = new String[numArtists];

            try {
                artistArray = getArtistDataFromJson(artistJsonStr, numArtists);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return artistArray;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            if (strings != null) {
                mArtistAdapter.clear();
                for (String artistName : strings) {
                    mArtistAdapter.add(artistName);
                }
            } else {
                // clear out the list
                mArtistAdapter.clear();

            }
        }
    }
}
