package com.daunkredit.program.sulu.bean;

/**
 * Created by Miaoke on 2017/3/13.
 */

public class DepositResponseBean {


    /**
     * currency : IDR
     * depositChannel : BLUEPAY
     * depositId : 0
     * depositMethod : ALFAMART
     * operatorId : string
     * paymentCode : string
     * price : 0
     * productId : string
     */

    private String currency;
    private String depositChannel;
    private String depositId;
    private String depositMethod;
    private String operatorId;
    private String paymentCode;
    private int price;
    private String productId;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDepositChannel() {
        return depositChannel;
    }

    public void setDepositChannel(String depositChannel) {
        this.depositChannel = depositChannel;
    }

    public String getDepositId() {
        return depositId;
    }

    public void setDepositId(String depositId) {
        this.depositId = depositId;
    }

    public String getDepositMethod() {
        return depositMethod;
    }

    public void setDepositMethod(String depositMethod) {
        this.depositMethod = depositMethod;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
