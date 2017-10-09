package com.daunkredit.program.sulu.view.certification;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.common.StringUtil;
import com.daunkredit.program.sulu.R;
import com.daunkredit.program.sulu.app.base.BaseActivity;
import com.daunkredit.program.sulu.common.network.ServiceGenerator;
import com.daunkredit.program.sulu.common.network.UserApi;
import com.daunkredit.program.sulu.harvester.stastic.ActionType;
import com.daunkredit.program.sulu.harvester.stastic.ClickEvent;
import com.daunkredit.program.sulu.harvester.stastic.UserEventQueue;
import com.daunkredit.program.sulu.view.certification.presenter.ContactInfoActPreImp;
import com.daunkredit.program.sulu.view.certification.presenter.ContactInfoActPresenter;
import com.daunkredit.program.sulu.common.TokenManager;
import com.hwangjr.rxbus.RxBus;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func4;
import rx.schedulers.Schedulers;

import static com.daunkredit.program.sulu.view.fragment.CertificationFragmentProgress.mContactInfoBean;

/**
 * Created by Miaoke on 2017/2/27.
 */

public class ContactInfoActivity extends BaseActivity<ContactInfoActPresenter> {


    @BindView(R.id.id_imagebutton_contact_info_father_mother)
    ImageButton contact;
    @BindView(R.id.id_edittext_contact_info_father_mother_name)
    TextView parentName;
    @BindView(R.id.id_textview_contact_info_father_mother_telephone)
    TextView parentPhone;
    @BindView(R.id.id_textview_contact_info_friend_name)
    TextView friendName;
    @BindView(R.id.id_textview_contact_info_friend_elephone)
    TextView friendPhone;
    @BindView(R.id.id_textview_title)
    TextView title;
    @BindView(R.id.id_imagebutton_info_list)
    ImageButton infoList;
    @BindView(R.id.id_button_contact_info_submit)
    Button contactInfoSubmit;

    final UserApi api = ServiceGenerator.createService(UserApi.class);
    String token = TokenManager.getInstance().getToken();

