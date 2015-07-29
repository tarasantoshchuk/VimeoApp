package com.example.tarasantoshchuk.vimeoapp.entity.video;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.LruCache;

import com.example.tarasantoshchuk.vimeoapp.entity.user.User;

import java.util.Date;

public class Video implements Parcelable {
    private static final int SECONDS_IN_MINUTE = 60;
    private static final int SECONDS_IN_HOUR = 60 * 60;
    private static final String DURATION_FORMAT = "%d:%d:%d";
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
    private int mDuration;
    private String mEmbedHtml;

    private Date mDateCreated;
    private String mPictureUrl;
    private int mPlayCount;
    private User mOwner;

    private int mLikesCount;
    private int mCommentsCount;

    public Video(String id, String name, int duration, String description,
                 String embedHtml, Date dateCreated, String pictureUrl, int playCount,
                 User owner, int likesCount, int commentsCount) {

        mId = id;
        mName = name;
        mDuration = duration;
        mDescription = description;
        mEmbedHtml = embedHtml;

        mDateCreated = dateCreated;
        mPictureUrl = pictureUrl;
        mPlayCount = playCount;
        mOwner = owner;

        mLikesCount = likesCount;
        mCommentsCount = commentsCount;
    }

    private Video(Parcel source) {
        mId = source.readString();
        mName = source.readString();
        mDuration = source.readInt();
        mDescription = source.readString();
        mEmbedHtml = source.readString();

        mDateCreated = (Date) source.readSerializable();
        mPictureUrl = source.readString();
        mPlayCount = source.readInt();
        mOwner = source.readParcelable(User.class.getClassLoader());

        mLikesCount = source.readInt();
        mCommentsCount = source.readInt();
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

    public int getDuration() {
        return mDuration;
    }

    public String getDurationString() {
        int seconds = mDuration % SECONDS_IN_MINUTE;
        int minutes = (mDuration % SECONDS_IN_HOUR) / SECONDS_IN_MINUTE;
        int hours = mDuration / SECONDS_IN_HOUR;

        return String.format(DURATION_FORMAT, hours, minutes, seconds);
    }

    public String getEmbedHtml() {
        return mEmbedHtml;
    }

    public Date getDateCreated() {
        return mDateCreated;
    }

    public String getPictureUrl() {
        return mPictureUrl;
    }

    public int getPlayCount() {
        return mPlayCount;
    }

    public User getOwner() {
        return mOwner;
    }

    public int getLikesCount() {
        return mLikesCount;
    }

    public int getCommentsCount() {
        return mCommentsCount;
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

    public static final Parcelable.Creator<Video> CREATOR =
            new Parcelable.Creator<Video>() {

                @Override
                public Video createFromParcel(Parcel source) {
                    return new Video(source);
                }

                @Override
                public Video[] newArray(int size) {
                    return new Video[size];
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
        dest.writeInt(mDuration);
        dest.writeString(mDescription);
        dest.writeString(mEmbedHtml);

        dest.writeSerializable(mDateCreated);
        dest.writeString(mPictureUrl);
        dest.writeInt(mPlayCount);
        dest.writeParcelable(mOwner, 0);

        dest.writeInt(mLikesCount);
        dest.writeInt(mCommentsCount);
    }
}
