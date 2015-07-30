package com.example.tarasantoshchuk.vimeoapp.entity.comment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tarasantoshchuk.vimeoapp.R;
import com.example.tarasantoshchuk.vimeoapp.entity.user.UserActivity;
import com.example.tarasantoshchuk.vimeoapp.util.HttpRequestInfo;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class CommentListAdapter extends BaseAdapter{
    private static final String TAG = CommentListAdapter.class.getSimpleName();

    private ArrayList<Comment> mList;
    private LayoutInflater mInflater;
    private CommentListActivity mActivity;

    private HashMap<CommentViewHolder, BitmapDownloadTask> mHolderMap =
            new HashMap<CommentViewHolder, BitmapDownloadTask>();

    public CommentListAdapter(LayoutInflater inflater, CommentListActivity activity) {
        mList = new ArrayList<Comment>();
        mInflater = inflater;
        mActivity = activity;
    }

    private Comment getComment(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getView");
        final Comment currComment = getComment(position);
        CommentViewHolder holder;

        if(convertView == null) {
            Log.d(TAG, "getView: inflate view");
            convertView = mInflater.inflate(R.layout.comment_item, null);

            holder = new CommentViewHolder();

            holder.imgCommentOwner = (ImageView) convertView.findViewById(R.id.imgCommentOwner);

            holder.txtCommentItemOwnerName = (TextView)
                    convertView.findViewById(R.id.txtCommentItemOwnerName);

            holder.txtCommentItemText = (TextView)
                    convertView.findViewById(R.id.txtCommentItemText);

            holder.txtCommentItemRepliesCountTitle = (TextView)
                    convertView.findViewById(R.id.txtCommentItemRepliesCountTitle);

            holder.txtCommentItemRepliesCount = (TextView)
                    convertView.findViewById(R.id.txtCommentItemRepliesCount);

            holder.txtCommentItemTimeStamp = (TextView)
                    convertView.findViewById(R.id.txtCommentItemTimeStamp);

            convertView.setTag(holder);
        } else {
            Log.d(TAG, "getView: reuse view");
            holder = (CommentViewHolder) convertView.getTag();
        }

        holder.txtCommentItemOwnerName.setText(currComment.getOwner().getName());
        holder.txtCommentItemText.setText(currComment.getText());

        holder.txtCommentItemRepliesCount.setText(Integer.toString(currComment.getRepliesCount()));
        holder.txtCommentItemTimeStamp.setText(currComment.getTimeStamp());

        if(currComment.getOwner().isPictureLoaded()) {
            holder.imgCommentOwner.setImageBitmap(currComment.getOwner().getPicture());
        } else {
            BitmapDownloadTask task = mHolderMap.get(holder);

            if(task != null && task.getStatus() != AsyncTask.Status.FINISHED) {
                Log.d(TAG, "getView: cancel async task");
                task.cancel(false);
            }

            Log.d(TAG, "getView: create async task");
            mHolderMap.put(holder, (BitmapDownloadTask)
                    new BitmapDownloadTask(holder, currComment, mActivity).execute());
        }

        holder.imgCommentOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ownerActivityIntent = new Intent(mActivity, UserActivity.class);

                ownerActivityIntent.putExtras(UserActivity.getStartExtras(currComment.getOwner()));

                Log.d(TAG, "image onClick: start owner's UserAcitivity");
                mActivity.startActivity(ownerActivityIntent);
            }
        });

        holder.txtCommentItemRepliesCountTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currComment.getRepliesCount() != 0) {
                    Intent repliesActivityIntent = new Intent(mActivity, CommentListActivity.class);

                    HttpRequestInfo repliesRequest = HttpRequestInfo.getCommentRepliesRequest(
                            currComment.getVideoId(), currComment.getId());

                    repliesActivityIntent.putExtras(
                            CommentListActivity.getStartExtras(
                                    mActivity.getString(R.string.txt_replies), repliesRequest));
                    Log.d(TAG, "replies onClick: start CommentListActivity with replies");
                    mActivity.startActivity(repliesActivityIntent);
                }
            }
        });

        return convertView;
    }

    public void cancelAllTasks() {
        Log.d(TAG, "cancelAllTasks");
        for(BitmapDownloadTask task: mHolderMap.values()) {
            if(task.getStatus() != AsyncTask.Status.FINISHED) {
                task.cancel(false);
            }
        }
    }

    public void updateList(ArrayList<Comment> list) {
        Log.d(TAG, "updateList");
        cancelAllTasks();

        mList.clear();
        mList.addAll(list);

        notifyDataSetChanged();
    }

    private static class BitmapDownloadTask extends AsyncTask<Void, Void, Bitmap> {
        private CommentViewHolder mHolder;
        private Comment mComment;
        private Context mContext;

        public BitmapDownloadTask(CommentViewHolder holder, Comment comment, Context context) {
            mHolder = holder;
            mComment = comment;
            mContext = context;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {

                URL url = new URL(mComment.getOwner().getPictureUrl());
                return BitmapFactory.decodeStream(url.openConnection().getInputStream());

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.d(TAG, "BitmapDownloadTask.onPostExecute");
            if(bitmap == null) {
                Toast.makeText(mContext, mContext.getString(R.string.txt_bitmap_load_fail),
                        Toast.LENGTH_LONG).show();
            } else {

                mHolder.imgCommentOwner.setImageBitmap(bitmap);
                mComment.getOwner().setPicture(bitmap);

            }
        }
    }
}
