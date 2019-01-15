package com.jeg.te.justenoughgoods;

public class Slaves {
    private String sId;
    private String name;
    private Double amount;
    private Double notificationAmount;
    private Integer amountNotificationEnable;
    private Integer failFlag;
    private Integer failNotificationFlag;
    private Long lastUpdate;

    public String getSId() {
        return sId;
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

    public Integer getAmountNotificationEnable() {
        return amountNotificationEnable;
    }

    public Integer getFailFlag() {
        return failFlag;
    }

    public Integer getFailNotificationFlag() {
        return failNotificationFlag;
    }

    public Long getLastUpdate() {
        return lastUpdate;
    }

    public void setSId(String sId) {
        this.sId = sId;
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

    public void setAmountNotificationEnable(Integer amountNotificationEnable) {
        this.amountNotificationEnable = amountNotificationEnable;
    }

    public void setFailFlag(Integer failFlag) {
        this.failFlag = failFlag;
    }

    public void setFailNotificationFlag(Integer failNotificationFlag) {
        this.failNotificationFlag = failNotificationFlag;
    }

    public void setLastUpdate(Long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
