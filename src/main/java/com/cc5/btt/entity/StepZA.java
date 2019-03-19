package com.cc5.btt.entity;

public class StepZA {

    private int id;
    private int userId;
    private int posId;
    private String field1;                  //Scenario_Name
    private String prodCd;
    private String size;
    private String scenarioName;            //Scenario name
    private Integer freqFct;
    private String demandFct;
    private Integer targetDaysFct;
    private Integer totalDemandRsp;
    private Integer initialInventoryRsp;
    private Integer dayRsp;
    private Integer inventoryRsp;
    private Integer dcCheckRsp;
    private Integer instockRecordRsp;
    private Integer actualSalesRsp;
    private Integer last7daysSalesRsp;
    private Integer accumulatedSalesRsp;
    private Integer replenQtyRsp;
    private String orderingCostRsp;
    private String transportingCostRsp;
    private String holdingCostRsp;
    private String prodEngnDesc;
    private String coreSizeVal;
    private Integer dcStock;
    private Integer accReplen;
    private Integer dcCheckNew;
    private Integer acc4weeksSales;
    private Integer wohInv;

    //中间字段
    private String field;
    private int record;
    private String prodCd2;
    private String gblCatSumLongDesc;
    private String gender;
    private String itemCategory;
    private String region;
    private String coreSize;
    private int coreSizeFlag;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
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

    public String getScenarioName() {
        return scenarioName;
    }

    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }

    public Integer getFreqFct() {
        return freqFct;
    }

    public void setFreqFct(Integer freqFct) {
        this.freqFct = freqFct;
    }

    public String getDemandFct() {
        return demandFct;
    }

    public void setDemandFct(String demandFct) {
        this.demandFct = demandFct;
    }

    public Integer getTargetDaysFct() {
        return targetDaysFct;
    }

    public void setTargetDaysFct(Integer targetDaysFct) {
        this.targetDaysFct = targetDaysFct;
    }

    public Integer getTotalDemandRsp() {
        return totalDemandRsp;
    }

    public void setTotalDemandRsp(Integer totalDemandRsp) {
        this.totalDemandRsp = totalDemandRsp;
    }

    public Integer getInitialInventoryRsp() {
        return initialInventoryRsp;
    }

    public void setInitialInventoryRsp(Integer initialInventoryRsp) {
        this.initialInventoryRsp = initialInventoryRsp;
    }

    public Integer getDayRsp() {
        return dayRsp;
    }

    public void setDayRsp(Integer dayRsp) {
        this.dayRsp = dayRsp;
    }

    public Integer getInventoryRsp() {
        return inventoryRsp;
    }

    public void setInventoryRsp(Integer inventoryRsp) {
        this.inventoryRsp = inventoryRsp;
    }

    public Integer getDcCheckRsp() {
        return dcCheckRsp;
    }

    public void setDcCheckRsp(Integer dcCheckRsp) {
        this.dcCheckRsp = dcCheckRsp;
    }

    public Integer getInstockRecordRsp() {
        return instockRecordRsp;
    }

    public void setInstockRecordRsp(Integer instockRecordRsp) {
        this.instockRecordRsp = instockRecordRsp;
    }

    public Integer getActualSalesRsp() {
        return actualSalesRsp;
    }

    public void setActualSalesRsp(Integer actualSalesRsp) {
        this.actualSalesRsp = actualSalesRsp;
    }

    public Integer getLast7daysSalesRsp() {
        return last7daysSalesRsp;
    }

    public void setLast7daysSalesRsp(Integer last7daysSalesRsp) {
        this.last7daysSalesRsp = last7daysSalesRsp;
    }

    public Integer getAccumulatedSalesRsp() {
        return accumulatedSalesRsp;
    }

    public void setAccumulatedSalesRsp(Integer accumulatedSalesRsp) {
        this.accumulatedSalesRsp = accumulatedSalesRsp;
    }

    public Integer getReplenQtyRsp() {
        return replenQtyRsp;
    }

    public void setReplenQtyRsp(Integer replenQtyRsp) {
        this.replenQtyRsp = replenQtyRsp;
    }

    public String getOrderingCostRsp() {
        return orderingCostRsp;
    }

    public void setOrderingCostRsp(String orderingCostRsp) {
        this.orderingCostRsp = orderingCostRsp;
    }

    public String getTransportingCostRsp() {
        return transportingCostRsp;
    }

    public void setTransportingCostRsp(String transportingCostRsp) {
        this.transportingCostRsp = transportingCostRsp;
    }

    public String getHoldingCostRsp() {
        return holdingCostRsp;
    }

    public void setHoldingCostRsp(String holdingCostRsp) {
        this.holdingCostRsp = holdingCostRsp;
    }

    public String getProdEngnDesc() {
        return prodEngnDesc;
    }

    public void setProdEngnDesc(String prodEngnDesc) {
        this.prodEngnDesc = prodEngnDesc;
    }

    public String getCoreSizeVal() {
        return coreSizeVal;
    }

    public void setCoreSizeVal(String coreSizeVal) {
        this.coreSizeVal = coreSizeVal;
    }

    public Integer getDcStock() {
        return dcStock;
    }

    public void setDcStock(Integer dcStock) {
        this.dcStock = dcStock;
    }

    public Integer getAccReplen() {
        return accReplen;
    }

    public void setAccReplen(Integer accReplen) {
        this.accReplen = accReplen;
    }

    public Integer getDcCheckNew() {
        return dcCheckNew;
    }

    public void setDcCheckNew(Integer dcCheckNew) {
        this.dcCheckNew = dcCheckNew;
    }

    public Integer getAcc4weeksSales() {
        return acc4weeksSales;
    }

    public void setAcc4weeksSales(Integer acc4weeksSales) {
        this.acc4weeksSales = acc4weeksSales;
    }

    public Integer getWohInv() {
        return wohInv;
    }

    public void setWohInv(Integer wohInv) {
        this.wohInv = wohInv;
    }


    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public int getRecord() {
        return record;
    }

    public void setRecord(int record) {
        this.record = record;
    }

    public String getProdCd2() {
        return prodCd2;
    }

    public void setProdCd2(String prodCd2) {
        this.prodCd2 = prodCd2;
    }

    public String getGblCatSumLongDesc() {
        return gblCatSumLongDesc;
    }

    public void setGblCatSumLongDesc(String gblCatSumLongDesc) {
        this.gblCatSumLongDesc = gblCatSumLongDesc;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCoreSize() {
        return coreSize;
    }

    public void setCoreSize(String coreSize) {
        this.coreSize = coreSize;
    }

    public int getCoreSizeFlag() {
        return coreSizeFlag;
    }

    public void setCoreSizeFlag(int coreSizeFlag) {
        this.coreSizeFlag = coreSizeFlag;
    }
}
