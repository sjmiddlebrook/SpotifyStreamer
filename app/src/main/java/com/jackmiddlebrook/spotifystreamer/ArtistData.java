package com.jackmiddlebrook.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jackmiddlebrook on 6/19/15.
 * Plain Old Java Object used for storing artist data and
 * transporting the data in Spotify Streamer app.
 */
public class ArtistData implements Parcelable {

    private String mName;
    private String mImageUrl;
    private String mSpotifyId;

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

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public String getSpotifyId() {
        return mSpotifyId;
    }

    public void setSpotifyId(String spotifyId) {
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
