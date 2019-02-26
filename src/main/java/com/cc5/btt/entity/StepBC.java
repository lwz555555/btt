package com.cc5.btt.entity;

public class StepBC {

    private int userId;      //用户id
    private String sizeCode;      //产品id+尺码
    private Integer startInv;     //初始库存
    private Integer sumSalQty;    //销量总和
    private Integer sumFirst4wksSalQty;        //前4周的销量总和
    private int posId;          //门店id

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSizeCode() {
        return sizeCode;
    }

    public void setSizeCode(String sizeCode) {
        this.sizeCode = sizeCode;
    }

    public Integer getStartInv() {
        return startInv;
    }

    public void setStartInv(Integer startInv) {
        this.startInv = startInv;
    }

    public Integer getSumSalQty() {
        return sumSalQty;
    }

    public void setSumSalQty(Integer sumSalQty) {
        this.sumSalQty = sumSalQty;
    }

    public Integer getSumFirst4wksSalQty() {
        return sumFirst4wksSalQty;
    }

    public void setSumFirst4wksSalQty(Integer sumFirst4wksSalQty) {
        this.sumFirst4wksSalQty = sumFirst4wksSalQty;
    }

    public int getPosId() {
        return posId;
    }

    public void setPosId(int posId) {
        this.posId = posId;
    }
}
