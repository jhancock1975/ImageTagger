package com.kewlala.imagetaggger;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by jhancock2010 on 1/27/18.
 * POJO for list item data
 */

public class PhotoTaggerListItem implements Parcelable{

    String tag;
    Double confidence;

    public PhotoTaggerListItem(String tag, Double confidence){
        this.tag = tag;
        this.confidence = confidence;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    @Override
    public String toString() {
        return "PhotoTaggerListItem{" +
                "tag='" + tag + '\'' +
                ", confidence=" + confidence +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(tag);
        parcel.writeDouble(confidence);
    }
    public static final Parcelable.Creator<PhotoTaggerListItem> CREATOR
            = new Parcelable.Creator<PhotoTaggerListItem>() {
        public PhotoTaggerListItem createFromParcel(Parcel in) {
            return new PhotoTaggerListItem(in);
        }

        public PhotoTaggerListItem[] newArray(int size) {
            return new PhotoTaggerListItem[size];
        }
    };

    private PhotoTaggerListItem(Parcel in){
        this.setTag(in.readString());
        this.setConfidence(in.readDouble());
    }
}
