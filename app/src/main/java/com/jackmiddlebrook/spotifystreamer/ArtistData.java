package com.jackmiddlebrook.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jackmiddlebrook on 6/19/15.
 * Plain Old Java Object used for storing artist data and
 * transporting the data in Spotify Streamer app.
 */
public class ArtistData implements Parcelable {

    public String mName;
    public String mImageUrl;
    public String mSpotifyId;

    /**
     * Constructor
     *
     * @param name
     * @param imageUrl
     * @param spotifyId
     */

    public ArtistData(String name, String imageUrl, String spotifyId) {
        mName = name;
        mImageUrl = imageUrl;
        mSpotifyId = spotifyId;
    }

    @Override
    public String toString() {
        return "Artist Data [name=" + mName
                + ", image url= " + mImageUrl
                + ", spotify id= " + mSpotifyId + "]";
     }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mName);
        parcel.writeString(mImageUrl);
        parcel.writeString(mSpotifyId);
    }

    private ArtistData(Parcel in) {
        mName = in.readString();
        mImageUrl = in.readString();
        mSpotifyId = in.readString();
    }

    public static final Parcelable.Creator<ArtistData> CREATOR =
            new Parcelable.Creator<ArtistData>() {
                public ArtistData createFromParcel(Parcel in) {
                    return new ArtistData(in);
                }

                public ArtistData[] newArray(int size) {
                    return new ArtistData[size];
                }
            };
}
