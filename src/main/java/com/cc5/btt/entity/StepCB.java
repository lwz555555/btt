package com.cc5.btt.entity;

public class StepCB {

    private int userId;         //用户ID
    private int posId;          //门店ID

    private int name;           //CB步骤中间字段

    private String tabName;     //结果表sheet名

    private int record;         //CB步骤结果字段
    private String field;       //CB步骤结果字段
    private String prodCd;      //CB步骤结果字段

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPosId() {
        return posId;
    }

    public void setPosId(int posId) {
        this.posId = posId;
    }

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public int getRecord() {
        return record;
    }

    public void setRecord(int record) {
        this.record = record;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getProdCd() {
        return prodCd;
    }

    public void setProdCd(String prodCd) {
        this.prodCd = prodCd;
    }
}
