package com.example.tarasantoshchuk.vimeoapp.entity.user;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.LruCache;

public class User implements Parcelable{
    private static String sLoggedUserId;

    private static final int CACHE_SIZE = 4 * 1024 * 1024;

    private static LruCache<String, Bitmap> sBitmapCache = new LruCache<String, Bitmap>(CACHE_SIZE) {
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount();
        }
    };

    private String mId;
    private String mName;
    private String mLocation;
    private String mBio;
    private String mPictureUrl;

    private int mDaysAgoJoined;
    private int mVideoCount;
    private int mFollowingCount;
    private int mFollowersCount;
    private int mGroupsCount;
    private int mChannelsCount;
    private int mLikesCount;

    public User(String mId, String mName, String mLocation, String mBio, String mPictureUrl,
                int mDaysAgoJoined, int mVideoCount, int mFollowingCount, int mFollowersCount,
                int mGroupsCount, int mChannelsCount, int mLikesCount) {
        this.mId = mId;
        this.mName = mName;
        this.mLocation = mLocation;
        this.mBio = mBio;
        this.mPictureUrl = mPictureUrl;

        this.mDaysAgoJoined = mDaysAgoJoined;
        this.mVideoCount = mVideoCount;
        this.mFollowingCount = mFollowingCount;
        this.mFollowersCount = mFollowersCount;
        this.mGroupsCount = mGroupsCount;
        this.mChannelsCount = mChannelsCount;
        this.mLikesCount = mLikesCount;
    }

    private User(Parcel source) {
        mId = source.readString();
        mName = source.readString();
        mLocation = source.readString();
        mBio = source.readString();
        mPictureUrl = source.readString();

        mDaysAgoJoined = source.readInt();
        mVideoCount = source.readInt();
        mFollowingCount = source.readInt();
        mFollowersCount = source.readInt();
        mGroupsCount = source.readInt();
        mChannelsCount = source.readInt();
        mLikesCount = source.readInt();
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getLocation() {
        return mLocation;
    }

    public String getBio() {
        return mBio;
    }

    public String getPictureUrl() {
        return mPictureUrl;
    }

    public int getDaysAgoJoined() {
        return mDaysAgoJoined;
    }

    public int getVideoCount() {
        return mVideoCount;
    }

    public int getFollowingCount() {
        return mFollowingCount;
    }

    public int getGroupsCount() {
        return mGroupsCount;
    }

    public int getLikesCount() {
        return mLikesCount;
    }

    public int getFollowersCount() {
        return mFollowersCount;
    }

    public int getChannelsCount() {
        return mChannelsCount;
    }


    public Bitmap getPicture() {
        return sBitmapCache.get(mPictureUrl);
    }

    public Bitmap setPicture(Bitmap bitmap) {
        return sBitmapCache.put(mPictureUrl, bitmap);
    }

    public static final Parcelable.Creator<User> CREATOR =
            new Parcelable.Creator<User>() {

                @Override
                public User createFromParcel(Parcel source) {
                    return new User(source);
                }

                @Override
                public User[] newArray(int size) {
                    return new User[size];
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
        dest.writeString(mLocation);
        dest.writeString(mBio);
        dest.writeString(mPictureUrl);

        dest.writeInt(mDaysAgoJoined);
        dest.writeInt(mVideoCount);
        dest.writeInt(mFollowingCount);
        dest.writeInt(mFollowersCount);
        dest.writeInt(mGroupsCount);
        dest.writeInt(mChannelsCount);
        dest.writeInt(mLikesCount);
    }
}
