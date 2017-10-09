package com.daunkredit.program.sulu.harvester;

import com.daunkredit.program.sulu.app.App;

import org.afinal.simplecache.ACache;
import org.json.JSONObject;

/**
 * Created by Miaoke on 14/04/2017.
 */

public class HarvestInfoManager {

    ACache info;

    private  String CONTACT_STATUS_KEY = "contact_status_key";
    private  String CONTACT_JSONOBJECT_KEY = "contact_json_object_key";
    private  String SMS_STATUS_KEY = "sms_status_key";
    private  String SMS_JSONOBJECT_KEY = "sms_json_object_key";
    private  String CALL_LOGS_STATUS_KEY = "call_log_status_key";
    private  String CALL_LOGS_JSONOBJECT_KEY = "call_log_json_object_key";
    private  String LOCATION_STATUS_KEY = "location_status_key";
    private  String LOCATION_JSONOBJECT_KEY = "location_json_object_key";
    private  String PERMISSION_STATUS_KEY = "permission_status_key";
    private  String PERMISSION_JSONOBJECT_KEY = "permission_json_object_key";
    private  String INSTALL_APP_STATUS_KEY = "install_app_status_key";
    private  String INSTALL_APP_JSONOBJECT_KEY = "install_app_jsonobject_key";
    private  String MACHINE_TYPE_STATUS_KEY = "machine_type_status_key";
    private  String MACHINE_TYPE_JSONOBJECT_KEY = "machine_type_jsonobject_key";


    private  String IMEI_KEY = "imei_key";

    public  String getImeiKey() {
        return info.getAsString(IMEI_KEY);
    }

    public void setImeiKey(String imeiKey) {
        info.put(IMEI_KEY,imeiKey);
    }

    public String getContactStatus() {
        return info.getAsString(CONTACT_STATUS_KEY);
    }

    public void setContactStatus(String contactStatus) {
        info.put(CONTACT_STATUS_KEY, contactStatus);
    }

    public  JSONObject getContactJsonobject() {
        return info.getAsJSONObject(CONTACT_JSONOBJECT_KEY);
    }

    public void setContactJsonobject(JSONObject contactJsonobject) {
        info.put(CONTACT_JSONOBJECT_KEY,contactJsonobject);
    }

    public String getSmsStatus() {
        return info.getAsString(SMS_STATUS_KEY);
    }

    public void setSmsStatus(String smsStatus) {
        info.put(SMS_STATUS_KEY,smsStatus);
    }

    public JSONObject getSmsJsonobject() {
        return info.getAsJSONObject(SMS_JSONOBJECT_KEY);
    }

    public void setSmsJsonobject(JSONObject smsJsonobject) {
        info.put(SMS_JSONOBJECT_KEY, smsJsonobject);
    }

    public  String getCallLogsStatus() {
        return info.getAsString(CALL_LOGS_STATUS_KEY);
    }

    public void setCallLogsStatus(String callLogsStatus) {
        info.put(CALL_LOGS_STATUS_KEY,callLogsStatus);
    }

    public JSONObject getCallLogsJsonobject() {
        return info.getAsJSONObject(CALL_LOGS_JSONOBJECT_KEY);
    }

    public  void setCallLogsJsonobject(JSONObject callLogsJsonobject) {
        info.put(CALL_LOGS_JSONOBJECT_KEY,callLogsJsonobject);
    }

    public void setLocationStatus(String locationStatus){
        info.put(LOCATION_STATUS_KEY,locationStatus);
    }

    public String getLocationStatus(){
        return info.getAsString(LOCATION_STATUS_KEY);
    }

    public void setLocationJsonobject(JSONObject locationJsonobject){
        info.put(LOCATION_JSONOBJECT_KEY, locationJsonobject );
    }

    public JSONObject getLocationJsonobject(){
        return info.getAsJSONObject(LOCATION_JSONOBJECT_KEY);
    }


    public String getPermissionStatus() {
        return info.getAsString(PERMISSION_STATUS_KEY);
    }

