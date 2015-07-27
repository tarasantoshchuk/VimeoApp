package com.example.tarasantoshchuk.vimeoapp.entity.comment;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.tarasantoshchuk.vimeoapp.entity.user.User;
import com.example.tarasantoshchuk.vimeoapp.entity.video.Video;

import java.util.Date;

public class Comment implements Parcelable {
    private String mId;
    private String mText;

    private Date mDateCreated;
    private User mOwner;

    private int mRepliesCount;
    private Video mVideo;

    public Comment(String mId, String mText, Date mDateCreated, User mOwner, int mRepliesCount,
                   Video mVideo) {
        this.mId = mId;
        this.mText = mText;
        this.mDateCreated = mDateCreated;
        this.mOwner = mOwner;
        this.mRepliesCount = mRepliesCount;
        this.mVideo = mVideo;
    }

    private Comment(Parcel source) {
        mId = source.readString();
        mText = source.readString();

        mDateCreated = (Date) source.readSerializable();
        mOwner = source.readParcelable(User.class.getClassLoader());

        mRepliesCount = source.readInt();
        mVideo = source.readParcelable(Video.class.getClassLoader());
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

    public Video getVideo() {
        return mVideo;
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
        dest.writeParcelable(mVideo, 0);
    }
}