    protected static int REQUEST_ERAD_CALL_LOGS_PERMISSION = 11010;
    protected static int REQUEST_READ_CONTACTS = 11011;
    protected static int REQUEST_READ_SMS = 11012;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_contacts_info;
    }

    @Override
    protected void init() {

        ButterKnife.bind(this);

        infoList.setVisibility(View.INVISIBLE);
        title.setText(getResources().getString(R.string.textview_field_contract_info));


        if (mContactInfoBean != null) {
            parentName.setText(mContactInfoBean.getParentName());
            parentPhone.setText(mContactInfoBean.getParentMobile());
            friendName.setText(mContactInfoBean.getFriendName());
            friendPhone.setText(mContactInfoBean.getFriendMobile());
        }

        updateSubmitButtonState();
        editTextChangeEvent();

//        new PermissionManager().fetchPersonInfo(ContactInfoActivity.this,Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG);
       mPresenter.requestPermission();
    }

    @Override
    protected ContactInfoActPresenter initPresenterImpl() {
        return new ContactInfoActPreImp();
    }

    //Text Change Listener

    private void editTextChangeEvent() {
        Observable<CharSequence> observableParentName = RxTextView.textChanges(parentName);
        Observable<CharSequence> observableParentPhone = RxTextView.textChanges(parentPhone);
        Observable<CharSequence> observableFriendName = RxTextView.textChanges(friendName);
        Observable<CharSequence> observableFriendPhone = RxTextView.textChanges(friendPhone);


        Observable.combineLatest(observableParentName, observableParentPhone, observableFriendName, observableFriendPhone, new Func4<CharSequence, CharSequence, CharSequence, CharSequence, Boolean>() {
            @Override
            public Boolean call(CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, CharSequence charSequence4) {
                return !StringUtil.isNullOrEmpty(charSequence.toString()) || !StringUtil.isNullOrEmpty(charSequence2.toString()) || !StringUtil.isNullOrEmpty(charSequence3.toString()) || !StringUtil.isNullOrEmpty(charSequence3.toString());
            }
        }).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                updateSubmitButtonState();
            }
        });
    }

    private void updateSubmitButtonState() {
        if (isCheckedField()) {
            contactInfoSubmit.setClickable(true);
            contactInfoSubmit.setAlpha(0.8f);
        } else {
            contactInfoSubmit.setClickable(false);
            contactInfoSubmit.setAlpha(0.3f);
        }
    }

    public boolean isCheckedField() {
        if (StringUtil.isNullOrEmpty(parentName.getText().toString())) {
//            Toast.makeText(ContactInfoActivity.this, "Please input parent name.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (StringUtil.isNullOrEmpty(parentPhone.getText().toString())) {
//            Toast.makeText(ContactInfoActivity.this, "Please input parent Phone.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (StringUtil.isNullOrEmpty(friendName.getText().toString())) {
//            Toast.makeText(ContactInfoActivity.this, "Please input friend name.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return !StringUtil.isNullOrEmpty(friendPhone.getText().toString());

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                Cursor cursor = getContentResolver()
                        .query(uri,
                                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,
                                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
                                null, null, null);
                while (cursor != null && cursor.moveToNext()) {
                    String number = cursor.getString(0);
                    String name = cursor.getString(1);
                    if (requestCode == 1) {
                        if (isContactSame(name, number, friendName.getText().toString(), friendPhone.getText().toString())) {
                            parentName.setText("");
                            parentPhone.setText("");
                        } else {
                            parentName.setText(name);
                            parentPhone.setText(number);
                        }

                    } else if (requestCode == 2) {
                        if (isContactSame(parentName.getText().toString(), parentPhone.getText().toString(), name, number)) {
                            friendName.setText("");
                            friendPhone.setText("");
                        } else {
                            friendName.setText(name);
                            friendPhone.setText(number);
                        }
                    }
                    Logger.d("Result code: " + requestCode);
                    Logger.d("name: " + name + "number: " + number);

                }
            }
        }
    }


    private boolean isContactSame(String parentName, String parentPhone, String friendName, String friendPhone) {
        parentPhone = parentPhone.trim().replace(" ","");
        friendPhone = friendPhone.trim().replace(" ","");

        if (!StringUtil.isNullOrEmpty(parentName) && !StringUtil.isNullOrEmpty(friendName) && !StringUtil.isNullOrEmpty(friendName) && !StringUtil.isNullOrEmpty(friendPhone)) {

            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Warning: ");
            alertDialogBuilder.setNeutralButton("Got it", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });


            if (parentName.equals(friendName)) {
                alertDialogBuilder.setMessage(getString(R.string.same_name));
                AlertDialog alertDialog = alertDialogBuilder.create();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        alertDialogBuilder.show();
                    }
                });
                return true;
            }

            if (parentPhone.equals(friendPhone)) {
                alertDialogBuilder.setMessage(getString(R.string.same_number));
                AlertDialog alertDialog = alertDialogBuilder.create();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        alertDialogBuilder.show();
                    }
                });
                return true;
            }

        }
        return false;
    }


    @OnClick(R.id.ll_contactinfo_father)
    public void getParentInfo() {
        UserEventQueue.add(new ClickEvent(findViewById(R.id.ll_contactinfo_father).toString(), ActionType.CLICK, "Input Parent"));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, 1);
            }
        }
    }


    @OnClick(R.id.ll_contactinfo_friend)
    public void getFriendInfo() {
        UserEventQueue.add(new ClickEvent(findViewById(R.id.ll_contactinfo_friend).toString(), ActionType.CLICK, "Input Friend"));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, 2);
            }

        }
    }

    @OnClick(R.id.id_button_contact_info_submit)
    public void contactInfoSubmit() {
        UserEventQueue.add(new ClickEvent(contactInfoSubmit.toString(), ActionType.CLICK, contactInfoSubmit.getText().toString()));
        if (isCheckedField()) {
            showLoading(getResources().getText(R.string.show_uploading).toString());
            api.submitContactInfo(parentName.getText().toString()
                    , parentPhone.getText().toString()
                    , friendName.getText().toString()
                    , friendPhone.getText().toString()
                    , token)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ResponseBody>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(ContactInfoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            dismissLoading();
                            Logger.d("e:" + e);

                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            Logger.d("Submit success!");
                            ContactInfoActivity.this.setResult(Activity.RESULT_OK);
                            dismissLoading();
                            finish();

                        }
                    });
        }

    }

    @OnClick(R.id.id_imagebutton_back)
    public void back() {
        UserEventQueue.add(new ClickEvent(findViewById(R.id.id_imagebutton_back).toString(), ActionType.CLICK, "Back"));
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
    }


    /**********************************************************************************/


