package com.cc5.btt.entity;

public class StepAC {


    private int id;             //索引id
    private int userId;         //用户id
    private int posId;          //门店id
    private String prodCd;      //产品id
    private String size;        //尺码
    private Integer units;          //单位
    private Integer sales;          //销售额
    private Integer invQty;         //库存量
    private String date;          //日期
    private String fileName;      //文件名
    private String skuCode;      //产品id+尺码
    private String dayInWeek;    //当前日期是周几
    private Integer recId;       //recId
    private String minDate;      //最小日期 （中间字段）
    private String maxDate;      //最大日期 （中间字段）


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Integer getRecId() {
        return recId;
    }
    public void setRecId(Integer recId) {
        this.recId = recId;
    }
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

    public Integer getUnits() {
        return units;
    }

    public void setUnits(Integer units) {
        this.units = units;
    }

    public Integer getSales() {
        return sales;
    }

    public void setSales(Integer sales) {
        this.sales = sales;
    }

    public Integer getInvQty() {
        return invQty;
    }

    public void setInvQty(Integer invQty) {
        this.invQty = invQty;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getDayInWeek() {
        return dayInWeek;
    }

    public void setDayInWeek(String dayInWeek) {
        this.dayInWeek = dayInWeek;
    }

    public String getMinDate() {
        return minDate;
    }

    public void setMinDate(String minDate) {
        this.minDate = minDate;
    }

    public String getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(String maxDate) {
        this.maxDate = maxDate;
    }
}
