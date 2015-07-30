package com.example.tarasantoshchuk.vimeoapp.entity.user;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.LruCache;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class User implements Parcelable{
    private static final String TAG = User.class.getSimpleName();

    private static final String DAY_FORMAT_STRING = "Joined %d day(s) ago";
    private static final String MONTH_FORMAT_STRING = "Joined %d month(s) ago";
    private static final String YEAR_FORMAT_STRING = "Joined %d year(s) ago";

    private static final int DAYS_IN_MONTH = 30;
    private static final int DAYS_IN_YEAR = 365;

    private static String sLoggedUserId;

    private static final int CACHE_SIZE = 2 * 1024 * 1024;

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

    private Date mDateCreated;
    private int mVideoCount;
    private int mFollowingCount;
    private int mFollowersCount;
    private int mGroupsCount;
    private int mChannelsCount;
    private int mLikesCount;

    public User(String id, String name, String location, String bio, String pictureUrl,
                Date dateCreated, int videoCount, int followingCount, int followersCount,
                int groupsCount, int channelsCount, int likesCount) {
        mId = id;
        mName = name;
        mLocation = location;
        mBio = bio;
        mPictureUrl = pictureUrl;

        mDateCreated = dateCreated;
        mVideoCount = videoCount;
        mFollowingCount = followingCount;
        mFollowersCount = followersCount;
        mGroupsCount = groupsCount;
        mChannelsCount = channelsCount;
        mLikesCount = likesCount;
    }

    private User(Parcel source) {
        mId = source.readString();
        mName = source.readString();
        mLocation = source.readString();
        mBio = source.readString();
        mPictureUrl = source.readString();

        mDateCreated = (Date) source.readSerializable();
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

    public Date getDateCreated() {
        return mDateCreated;
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

    public String getJoinedString() {
        long joinedMillisCount = System.currentTimeMillis() - mDateCreated.getTime();

        long joinedDaysCount = TimeUnit.DAYS.convert(joinedMillisCount, TimeUnit.MILLISECONDS);

        if(joinedDaysCount < DAYS_IN_MONTH) {
            return String.format(DAY_FORMAT_STRING, joinedDaysCount);
        } else if (joinedDaysCount < DAYS_IN_YEAR) {
            return String.format(MONTH_FORMAT_STRING, joinedDaysCount / DAYS_IN_MONTH);
        } else {
            return String.format(YEAR_FORMAT_STRING, joinedDaysCount / DAYS_IN_YEAR);
        }
    }

    public boolean isPictureLoaded() {
        return (sBitmapCache.get(mPictureUrl) != null);
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
                    Log.d(TAG, "CREATOR.createFromParcel");
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
        Log.d(TAG, "writeToParcel");

        dest.writeString(mId);
        dest.writeString(mName);
        dest.writeString(mLocation);
        dest.writeString(mBio);
        dest.writeString(mPictureUrl);

        dest.writeSerializable(mDateCreated);
        dest.writeInt(mVideoCount);
        dest.writeInt(mFollowingCount);
        dest.writeInt(mFollowersCount);
        dest.writeInt(mGroupsCount);
        dest.writeInt(mChannelsCount);
        dest.writeInt(mLikesCount);
    }
}
