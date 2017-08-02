package com.eazeup.eazehomework.api.model;

import android.os.Parcel;

import com.eazeup.eazehomework.api.model.response.DataItem;

public class Gif implements android.os.Parcelable {

    private String mImageUrl;
    private String mImageUrlSmall;
    private String mShareUrl;
    private String mId;
    private String mPreviewGifUrl;
    private String mDownsizedMediumGifUrl;
    private String mDownsizedGifUrl;

    public static Gif from(DataItem dataItem) {
        Gif gif = new Gif();
        gif.mImageUrl = dataItem.images.original.url;
        gif.mImageUrlSmall = dataItem.images.fixedHeightStill.url;
        gif.mShareUrl = dataItem.bitlyGifUrl;
        gif.mId = dataItem.id;
        if ((dataItem.images.previewGif != null)) {
            gif.mPreviewGifUrl = dataItem.images.previewGif.url;
        }
        gif.mDownsizedMediumGifUrl = dataItem.images.downsizedMedium.url;
        gif.mDownsizedGifUrl = dataItem.images.downsized.url;
        return gif;
    }

    public String getImageUrlSmall() {
        return mImageUrlSmall;
    }

    public String getOriginalImageUrl() {
        return mImageUrl;
    }

    public String getDownsizeGifUrl() {
        return mDownsizedGifUrl;
    }

    public String getDownsizeMediumGifUrl() {
        return mDownsizedMediumGifUrl;//downsized_medium
    }

    public String getId() {
        return mId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getPreviewGifUrl() {
        return mPreviewGifUrl;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mImageUrl);
        dest.writeString(this.mImageUrlSmall);
        dest.writeString(this.mShareUrl);
        dest.writeString(this.mId);
        dest.writeString(this.mPreviewGifUrl);
        dest.writeString(this.mDownsizedMediumGifUrl);
        dest.writeString(this.mDownsizedGifUrl);
    }

    public Gif() {
    }

    protected Gif(Parcel in) {
        this.mImageUrl = in.readString();
        this.mImageUrlSmall = in.readString();
        this.mShareUrl = in.readString();
        this.mId = in.readString();
        this.mPreviewGifUrl = in.readString();
        this.mDownsizedMediumGifUrl = in.readString();
        this.mDownsizedGifUrl = in.readString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id : ").append(mId).append(" : ");
        sb.append(mImageUrl);
        return sb.toString();
    }

    public static final Creator<Gif> CREATOR = new Creator<Gif>() {
        @Override
        public Gif createFromParcel(Parcel source) {
            return new Gif(source);
        }

        @Override
        public Gif[] newArray(int size) {
            return new Gif[size];
        }
    };

    public String getShareUrl() {
        return mShareUrl;
    }

}
