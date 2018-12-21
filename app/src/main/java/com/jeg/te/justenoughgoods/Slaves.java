package com.jeg.te.justenoughgoods;

public class Slaves {
    private String cId;
    private String name;
    private Double amount;
    private Double notificationAmount;
    private Integer amountNotificationFlag;
    private Integer failFlag;
    private Integer failNotificationFlag;
    private String recentDate;

    public String getCId() {
        return cId;
    }

    public String getName() {
        return name;
    }

    public Double getAmount() {
        return amount;
    }

    public  Double getNotificationAmount(){
        return notificationAmount;
    }

    public Integer getAmountNotificationFlag() {
        return amountNotificationFlag;
    }

    public Integer getFailFlag() {
        return failFlag;
    }

    public Integer getFailNotificationFlag() {
        return failNotificationFlag;
    }

    public String getRecentDate() {
        return recentDate;
    }

    public void setCId(String cId) {
        this.cId = cId;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNotificationAmount(Double notificationAmount) {
        this.notificationAmount = notificationAmount;
    }

    public void setAmountNotificationFlag(Integer amountNotificationFlag) {
        this.amountNotificationFlag = amountNotificationFlag;
    }

    public void setFailFlag(Integer failFlag) {
        this.failFlag = failFlag;
    }

    public void setFailNotificationFlag(Integer failNotificationFlag) {
        this.failNotificationFlag = failNotificationFlag;
    }

    public void setRecentDate(String recentDate) {
        this.recentDate = recentDate;
    }
}
