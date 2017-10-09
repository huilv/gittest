package com.daunkredit.program.sulu.harvester.stastic;

/**
 * Created by Miaoke on 11/05/2017.
 */

public class UserEvent {

    protected String actionName;
    protected ActionType actionType;

    public UserEvent(String actionName, ActionType actionType) {
        this.actionName = actionName;
        this.actionType = actionType;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    @Override
    public String toString() {
        return "UserEvent{" +
                "actionName='" + actionName + '\'' +
                ", actionType=" + actionType +
                '}';
    }
}