    public void setPermissionStatus(String PermissionStatus) {
        info.put(PERMISSION_STATUS_KEY,PermissionStatus);
    }

    public JSONObject getPermissionJsonobject() {
        return info.getAsJSONObject(PERMISSION_JSONOBJECT_KEY);
    }

    public void setPermissionJsonobject(JSONObject PermissionJsonobject) {
        info.put(PERMISSION_JSONOBJECT_KEY,PermissionJsonobject);
    }

    public String getInstallAppStatus() {
        return info.getAsString(INSTALL_APP_STATUS_KEY);
    }

    public void setInstallAppStatus(String installAppStatus) {
        info.put(INSTALL_APP_STATUS_KEY,installAppStatus);
    }

    public JSONObject getInstallAppJsonobject() {
        return info.getAsJSONObject(INSTALL_APP_JSONOBJECT_KEY);
    }

    public void setInstallAppJsonobject(JSONObject installAppJsonobject) {
        info.put(INSTALL_APP_JSONOBJECT_KEY,installAppJsonobject);
    }

    public String getMachineTypeStatus() {
        return info.getAsString(MACHINE_TYPE_STATUS_KEY);
    }

    public void setMachineTypeStatus(String machineTypeStatus) {
        info.put(MACHINE_TYPE_STATUS_KEY,machineTypeStatus);
    }

    public JSONObject getMachineTypeJsonobject() {
        return info.getAsJSONObject(MACHINE_TYPE_JSONOBJECT_KEY);
    }

    public void setMachineTypeJsonobject(JSONObject machineTypeJsonobject) {
        info.put(MACHINE_TYPE_JSONOBJECT_KEY,machineTypeJsonobject);
    }

    private HarvestInfoManager(){
        info = ACache.get(App.instance);
    }

    private static class Holder{
        private static final HarvestInfoManager INSTANCE = new HarvestInfoManager();
    }



    public static HarvestInfoManager getInstance(){
        return Holder.INSTANCE;
    }

    public void contactClear(){
        info.remove(CONTACT_STATUS_KEY);
        info.remove(CONTACT_JSONOBJECT_KEY);
    }
    public void smsClear(){
        info.remove(SMS_STATUS_KEY);
        info.remove(CONTACT_JSONOBJECT_KEY);
    }
    public void callLogsClear(){
        info.remove(CALL_LOGS_STATUS_KEY);
        info.remove(CALL_LOGS_JSONOBJECT_KEY);
    }

    public void locationsClear(){
        info.remove(LOCATION_STATUS_KEY);
        info.remove(LOCATION_JSONOBJECT_KEY);
    }

    public void permissionClear(){
        info.remove(PERMISSION_STATUS_KEY);
        info.remove(PERMISSION_JSONOBJECT_KEY);
    }

    public  void installAppInfoClear(){
        info.remove(INSTALL_APP_STATUS_KEY);
        info.remove(INSTALL_APP_JSONOBJECT_KEY);
    }

    public void machineTypeCLear(){
        info.remove(MACHINE_TYPE_JSONOBJECT_KEY);
        info.remove(MACHINE_TYPE_STATUS_KEY);
    }

    public boolean hasCallLogs(){
        return getCallLogsJsonobject() !=null && getCallLogsStatus() != null;
    }


    public  boolean hasSms(){
        return getSmsJsonobject() != null && getSmsStatus() != null;
    }

    public boolean hasContact(){
        return getContactJsonobject() != null && getContactStatus() != null;
    }

    public boolean hasLocation(){
        return getLocationJsonobject() != null && getLocationStatus() != null;
    }

    public boolean hasPermissionInfo(){
        return getPermissionJsonobject() != null &&  getPermissionStatus()  != null;
    }

    public boolean hasInstallAppInfo(){
        return getInstallAppJsonobject() != null && getInstallAppStatus() != null;
    }

    public boolean hasMachineType(){
        return getMachineTypeJsonobject() != null && getMachineTypeStatus() != null;
    }

    public boolean hasImei(){
        return getImeiKey() != null;
    }

    

}
