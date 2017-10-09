package com.daunkredit.program.sulu.bean;

/**
 * Created by Miaoke on 30/03/2017.
 */

public class QualityErrorBean {

    /**
     * error : err.auth.access.denied
     * message : 验证失败
     */

    private String error;
    private String message;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