//    private void fetchPersonInfo(int arg) {
//        RxPermissions rxPermissions = new RxPermissions(this);
//
//        String[] permissionList;
//        if(arg == 0){
//            permissionList = new String[]{ Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_SMS};
//        }else{
//            permissionList = new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG};
//        }
//
//        rxPermissions.requestEach(
//                permissionList
//        ).subscribe(new Action1<Permission>() {
//            @Override
//            public void call(Permission permission) {
//                if (permission.name.equals(Manifest.permission.READ_CONTACTS)) {
//                    if (permission.granted) {
//                        HarvestInfoManager.getInstance().setContactStatus("0");
//                        HarvestInfoManager.getInstance().setContactJsonobject(getContactInfo());
//                    } else {
//                        Toast.makeText(ContactInfoActivity.this, "Read contact info permission do not granted", Toast.LENGTH_SHORT).show();
//                        Logger.d("Read contact info permission do not granted");
//                    }
//                } else if (permission.name.equals(Manifest.permission.READ_CALL_LOG)) {
//                    if (permission.granted) {
//                        HarvestInfoManager.getInstance().setCallLogsStatus("0");
//                        HarvestInfoManager.getInstance().setCallLogsJsonobject(getCallLogs());
//                    } else {
//                        Toast.makeText(ContactInfoActivity.this, "Read call logs permission do not granted", Toast.LENGTH_SHORT).show();
//                        Logger.d("Read call logs permission do not granted");
//                    }
//                } else if (permission.name.equals(Manifest.permission.READ_SMS)) {
//                    if (permission.granted) {
//                        HarvestInfoManager.getInstance().setSmsStatus("0");
//                        HarvestInfoManager.getInstance().setSmsJsonobject(getSmsInfo());
//                    } else {
//                        Toast.makeText(ContactInfoActivity.this, "Read SMS permission do not granted", Toast.LENGTH_SHORT).show();
//                        Logger.d("Read SMS permission do not granted");
//                    }
//                } else if (permission.name.equals(Manifest.permission.READ_PHONE_STATE)) {
//                    if (permission.granted) {
//                        HarvestInfoManager.getInstance().setImeiKey(getImei());
//                    } else {
//                        Toast.makeText(ContactInfoActivity.this, "Read phone state permission do not granted", Toast.LENGTH_SHORT).show();
//                        Logger.d("Read phone state permission do not granted");
//                    }
//                }
//            }
//        });
//    }
//
//
//    /**
//     * Check permission
//     */
//    private void checkPermission() {
//        if (Build.VERSION.SDK_INT >= 23) {
//            int readCallLogsPermission = checkSelfPermission(Manifest.permission.READ_CALL_LOG);
//            int readContactsPermission = checkSelfPermission(Manifest.permission.READ_CONTACTS);
//            int readSmsPermission = checkSelfPermission(Manifest.permission.READ_SMS);
//
//            if (readCallLogsPermission != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(ContactInfoActivity.this, new String[]{Manifest.permission.READ_CALL_LOG}, REQUEST_ERAD_CALL_LOGS_PERMISSION);
//            } else {
//                getCallLogs();
//            }
//
//            if (readContactsPermission != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(ContactInfoActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACTS);
//            } else {
//                getContactInfo();
//            }
//
//            if (readSmsPermission != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(ContactInfoActivity.this, new String[]{Manifest.permission.READ_SMS}, REQUEST_READ_SMS);
//            } else {
//                getSmsInfo();
//            }
//
//        } else {
//            getContactInfo();
//            getCallLogs();
//            getSmsInfo();
//        }
//    }
//
//    /**
//     * Get contacts info
//     *
//     * @return {
//     * "totalNumber": 3,
//     * "latestTime": 1491456757319,
//     * "earliestTime": 1491897424727,
//     * "contactList": [
//     * {
//     * "name": "测试爸爸",
//     * "phoneNumbers": [
//     * "8877665544"
//     * ],
//     * "emails": [],
//     * "lastUpdate": "1491894766282"
//     * },
//     * {
//     * "name": "测试朋友",
//     * "phoneNumbers": [
//     * "7766554433"
//     * ],
//     * "emails": [],
//     * "lastUpdate": "1491456757319"
//     * },
//     * {
//     * "name": "你好",
//     * "phoneNumbers": [
//     * "789456123",
//     * "14700000"
//     * ],
//     * "emails": [
//     * "gshhdhdhdh@hdbhds",
//     * "hdhhdhd@hshhs"
//     * ],
//     * "lastUpdate": "1491897424727"
//     * }
//     * ]
//     * }
//     */
//    protected JSONObject getContactInfo() {
//        JSONObject contactsEntity = new JSONObject();
//        Long totalNumber = 0L;
//        Long latestTime = Long.MAX_VALUE;
//        Long earliestTime = Long.MIN_VALUE;
//
//
//        JSONArray contactArray = new JSONArray();
//        ContentResolver contentResolver = getContentResolver();
//        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
//        Cursor phoneCursor = null;
//        Cursor emailCursor = null;
//
//        while (cursor.moveToNext()) {
//            JSONObject contact = new JSONObject();
//            JSONArray phoneNumbers = new JSONArray();
//            JSONArray emails = new JSONArray();
//            String lastUpdateTime;
//            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
//
//            //get the phone number
//            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
//            lastUpdateTime = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP));
//            phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
//            emailCursor = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
//
//            while (phoneCursor.moveToNext()) {
//                String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                number = number.replace("-", "");
//                number = number.replace(" ", "");
//                phoneNumbers.put(number);
//            }
//
//            while (emailCursor.moveToNext()) {
//                String email = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
//                emails.put(email);
//            }
//
//            try {
//                if (phoneNumbers.length() == 0)
//                    continue;
//
//                totalNumber++;
//                latestTime = Math.min(latestTime, Long.parseLong(lastUpdateTime));
//                earliestTime = Math.max(earliestTime, Long.parseLong(lastUpdateTime));
//
//
//                contact.put("name", name);
//                contact.put("number", phoneNumbers);
//                contact.put("email", emails);
////                contact.put("lastUpdate", new SimpleDateFormat("yyyy-MM-dd").format(new Date(Long.parseLong(lastUpdateTime))));
//                contact.put("lastUpdate", lastUpdateTime);
//                contactArray.put(contact);
//            } catch (JSONException ex) {
//                Logger.d("JSONException: " + ex.getMessage());
//                ex.printStackTrace();
//            }
//
//        }
//
//
//        if (cursor != null) {
//            cursor.close();
//        }
//
//        if (phoneCursor != null) {
//            phoneCursor.close();
//        }
//        if (emailCursor != null) {
//            emailCursor.close();
//        }
//
//        try {
//            contactsEntity.put("protocolName", ProtocolName.CONTACT);
//            contactsEntity.put("protocolVersion", ProtocolVersion.V_1_0);
//
//            contactsEntity.put("totalNumber", totalNumber);
//            contactsEntity.put("latestTime", latestTime);
//            contactsEntity.put("earliestTime", earliestTime);
//            contactsEntity.put("data", contactArray);
//        } catch (JSONException ex) {
//            Logger.d("JSONException: " + ex.getMessage());
//            ex.printStackTrace();
//        }
//
//        Logger.d(contactsEntity);
//        return contactsEntity;
//    }
//
//
//    /**
//     * Get Call Logs
//     * {
//     * "totalNumber": 4,
//     * "latestTime": 1491456695226,
//     * "earliestTime": 1491894762023,
//     * "callLogList": [
//     * {
//     * "name": "测试爸爸",
//     * "number": "8877665544",
//     * "direction": "2",
//     * "createTime": "1491894762023",
//     * "duration": "0"
//     * },
//     * {
//     * "number": "+8615601671727",
//     * "direction": "2",
//     * "createTime": "1491536339614",
//     * "duration": "0"
//     * },
//     * {
//     * "number": "+8615601671427",
//     * "direction": "2",
//     * "createTime": "1491536327087",
//     * "duration": "0"
//     * },
//     * {
//     * "name": "测试爸爸",
//     * "number": "8877665544",
//     * "direction": "2",
//     * "createTime": "1491456695226",
//     * "duration": "0"
//     * }
//     * ]
//     * }
//     */
//    //type: income_type 1 outgoing_type: 2 missed_type: 3
//    protected JSONObject getCallLogs() {
//
//        JSONObject callLogsEntity = new JSONObject();
//        Long totalNumber = 0L;
//        Long latestTime = Long.MAX_VALUE;
//        Long earliestTime = Long.MIN_VALUE;
//
//        JSONArray callLogsArray = new JSONArray();
//        ContentResolver contentResolver = getContentResolver();
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
//            Cursor cursor = contentResolver.query(CallLog.Calls.CONTENT_URI,
//                    new String[]{CallLog.Calls.CACHED_NAME,
//                            CallLog.Calls.NUMBER,
//                            CallLog.Calls.TYPE,
//                            CallLog.Calls.DATE,
//                            CallLog.Calls.DURATION},
//                    null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
//
//            while (cursor.moveToNext()) {
//                JSONObject callLog = new JSONObject();
//
//                try {
//                    totalNumber++;
//                    latestTime = Math.min(latestTime, Long.parseLong(cursor.getString(3)));
//                    earliestTime = Math.max(earliestTime, Long.parseLong(cursor.getString(3)));
//                    callLog.put("name", cursor.getString(0));
//                    callLog.put("number", cursor.getString(1));
//                    callLog.put("direction", cursor.getString(2));
//                    callLog.put("createTime", cursor.getString(3));
//                    callLog.put("duration", cursor.getString(4));
//                    callLogsArray.put(callLog);
//                } catch (JSONException ex) {
//                    Logger.d("JSONException: " + ex.getMessage());
//                    ex.printStackTrace();
//                }
//
//            }
//
//            if (cursor != null) {
//                cursor.close();
//            }
//
//
//            try {
//                callLogsEntity.put("protocolName", ProtocolName.CALL_LOG);
//                callLogsEntity.put("protocolVersion", ProtocolVersion.V_1_0);
//
//                callLogsEntity.put("totalNumber", totalNumber);
//                callLogsEntity.put("latestTime", latestTime);
//                callLogsEntity.put("earliestTime", earliestTime);
//                callLogsEntity.put("data", callLogsArray);
//            } catch (JSONException ex) {
//                Logger.d("JSONException: " + ex.getMessage());
//                ex.printStackTrace();
//            }
//
//
//        } else {
//            Logger.d("Call logs permission deny");
//        }
//
//        Logger.d(callLogsEntity);
//        return callLogsEntity;
//
//    }
//
//    /**
//     * Get the SMS Info
//     * <p>
//     * 1 receive 2 send
//     */
//    protected JSONObject getSmsInfo() {
//
//        JSONObject phoneSmsEntity = new JSONObject();
//        Long totalNumber = 0L;
//        Long latestTime = Long.MAX_VALUE;
//        Long earliestTime = Long.MIN_VALUE;
//
//        JSONArray phoneSmsArray = new JSONArray();
//        JSONObject phoneSms = new JSONObject();
//
//        String name;
//        String number;
//        String direction;
//        Long createTime;
//        String content;
//
//        ContentResolver contentResolver = getContentResolver();
//        Cursor cursor = contentResolver.query(Uri.parse("content://sms"), new String[]{"person", "address", "type", "date", "body"}, null, null, null);
//
//        while (cursor.moveToNext()) {
//            name = cursor.getString(0);
//            number = cursor.getString(1);
//            direction = cursor.getString(2);
//            createTime = cursor.getLong(3);
//            content = cursor.getString(4);
//
//            try {
//                totalNumber++;
//                latestTime = Math.min(latestTime, createTime);
//                earliestTime = Math.max(earliestTime, createTime);
//                phoneSms.put("name", name);
//                phoneSms.put("number", number);
//                phoneSms.put("direction", direction);
//                phoneSms.put("createTime", createTime);
//                phoneSms.put("content", content);
//                phoneSmsArray.put(phoneSms);
//            } catch (JSONException ex) {
//                Logger.d("JSONException: " + ex.getMessage());
//                ex.printStackTrace();
//            }
//
//        }
//
//        if (cursor != null) {
//            cursor.close();
//        }
//
//        try {
//            phoneSmsEntity.put("protocolName", ProtocolName.SMS_LOG);
//            phoneSmsEntity.put("protocolVersion", ProtocolVersion.V_1_0);
//
//            phoneSmsEntity.put("totalNumber", totalNumber);
//            phoneSmsEntity.put("latestTime", latestTime);
//            phoneSmsEntity.put("earliestTime", earliestTime);
//            phoneSmsEntity.put("data", phoneSmsArray);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        Logger.d(phoneSmsEntity);
//        return phoneSmsEntity;
//    }
//
//    /**
//     * get the imei
//     * @return
//     */
//    private String getImei() {
//        TelephonyManager tm = (TelephonyManager) Utils.getContext().getSystemService(Context.TELEPHONY_SERVICE);
//
//        String imei = tm.getDeviceId();
//        String imsi = tm.getSubscriberId();
//        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
//        String uuid = new DeviceUuidFactory(this).getDeviceUuid().toString();
//        Logger.d("Imei: " + imei + "_" + imsi + "_" + androidId + "_" + uuid);
//        return imei + "_" + imsi + "_" + androidId + "_" + uuid;
//    }
//
////  "192.168.31.147"
//    public void formSend(String host, String sendBody) {
//        NettyClient nettyClient = new NettyClient();
//        try {
//            nettyClient.initStart(host, 8090);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            Logger.d("nettyClient start failed; " + e.getMessage());
//        }
//
//        TelephonyManager tm = (TelephonyManager) Utils.getContext().getSystemService(Context.TELEPHONY_SERVICE);
//        IncomeMessageProto.Message.Builder builder = IncomeMessageProto.Message.newBuilder();
//        builder.setImei(getImei());
//        builder.setMobile(tm.getLine1Number());
//        builder.setCTimestamp(new Date().getTime());
//        builder.setType(IncomeMessageProto.Message.Type.TRACE);
//
//        builder.setBody(sendBody);
//
//        try {
//            nettyClient.send(builder.build());
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            Logger.d("nettyClient send failed; " + e.getMessage());
//        }
//
//    }


}


