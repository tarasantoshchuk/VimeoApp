package com.example.tarasantoshchuk.vimeoapp.entity.channel;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.LruCache;

import com.example.tarasantoshchuk.vimeoapp.entity.user.User;

import java.util.Date;

public class Channel implements Parcelable {
    private static final int CACHE_SIZE = 2 * 1024 * 1024;

    private static LruCache<String, Bitmap> sBitmapCache = new LruCache<String, Bitmap>(CACHE_SIZE) {
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount();
        }
    };

    private String mId;
    private String mName;
    private String mDescription;
    private Date mDateCreated;
    private String mPictureUrl;
    private User mOwner;

    private int mUsersCount;
    private int mVideosCount;



    public Channel(String mId, String mName, String mDescription, Date mDateCreated, String mPictureUrl,
                   User mOwner, int mUsersCount, int mVideosCount) {
        this.mId = mId;
        this.mName = mName;
        this.mDescription = mDescription;
        this.mDateCreated = mDateCreated;
        this.mPictureUrl = mPictureUrl;
        this.mOwner = mOwner;

        this.mUsersCount = mUsersCount;
        this.mVideosCount = mVideosCount;
    }

    private Channel(Parcel source) {
        mId = source.readString();
        mName = source.readString();
        mDescription = source.readString();
        mDateCreated = (Date) source.readSerializable();
        mPictureUrl = source.readString();
        mOwner = source.readParcelable(User.class.getClassLoader());

        mUsersCount = source.readInt();
        mVideosCount = source.readInt();
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public Date getDateCreated() {
        return mDateCreated;
    }

    public String getPictureUrl() {
        return mPictureUrl;
    }

    public User getUser() {
        return mOwner;
    }

    public int getUsersCount() {
        return mUsersCount;
    }

    public int getVideosCount() {
        return mVideosCount;
    }

    public Bitmap getPicture() {
        return sBitmapCache.get(mPictureUrl);
    }

    public void setPicture(Bitmap picture) {
        sBitmapCache.put(mPictureUrl, picture);
    }

    public static final Parcelable.Creator<Channel> CREATOR =
            new Parcelable.Creator<Channel>() {

                @Override
                public Channel createFromParcel(Parcel source) {
                    return new Channel(source);
                }

                @Override
                public Channel[] newArray(int size) {
                    return new Channel[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mName);
        dest.writeString(mDescription);
        dest.writeSerializable(mDateCreated);
        dest.writeString(mPictureUrl);
        dest.writeParcelable(mOwner, 0);

        dest.writeInt(mUsersCount);
        dest.writeInt(mVideosCount);
    }
}