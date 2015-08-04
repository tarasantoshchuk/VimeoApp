package com.example.tarasantoshchuk.vimeoapp.service;

import java.io.Serializable;
import java.util.HashMap;


/**
 * class that carries all needed information to perform http request
 * after HttpRequestService is created HttpRequestInfo should be moved as HttpRequestService's
 * nested class
 */
public class HttpRequestInfo implements Serializable {
    static final String BASIC_URL = "https://api.vimeo.com";

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

    enum RequestMethod {
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

    enum ExpectedResult {
        USER, USER_LIST, VIDEO, VIDEO_LIST, GROUP, GROUP_LIST, CHANNEL, CHANNEL_LIST, COMMENT_LIST,
        BOOLEAN
    }

    RequestMethod mMethod;
    String mAction;
    ExpectedResult mResult;
    String mEndpoint;
    HashMap<String, String> mParameters;

    private HttpRequestInfo() {
        mParameters = new HashMap<String, String>();
        mEndpoint = "";
    }

    private HttpRequestInfo(RequestMethod method, ExpectedResult result, String action) {
        this();

        mMethod = method;
        mResult = result;
        mAction = action;
    }

    private void appendToEndpoint(String str) {
        mEndpoint += "/" + str;
    }

    /**
     * methods that create HttpRequestInfo for specific requests
     */



    public static HttpRequestInfo getMyInfoRequest() {
        HttpRequestInfo myInfoRequest = new HttpRequestInfo(RequestMethod.GET, ExpectedResult.USER,
                HttpRequestService.USER_ACTION);

        myInfoRequest.appendToEndpoint(ME);

        return myInfoRequest;
    }


    public static HttpRequestInfo getIsChannelSubscriberRequest(String channelId) {
        HttpRequestInfo isChannelSubscriber =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.BOOLEAN,
                        HttpRequestService.IS_SUBSCRIBER_ACTION);

        isChannelSubscriber.appendToEndpoint(ME);
        isChannelSubscriber.appendToEndpoint(CHANNELS);
        isChannelSubscriber.appendToEndpoint(channelId);

        return isChannelSubscriber;
    }

    public static HttpRequestInfo getSubscribeRequest(String channelId) {
        HttpRequestInfo subscribeRequest =
                new HttpRequestInfo(RequestMethod.PUT, ExpectedResult.BOOLEAN,
                        HttpRequestService.CHANGE_SUBSCRIBE_STATE_ACTION);

        subscribeRequest.appendToEndpoint(ME);
        subscribeRequest.appendToEndpoint(CHANNELS);
        subscribeRequest.appendToEndpoint(channelId);

        return subscribeRequest;
    }

    public static HttpRequestInfo getUnsubscribeRequest(String channelId) {
        HttpRequestInfo unsubscribeRequest =
                new HttpRequestInfo(RequestMethod.DELETE, ExpectedResult.BOOLEAN,
                        HttpRequestService.CHANGE_SUBSCRIBE_STATE_ACTION);

        unsubscribeRequest.appendToEndpoint(ME);
        unsubscribeRequest.appendToEndpoint(CHANNELS);
        unsubscribeRequest.appendToEndpoint(channelId);

        return unsubscribeRequest;
    }


    public static HttpRequestInfo getIsInGroupRequest(String groupId) {
        HttpRequestInfo isInGroup =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.BOOLEAN,
                        HttpRequestService.IS_MEMBER_ACTION);

        isInGroup.appendToEndpoint(ME);
        isInGroup.appendToEndpoint(GROUPS);
        isInGroup.appendToEndpoint(groupId);

