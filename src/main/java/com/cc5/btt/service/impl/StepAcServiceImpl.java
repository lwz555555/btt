package com.cc5.btt.service.impl;

import com.cc5.btt.dao.StepAbDao;
import com.cc5.btt.dao.StepAcDao;
import com.cc5.btt.entity.StepAB;
import com.cc5.btt.entity.StepAC;
import com.cc5.btt.entity.StepACDate;
import com.cc5.btt.service.StepAcService;
import com.cc5.btt.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class StepAcServiceImpl implements StepAcService {

    @Autowired
    StepAbDao stepAbDao;
    @Autowired
    private StepAcDao stepAcDao;


    @Override
    public int runAcStep(int userId) {
        Map<String, List<StepAB>> abMap = stepAbDao.getMapList(userId);
        List<StepAC> dataResult = new ArrayList<>();
        for (Map.Entry<String, List<StepAB>> entry : abMap.entrySet()) {
            List<StepACDate> stepACDateList = handleAcDate (entry.getValue());
            if (stepACDateList == null) {
                return 0;
            }
            List<StepAC> newACDateList = retainAll(stepACDateList, entry.getValue());
            if (newACDateList == null) {
               return 0;
            }
            dataResult.addAll(newACDateList);
        }
        int saveRet = stepAcDao.insert(userId, dataResult);
        if (saveRet == 0) {
            return 0;
        }
        return 1;
    }


    private List<StepAC> retainAll (List<StepACDate> stepACDateList, List<StepAB> list) {
        List<StepAC> result = null;
        Map<String, StepAB> acMap = new HashMap<>();
        for (StepAB stepAB : list) {
            String key = stepAB.getSkuCode() + "_" + stepAB.getDate();
            if (acMap.containsKey(key)) {
                return null;
            }
            acMap.put(key, stepAB);
        }
        if (stepACDateList != null && list != null) {
            result = new ArrayList<>();
            for (StepACDate stepACDate : stepACDateList) {
                String key = stepACDate.getSkuCode() + "_" + stepACDate.getDate();
                if (acMap.containsKey(key)) {
                    StepAB stepAB = acMap.get(key);
                    StepAC stepAC = new StepAC();
                    stepAC.setUnits(stepAB.getUnits());
                    stepAC.setInvQty(stepAB.getInvQty());
                    stepAC.setSales(stepAB.getSales());
                    stepAC.setUserId(stepAB.getUserId());
                    stepAC.setSize(stepAB.getSize());
                    stepAC.setSkuCode(stepAB.getSkuCode());
                    stepAC.setProdCd(stepAB.getProdCd());
                    stepAC.setPosId(stepAB.getPosId());
                    stepAC.setDate(stepAB.getDate());
                    stepAC.setFileName(stepAB.getPosId() + "_" + stepAB.getProdCd());
                    result.add(stepAC);
                } else {
                    StepAC stepAC = new StepAC();
                    stepAC.setUnits(0);
                    stepAC.setInvQty(null);
                    stepAC.setSales(null);
                    stepAC.setUserId(list.get(0).getUserId());
                    stepAC.setSize(stepACDate.getSkuCode().substring(14));
                    stepAC.setSkuCode(stepACDate.getSkuCode());
                    stepAC.setProdCd(stepACDate.getSkuCode().substring(0, 10));
                    stepAC.setPosId(list.get(0).getPosId());
                    stepAC.setDate(stepACDate.getDate());
                    stepAC.setFileName(list.get(0).getPosId() + "_" + stepACDate.getSkuCode().substring(0, 10));
                    result.add(stepAC);
                }
            }
        }
        return result;
    }


    /**
     * 处理AC步骤中的日期
     * @param acList
     * @return
     */
    private List<StepACDate> handleAcDate (List<StepAB> acList) {
        List<StepACDate> newList = null;
        Map<String, Set<Long>> groupMap = new HashMap<>();
        if (acList != null) {
            for (StepAB ab : acList) {
                if (groupMap.containsKey(ab.getSkuCode())) {
                    Set<Long> set = groupMap.get(ab.getSkuCode());
                    Long longTime = DateUtil.getLongTime(ab.getDate());
                    if (longTime == null) {
                        return null;
                    }
                    set.add(longTime);
                } else {
                    Set<Long> set = new TreeSet<>();
                    Long longTime = DateUtil.getLongTime(ab.getDate());
                    if (longTime == null) {
                        return null;
                    }
                    set.add(longTime);
                    groupMap.put(ab.getSkuCode(), set);
                }
            }
            List<StepACDate> begainList = new ArrayList<>();
            if (!groupMap.isEmpty()) {
                for (Map.Entry<String, Set<Long>> entry : groupMap.entrySet()) {
                    Set<Long> set = entry.getValue();
                    List<Long> list = new ArrayList<>(set);
                    StepACDate stepACDate = new StepACDate();
                    stepACDate.setSkuCode(entry.getKey());
                    String minDate = DateUtil.getDate(list.get(0));
                    if (minDate == null) {
                        return null;
                    }
                    stepACDate.setMinDate(minDate);
                    String maxDate = DateUtil.getDate(list.get(list.size()-1));
                    if (maxDate == null) {
                        return null;
                    }
                    stepACDate.setMaxDate(maxDate);
                    begainList.add(stepACDate);
                }
            }
            if (!begainList.isEmpty()) {
                newList = new ArrayList<>();
                for (StepACDate stepACDate : begainList) {
                    List<String> dataList =
                            DateUtil.getDateList(stepACDate.getMinDate(), stepACDate.getMaxDate());
                    if (dataList == null) {
                        return null;
                    }
                    List<StepACDate> list = new ArrayList<>();
                    for (String str : dataList) {
                        StepACDate sd = new StepACDate();
                        sd.setSkuCode(stepACDate.getSkuCode());
                        sd.setMinDate(stepACDate.getMinDate());
                        sd.setMaxDate(stepACDate.getMaxDate());
                        sd.setDate(str);
                        list.add(sd);
                    }
                    newList.addAll(list);
                }
            }
        }
        return newList;
    }
}
