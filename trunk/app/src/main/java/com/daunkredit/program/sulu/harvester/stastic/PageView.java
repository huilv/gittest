package com.daunkredit.program.sulu.harvester.stastic;

import com.orhanobut.logger.Logger;

/**
 * Created by Miaoke on 05/05/2017.
 */

public class PageView extends UserEvent{

    private String pageDescription;

    private Long startPageTime;
    private Long endPageTime;
    private Long duration;

    public PageView(String pageName, ActionType actionType) {

       super(pageName,actionType);
    }


    public String getPageDescription() {
        return pageDescription;
    }

    public void setPageDescription(String pageDescription) {
        this.pageDescription = pageDescription;
    }

    public Long getStartPageTime() {
        return startPageTime;
    }

    public void startPage() {
        this.startPageTime = System.currentTimeMillis();
        Logger.d("------> start view page");
    }

    public Long getEndPageTime() {
        return endPageTime;
    }

    public void endPage() {
        this.endPageTime = System.currentTimeMillis();
        Logger.d("add page view event queue");
        UserEventQueue.add(this);
        Logger.d("<------ end view page.");
    }


    public Long getDuration() {
        if (startPageTime!=null && endPageTime!= null && startPageTime > 0  && endPageTime > 0) {
            this.duration = endPageTime - startPageTime;
        } else {
            this.duration = 0L;
        }
        return duration;
    }

    @Override
    public String toString() {
        getDuration();
        return "PageView{" +
                "pageName='" + actionName + '\'' +
                ", actionType=" + actionType +
                ", pageDescription='" + pageDescription + '\'' +
                ", startPageTime=" + startPageTime +
                ", endPageTime=" + endPageTime +
                ", duration=" + duration +
                '}';
    }
}
