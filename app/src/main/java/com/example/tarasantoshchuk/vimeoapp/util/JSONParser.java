package com.example.tarasantoshchuk.vimeoapp.util;

import android.util.Log;

import com.example.tarasantoshchuk.vimeoapp.entity.channel.Channel;
import com.example.tarasantoshchuk.vimeoapp.entity.comment.Comment;
import com.example.tarasantoshchuk.vimeoapp.entity.group.Group;
import com.example.tarasantoshchuk.vimeoapp.entity.user.User;
import com.example.tarasantoshchuk.vimeoapp.entity.user.UserList;
import com.example.tarasantoshchuk.vimeoapp.entity.video.Video;
import com.example.tarasantoshchuk.vimeoapp.entity.video.VideoList;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;


public class JSONParser {
    private static final String TAG = JSONParser.class.getSimpleName();

    private static final String FIELD_ACCESS_TOKEN = "access_token";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_LOCATION = "location";
    private static final String FIELD_BIO = "bio";
    private static final String FIELD_URI = "uri";
    private static final String FIELD_PICTURES = "pictures";
    private static final String FIELD_SIZES = "sizes";
    private static final String FIELD_LINK = "link";
    private static final String FIELD_CREATED_TIME = "created_time";
    private static final String FIELD_METADATA = "metadata";
    private static final String FIELD_CONNECTIONS = "connections";
    private static final String FIELD_VIDEOS = "videos";
    private static final String FIELD_LIKES = "likes";
    private static final String FIELD_FOLLOWING = "following";
    private static final String FIELD_FOLLOWERS = "followers";
    private static final String FIELD_GROUPS = "groups";
    private static final String FIELD_CHANNELS = "channels";
    private static final String FIELD_TOTAL = "total";
    private static final String FIELD_DATA = "data";
    private static final String FIELD_USER = "user";
    private static final String FIELD_DESCRIPTION = "description";
    private static final String FIELD_DURATION = "duration";
    private static final String FIELD_EMBED = "embed";
    private static final String FIELD_HTML = "html";
    private static final String FIELD_STATS = "stats";
    private static final String FIELD_PLAYS = "plays";
    private static final String FIELD_COMMENTS = "comments";
    private static final String FIELD_TEXT = "text";
    private static final String FIELD_CREATED_ON = "created_on";
    private static final String FIELD_REPLIES = "replies";
    private static final String FIELD_USERS = "users";
    private static final String FIELD_PAGING = "paging";
    private static final String FIELD_NEXT = "next";
    private static final String FIELD_PREVIOUS = "previous";


    public static String getAccessToken(JSONObject json) {
        Log.d(TAG, "getAccessToken");

        String accessToken;
        try {
            accessToken = json.getString(FIELD_ACCESS_TOKEN);
        } catch (JSONException e) {
            Log.e(TAG, "getAccessToken: exception", e);
            throw new RuntimeException(e);
        }
        return accessToken;
    }

    public static User getUser(JSONObject jsonUser) {
        Log.d(TAG, "getUser");
        try {
            String id = getId(jsonUser);
            String name = getName(jsonUser);
            String location = getLocation(jsonUser);
            String bio = getBio(jsonUser);
            String pictureUrl = getPictureUrl(jsonUser);

            Date dateCreated = getDateCreated(jsonUser);

            JSONObject userConnections = getConnections(jsonUser);

            int videoCount = getVideoCount(userConnections);
            int followingCount = getFollowingCount(userConnections);
            int followersCount = getFollowersCount(userConnections);
            int groupsCount = getGroupsCount(userConnections);
            int channelsCount = getChannelsCount(userConnections);
            int likesCount = getLikesCount(userConnections);

            return new User(id, name, location, bio, pictureUrl, dateCreated, videoCount,
                    followingCount, followersCount, groupsCount, channelsCount, likesCount);
        } catch (Exception e) {
            Log.e(TAG, "getUser: exception", e);
            return null;
        }
    }

