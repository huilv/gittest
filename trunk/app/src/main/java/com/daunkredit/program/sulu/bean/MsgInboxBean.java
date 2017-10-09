package com.daunkredit.program.sulu.bean;

/**
 * Created by Miaoke on 2017/3/24.
 */

public class MsgInboxBean {

    /**
     * createTime : 2017-03-24T09:56:37.757Z
     * msgBody : string
     * msgId : 0
     * msgTitle : string
     * msgType : NOTIFICATION
     * read : true
     */

    private String createTime;
    private String msgBody;
    private int msgId;
    private String msgTitle;
    private String msgType;
    private boolean read;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(String msgBody) {
        this.msgBody = msgBody;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public String getMsgTitle() {
        return msgTitle;
    }

    public void setMsgTitle(String msgTitle) {
        this.msgTitle = msgTitle;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
