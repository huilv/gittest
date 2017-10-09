package com.daunkredit.program.sulu.app;

/**
 * @作者:My
 * @创建日期: 2017/3/21 17:11
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public interface FieldParams {
    String PHOTO_TYPE = "photo_type";
    String LOAN_APP_INFO_BEAN = "show_loan_detail";
    String TEMP_INTENT = "tempintent";
    String REQUEST_CODE = "request_code";
    String LATESTBEAN = "latestbean";

    String SHOW_MY_LOAN_INFO = "show my loan info";
    String LOANSTATUS = "loanstatus";
    String VIDEOPATH = "videopath";
    String FACEBOOK_ID = "facebook id";
    String LOANRESULT = "loan result";

    String UPDATE_VERSON_BEAN = "update verson bean";
    String USERNAME = "username";
    String MESSAGE_NUMBER = "Message_Number";
    String APP_NAME = "sulu";
    String KTP_IMAGE = "ktp_image.jpg";
    String WORK_IMAGE = "work_image.jpg";
    String TO_LOAN_DATA = "to_loan_data";
    String LOAN_RANGE = "loan_range";
    String DATA_OBTAIN_START_TIME = "data_obtain_start_time";
    String DATA_OBTAIN_END_TIME = "data_obtain_end_time";
    String LOCATION_RESULT = "location_result";
    String LOCATION_RESULT_GEO = "location_result_geo";
    String LOCATION_RESULT_BUNDLE = "location_result_bundle";
    String HAS_YW_LOGIN = "has yw login";
    String YW_ACCOUNT_USERID = "yw account userid";
    String YW_ACCOUNT_PASSWORD = "yw account password";
    int NOTIFICATION_CLICKED_REQUEST_CODE = 120011;
    String LockViewMode = "lock view mode";
    String AREA_UPPER_AREA_ID = "area upper area id";
    String NEED_UPDATE_REGION = "need update region";
    String COUPON_CHOOSED_NAME = "Coupon choosed Name";
    String ACTIVITY_DETAIL_IMAGE_URL = "activity detail image url";
    String CURRENT_LOAN_RATE = "current_loan_rate";
    String COUPON_TYPE_OF_REQUEST = "request coupon type";
    String ASSERTS_PRESTR = "file:///android_asset/";
    String COUPON_TO_SHOW = "coupon_to_show";


    class LoanStatus {
        public static final String SUBMITTED = "SUBMITTED";
        public static final String LOANDED = "CURRENT";
        public static final String EXPIRED = "EXPIRED";
        public static final String WITHDRAWN = "WITHDRAWN";
        public static final String IN_REVIEW = "IN_REVIEW";
        public static final String OVERDUE = "OVERDUE";
        public static final String PAID_OFF = "PAID_OFF";
        public static final String REJECTED = "REJECTED";
        public static final String CLOSED = "CLOSED";
    }

    public class NotifactionId {
        public static final int ONE = 0x1101;
    }

    public class BroadcastAction {
        public static final String NEW_MESSAGE = "com.sulu.broadcast.new_message";
        public static final String BROADCASTACTION = "com.sulu.broadcast";
        public static final String BROADCASTACTION_UPDATE = "com.sulu.broadcast.update";
    }

    public class PageStatus {
        public static final String HAS_NEW_MESSAGE = "has_new_message";
    }

    public static class PersonInfo {
        public static final String FULLNAME = " full name";
        public static final String KTP_NO = "ktp no";
        public static final String GENDER = "gender";
        public static final String EDUCATION = " education";
        public static final String MARITAL = "marital";
        public static final String CHILDRENACCOUNT = "children account";
        public static final String REGION = "region";
        public static final String ADDRESS = "address";
        public static final String DURATION = "duration";
        public static final String FAMILYNAMEINLAW = "family name in law";
    }

    public static class ProfessionInfo {
        public static final String JOBTYPE = "job type";
        public static final String SALARY = "salary";
        public static final String COMPANYNAME = "company name";
        public static final String COMPANYADDRESS = "company address";
        public static final String COMPANYTEL = "company tel";
        public static final String COMPANYTELPRE = "company tel pre";
        public static final String REGION = "region";
    }

    public static class MainActivity {
        public static final String TO_TAB_ONE = "to tab one";
        public static final String TAB_CHOOSE = "tab choose";
    }
}