    public static User getUserFromAccessTokenRequest(JSONObject json) {
        try {
            JSONObject userJson = json.getJSONObject(FIELD_USER);

            return getUser(userJson);
        } catch(JSONException e) {
            Log.e(TAG, "getUserFromAccessTokenRequest: exception", e);
            return null;
        }
    }

    public static UserList getUserList(JSONObject json) {
        Log.d(TAG, "getUserList");
        try {
            ArrayList<User> users = new ArrayList<User>();

            JSONArray usersJsonArray = json.getJSONArray(FIELD_DATA);

            for(int i = 0; i < usersJsonArray.length(); i++) {
                User nextUser = getUser(usersJsonArray.getJSONObject(i));
                if (nextUser != null) {
                    users.add(nextUser);
                }
            }

            return new UserList(users);
        } catch (JSONException e) {
            Log.e(TAG, "getUserList: exception", e);
            return null;
        }
    }

    public static Video getVideo(JSONObject jsonVideo) {
        Log.d(TAG, "getVideo");

        try {
            String id = getId(jsonVideo);
            String name = getName(jsonVideo);

            int duration = getDuration(jsonVideo);

            String description = getDescription(jsonVideo);
            String embedHtml = getEmbedHtml(jsonVideo);

            Date dateCreated = getDateCreated(jsonVideo);

            String pictureUrl = getPictureUrl(jsonVideo);

            int playCount = getPlayCount(jsonVideo);

            User owner = getUser(jsonVideo.getJSONObject(FIELD_USER));

            JSONObject connections = getConnections(jsonVideo);

            int likesCount = getLikesCount(connections);
            int commentsCount = getCommentsCount(connections);

            return new Video(id, name, duration, description, embedHtml, dateCreated, pictureUrl,
                    playCount, owner, likesCount, commentsCount);
        } catch (JSONException e) {
            Log.e(TAG, "getVideo: exception", e);
            return null;
        }
    }


    public static VideoList getVideoList(JSONObject json) {
        Log.d(TAG, "getVideoList");

        try {
            ArrayList<Video> videos = new ArrayList<Video>();

            JSONArray videosJson = json.getJSONArray(FIELD_DATA);

            for(int i = 0; i < videosJson.length(); i++) {
                Video nextVideo = getVideo(videosJson.getJSONObject(i));
                if (nextVideo != null) {
                    videos.add(nextVideo);
                }
            }

            return new VideoList(videos);

        } catch (JSONException e) {
            Log.e(TAG, "getVideoList: exception", e);
            return null;
        }
    }

    public static Channel getChannel(JSONObject json) {
        Log.d(TAG, "getChannel");

        try {
            String id = getId(json);
            String name = getName(json);
            String description = getDescription(json);

            Date createdDate = getDateCreated(json);

            String pictureUrl = getPictureUrl(json);

            User owner = getUser(json.getJSONObject(FIELD_USER));

            JSONObject connections = getConnections(json);

            int usersCount = getChannelUserCount(connections);
            int videosCount = getVideoCount(connections);

            return new Channel(id, name, description, createdDate, pictureUrl, owner,
                    usersCount, videosCount);
        } catch (JSONException e) {
            Log.e(TAG, "getChannel: exception", e);
            return null;
        }
    }

    public static ArrayList<Channel> getChannelList(JSONObject json) {
        Log.d(TAG, "getChannelList: exception");

        try {

            ArrayList<Channel> channels = new ArrayList<Channel>();

            JSONArray channelsJson = json.getJSONArray(FIELD_DATA);

            for(int i = 0; i < channelsJson.length(); i++) {
                Channel nextChannel = getChannel(channelsJson.getJSONObject(i));
                if (nextChannel != null) {
                    channels.add(nextChannel);
                }
            }

            return channels;

        } catch (JSONException e) {
            Log.e(TAG, "getChannelList: exception", e);
            return null;
        }
    }

    public static Group getGroup(JSONObject json) {
        Log.d(TAG, "getGroup");

        try {
            String id = getId(json);
            String name = getName(json);
            String description = getDescription(json);

            Date dateCreated = getDateCreated(json);

            String pictureUrl = getPictureUrl(json);

            User owner = getUser(json.getJSONObject(FIELD_USER));

            JSONObject connections = getConnections(json);

            int usersCount = getGroupUserCount(connections);
            int videosCount = getVideoCount(connections);

            return new Group(id, name, description, dateCreated,
                    pictureUrl, usersCount, owner, videosCount);
        } catch (JSONException e) {
            Log.e(TAG, "getGroup: exception", e);
            return null;
        }
    }

