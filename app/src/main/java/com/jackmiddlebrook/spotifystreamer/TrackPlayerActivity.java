package com.jackmiddlebrook.spotifystreamer;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;


public class TrackPlayerActivity extends AppCompatActivity {

    private static final String TAG_TRACK_PLAYER_FRAGMENT = "track_player_fragment";
    private TrackPlayerFragment mTrackPlayerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_player);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        FragmentManager fm = getFragmentManager();
        mTrackPlayerFragment = (TrackPlayerFragment) fm.findFragmentByTag(TAG_TRACK_PLAYER_FRAGMENT);

        if (mTrackPlayerFragment == null) {
            mTrackPlayerFragment = new TrackPlayerFragment();
            Bundle arguments = new Bundle();
            Intent intent = getIntent();
            String artistName = intent.getStringExtra(TrackPlayerFragment.ARTIST_NAME_ID);
            int songNumber = intent.getIntExtra(TrackPlayerFragment.SONG_NUMBER_ID, 1);
            List<TrackData> trackDataList = intent.getExtras().getParcelableArrayList(TrackPlayerFragment.TRACK_LIST_ID);
            arguments.putString(TrackPlayerFragment.ARTIST_NAME_ID, artistName);
            arguments.putInt(TrackPlayerFragment.SONG_NUMBER_ID, songNumber);
            arguments.putParcelableArrayList(TrackPlayerFragment.TRACK_LIST_ID, (ArrayList<? extends Parcelable>) trackDataList);
            mTrackPlayerFragment.setArguments(arguments);

            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.track_player_container, mTrackPlayerFragment, TAG_TRACK_PLAYER_FRAGMENT);
            ft.commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_track_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
