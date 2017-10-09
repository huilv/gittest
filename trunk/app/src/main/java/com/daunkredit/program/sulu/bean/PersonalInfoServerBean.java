package com.daunkredit.program.sulu.bean;

import android.support.annotation.Nullable;

/**
 * Created by Miaoke on 31/03/2017.
 */

public class PersonalInfoServerBean {

    /**
     * address : string
     * area : string
     * childrenNumber : ZERO
     * city : string
     * credentialNo : string
     * credentialType : KTP
     * district : string
     * fullName : string
     * gender : MALE
     * lastEducation : DIPLOMA_I
     * maritalStatus : MARRIED
     * province : string
     * residenceDuration : THREE_MONTH
     */

    private String address;
    private String area;
    private String childrenNumber;
    private String city;
    private String credentialNo;
    private String credentialType;
    private String district;
    private String fullName;
    private String gender;
    private String lastEducation;
    private String maritalStatus;
    private String province;
    private String residenceDuration;
    private String familyNameInLaw;


    @Nullable
    private String facebookId;

    public void setFacebookId(@Nullable String facebookId) {
        this.facebookId = facebookId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getChildrenNumber() {
        return childrenNumber;
    }

    public void setChildrenNumber(String childrenNumber) {
        this.childrenNumber = childrenNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCredentialNo() {
        return credentialNo;
    }

    public void setCredentialNo(String credentialNo) {
        this.credentialNo = credentialNo;
    }

    public String getCredentialType() {
        return credentialType;
    }

    public void setCredentialType(String credentialType) {
        this.credentialType = credentialType;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getResidenceDuration() {
        return residenceDuration;
    }

    public void setResidenceDuration(String residenceDuration) {
        this.residenceDuration = residenceDuration;
    }

    public String getFamilyNameInLaw() {
        return familyNameInLaw;
    }

    public void setFamilyNameInLaw(String familyNameInLaw) {
        this.familyNameInLaw = familyNameInLaw;
    }


    public String getFacebookId() {
        return facebookId;
    }
}
