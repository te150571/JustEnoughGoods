package com.jeg.te.justenoughgoods.list_item_data_class;

public class Slave {
    private String sId;
    private String name;
    private Double amount;
    private Double notificationAmount;
    private Integer amountNotificationEnable;
    private Integer exceptionFlag;
    private Integer exceptionNotificationFlag;
    private Long lastUpdate;
    private int isNew;

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

    public Integer getExceptionFlag() {
        return exceptionFlag;
    }

    public Integer getExceptionNotificationFlag() {
        return exceptionNotificationFlag;
    }

    public Long getLastUpdate() {
        return lastUpdate;
    }

    public int getIsNew() {
        return isNew;
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

    public void setExceptionFlag(Integer exceptionFlag) {
        this.exceptionFlag = exceptionFlag;
    }

    public void setExceptionNotificationFlag(Integer exceptionNotificationFlag) {
        this.exceptionNotificationFlag = exceptionNotificationFlag;
    }

    public void setLastUpdate(Long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void setIsNew(int isNew) {
        this.isNew = isNew;
    }
}