        return isInGroup;
    }

    public static HttpRequestInfo getJoinGroupRequest(String groupId) {
        HttpRequestInfo joinGroup =
                new HttpRequestInfo(RequestMethod.PUT, ExpectedResult.BOOLEAN,
                        HttpRequestService.CHANGE_MEMBER_STATE_ACTION);

        joinGroup.appendToEndpoint(ME);
        joinGroup.appendToEndpoint(GROUPS);
        joinGroup.appendToEndpoint(groupId);

        return joinGroup;
    }

    public static HttpRequestInfo getLeaveGroupRequest(String groupId) {
        HttpRequestInfo leaveGroup =
                new HttpRequestInfo(RequestMethod.DELETE, ExpectedResult.BOOLEAN,
                        HttpRequestService.CHANGE_MEMBER_STATE_ACTION);

        leaveGroup.appendToEndpoint(ME);
        leaveGroup.appendToEndpoint(GROUPS);
        leaveGroup.appendToEndpoint(groupId);

        return leaveGroup;
    }


    public static HttpRequestInfo getIsFollowingRequest(String userId) {
        HttpRequestInfo isFollowing =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.BOOLEAN,
                        HttpRequestService.IS_FOLLOWING_ACTION);

        isFollowing.appendToEndpoint(ME);
        isFollowing.appendToEndpoint(FOLLOWING);
        isFollowing.appendToEndpoint(userId);

        return isFollowing;
    }

    public static HttpRequestInfo getFollowRequest(String userId) {
        HttpRequestInfo followRequest = new
                HttpRequestInfo(RequestMethod.PUT, ExpectedResult.BOOLEAN,
                HttpRequestService.CHANGE_FOLLOW_STATE_ACTION);

        followRequest.appendToEndpoint(ME);
        followRequest.appendToEndpoint(FOLLOWING);
        followRequest.appendToEndpoint(userId);

        return followRequest;
    }

    public static HttpRequestInfo getUnfollowRequest(String userId) {
        HttpRequestInfo unfollowRequest =
                new HttpRequestInfo(RequestMethod.DELETE, ExpectedResult.BOOLEAN,
                        HttpRequestService.CHANGE_FOLLOW_STATE_ACTION);

        unfollowRequest.appendToEndpoint(ME);
        unfollowRequest.appendToEndpoint(FOLLOWING);
        unfollowRequest.appendToEndpoint(userId);

        return unfollowRequest;
    }


    public static HttpRequestInfo getIsLikedRequest(String videoId) {
        HttpRequestInfo isLiked =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.BOOLEAN,
                        HttpRequestService.IS_LIKED_ACTION);

        isLiked.appendToEndpoint(ME);
        isLiked.appendToEndpoint(LIKES);
        isLiked.appendToEndpoint(videoId);

        return isLiked;
    }

    public static HttpRequestInfo getLikeRequest(String videoId) {
        HttpRequestInfo likeRequest =
                new HttpRequestInfo(RequestMethod.PUT, ExpectedResult.BOOLEAN,
                        HttpRequestService.CHANGE_LIKE_STATE_ACTION);

        likeRequest.appendToEndpoint(ME);
        likeRequest.appendToEndpoint(LIKES);
        likeRequest.appendToEndpoint(videoId);

        return likeRequest;
    }

    public static HttpRequestInfo getDislikeRequest(String videoId) {
        HttpRequestInfo dislikeRequest =
                new HttpRequestInfo(RequestMethod.DELETE, ExpectedResult.BOOLEAN,
                        HttpRequestService.CHANGE_LIKE_STATE_ACTION);

        dislikeRequest.appendToEndpoint(ME);
        dislikeRequest.appendToEndpoint(LIKES);
        dislikeRequest.appendToEndpoint(videoId);

        return dislikeRequest;
    }


    public static HttpRequestInfo getUserSearchRequest(String query) {
        HttpRequestInfo userSearchRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.USER_LIST,
                        HttpRequestService.USER_LIST_ACTION);

        userSearchRequest.appendToEndpoint(USERS);

        userSearchRequest.mParameters.put(QUERY, query);

        return userSearchRequest;
    }

    public static HttpRequestInfo getVideoSearchRequest(String query) {
        HttpRequestInfo userSearchRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.VIDEO_LIST,

                        HttpRequestService.VIDEO_LIST_ACTION);

        userSearchRequest.appendToEndpoint(VIDEOS);

        userSearchRequest.mParameters.put(QUERY, query);

        return userSearchRequest;
    }

    public static HttpRequestInfo getChannelSearchRequest(String query) {
        HttpRequestInfo userSearchRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.CHANNEL_LIST,
                        HttpRequestService.CHANNEL_LIST_ACTION);

        userSearchRequest.appendToEndpoint(CHANNELS);

        userSearchRequest.mParameters.put(QUERY, query);

        return userSearchRequest;
    }

    public static HttpRequestInfo getGroupSearchRequest(String query) {
        HttpRequestInfo userSearchRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.GROUP_LIST,
                        HttpRequestService.GROUP_LIST_ACTION);

        userSearchRequest.appendToEndpoint(GROUPS);

        userSearchRequest.mParameters.put(QUERY, query);

        return userSearchRequest;
    }


    public static HttpRequestInfo getUserInfoRequest(String userId) {
        HttpRequestInfo userInfoRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.USER,
                        HttpRequestService.USER_ACTION);

        userInfoRequest.appendToEndpoint(USERS);
        userInfoRequest.appendToEndpoint(userId);

        return userInfoRequest;
    }

    public static HttpRequestInfo getGroupInfoRequest(String groupId) {
        HttpRequestInfo groupInfoRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.GROUP,
                        HttpRequestService.GROUP_ACTION);

        groupInfoRequest.appendToEndpoint(GROUPS);
        groupInfoRequest.appendToEndpoint(groupId);

        return groupInfoRequest;
    }

    public static HttpRequestInfo getChannelInfoRequest(String channelId) {
        HttpRequestInfo channelInfoRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.CHANNEL,
                        HttpRequestService.CHANNEL_ACTION);

        channelInfoRequest.appendToEndpoint(CHANNELS);
        channelInfoRequest.appendToEndpoint(channelId);

        return channelInfoRequest;
    }

    public static HttpRequestInfo getVideoInfoRequest(String videoId) {
        HttpRequestInfo videoInfoRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.VIDEO,
                        HttpRequestService.VIDEO_ACTION);

        videoInfoRequest.appendToEndpoint(VIDEOS);
        videoInfoRequest.appendToEndpoint(videoId);

        return videoInfoRequest;
    }


    public static HttpRequestInfo getUserVideosRequest(String userId) {
        HttpRequestInfo userVideosRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.VIDEO_LIST,
                        HttpRequestService.VIDEO_LIST_ACTION);

        userVideosRequest.appendToEndpoint(USERS);
        userVideosRequest.appendToEndpoint(userId);
        userVideosRequest.appendToEndpoint(VIDEOS);

        return userVideosRequest;
    }

    public static HttpRequestInfo getUserChannelsRequest(String userId) {
        HttpRequestInfo userChannelsRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.CHANNEL_LIST,
                        HttpRequestService.CHANNEL_LIST_ACTION);

        userChannelsRequest.appendToEndpoint(USERS);
        userChannelsRequest.appendToEndpoint(userId);
        userChannelsRequest.appendToEndpoint(CHANNELS);

        return userChannelsRequest;
    }

    public static HttpRequestInfo getUserGroupsRequest(String userId) {
        HttpRequestInfo userGroupsRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.GROUP_LIST,
                        HttpRequestService.GROUP_LIST_ACTION);

        userGroupsRequest.appendToEndpoint(USERS);
        userGroupsRequest.appendToEndpoint(userId);
        userGroupsRequest.appendToEndpoint(GROUPS);

        return userGroupsRequest;
    }

    public static HttpRequestInfo getUserFollowersRequest(String userId) {
        HttpRequestInfo userFollowersRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.USER_LIST,
                        HttpRequestService.USER_LIST_ACTION);

        userFollowersRequest.appendToEndpoint(USERS);
        userFollowersRequest.appendToEndpoint(userId);
        userFollowersRequest.appendToEndpoint(FOLLOWERS);

        return userFollowersRequest;
    }

    public static HttpRequestInfo getFollowedUsersRequest(String userId) {
        HttpRequestInfo followingUsersRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.USER_LIST,
                        HttpRequestService.USER_LIST_ACTION);

        followingUsersRequest.appendToEndpoint(USERS);
        followingUsersRequest.appendToEndpoint(userId);
        followingUsersRequest.appendToEndpoint(FOLLOWING);

        return followingUsersRequest;
    }

    public static HttpRequestInfo getUserLikes(String userId) {
        HttpRequestInfo userLikesRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.VIDEO_LIST,
                        HttpRequestService.VIDEO_LIST_ACTION);

        userLikesRequest.appendToEndpoint(USERS);
        userLikesRequest.appendToEndpoint(userId);
        userLikesRequest.appendToEndpoint(LIKES);

        return userLikesRequest;
    }


    public static HttpRequestInfo getRelatedVideosRequest(String videoId) {
        HttpRequestInfo relatedVideosRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.VIDEO_LIST,
                        HttpRequestService.VIDEO_LIST_ACTION);

        relatedVideosRequest.appendToEndpoint(VIDEOS);
        relatedVideosRequest.appendToEndpoint(videoId);
        relatedVideosRequest.appendToEndpoint(VIDEOS);

        return relatedVideosRequest;
    }

    public static HttpRequestInfo getCommentsRequest(String videoId) {
        HttpRequestInfo commentsRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.COMMENT_LIST,
                        HttpRequestService.CHANNEL_LIST_ACTION);

        commentsRequest.appendToEndpoint(VIDEOS);
        commentsRequest.appendToEndpoint(videoId);
        commentsRequest.appendToEndpoint(COMMENTS);

        return commentsRequest;
    }

    public static HttpRequestInfo getCommentRepliesRequest(String videoId, String commentId) {
        HttpRequestInfo repliesRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.COMMENT_LIST,
                        HttpRequestService.COMMENT_LIST_ACTION);

        repliesRequest.appendToEndpoint(VIDEOS);
        repliesRequest.appendToEndpoint(videoId);
        repliesRequest.appendToEndpoint(COMMENTS);
        repliesRequest.appendToEndpoint(commentId);
        repliesRequest.appendToEndpoint(REPLIES);

        return repliesRequest;
    }

    public static HttpRequestInfo getVideoLikesRequest(String videoId) {
        HttpRequestInfo videoLikesRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.USER_LIST,
                        HttpRequestService.USER_LIST_ACTION);

        videoLikesRequest.appendToEndpoint(VIDEOS);
        videoLikesRequest.appendToEndpoint(videoId);
        videoLikesRequest.appendToEndpoint(LIKES);

        return videoLikesRequest;
    }


    public static HttpRequestInfo getChannelVideosRequest(String channelId) {
        HttpRequestInfo channelVideosRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.VIDEO_LIST,
                        HttpRequestService.VIDEO_LIST_ACTION);

        channelVideosRequest.appendToEndpoint(CHANNELS);
        channelVideosRequest.appendToEndpoint(channelId);
        channelVideosRequest.appendToEndpoint(VIDEOS);

        return channelVideosRequest;
    }

    public static HttpRequestInfo getChannelUsersRequest(String channelId) {
        HttpRequestInfo channelUsersRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.USER_LIST,
                        HttpRequestService.USER_LIST_ACTION);

        channelUsersRequest.appendToEndpoint(CHANNELS);
        channelUsersRequest.appendToEndpoint(channelId);
        channelUsersRequest.appendToEndpoint(USERS);

        return channelUsersRequest;
    }


    public static HttpRequestInfo getGroupVideosRequest(String groupId) {
        HttpRequestInfo groupVideosRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.VIDEO_LIST,
                        HttpRequestService.VIDEO_LIST_ACTION);

        groupVideosRequest.appendToEndpoint(GROUPS);
        groupVideosRequest.appendToEndpoint(groupId);
        groupVideosRequest.appendToEndpoint(VIDEOS);

        return groupVideosRequest;
    }

    public static HttpRequestInfo getGroupUsersRequest(String groupId) {
        HttpRequestInfo groupUsersRequest =
                new HttpRequestInfo(RequestMethod.GET, ExpectedResult.USER_LIST,
                        HttpRequestService.USER_LIST_ACTION);

        groupUsersRequest.appendToEndpoint(GROUPS);
        groupUsersRequest.appendToEndpoint(groupId);
        groupUsersRequest.appendToEndpoint(USERS);

        return groupUsersRequest;
    }
}
