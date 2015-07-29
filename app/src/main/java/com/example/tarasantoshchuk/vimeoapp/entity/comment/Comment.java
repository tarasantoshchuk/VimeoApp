package com.example.tarasantoshchuk.vimeoapp.entity.comment;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.tarasantoshchuk.vimeoapp.entity.user.User;

import java.text.DateFormat;
import java.util.Date;

public class Comment implements Parcelable {
    private String mId;
    private String mText;

    private Date mDateCreated;
    private User mOwner;

    private int mRepliesCount;
    private String mVideoId;

    public Comment(String id, String text, Date dateCreated, User owner, int repliesCount,
                   String videoId) {
        mId = id;
        mText = text;
        mDateCreated = dateCreated;
        mOwner = owner;
        mRepliesCount = repliesCount;
        mVideoId = videoId;
    }

    private Comment(Parcel source) {
        mId = source.readString();
        mText = source.readString();

        mDateCreated = (Date) source.readSerializable();
        mOwner = source.readParcelable(User.class.getClassLoader());

        mRepliesCount = source.readInt();
        mVideoId = source.readString();
    }

    public String getId() {
        return mId;
    }

    public String getText() {
        return mText;
    }

    public Date getDateCreated() {
        return mDateCreated;
    }

    public User getOwner() {
        return mOwner;
    }

    public int getRepliesCount() {
        return mRepliesCount;
    }

    public String getVideoId() {
        return mVideoId;
    }

    public String getTimeStamp() {
        return DateFormat.getDateTimeInstance().format(mDateCreated);
    }

    public static final Parcelable.Creator<Comment> CREATOR =
            new Parcelable.Creator<Comment>() {

                @Override
                public Comment createFromParcel(Parcel source) {
                    return new Comment(source);
                }

                @Override
                public Comment[] newArray(int size) {
                    return new Comment[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mText);

        dest.writeSerializable(mDateCreated);
        dest.writeParcelable(mOwner, 0);

        dest.writeInt(mRepliesCount);
        dest.writeString(mVideoId);
    }
}
