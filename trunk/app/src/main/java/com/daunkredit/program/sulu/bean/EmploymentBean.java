package com.daunkredit.program.sulu.bean;

import com.daunkredit.program.sulu.common.InfoAdapterString;

/**
 * Created by Miaoke on 2017/3/1.
 */

public class EmploymentBean {


    /**
     * companyName : string
     * companyProvince : string
     * companyCity : string
     * companyDistrict : string
     * companyArea : string
     * companyAddress : string
     * companyPhone : string
     * profession : PROGRAMMER
     * salary : BELOW_2B
     * workEmail : string
     */

    private String companyName;
    private RegionBean.RegionsBean companyProvince ;
    private RegionBean.RegionsBean companyCity;
    private RegionBean.RegionsBean companyDistrict;
    private RegionBean.RegionsBean companyArea;
    private String companyAddress;
    private String companyPhone;
    private String salary;
    private String mJobType;

    public EmploymentBean() {
    }

    public EmploymentBean(EmploymentServerBean employmentServerBean) {

        companyProvince = new RegionBean.RegionsBean();
        companyCity = new RegionBean.RegionsBean();
        companyDistrict = new RegionBean.RegionsBean();
        companyArea = new RegionBean.RegionsBean();

        this.setCompanyName(employmentServerBean.getCompanyName());
        companyProvince.setName(employmentServerBean.getCompanyProvince());
        companyCity.setName(employmentServerBean.getCompanyCity());
        companyDistrict.setName(employmentServerBean.getCompanyDistrict());
        companyArea.setName(employmentServerBean.getCompanyArea());
        this.setCompanyAddress(employmentServerBean.getCompanyAddress());
        this.setCompanyPhone(employmentServerBean.getCompanyPhone());
        this.setJobType(employmentServerBean.getProfession());
        this.setSalary(employmentServerBean.getSalary());
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public RegionBean.RegionsBean getCompanyProvince() {
        return companyProvince;
    }

    public void setCompanyProvince(RegionBean.RegionsBean companyProvince) {
        this.companyProvince = companyProvince;
    }

    public void setCompanyProvince(InfoAdapterString.InfoItem companyProvince) {
        this.companyProvince = new RegionBean.RegionsBean(companyProvince.getId(),companyProvince.getLevel(),companyProvince.getInfoStr());
    }


    public RegionBean.RegionsBean getCompanyCity() {
        return companyCity;
    }

    public void setCompanyCity(RegionBean.RegionsBean companyCity) {
        this.companyCity = companyCity;
    }

    public void setCompanyCity(InfoAdapterString.InfoItem companyCity) {
        this.companyCity = new RegionBean.RegionsBean(companyCity.getId(),companyCity.getLevel(),companyCity.getInfoStr());
    }

    public RegionBean.RegionsBean getCompanyDistrict() {
        return companyDistrict;
    }

    public void setCompanyDistrict(RegionBean.RegionsBean companyDistrict) {
        this.companyDistrict = companyDistrict;
    }

    public void setCompanyDistrict(InfoAdapterString.InfoItem companyDistrict) {
        this.companyDistrict = new RegionBean.RegionsBean(companyDistrict.getId(),companyDistrict.getLevel(),companyDistrict.getInfoStr());
    }

    public RegionBean.RegionsBean getCompanyArea() {
        return companyArea;
    }

    public void setCompanyArea(RegionBean.RegionsBean companyArea) {
        this.companyArea = companyArea;
    }

    public void setCompanyArea(InfoAdapterString.InfoItem companyArea) {
        this.companyArea = new RegionBean.RegionsBean(companyArea.getId(),companyArea.getLevel(),companyArea.getInfoStr());
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getCompanyPhone() {
        return companyPhone;
    }

    public void setCompanyPhone(String companyPhone) {
        this.companyPhone = companyPhone;
    }

//    public String getProfession() {
//        return profession;
//    }
//
//    public void setProfession(String profession) {
//        this.profession = profession;
//    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }


    public void setJobType(String jobType) {
        mJobType = jobType;
    }

    public String getJobType() {
        return mJobType;
    }
}
