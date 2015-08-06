package com.example.tarasantoshchuk.vimeoapp.entity.video;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.LruCache;

import com.example.tarasantoshchuk.vimeoapp.entity.user.User;
import com.example.tarasantoshchuk.vimeoapp.service.HttpRequestService;

import java.util.Date;

public class Video implements Parcelable {
    private static final String TAG = Video.class.getSimpleName();
    private static final String EMBED_CODE_FORMAT_STRING =
            "<html><body style=\"margin: 0; padding: 0;height: 100%%\">%s</body></html>";

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

    private static final String DEFAULT_VIDEO_PICTURE_URL =
            "https://i1.wp.com/i.vimeocdn.com/portrait/default-yellow_300x300.png?ssl=1";

    private static final String HTML_HEIGHT_ATTR = "height=";
    private static final String HTML_WIDTH_ATTR = "width=";
    private static final String HTML_SIZE_ATTR_VALUE = "100%";

    private static final int DESCRIPTION_MAX_LENGTH = 140;

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
        setDescription(description);
        setEmbedHtml(embedHtml);

        mDateCreated = dateCreated;
        setPictureUrl(pictureUrl);
        mPlayCount = playCount;
        mOwner = owner;

        mLikesCount = likesCount;
        mCommentsCount = commentsCount;
    }

    private void setEmbedHtml(String embedHtml) {
        int indexOfWidthAttrStart = embedHtml.indexOf(HTML_WIDTH_ATTR);
        int indexOfWidthAttrEnd = embedHtml.indexOf(" ", indexOfWidthAttrStart);

        String widthAttr = embedHtml.substring(indexOfWidthAttrStart, indexOfWidthAttrEnd);

        String embedHtmlWidthReplaced = embedHtml
                .replace(widthAttr, HTML_WIDTH_ATTR + HTML_SIZE_ATTR_VALUE);

        int indexOfHeightAttrStart = embedHtml.indexOf(HTML_HEIGHT_ATTR);
        int indexOfHeightAttrEnd = embedHtml.indexOf(" ", indexOfHeightAttrStart);

        String heightAttr = embedHtml.substring(indexOfHeightAttrStart, indexOfHeightAttrEnd);

        mEmbedHtml = embedHtmlWidthReplaced
                .replace(heightAttr, HTML_HEIGHT_ATTR + HTML_SIZE_ATTR_VALUE);
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

    private void setDescription(String description) {
        if(description != null) {
            if(description.length() > DESCRIPTION_MAX_LENGTH) {
                mDescription = description.substring(0, DESCRIPTION_MAX_LENGTH) + "...";
            } else {
                mDescription = description;
            }
        } else {
            mDescription = "";
        }
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
        return String.format(EMBED_CODE_FORMAT_STRING, mEmbedHtml);
    }

    public Date getDateCreated() {
        return mDateCreated;
    }

    public String getPictureUrl() {
        return mPictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        if(pictureUrl != null) {
            mPictureUrl = pictureUrl;
        } else {
            mPictureUrl = DEFAULT_VIDEO_PICTURE_URL;
        }
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
                    Log.d(TAG, "CREATOR.createFromParcel");
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
        Log.d(TAG, "writeToParcel");

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
