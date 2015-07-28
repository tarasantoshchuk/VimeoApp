package com.example.tarasantoshchuk.vimeoapp.entity.group;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.LruCache;

import com.example.tarasantoshchuk.vimeoapp.entity.user.User;

import java.util.Date;

public class Group implements Parcelable {
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

    private int mUsersCount;
    private int mVideosCount;

    private User mOwner;

    public Group(String mId, String mName, String mDescription, Date mDateCreated,
                 String mPictureUrl, int mUsersCount, User mOwner, int mVideosCount) {
        this.mId = mId;
        this.mName = mName;
        this.mDescription = mDescription;

        this.mDateCreated = mDateCreated;
        this.mPictureUrl = mPictureUrl;

        this.mUsersCount = mUsersCount;
        this.mOwner = mOwner;
        this.mVideosCount = mVideosCount;
    }

    private Group(Parcel source) {
        mId = source.readString();
        mName = source.readString();
        mDescription = source.readString();

        mDateCreated = (Date) source.readSerializable();
        mPictureUrl = source.readString();

        mUsersCount = source.readInt();
        mVideosCount = source.readInt();

        mOwner = source.readParcelable(User.class.getClassLoader());
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

    public int getUsersCount() {
        return mUsersCount;
    }

    public int getVideosCount() {
        return mVideosCount;
    }

    public User getOwner() {
        return mOwner;
    }

    public boolean isPictureLoaded() {
        return (sBitmapCache.get(mPictureUrl) != null);
    }

    public Bitmap getPicture() {
        return sBitmapCache.get(mPictureUrl);
    }

    public void setPicture(Bitmap picture) {
        sBitmapCache.put(mPictureUrl, picture);
    }

    public static final Parcelable.Creator<Group> CREATOR =
            new Parcelable.Creator<Group>() {

                @Override
                public Group createFromParcel(Parcel source) {
                    return new Group(source);
                }

                @Override
                public Group[] newArray(int size) {
                    return new Group[size];
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

        dest.writeInt(mUsersCount);
        dest.writeInt(mVideosCount);

        dest.writeParcelable(mOwner, 0);
    }
}
