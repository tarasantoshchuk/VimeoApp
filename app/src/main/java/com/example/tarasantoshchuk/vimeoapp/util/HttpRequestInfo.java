package com.example.tarasantoshchuk.vimeoapp.util;

import android.provider.MediaStore;

import java.util.HashMap;


/**
 * class that carries all needed information to perform http request
 * after HttpRequestService is created HttpRequestInfo should be moved as HttpRequestService's
 * nested class
 */
public class HttpRequestInfo {
    private static final String BASIC_URL = "https://api.vimeo.com";
    private static final String EMPTY = "";

    /**
     * endpoint strings
     */
    private static final String ME = "me";
    private static final String USERS = "users";
    private static final String CHANNELS = "channels";
    private static final String VIDEOS = "videos";
    private static final String GROUPS = "groups";
    private static final String COMMENTS = "comments";
    private static final String LIKES = "likes";
    private static final String FOLLOWERS = "followers";
    private static final String FOLLOWING = "following";
    private static final String REPLIES = "replies";

    /**
     * parameter strings
     */
    private static final String QUERY = "query";


    private enum RequestMethod {
        GET("GET"),
        POST("POST"),
        PUT("PUT"),
        DELETE("DELETE"),
        PATCH("PATCH");
        String name;
        RequestMethod(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private enum ExpectedResult {
        USER, USER_LIST, VIDEO, VIDEO_LIST, GROUP, GROUP_LIST, CHANNEL, CHANNEL_LIST, COMMENT_LIST,
        BOOLEAN, VOID;
    }

    private RequestMethod mMethod;
    private ExpectedResult mResult;
    private String mEndpoint;
    private HashMap<String, String> mParameters;

    private HttpRequestInfo() {
        mParameters = new HashMap<String, String>();
        mEndpoint = EMPTY;
    }

    private HttpRequestInfo(RequestMethod method, ExpectedResult result) {
        this();

        mMethod = method;
        mResult = result;
    }

    private void appendToEndpoint(String str) {
        mEndpoint += "/" + str;
    }

    /**
     * methods that create HttpRequestInfo for specific requests
     */

    public static HttpRequestInfo getMyInfoRequest() {
        HttpRequestInfo myInfoRequest = new HttpRequestInfo(RequestMethod.GET, ExpectedResult.USER);

        myInfoRequest.appendToEndpoint(ME);

        return myInfoRequest;
    }


    public static HttpRequestInfo getIsChannelSubscriberRequest(String channelId) {
        HttpRequestInfo isChannelSubscriber =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.BOOLEAN);

        isChannelSubscriber.appendToEndpoint(ME);
        isChannelSubscriber.appendToEndpoint(CHANNELS);
        isChannelSubscriber.appendToEndpoint(channelId);

        return isChannelSubscriber;
    }

    public static HttpRequestInfo getSubscribeRequest(String channelId) {
        HttpRequestInfo isChannelSubscriber =
                new HttpRequestInfo(RequestMethod.PUT, ExpectedResult.VOID);

        isChannelSubscriber.appendToEndpoint(ME);
        isChannelSubscriber.appendToEndpoint(CHANNELS);
        isChannelSubscriber.appendToEndpoint(channelId);

        return isChannelSubscriber;
    }

    public static HttpRequestInfo getUnsubscribeRequest(String channelId) {
        HttpRequestInfo isChannelSubscriber =
                new HttpRequestInfo(RequestMethod.DELETE, ExpectedResult.VOID);

        isChannelSubscriber.appendToEndpoint(ME);
        isChannelSubscriber.appendToEndpoint(CHANNELS);
        isChannelSubscriber.appendToEndpoint(channelId);

        return isChannelSubscriber;
    }


    public static HttpRequestInfo getIsInGroupRequest(String groupId) {
        HttpRequestInfo isInGroup =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.BOOLEAN);

        isInGroup.appendToEndpoint(ME);
        isInGroup.appendToEndpoint(GROUPS);
        isInGroup.appendToEndpoint(groupId);

        return isInGroup;
    }

    public static HttpRequestInfo getJoinGroupRequest(String groupId) {
        HttpRequestInfo joinGroup =
                new HttpRequestInfo(RequestMethod.PUT, ExpectedResult.VOID);

        joinGroup.appendToEndpoint(ME);
        joinGroup.appendToEndpoint(GROUPS);
        joinGroup.appendToEndpoint(groupId);

        return joinGroup;
    }

    public static HttpRequestInfo getLeaveGroupRequest(String groupId) {
        HttpRequestInfo leaveGroup =
                new HttpRequestInfo(RequestMethod.DELETE, ExpectedResult.VOID);

        leaveGroup.appendToEndpoint(ME);
        leaveGroup.appendToEndpoint(GROUPS);
        leaveGroup.appendToEndpoint(groupId);

        return leaveGroup;
    }


    public static HttpRequestInfo getIsFollowingRequest(String userId) {
        HttpRequestInfo isFollowing =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.BOOLEAN);