    public static ArrayList<Group> getGroupList(JSONObject json) {
        Log.d(TAG, "getGroupList");

        try {
            ArrayList<Group> groups = new ArrayList<Group>();

            JSONArray groupsJson = json.getJSONArray(FIELD_DATA);

            for(int i = 0; i < groupsJson.length(); i++) {
                Group nextGroup = getGroup(groupsJson.getJSONObject(i));
                if (nextGroup != null) {
                    groups.add(nextGroup);
                }
            }

            return groups;
        } catch (JSONException e) {
            Log.e(TAG, "getGroupList: exception");
            return null;
        }

    }

    public static ArrayList<Comment> getCommentList(JSONObject json) {
        Log.d(TAG, "getCommentList");

        try {

            ArrayList<Comment> comments = new ArrayList<Comment>();

            JSONArray commentsJson = json.getJSONArray(FIELD_DATA);

            for(int i = 0; i < commentsJson.length(); i++) {
                Comment nextComment = getComment(commentsJson.getJSONObject(i));
                if (nextComment != null) {
                    comments.add(nextComment);
                }
            }

            return comments;
        } catch (JSONException e) {
            Log.e(TAG, "getCommentList", e);
            return null;
        }
    }

    public static String getNextPageEndpoint(JSONObject json) {
        try {
            JSONObject paging = json.getJSONObject(FIELD_PAGING);

            if (paging.isNull(FIELD_NEXT)) {
                return null;
            } else {
                return paging.getString(FIELD_NEXT);
            }
        } catch (JSONException e) {
            Log.wtf(TAG, "getNextPageEndpoing: exception", e);
            throw new RuntimeException(e);
        }
    }

    public static String getPrevPageEndpoint(JSONObject json) {
        JSONObject paging = null;
        try {
            paging = json.getJSONObject(FIELD_PAGING);

            if (paging.isNull(FIELD_PREVIOUS)) {
                return null;
            } else {
                return paging.getString(FIELD_PREVIOUS);
            }
        } catch (JSONException e) {
            Log.wtf(TAG, "getPrevPageEndpoint: exception", e);
            throw new RuntimeException(e);
        }

    }


    private static Comment getComment(JSONObject json) {
        Log.d(TAG, "getComment");

        try {
            String id = getId(json);
            String text = getText(json);
            Date createdDate = getCreatedOnDate(json);

            User owner = getUser(json.getJSONObject(FIELD_USER));

            JSONObject connections = getConnections(json);

            int repliesCount = getRepliesCount(connections);

            String videoId = getCommentVideoId(json);

            return new Comment(id, text, createdDate, owner, repliesCount, videoId);
        } catch(JSONException e) {
            Log.e(TAG, "getComment: exception", e);
            return null;
        }
    }

    private static String getCommentVideoId(JSONObject json) throws JSONException {
        String uri = json.getString(FIELD_URI);

        int videoIdEndIndex = uri.indexOf("/" + FIELD_COMMENTS);
        int videoIdStartIndex = uri.lastIndexOf('/', videoIdEndIndex - 1) + 1;

        return uri.substring(videoIdStartIndex, videoIdEndIndex);
    }


    private static Date getCreatedOnDate(JSONObject json) throws JSONException {
        String dateString = json.getString(FIELD_CREATED_ON);
        DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis();
        return formatter
                .parseDateTime(dateString)
                .toDate();
    }

    private static String getText(JSONObject json) throws JSONException {
        return json.getString(FIELD_TEXT);
    }

    private static String getId(JSONObject json) throws JSONException {
        String uri =  json.getString(FIELD_URI);

        String id = uri.substring(uri.lastIndexOf("/") + 1);

        return id;
    }

    private static String getName(JSONObject json) throws JSONException {
        return json.getString(FIELD_NAME);
    }

