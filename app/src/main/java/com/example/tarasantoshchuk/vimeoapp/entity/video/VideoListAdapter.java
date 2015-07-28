package com.example.tarasantoshchuk.vimeoapp.entity.video;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tarasantoshchuk.vimeoapp.R;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class VideoListAdapter extends BaseAdapter {
    private VideoList mList;
    private LayoutInflater mInflater;
    private VideoListActivity mActivity;

    private HashMap<VideoViewHolder, BitmapDownloadTask> mHolderMap =
            new HashMap<VideoViewHolder, BitmapDownloadTask>();

    public VideoListAdapter(LayoutInflater inflater, VideoListActivity activity) {
        mList = new VideoList();
        mInflater = inflater;
        mActivity = activity;
    }

    private Video getVideo(int position) {
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
        final Video currVideo = getVideo(position);
        VideoViewHolder holder;

        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.video_item, null);

            holder = new VideoViewHolder();

            holder.imgVideoItem = (ImageView) convertView.findViewById(R.id.imgVideoItem);

            holder.txtVideoItemName = (TextView) convertView.findViewById(R.id.txtVideoItemName);
            holder.txtVideoItemOwner = (TextView) convertView.findViewById(R.id.txtVideoItemOwner);

            holder.txtVideoItemDuration = (TextView)
                    convertView.findViewById(R.id.txtVideoItemDuration);

            holder.txtVideoItemPlayCount = (TextView)
                    convertView.findViewById(R.id.txtVideoItemPlayCount);

            convertView.setTag(holder);
        } else {
            holder = (VideoViewHolder) convertView.getTag();
        }

        holder.txtVideoItemName.setText(currVideo.getName());
        holder.txtVideoItemOwner.setText(currVideo.getOwner().getName());
        holder.txtVideoItemDuration.setText(currVideo.getDurationString());
        holder.txtVideoItemPlayCount.setText(Integer.toString(currVideo.getPlayCount()));

        if(currVideo.isPictureLoaded()) {
            holder.imgVideoItem.setImageBitmap(currVideo.getPicture());
        } else {
            BitmapDownloadTask task = mHolderMap.get(holder);

            if(task != null && task.getStatus() != AsyncTask.Status.FINISHED) {
                task.cancel(false);
            }

            mHolderMap.put(holder, (BitmapDownloadTask)
                    new BitmapDownloadTask(holder, currVideo).execute());

        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent videoActivityIntent = new Intent(mActivity, VideoActivity.class);

                videoActivityIntent.putExtras(VideoActivity.getStartExtras(currVideo));

                mActivity.startActivity(videoActivityIntent);
            }
        });

        return convertView;
    }

    public void cancelAllTasks() {
        for(BitmapDownloadTask task: mHolderMap.values()) {
            if(task.getStatus() != AsyncTask.Status.FINISHED) {
                task.cancel(false);
            }
        }
    }

    public void updateList(VideoList list) {
        cancelAllTasks();
        mList.update(list);
        notifyDataSetChanged();
    }

    private static class BitmapDownloadTask extends AsyncTask<String, Void, Bitmap> {
        private VideoViewHolder mHolder;
        private Video mVideo;

        public BitmapDownloadTask(VideoViewHolder holder, Video video) {
            mHolder = holder;
            mVideo = video;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            URL url = null;

            try {
                url = new URL(mVideo.getPictureUrl());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            Bitmap image = null;
            try {
                image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                return image;
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mHolder.imgVideoItem.setImageBitmap(bitmap);
            mVideo.setPicture(bitmap);
        }
    }
}
