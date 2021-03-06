package com.example.tarasantoshchuk.vimeoapp.entity.video;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

public class VideoList implements Parcelable {
    private static final String TAG = VideoList.class.getSimpleName();
    private ArrayList<Video> mList;

    public VideoList() {
        mList = new ArrayList<Video>();
    }

    public VideoList(ArrayList<Video> list) {
        mList = list;
    }

    private VideoList(Parcel source) {
        mList = new ArrayList<Video>();

        int length = source.readInt();

        for(int i = 0; i < length; i++) {
            mList.add((Video) source.readParcelable(Video.class.getClassLoader()));
        }
    }

    public Video get(int position) {
        return mList.get(position);
    }

    public int size() {
        return mList.size();
    }

    public void update(VideoList list) {
        Log.d(TAG, "update");
        mList.clear();
        mList.addAll(list.mList);
    }


    public static final Parcelable.Creator<VideoList> CREATOR =
            new Parcelable.Creator<VideoList>() {

                @Override
                public VideoList createFromParcel(Parcel source) {
                    Log.d(TAG, "CREATOR.createFromParcel");
                    return new VideoList(source);
                }

                @Override
                public VideoList[] newArray(int size) {
                    return new VideoList[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Log.d(TAG, "writeToParcel");

        dest.writeInt(mList.size());

        for(Video video: mList) {
            dest.writeParcelable(video, 0);
        }
    }
}
