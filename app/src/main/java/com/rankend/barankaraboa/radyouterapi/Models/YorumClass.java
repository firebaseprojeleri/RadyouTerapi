package com.rankend.barankaraboa.radyouterapi.Models;

/**
 * Created by SE on 18.04.2017.
 */

public class YorumClass {
    public String key;
    public String userId;
    public String userImage;
    public String userNickName;
    public String yorum;
    public long addedTime;
    public boolean joined = false;

    public YorumClass(){

    }
    public YorumClass(String userId, String userImage, String userNickName, String yorum, long addedTime){
        this.userId = userId;
        this.userImage = userImage;
        this.userNickName = userNickName;
        this.yorum = yorum;
        this.addedTime = addedTime;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public String getYorum() {
        return yorum;
    }

    public void setYorum(String yorum) {
        this.yorum = yorum;
    }

    public long getAddedTime() {
        return addedTime;
    }

    public void setAddedTime(long addedTime) {
        this.addedTime = addedTime;
    }
}
