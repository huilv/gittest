package com.daunkredit.program.sulu.bean;

/**
 * @作者:My
 * @创建日期: 2017/7/19 17:47
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class LoanRange {
    Double amountStep;
    Double interestRate;
    Double maxAmount;
    Double maxPeriod;
    Double minAmount;
    Double minPeriod;
    Double periodStep;
    String periodUnit;
    Double serviceFee;

    public Double getAmountStep() {
        return amountStep;
    }

    public void setAmountStep(Double amountStep) {
        this.amountStep = amountStep;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(Double interestRate) {
        this.interestRate = interestRate;
    }

    public Double getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(Double maxAmount) {
        this.maxAmount = maxAmount;
    }

    public Double getMaxPeriod() {
        return maxPeriod;
    }

    public void setMaxPeriod(Double maxPeriod) {
        this.maxPeriod = maxPeriod;
    }

    public Double getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(Double minAmount) {
        this.minAmount = minAmount;
    }

    public Double getMinPeriod() {
        return minPeriod;
    }

    public void setMinPeriod(Double minPeriod) {
        this.minPeriod = minPeriod;
    }

    public Double getPeriodStep() {
        return periodStep;
    }

    public void setPeriodStep(Double periodStep) {
        this.periodStep = periodStep;
    }

    public String getPeriodUnit() {
        return periodUnit;
    }

    public void setPeriodUnit(String periodUnit) {
        this.periodUnit = periodUnit;
    }

    public Double getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(Double serviceFee) {
        this.serviceFee = serviceFee;
    }
}
