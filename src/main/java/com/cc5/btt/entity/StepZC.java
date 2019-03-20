package com.cc5.btt.entity;

public class StepZC {

    private int posId;
    private String name;            //MATL_NBR
    private String styleCd;
    private String prodEngnDesc;
    private String itemCategory;
    private int regMsrp;

    public int getPosId() {
        return posId;
    }

    public void setPosId(int posId) {
        this.posId = posId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStyleCd() {
        return styleCd;
    }

    public void setStyleCd(String styleCd) {
        this.styleCd = styleCd;
    }

    public String getProdEngnDesc() {
        return prodEngnDesc;
    }

    public void setProdEngnDesc(String prodEngnDesc) {
        this.prodEngnDesc = prodEngnDesc;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public int getRegMsrp() {
        return regMsrp;
    }

    public void setRegMsrp(int regMsrp) {
        this.regMsrp = regMsrp;
    }
}
