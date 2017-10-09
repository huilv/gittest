package com.daunkredit.program.sulu.bean;

/**
 * @作者:My
 * @创建日期: 2017/7/13 15:43
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class TotalAmount {
    private double amount = 600000;
    private int  day    = 7;
    private double  rate   = 1;
    private double totalRepayment;


    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public double getTotalRepayment() {
        return totalRepayment = amount + amount * day * rate / 100;
    }

}

