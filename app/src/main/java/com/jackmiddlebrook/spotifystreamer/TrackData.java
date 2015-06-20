package com.jackmiddlebrook.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jackmiddlebrook on 6/20/15.
 * Plain Old Java Object used for storing track data and
 * transporting the data in Spotify Streamer app.
 */
public class TrackData implements Parcelable {


    public String mTrackName;
    public String mAlbumImageUrl;
    public String mAlbumName;

    /**
     * Constructor
     *
     * @param trackName
     * @param imageUrl
     * @param albumName
     */

    public TrackData(String trackName, String imageUrl, String albumName) {
        mTrackName = trackName;
        mAlbumImageUrl = imageUrl;
        mAlbumName = albumName;
    }

    @Override
    public String toString() {
        return "Artist Data [track name=" + mTrackName
                + ", image url= " + mAlbumImageUrl
                + ", album name= " + mAlbumName + "]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mTrackName);
        parcel.writeString(mAlbumImageUrl);
        parcel.writeString(mAlbumName);
    }

    private TrackData(Parcel in) {
        mTrackName = in.readString();
        mAlbumImageUrl = in.readString();
        mAlbumName = in.readString();
    }

    public static final Parcelable.Creator<TrackData> CREATOR =
            new Parcelable.Creator<TrackData>() {
                public TrackData createFromParcel(Parcel in) {
                    return new TrackData(in);
                }

                public TrackData[] newArray(int size) {
                    return new TrackData[size];
                }
            };

}
