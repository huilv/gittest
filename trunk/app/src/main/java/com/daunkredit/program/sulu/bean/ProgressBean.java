package com.daunkredit.program.sulu.bean;

/**
 * Created by Miaoke on 2017/3/20.
 */

public class ProgressBean {

    /**
     * personalInfoPart : false
     * employmentPart : false
     * contactPart : false
     * filePart : true
     * completed : false
     */

    private boolean personalInfoPart;
    private boolean employmentPart;
    private boolean contactPart;
    private boolean filePart;
    private boolean completed;

    public boolean isPersonalInfoPart() {
        return personalInfoPart;
    }

    public void setPersonalInfoPart(boolean personalInfoPart) {
        this.personalInfoPart = personalInfoPart;
    }

    public boolean isEmploymentPart() {
        return employmentPart;
    }

    public void setEmploymentPart(boolean employmentPart) {
        this.employmentPart = employmentPart;
    }

    public boolean isContactPart() {
        return contactPart;
    }

    public void setContactPart(boolean contactPart) {
        this.contactPart = contactPart;
    }

    public boolean isFilePart() {
        return filePart;
    }

    public void setFilePart(boolean filePart) {
        this.filePart = filePart;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