    private static String getLocation(JSONObject json) throws JSONException {
        if(json.isNull(FIELD_LOCATION)) {
            return null;
        } else {
            return json.getString(FIELD_LOCATION);
        }
    }

    private static String getBio(JSONObject json) throws JSONException {
        if(json.isNull(FIELD_BIO)) {
            return null;
        } else {
            return json.getString(FIELD_BIO);
        }
    }

    private static String getDescription(JSONObject json) throws JSONException {
        if(json.isNull(FIELD_DESCRIPTION)) {
            return null;
        } else {
            return json.getString(FIELD_DESCRIPTION);
        }
    }

    private static String getPictureUrl(JSONObject json) {
        try {
            JSONObject picturesInfo = json.getJSONObject(FIELD_PICTURES);
            JSONArray sizesArray = picturesInfo.getJSONArray(FIELD_SIZES);

            JSONObject bestQualityPicture = (JSONObject)
                        sizesArray.get(sizesArray.length() - 1);

            String pictureUrl = bestQualityPicture.getString(FIELD_LINK);

            return pictureUrl;
        } catch (JSONException e) {
            return null;
        }
    }

    private static Date getDateCreated(JSONObject json) throws JSONException {
        String dateString = json.getString(FIELD_CREATED_TIME);
        DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis();
        return formatter
                .parseDateTime(dateString)
                .toDate();
    }

    private static int getDuration(JSONObject json) throws JSONException {
        return json.getInt(FIELD_DURATION);
    }
    
    private static String getEmbedHtml(JSONObject json) throws JSONException {
        return json
                .getJSONObject(FIELD_EMBED)
                .getString(FIELD_HTML);
    }

    private static int getPlayCount(JSONObject json) throws JSONException {
        JSONObject stats = json.getJSONObject(FIELD_STATS);

        if(stats.isNull(FIELD_PLAYS)) {
            return 0;
        } else {
            return stats.getInt(FIELD_PLAYS);
        }
    }

    private static JSONObject getConnections(JSONObject jsonVideo) throws JSONException {
        return jsonVideo
                .getJSONObject(FIELD_METADATA)
                .getJSONObject(FIELD_CONNECTIONS);
    }

    private static int getVideoCount(JSONObject connections) throws JSONException {
        return connections
                .getJSONObject(FIELD_VIDEOS)
                .getInt(FIELD_TOTAL);
    }

    private static int getGroupUserCount(JSONObject connections) throws JSONException {
        return connections
                .getJSONObject(FIELD_USERS)
                .getInt(FIELD_USERS);
    }

    private static int getChannelUserCount(JSONObject connections) throws JSONException {
        return connections
                .getJSONObject(FIELD_USERS)
                .getInt(FIELD_TOTAL);
    }

    private static int getLikesCount(JSONObject connections) throws JSONException {
        return connections
                .getJSONObject(FIELD_LIKES)
                .getInt(FIELD_TOTAL);
    }

    private static int getFollowingCount(JSONObject connections) throws JSONException {
        return connections
                .getJSONObject(FIELD_FOLLOWING)
                .getInt(FIELD_TOTAL);
    }

    private static int getFollowersCount(JSONObject connections) throws JSONException {
        return connections
                .getJSONObject(FIELD_FOLLOWERS)
                .getInt(FIELD_TOTAL);
    }

    private static int getGroupsCount(JSONObject connections) throws JSONException {
        return connections
                .getJSONObject(FIELD_GROUPS)
                .getInt(FIELD_TOTAL);
    }

    private static int getChannelsCount(JSONObject connections) throws JSONException {
        return connections
                .getJSONObject(FIELD_CHANNELS)
                .getInt(FIELD_TOTAL);
    }

    private static int getCommentsCount(JSONObject connections) throws JSONException {
        return connections
                .getJSONObject(FIELD_COMMENTS)
                .getInt(FIELD_TOTAL);
    }

    private static int getRepliesCount(JSONObject connections) throws JSONException {
        return connections
                .getJSONObject(FIELD_REPLIES)
                .getInt(FIELD_TOTAL);
    }
}
