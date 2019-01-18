package com.cc5.btt.entity;

public class StepAB {

    private int posId;          //门店id
    private String prodCd;      //产品id
    private String size;        //尺码
    private int units;          //单位
    private int sales;          //销售额
    private int invQty;         //库存量
    private String date;          //日期
    private String posProd;     //门店id和产品id组合

    public int getPosId() {
        return posId;
    }

    public void setPosId(int posId) {
        this.posId = posId;
    }

    public String getProdCd() {
        return prodCd;
    }

    public void setProdCd(String prodCd) {
        this.prodCd = prodCd;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public int getSales() {
        return sales;
    }

    public void setSales(int sales) {
        this.sales = sales;
    }

    public int getInvQty() {
        return invQty;
    }

    public void setInvQty(int invQty) {
        this.invQty = invQty;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPosProd() {
        return posProd;
    }

    public void setPosProd(String posProd) {
        this.posProd = posProd;
    }
}
