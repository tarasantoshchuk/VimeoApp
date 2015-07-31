package com.example.tarasantoshchuk.vimeoapp.entity.group;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.LruCache;

import com.example.tarasantoshchuk.vimeoapp.entity.user.User;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Group implements Parcelable {
    private static final String TAG = Group.class.getSimpleName();

    private static final long DAYS_IN_MONTH = 30;
    private static final long DAYS_IN_YEAR = 365;

    private static final String DAY_FORMAT_STRING = "Created %d day(s) ago";
    private static final String MONTH_FORMAT_STRING = "Created %d month(s) ago";
    private static final String YEAR_FORMAT_STRING = "Created %d year(s) ago";

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

    public Group(String id, String name, String description, Date dateCreated,
                 String pictureUrl, int usersCount, User owner, int videosCount) {
        mId = id;
        mName = name;
        mDescription = description;

        mDateCreated = dateCreated;
        mPictureUrl = pictureUrl;

        mUsersCount = usersCount;
        mOwner = owner;
        mVideosCount = videosCount;
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

    public String getCreatedString() {
        long createdMillisCount = System.currentTimeMillis() - mDateCreated.getTime();

        long createdDaysCount = TimeUnit.DAYS.convert(createdMillisCount, TimeUnit.MILLISECONDS);

        if(createdDaysCount < DAYS_IN_MONTH) {
            return String.format(DAY_FORMAT_STRING, createdDaysCount);
        } else if (createdDaysCount < DAYS_IN_YEAR) {
            return String.format(MONTH_FORMAT_STRING, createdDaysCount / DAYS_IN_MONTH);
        } else {
            return String.format(YEAR_FORMAT_STRING, createdDaysCount / DAYS_IN_YEAR);
        }
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
                    Log.d(TAG, "CREATOR.createFromParcel");
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
        Log.d(TAG, "writeToParcel");
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
