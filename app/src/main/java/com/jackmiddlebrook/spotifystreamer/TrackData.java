package com.jackmiddlebrook.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jackmiddlebrook on 6/20/15.
 * Plain Old Java Object used for storing track data and
 * transporting the data in Spotify Streamer app.
 */
public class TrackData implements Parcelable {

    private String mTrackName;
    private String mAlbumImageUrl;
    private String mAlbumName;
    private String mPreviewUrl;

    /**
     * Constructor
     *
     * @param trackName
     * @param imageUrl
     * @param albumName
     * @param previewUrl
     */

    public TrackData(String trackName, String imageUrl, String albumName, String previewUrl) {
        mTrackName = trackName;
        mAlbumImageUrl = imageUrl;
        mAlbumName = albumName;
        mPreviewUrl = previewUrl;
    }

    public String getTrackName() {
        return mTrackName;
    }

    public void setTrackName(String trackName) {
        mTrackName = trackName;
    }

    public String getAlbumImageUrl() {
        return mAlbumImageUrl;
    }

    public void setAlbumImageUrl(String albumImageUrl) {
        mAlbumImageUrl = albumImageUrl;
    }

    public String getAlbumName() {
        return mAlbumName;
    }

    public void setAlbumName(String albumName) {
        mAlbumName = albumName;
    }

    public String getPreviewUrl() {
        return mPreviewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        mPreviewUrl = previewUrl;
    }

    @Override
    public String toString() {
        return "Artist Data [track name=" + mTrackName
                + ", image url= " + mAlbumImageUrl
                + ", album name= " + mAlbumName
                + ", preview url= " + mPreviewUrl + "]";
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
        parcel.writeString(mPreviewUrl);
    }

    private TrackData(Parcel in) {
        mTrackName = in.readString();
        mAlbumImageUrl = in.readString();
        mAlbumName = in.readString();
        mPreviewUrl = in.readString();
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
