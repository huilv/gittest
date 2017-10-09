package com.daunkredit.program.sulu.bean;

import android.support.annotation.Nullable;

import com.daunkredit.program.sulu.common.InfoAdapterString;

/**
 * Created by Miaoke on 2017/3/1.
 */

public class PersonalInfoBean {


    /**
     * fullName : timbo
     * credentialNo : 987654321
     * gender: MALE
     * province : Shanghai
     * city : Shanghai
     * district : Pudong
     * area : Huamu
     * address : East Jin'an Road
     * lastEducation : SD
     * maritalStatus : SINGLE
     * childrenNumber : ZERO
     * residenceDuration : ONE_YEAR
     */

    private String fullName;
    private String credentialNo;
    private String gender;
    private RegionBean.RegionsBean province;
    private RegionBean.RegionsBean city;
    private RegionBean.RegionsBean district;
    private RegionBean.RegionsBean area;
    private String address;
    private String lastEducation;
    private String maritalStatus;
    private String childrenNumber;
    private String residenceDuration;

    public String getFamilyNameInLaw() {
        return familyNameInLaw;
    }

    public void setFamilyNameInLaw(String familyNameInLaw) {
        this.familyNameInLaw = familyNameInLaw;
    }

    private String familyNameInLaw;
    @Nullable
    private String facebookId;

    @Nullable
    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(@Nullable String facebookId) {
        this.facebookId = facebookId;
    }

    public PersonalInfoBean() {
    }

    public PersonalInfoBean(PersonalInfoServerBean personalInfoServerBean){

        province = new RegionBean.RegionsBean();
        province.setName(personalInfoServerBean.getProvince());
        city = new RegionBean.RegionsBean();
        city.setName(personalInfoServerBean.getCity());
        district = new RegionBean.RegionsBean();
        district.setName(personalInfoServerBean.getDistrict());
        area = new RegionBean.RegionsBean();
        area.setName(personalInfoServerBean.getArea());
        this.setFamilyNameInLaw(personalInfoServerBean.getFamilyNameInLaw());
        this.setFullName(personalInfoServerBean.getFullName());
        this.setCredentialNo(personalInfoServerBean.getCredentialNo());
        this.setGender(personalInfoServerBean.getGender());
        this.setFacebookId(personalInfoServerBean.getFacebookId());
        this.setAddress(personalInfoServerBean.getAddress());
        this.setLastEducation(personalInfoServerBean.getLastEducation());
        this.setMaritalStatus(personalInfoServerBean.getMaritalStatus());
        this.setChildrenNumber(personalInfoServerBean.getChildrenNumber());
        this.setResidenceDuration(personalInfoServerBean.getResidenceDuration());

    }
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCredentialNo() {
        return credentialNo;
    }

    public void setCredentialNo(String credentialNo) {
        this.credentialNo = credentialNo;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public RegionBean.RegionsBean getProvince() {
        return province;
    }

    public void setProvince(RegionBean.RegionsBean province) {
        this.province = province;
    }
    public void setProvince(InfoAdapterString.InfoItem province) {

        this.province = new RegionBean.RegionsBean(province.getId(),province.getLevel(),province.getInfoStr());

    }

    public RegionBean.RegionsBean getCity() {
        return city;
    }


    public void setCity(RegionBean.RegionsBean city) {
        this.city = city;
    }
    public void setCity(InfoAdapterString.InfoItem city) {
        this.city =  new RegionBean.RegionsBean(city.getId(),city.getLevel(),city.getInfoStr());

    }

    public RegionBean.RegionsBean getDistrict() {
        return district;
    }

    public void setDistrict(RegionBean.RegionsBean district) {
        this.district = district;
    }

    public void setDistrict(InfoAdapterString.InfoItem district) {
        this.district =  new RegionBean.RegionsBean(district.getId(),district.getLevel(),district.getInfoStr());

    }

    public RegionBean.RegionsBean getArea() {
        return area;
    }

    public void setArea(RegionBean.RegionsBean area) {
        this.area = area;
    }

    public void setArea(InfoAdapterString.InfoItem area) {
        this.area =  new RegionBean.RegionsBean(area.getId(),area.getLevel(),area.getInfoStr());
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLastEducation() {
        return lastEducation;
    }

    public void setLastEducation(String lastEducation) {
        this.lastEducation = lastEducation;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getChildrenNumber() {
        return childrenNumber;
    }

    public void setChildrenNumber(String childrenNumber) {
        this.childrenNumber = childrenNumber;
    }

    public String getResidenceDuration() {
        return residenceDuration;
    }

    public void setResidenceDuration(String residenceDuration) {
        this.residenceDuration = residenceDuration;
    }
}