        isFollowing.appendToEndpoint(ME);
        isFollowing.appendToEndpoint(FOLLOWING);
        isFollowing.appendToEndpoint(userId);

        return isFollowing;
    }

    public static HttpRequestInfo getFollowRequest(String userId) {
        HttpRequestInfo followRequest = new
                HttpRequestInfo(RequestMethod.PUT, ExpectedResult.VOID);

        followRequest.appendToEndpoint(ME);
        followRequest.appendToEndpoint(FOLLOWING);
        followRequest.appendToEndpoint(userId);

        return followRequest;
    }

    public static HttpRequestInfo getUnfollowRequest(String userId) {
        HttpRequestInfo unfollowRequest =
                new HttpRequestInfo(RequestMethod.DELETE, ExpectedResult.VOID);

        unfollowRequest.appendToEndpoint(ME);
        unfollowRequest.appendToEndpoint(FOLLOWING);
        unfollowRequest.appendToEndpoint(userId);

        return unfollowRequest;
    }


    public static HttpRequestInfo getIsLikedRequest(String videoId) {
        HttpRequestInfo isLiked =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.BOOLEAN);

        isLiked.appendToEndpoint(ME);
        isLiked.appendToEndpoint(LIKES);
        isLiked.appendToEndpoint(videoId);

        return isLiked;
    }

    public static HttpRequestInfo getLikeRequest(String videoId) {
        HttpRequestInfo likeRequest =
                new HttpRequestInfo(RequestMethod.PUT, ExpectedResult.VOID);

        likeRequest.appendToEndpoint(ME);
        likeRequest.appendToEndpoint(LIKES);
        likeRequest.appendToEndpoint(videoId);

        return likeRequest;
    }

    public static HttpRequestInfo getDislikeRequest(String videoId) {
        HttpRequestInfo dislikeRequest =
                new HttpRequestInfo(RequestMethod.DELETE, ExpectedResult.VOID);

        dislikeRequest.appendToEndpoint(ME);
        dislikeRequest.appendToEndpoint(LIKES);
        dislikeRequest.appendToEndpoint(videoId);

        return dislikeRequest;
    }


    public static HttpRequestInfo getUserSearchRequest(String query) {
        HttpRequestInfo userSearchRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.USER_LIST);

        userSearchRequest.appendToEndpoint(USERS);

        userSearchRequest.mParameters.put(QUERY, query);

        return userSearchRequest;
    }

    public static HttpRequestInfo getVideoSearchRequest(String query) {
        HttpRequestInfo userSearchRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.VIDEO_LIST);

        userSearchRequest.appendToEndpoint(VIDEOS);

        userSearchRequest.mParameters.put(QUERY, query);

        return userSearchRequest;
    }

    public static HttpRequestInfo getChannelSearchRequest(String query) {
        HttpRequestInfo userSearchRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.CHANNEL_LIST);

        userSearchRequest.appendToEndpoint(CHANNELS);

        userSearchRequest.mParameters.put(QUERY, query);

        return userSearchRequest;
    }

    public static HttpRequestInfo getGroupSearchRequest(String query) {
        HttpRequestInfo userSearchRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.GROUP_LIST);

        userSearchRequest.appendToEndpoint(GROUPS);

        userSearchRequest.mParameters.put(QUERY, query);

        return userSearchRequest;
    }


    public static HttpRequestInfo getUserInfoRequest(String userId) {
        HttpRequestInfo userInfoRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.USER);

        userInfoRequest.appendToEndpoint(USERS);
        userInfoRequest.appendToEndpoint(userId);

        return userInfoRequest;
    }

    public static HttpRequestInfo getGroupInfoRequest(String groupId) {
        HttpRequestInfo groupInfoRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.GROUP);

        groupInfoRequest.appendToEndpoint(GROUPS);
        groupInfoRequest.appendToEndpoint(groupId);

        return groupInfoRequest;
    }

    public static HttpRequestInfo getChannelInfoRequest(String channelId) {
        HttpRequestInfo channelInfoRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.CHANNEL);

        channelInfoRequest.appendToEndpoint(CHANNELS);
        channelInfoRequest.appendToEndpoint(channelId);

        return channelInfoRequest;
    }

    public static HttpRequestInfo getVideoInfoRequest(String videoId) {
        HttpRequestInfo videoInfoRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.VIDEO);

        videoInfoRequest.appendToEndpoint(VIDEOS);
        videoInfoRequest.appendToEndpoint(videoId);

        return videoInfoRequest;
    }


    public static HttpRequestInfo getUserVideosRequest(String userId) {
        HttpRequestInfo userVideosRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.VIDEO_LIST);

        userVideosRequest.appendToEndpoint(USERS);
        userVideosRequest.appendToEndpoint(userId);
        userVideosRequest.appendToEndpoint(VIDEOS);

        return userVideosRequest;
    }

    public static HttpRequestInfo getUserChannelsRequest(String userId) {
        HttpRequestInfo userChannelsRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.CHANNEL_LIST);

        userChannelsRequest.appendToEndpoint(USERS);
        userChannelsRequest.appendToEndpoint(userId);
        userChannelsRequest.appendToEndpoint(CHANNELS);

        return userChannelsRequest;
    }

    public static HttpRequestInfo getUserGroupsRequest(String userId) {
        HttpRequestInfo userGroupsRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.GROUP_LIST);

        userGroupsRequest.appendToEndpoint(USERS);
        userGroupsRequest.appendToEndpoint(userId);
        userGroupsRequest.appendToEndpoint(GROUPS);

        return userGroupsRequest;
    }

    public static HttpRequestInfo getUserFollowersRequest(String userId) {
        HttpRequestInfo userFollowersRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.USER_LIST);

        userFollowersRequest.appendToEndpoint(USERS);
        userFollowersRequest.appendToEndpoint(userId);
        userFollowersRequest.appendToEndpoint(FOLLOWERS);

        return userFollowersRequest;
    }

    public static HttpRequestInfo getFollowedUsersRequest(String userId) {
        HttpRequestInfo followingUsersRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.USER_LIST);

        followingUsersRequest.appendToEndpoint(USERS);
        followingUsersRequest.appendToEndpoint(userId);
        followingUsersRequest.appendToEndpoint(FOLLOWING);

        return followingUsersRequest;
    }

    public static HttpRequestInfo getUserLikes(String userId) {
        HttpRequestInfo userLikesRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.VIDEO_LIST);

        userLikesRequest.appendToEndpoint(USERS);
        userLikesRequest.appendToEndpoint(userId);
        userLikesRequest.appendToEndpoint(LIKES);

        return userLikesRequest;
    }


    public static HttpRequestInfo getRelatedVideosRequest(String videoId) {
        HttpRequestInfo relatedVideosRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.VIDEO_LIST);

        relatedVideosRequest.appendToEndpoint(VIDEOS);
        relatedVideosRequest.appendToEndpoint(videoId);
        relatedVideosRequest.appendToEndpoint(VIDEOS);

        return relatedVideosRequest;
    }

    public static HttpRequestInfo getCommentsRequest(String videoId) {
        HttpRequestInfo commentsRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.COMMENT_LIST);

        commentsRequest.appendToEndpoint(VIDEOS);
        commentsRequest.appendToEndpoint(videoId);
        commentsRequest.appendToEndpoint(COMMENTS);

        return commentsRequest;
    }

    public static HttpRequestInfo getCommentRepliesRequest(String videoId, String commentId) {
        HttpRequestInfo repliesRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.COMMENT_LIST );

        repliesRequest.appendToEndpoint(VIDEOS);
        repliesRequest.appendToEndpoint(videoId);
        repliesRequest.appendToEndpoint(COMMENTS);
        repliesRequest.appendToEndpoint(commentId);
        repliesRequest.appendToEndpoint(REPLIES);

        return repliesRequest;
    }

    public static HttpRequestInfo getVideoLikesRequest(String videoId) {
        HttpRequestInfo videoLikesRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.USER_LIST);

        videoLikesRequest.appendToEndpoint(VIDEOS);
        videoLikesRequest.appendToEndpoint(videoId);
        videoLikesRequest.appendToEndpoint(LIKES);

        return videoLikesRequest;
    }


    public static HttpRequestInfo getChannelVideosRequest(String channelId) {
        HttpRequestInfo channelVideosRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.VIDEO_LIST);

        channelVideosRequest.appendToEndpoint(CHANNELS);
        channelVideosRequest.appendToEndpoint(channelId);
        channelVideosRequest.appendToEndpoint(VIDEOS);

        return channelVideosRequest;
    }

    public static HttpRequestInfo getChannelUsersRequest(String channelId) {
        HttpRequestInfo channelUsersRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.USER_LIST);

        channelUsersRequest.appendToEndpoint(CHANNELS);
        channelUsersRequest.appendToEndpoint(channelId);
        channelUsersRequest.appendToEndpoint(USERS);

        return channelUsersRequest;
    }


    public static HttpRequestInfo getGroupVideosRequest(String groupId) {
        HttpRequestInfo groupVideosRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.VIDEO_LIST);

        groupVideosRequest.appendToEndpoint(GROUPS);
        groupVideosRequest.appendToEndpoint(groupId);
        groupVideosRequest.appendToEndpoint(VIDEOS);

        return groupVideosRequest;
    }

    public static HttpRequestInfo getGroupUsersRequest(String groupId) {
        HttpRequestInfo groupUsersRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.USER_LIST);

        groupUsersRequest.appendToEndpoint(GROUPS);
        groupUsersRequest.appendToEndpoint(groupId);
        groupUsersRequest.appendToEndpoint(USERS);

        return groupUsersRequest;
    }
}
