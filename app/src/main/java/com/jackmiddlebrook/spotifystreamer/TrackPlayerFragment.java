package com.jackmiddlebrook.spotifystreamer;

import android.app.Fragment;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class TrackPlayerFragment extends Fragment {

    private static final String SONG_PROGRESS_ID = "SONG_PROGRESS";
    private static final String SEEK_PROGRESS_ID = "SEEK_PROGRESS";
    private final String TAG = TrackPlayerFragment.class.getSimpleName();
    static final String ARTIST_NAME_ID = "ARTIST_NAME";
    static final String SONG_NUMBER_ID = "SONG_NUMBER";
    static final String TRACK_LIST_ID = "TRACK_DATA_LIST";
    private MediaPlayer mMediaPlayer;
    private List<TrackData> mTrackDataList;
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
    private TextView mPlayerSecondsTextView;
    private int mSongNumber;
    private int mCurrentPos;
    private int mSongPosition;
    private boolean mRotated;
    private String mArtistName;

    public TrackPlayerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mSongPosition = savedInstanceState.getInt(SONG_PROGRESS_ID);
            mCurrentPos = savedInstanceState.getInt(SEEK_PROGRESS_ID);
            mRotated = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_track_player, container, false);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mArtistName = arguments.getString(ARTIST_NAME_ID);
            mSongNumber = arguments.getInt(SONG_NUMBER_ID);
            mTrackDataList = arguments.getParcelableArrayList(TRACK_LIST_ID);
        }
        mTrackData = mTrackDataList.get(mSongNumber);

        mArtistNameTextView = (TextView) rootView.findViewById(R.id.player_artist_name);
        mAlbumNameTextView = (TextView) rootView.findViewById(R.id.player_album_name);
        mTrackNameTextView = (TextView) rootView.findViewById(R.id.player_track_name);
        mPlayerSecondsTextView = (TextView) rootView.findViewById(R.id.player_start_seconds);
        mAlbumImageView = (ImageView) rootView.findViewById(R.id.player_album_image);
        mPlayButton = (ImageButton) rootView.findViewById(R.id.play_button);
        mBackButton = (ImageButton) rootView.findViewById(R.id.back_button);
        mForwardButton = (ImageButton) rootView.findViewById(R.id.forward_button);
        mSeekBar = (SeekBar) rootView.findViewById(R.id.seek_bar);

        mArtistNameTextView.setText(mArtistName);
        updateTrack();

        playPreview(mTrackData.getPreviewUrl());
        mPlayButton.setImageResource(R.mipmap.ic_pause_black_24dp);

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

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMediaPlayer != null) {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                    mPlayButton.setImageResource(R.mipmap.ic_play_arrow_black_24dp);

                }
                if (mSongNumber > 0) {
                    mSongNumber -= 1;
                    mTrackData = mTrackDataList.get(mSongNumber);
                    updateTrack();
                } else {
                    mSeekBar.setProgress(0);
                    mPlayerSecondsTextView.setText("0:00");
                }

            }
        });

        mForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMediaPlayer != null) {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                    mPlayButton.setImageResource(R.mipmap.ic_play_arrow_black_24dp);

                }
                if (mSongNumber < mTrackDataList.size() - 1) {
                    mSongNumber += 1;
                    mTrackData = mTrackDataList.get(mSongNumber);
                    updateTrack();
                } else {
                    mSeekBar.setProgress(0);
                    mPlayerSecondsTextView.setText("0:00");
                }
            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mMediaPlayer != null) {
                    mMediaPlayer.seekTo(seekBar.getProgress());
                }
            }
        });

        return rootView;
    }

    private void updateTrack() {
        mAlbumNameTextView.setText(mTrackData.getAlbumName());
        mTrackNameTextView.setText(mTrackData.getTrackName());

        if (!mTrackData.getAlbumImageUrl().equals("")) {
            Picasso.with(getActivity().getApplicationContext())
                    .load(mTrackData.getAlbumImageUrl())
                    .resize(700, 700)
                    .into(mAlbumImageView);
        }
        mSeekBar.setProgress(0);
        mPlayerSecondsTextView.setText("0:00");
    }

    private void playPreview(String previewUrl) {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(previewUrl);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(final MediaPlayer mediaPlayer) {
                    mMediaPlayer.start();
                    mSeekBar.setMax(mMediaPlayer.getDuration());
                    Runnable progressRunnable = new Runnable() {
                        @Override
                        public void run() {
                            if (mMediaPlayer != null) {
                                if (mRotated) {
                                    mMediaPlayer.seekTo(mSongPosition);
                                    mSeekBar.setProgress(mCurrentPos);
                                    mRotated = false;
                                }
                                mSongPosition = mMediaPlayer.getCurrentPosition();
                                mCurrentPos = (int) Math.round(mSongPosition / 1000.0) * 1000;
                                mCurrentPos += 1000;
                                if (mCurrentPos > 30000) {
                                    mCurrentPos = 30000;
                                }
                                mSeekBar.setProgress(mCurrentPos);
                                String seconds;
                                if (mCurrentPos < 10000) {
                                    seconds = "0:0" + mCurrentPos / 1000;
                                } else {
                                    seconds = "0:" + mCurrentPos / 1000;
                                }
                                mPlayerSecondsTextView.setText(seconds);
                                mHandler.postDelayed(this, 1000);
                            }
                        }
                    };

                    progressRunnable.run();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMediaPlayer != null) mMediaPlayer.release();
        mMediaPlayer = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(SONG_PROGRESS_ID, mSongPosition);
        outState.putInt(SEEK_PROGRESS_ID, mCurrentPos);
        super.onSaveInstanceState(outState);
    }
}
