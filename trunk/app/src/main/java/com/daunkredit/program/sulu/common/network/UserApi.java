package com.daunkredit.program.sulu.common.network;

import com.daunkredit.program.sulu.bean.ContactInfoBean;
import com.daunkredit.program.sulu.bean.DepositMethodsBean;
import com.daunkredit.program.sulu.bean.DepositResponseBean;
import com.daunkredit.program.sulu.bean.EmploymentServerBean;
import com.daunkredit.program.sulu.bean.HistoryLoanAppInfoBean;
import com.daunkredit.program.sulu.bean.LatestLoanAppBean;
import com.daunkredit.program.sulu.bean.LoginRequestBean;
import com.daunkredit.program.sulu.bean.LoginTokenResponse;
import com.daunkredit.program.sulu.bean.MsgInboxBean;
import com.daunkredit.program.sulu.bean.PersonalInfoServerBean;
import com.daunkredit.program.sulu.bean.ProgressBean;
import com.daunkredit.program.sulu.bean.QualityErrorBean;
import com.daunkredit.program.sulu.bean.RegionBean;
import com.daunkredit.program.sulu.bean.VersionBean;
import com.daunkredit.program.sulu.bean.YWUser;
import com.sulu.kotlin.data.ActivityInfoBean;
import com.sulu.kotlin.data.CouponBean;
import com.sulu.kotlin.data.InviteeBean;
import com.sulu.kotlin.data.InviteePersonBean;
import com.sulu.kotlin.data.LoanRange;
import com.sulu.kotlin.data.MeInfoBean;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava.Result;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by localuser on 2017/2/27.
 */

public interface UserApi {

    @FormUrlEncoded
    @POST("auth/login/sms")
    Observable<ResponseBody> sendSms(@Field("mobile") String mobile);

    @FormUrlEncoded
    @POST("auth/login")
    Observable<LoginTokenResponse> login(@Header("X-SMS-CODE") LoginRequestBean.SMSCode smsCode,
                                         @Header("X-CAPTCHA-SID") LoginRequestBean.CaptchaSID captchaSid,
                                         @Header("X-CAPTCHA") LoginRequestBean.Captcha captcha,
                                         @Field("mobile") LoginRequestBean.Mobile mobile,
                                         @Field("code") String code);

    @POST("auth/logout")
    Observable<ResponseBody> logout(@Query("token") String token,
                                    @Query("refreshToken") String refreshToken);

    @FormUrlEncoded
    @PUT("record/employment")
    Observable<ResponseBody> submitEmploymentInfo(@Field("companyName") String companyName,
                                                  @Field("companyProvince") String companyProvince,
                                                  @Field("companyCity") String companyCity,
                                                  @Field("companyDistrict") String companyDistrict,
                                                  @Field("companyArea") String companyArea,
                                                  @Field("companyAddress") String companyAddress,
                                                  @Field("companyPhone") String companyPhone,
                                                  @Field("profession") String profession,
                                                  @Field("salary") String salary,
//                                                  @Field("workEmail") String workEmail,
                                                  @Header("X-AUTH-TOKEN") String token);

    @FormUrlEncoded
    @PUT("record/personalinfo")
    Observable<ResponseBody> submitPersonalInfo(@Field("fullName") String fullName,
                                                @Field("credentialNo") String credentialNo,
                                                @Field("familyNameInLaw") String familyNameInLaw,
                                                @Field("gender") String gender,
                                                @Field("province") String province,
                                                @Field("city") String city,
                                                @Field("district") String district,
                                                @Field("area") String area,
                                                @Field("address") String address,
                                                @Field("lastEducation") String lastEducation,
                                                @Field("maritalStatus") String maritalStatus,
                                                @Field("childrenNumber") String childrenNumber,
                                                @Field("residenceDuration") String residenceDuration,
                                                @Field("facebookId") String facebookId,
                                                @Header("X-AUTH-TOKEN") String token);

    @FormUrlEncoded
    @PUT("record/contact")
    Observable<ResponseBody> submitContactInfo(@Field("parentName") String parentName,
                                               @Field("parentMobile") String parentMobile,
                                               @Field("friendName") String friendName,
                                               @Field("friendMobile") String friendMobile,
                                               @Header("X-AUTH-TOKEN") String token);

    @GET("loanapp/latest/v2")
    Observable<LatestLoanAppBean> getLatestLoanApp(@Header("X-AUTH-TOKEN") String token, @Query("type") String type);

