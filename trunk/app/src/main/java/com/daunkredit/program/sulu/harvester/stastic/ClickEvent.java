package com.daunkredit.program.sulu.harvester.stastic;

import com.orhanobut.logger.Logger;

/**
 * Created by Miaoke on 05/05/2017.
 */

public class ClickEvent extends UserEvent {

    protected String clickViewText;
    protected String actionDescription;
    protected Long actionTime;

    public ClickEvent(String actionName, ActionType actionType) {
        super(actionName,actionType);
        String[] appidList  = actionName.split(" ");
        super.actionName = appidList[appidList.length - 1];

        this.actionTime = System.currentTimeMillis();
        UserEventQueue.add(this);
        Logger.d("------> Click action."+this);
    }

    public ClickEvent(String actionName, ActionType actionType, String clickViewText) {
        super(actionName,actionType);
        String[] appidList  = actionName.split(" ");
        super.actionName = appidList[appidList.length - 1];

        this.actionTime = System.currentTimeMillis();
        this.clickViewText = clickViewText;
        UserEventQueue.add(this);
        Logger.d("------> Click action."+ this.toString());
    }

    public String getActionDescription() {
        return actionDescription;
    }

    public void setActionDescription(String actionDescription) {
        this.actionDescription = actionDescription;
    }

    public Long getActionTime() {
        return actionTime;
    }

    public void addToQueue(){
        Logger.d("add click event to queue.");
        UserEventQueue.add(this);
    }

    @Override
    public String toString() {

        return "ClickEvent{" +
                "actionName='" + actionName + '\'' +
                ",actionType=" + actionType + '\''+
                ", clickViewText='" + clickViewText + '\'' +
                ", actionDescription='" + actionDescription + '\'' +
                ", actionTime=" + actionTime +
                '}';
    }
}
