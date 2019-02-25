package com.cc5.btt.entity;

public class StepBD {

    private int userId;                 //用户id
    private String sizeCode;            //尺码
    private String prodCd;              //产品码
    private int startInv;               //初始库存
    private int sumQty;                 //总销量
    private int first4WeeksSaleQty;     //开始4周的销量

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

    public String getProdCd() {
        return prodCd;
    }

    public void setProdCd(String prodCd) {
        this.prodCd = prodCd;
    }

    public int getStartInv() {
        return startInv;
    }

    public void setStartInv(int startInv) {
        this.startInv = startInv;
    }

    public int getSumQty() {
        return sumQty;
    }

    public void setSumQty(int sumQty) {
        this.sumQty = sumQty;
    }

    public int getFirst4WeeksSaleQty() {
        return first4WeeksSaleQty;
    }

    public void setFirst4WeeksSaleQty(int first4WeeksSaleQty) {
        this.first4WeeksSaleQty = first4WeeksSaleQty;
    }
}
