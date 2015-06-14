package com.jackmiddlebrook.spotifystreamer;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        String[] artistArray = {
                "The National",
                "Of Monsters and Men",
                "Arcade Fire",
                "Mumford and Sons",
                "Houndmouth",
                "St. Motel",
                "Jake Bugg",
                "Haim",
                "Neil Young",
                "The Beatles",
                "Chromeo"
        };

        List<String> artistList = new ArrayList<>(Arrays.asList(artistArray));

        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_artist,
                R.id.list_item_artist_textview,
                artistList
        );

        ListView listView = (ListView) rootView.findViewById(R.id.artist_listview);
        listView.setAdapter(listAdapter);

        return rootView;
    }
}
