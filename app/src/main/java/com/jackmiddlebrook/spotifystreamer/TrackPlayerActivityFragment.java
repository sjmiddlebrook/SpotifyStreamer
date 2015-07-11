package com.jackmiddlebrook.spotifystreamer;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;


/**
 * A placeholder fragment containing a simple view.
 */
public class TrackPlayerActivityFragment extends Fragment {

    private final String TAG = TrackPlayerActivityFragment.class.getSimpleName();
    private MediaPlayer mMediaPlayer;
    private TrackData mTrackData;
    private ImageButton mPlayButton;
    private TextView mArtistNameTextView;
    private TextView mAlbumNameTextView;
    private TextView mTrackNameTextView;
    private ImageView mAlbumImageView;
    private ImageButton mBackButton;
    private ImageButton mForwardButton;
    private SeekBar mSeekBar;
    private Handler mHandler = new Handler();

    public TrackPlayerActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_track_player, container, false);
        Intent intent = getActivity().getIntent();
        String artistName = intent.getStringExtra("ARTIST_NAME");
        mTrackData = intent.getExtras().getParcelable("TRACK_DATA");
        Log.v(TAG, "Track: " + mTrackData.toString());
        Log.v(TAG, "Artist Name: " + artistName);

        mArtistNameTextView = (TextView) rootView.findViewById(R.id.player_artist_name);
        mAlbumNameTextView = (TextView) rootView.findViewById(R.id.player_album_name);
        mTrackNameTextView = (TextView) rootView.findViewById(R.id.player_track_name);
        mAlbumImageView = (ImageView) rootView.findViewById(R.id.player_album_image);
        mPlayButton = (ImageButton) rootView.findViewById(R.id.play_button);
        mBackButton = (ImageButton) rootView.findViewById(R.id.back_button);
        mForwardButton = (ImageButton) rootView.findViewById(R.id.forward_button);
        mSeekBar = (SeekBar) rootView.findViewById(R.id.seek_bar);

        mArtistNameTextView.setText(artistName);
        mAlbumNameTextView.setText(mTrackData.getAlbumName());
        mTrackNameTextView.setText(mTrackData.getTrackName());

        if (!mTrackData.getAlbumImageUrl().equals("")) {
            Picasso.with(getActivity().getApplicationContext())
                    .load(mTrackData.getAlbumImageUrl())
                    .resize(700, 700)
                    .into(mAlbumImageView);
        }

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMediaPlayer == null) {
                    playPreview(mTrackData.getPreviewUrl());
                    mPlayButton.setImageResource(R.mipmap.ic_pause_black_24dp);

                } else {
                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.pause();
                        mPlayButton.setImageResource(R.mipmap.ic_play_arrow_black_24dp);
                    } else {
                        mMediaPlayer.start();
                        mPlayButton.setImageResource(R.mipmap.ic_pause_black_24dp);

                    }
                }
            }
        });

        return rootView;
    }

    private void playPreview(String previewUrl) {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(previewUrl);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mMediaPlayer.start();
                    mSeekBar.setMax(mMediaPlayer.getDuration());
                    Runnable progressRunnable = new Runnable() {
                        @Override
                        public void run() {
                            mSeekBar.setProgress(mMediaPlayer.getCurrentPosition());
                            mHandler.postDelayed(this, 1000);
                        }
                    };

                    new Thread(progressRunnable).start();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) mMediaPlayer.release();
        mMediaPlayer = null;
    }
}