    @FormUrlEncoded
    @POST("loanapp")
    Observable<ResponseBody> applyLoanApp(@Field("loanType") String loanType,
                                          @Field("amount") String amount,
                                          @Field("period") String period,
                                          @Field("periodUnit") String periodUnit,
                                          @Field("bankCode") String bankCode,
                                          @Field("cardNo") String cardNo,
                                          @Field("applyFor") String applyFor,
                                          @Field("applyChannel") String applyChannel,
                                          @Field("applyPlatform") String applyPlatform,
                                          @Field("couponId") long couponId,
                                          @Header("X-AUTH-TOKEN") String token
    );

    @POST("auth/refresh")
    Observable<Result<LoginTokenResponse>> refreshToken(@Query("mobile") String mobile,
                                                        @Query("refreshToken") String refreshToken);

    @GET("region/{level}/{id}")
    Observable<RegionBean> getRegion(@Path("level") String level,
                                     @Path("id") int id

    );

    @FormUrlEncoded
    @POST("auth/logout")
    Observable<ResponseBody> logout(@Field("token") String token);

    @GET("loanapp/deposit/methods")
    Observable<DepositMethodsBean> getDepostMethods(@Header("X-AUTH-TOKEN") String token);

    @FormUrlEncoded
    @POST("loanapp/deposit")
    Observable<DepositResponseBean> doDeposit(@Field("loanAppId") String loanAppId,
                                              @Field("currency") String currency,
                                              @Field("depositMethod") String method,
                                              @Header("X-AUTH-TOKEN") String token
    );

    @FormUrlEncoded
    @PUT("loanapp/deposit")
    Observable<ResponseBody> sendPayCode(@Field("depositId") String loanAppId,
                                         @Field("outerTransactionId") String paymentCode,
                                         @Header("X-AUTH-TOKEN") String token
    );

    @GET("loanapp/all/v2")
    Observable<List<HistoryLoanAppInfoBean>> getLoanAppAll(@Header("X-AUTH-TOKEN") String token);

    @GET("record/progress")
    Observable<ProgressBean> progress(@Header("X-AUTH-TOKEN") String token);

    @FormUrlEncoded
    @POST("loanapp/cancel")
    Observable<ResponseBody> cancelLoan(@Field("loanAppId") String loanAppId,
                                        @Header("X-AUTH-TOKEN") String token);

    @GET("info/inbox/all")
    Observable<List<MsgInboxBean>> getMsgInbox(@Header("X-AUTH-TOKEN") String token);

    @FormUrlEncoded
    @POST("info/inbox/read")
    Observable<ResponseBody> sendReadMsg(@Field("msgId") String msgId,
                                         @Header("X-AUTH-TOKEN") String token);

    @GET("loanapp/qualification")
    Observable<QualityErrorBean> isQualification(@Header("X-AUTH-TOKEN") String token);

    @GET("record/contact")
    Observable<ContactInfoBean> getContactInfo(@Header("X-AUTH-TOKEN") String token);

    @GET("record/employment")
    Observable<EmploymentServerBean> getEmploymentInfo(@Header("X-AUTH-TOKEN") String token);

    @GET("record/personalinfo")
    Observable<PersonalInfoServerBean> getPersonalInfo(@Header("X-AUTH-TOKEN") String token);


    @GET("chat/account")
    Observable<YWUser> getChatUserInfo(@Header("X-AUTH-TOKEN") String token);

    /**
     * obtain latest version
     * @return
     */
    @GET("version/latest")
    Observable<VersionBean> getVersionInfo();


    /**
     * obtain loan range(min  ---  max)
     * @return
     */
    @GET("loanapp/range")
    Observable<LoanRange> getLoanRange();

    @GET("invitation/mine/code")
    Observable<ResponseBody> getInviteCode(@Header("X-AUTH-TOKEN") String token);

    @GET("invitation/mine/invitee")
    Observable<InviteeBean> getInviteInfo(@Header("X-AUTH-TOKEN") String token);

    @GET("coupon/available")
    Observable<List<CouponBean>> getAvailableCoupon(@Header("X-AUTH-TOKEN") String token);

    @GET("coupon/used")
    Observable<List<CouponBean>> getUsedCoupon(@Header("X-AUTH-TOKEN") String token);

    @GET("coupon/outdated")
    Observable<List<CouponBean>> getOutdatedCoupon(@Header("X-AUTH-TOKEN") String token);

    @GET("info/infocenter")
    Observable<MeInfoBean> getMeInfo(@Header("X-AUTH-TOKEN") String token);

    @GET("banner")
    Observable<ArrayList<ActivityInfoBean>> getActivityList();

    @GET("invitation/mine/invitee/list")
    Observable<ArrayList<InviteePersonBean>> getInvitedList(@Header("X-AUTH-TOKEN") String token);
}