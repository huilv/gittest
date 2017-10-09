package com.daunkredit.program.sulu.bean;

/**
 * Created by localuser on 2017/2/27.
 */

public class LoginTokenResponse {
    /**
     * token : eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxNTAyNjU2MTM4NyIsImV4cCI6MTQ4ODE5NTc3MX0.HBjRY3XZxgr2fdnCpGt3zjClWAz-gyLYC7kSZAm7WT6yp3klllVEwmyDdAMJSQZp5VDh1D0mJOdtm3ainjOfBQ
     * refreshToken : eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxNTAyNjU2MTM4NyIsImV4cCI6MTQ4ODgwMDU3MX0.Rz5kr57TD82LAkr7b1ALKjcDVgboO6G29KjjiuyFQ7EkuTsmYoZI4iJ4PR0dv7kkSW6U2JCGcBt_S_8i4TE_ow
     */

    private String token;
    private String refreshToken;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
