package com.daunkredit.program.sulu.bean;

/**
 * Created by Miaoke on 2017/3/1.
 */

public class ContactInfoBean {

    /**
     * friendMobile : string
     * friendName : string
     * parentMobile : string
     * parentName : string
     */

    private String friendMobile;
    private String friendName;
    private String parentMobile;
    private String parentName;

    public String getFriendMobile() {
        return friendMobile;
    }

    public void setFriendMobile(String friendMobile) {
        this.friendMobile = friendMobile;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getParentMobile() {
        return parentMobile;
    }

    public void setParentMobile(String parentMobile) {
        this.parentMobile = parentMobile;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }
}
