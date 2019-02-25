package com.cc5.btt.service.impl;

import com.cc5.btt.dao.StepAcDao;
import com.cc5.btt.entity.StepAC;
import com.cc5.btt.entity.StepBC;
import com.cc5.btt.service.StepBcService;
import com.cc5.btt.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StepBcServiceImpl implements StepBcService {

    @Autowired
    private StepAcDao stepAcDao;

    @Override
    public int runBcStep(int userId) {
        List<StepBC> stepBCList = new ArrayList<>();
        Map<String, List<StepAC>> stepBaPart1Map = stepAcDao.getStepAc(userId);
        Map<String, List<StepAC>> groupByMap = stepAcDao.getGroupByCodeSize(userId);
        Map<String, List<String>> minDateMap = new HashMap<>();
        if (!stepBaPart1Map.isEmpty()) {
            for (Map.Entry<String, List<StepAC>> entry : stepBaPart1Map.entrySet()) {
                String key = entry.getKey();
                List<StepAC> part1List = entry.getValue();
                Integer posId = part1List.get(0).getPosId();
                Set<String> sizeSet = new HashSet<>();
                if (part1List != null) {
                    for (StepAC stepAC : part1List) {
                        String week = DateUtil.getWeek(stepAC.getDate());
                        if (week == null) {
                            return 0;
                        }
                        stepAC.setDayInWeek(week);
                        String size = stepAC.getSize();
                        if ("Monday".equals(week) && !sizeSet.contains(size)) {
                            sizeSet.add(size);
                            if (minDateMap.containsKey(key)) {
                                minDateMap.get(key).add(size);
                            } else {
                                List<String> sizeList = new ArrayList<>();
                                sizeList.add(size);
                                minDateMap.put(key, sizeList);
                            }
                            stepAC.setRecId(1);
                        }
                    }
                }
                List<StepAC> stepBaPart2List = handlePart2List (groupByMap, minDateMap, key, part1List);
                if (stepBaPart2List == null) {
                    return 0;
                }
                part1List.addAll(stepBaPart2List);
                //分组
                Map<String, List<StepAC>> groupMap = part1List.stream().collect(
                        Collectors.groupingBy(StepAC :: getSize));
                List<StepAC> sortList = new ArrayList<>();
                List<String> keyList = new ArrayList<>();
                for (Map.Entry<String, List<StepAC>> entry1 : groupMap.entrySet()) {
                    List<StepAC> list = entry1.getValue();
                    keyList.add(entry1.getKey());
                    //排序
                    Collections.sort(list, new Comparator<StepAC>() {
                        @Override
                        public int compare(StepAC o1, StepAC o2) {
                            Long o1Time = DateUtil.getLongTime(o1.getDate());
                            Long o2Time = DateUtil.getLongTime(o2.getDate());
                            if (o1Time == null || o2Time == null) {
                                return -1;
                            }
                            if (o1Time > o2Time) {
                                return 1;
                            }
                            if (o1Time == o2Time) {
                                return 0;
                            }
                            return -1;
                        }
                    });
                    int a = 1;
                    boolean b = false;
                    for (int j = 0; j < list.size(); j++) {
                        StepAC stepAC = list.get(j);
                        if (stepAC.getRecId() == null && !b) {
                            continue;
                        }
                        if (1 == stepAC.getRecId()) {
                            b = true;
                            continue;
                        }
                        if (stepAC.getRecId() == null) {
                            stepAC.setRecId(a++);
                        }
                    }
                    sortList.addAll(list);
                }
                List<StepBC> partOneList = new ArrayList<>();
                List<StepAC> partTwoList = new ArrayList<>();
                List<StepAC> partThreeList = new ArrayList<>();
                for (int i = 0; i < sortList.size(); i++) {
                    StepAC stepAC = sortList.get(i);
                    Integer recId = stepAC.getRecId();
                    if (i > 0 && i+1 <= sortList.size()) {
                        sortList.get(i+1).setInitialInv(stepAC.getInvQty());
                    }
                    if (recId == 1) {
                        StepBC stepBC = new StepBC();
                        stepBC.setPosId(posId);
                        stepBC.setSizeCode(stepAC.getSkuCode());
                        stepBC.setUserId(userId);
                        stepBC.setStartInv(stepAC.getInitialInv());
                        partOneList.add(stepBC);
                        partTwoList.add(stepAC);
                        partThreeList.add(stepAC);
                    }
                    if (recId != null && recId > 1
                            && recId <= 14) {
                        partTwoList.add(stepAC);
                        partThreeList.add(stepAC);
                    }
                    if (recId != null && recId > 14
                        && recId <= 91) {
                        partTwoList.add(stepAC);
                    }
                }
                integrationPartData (partOneList, partTwoList, partThreeList);
                stepBCList.addAll(partOneList);
            }

        }
        return 0;
    }


    private void integrationPartData (List<StepBC> partOneList, List<StepAC> partTwoList,
                                      List<StepAC> partThreeList) {
        Map<String, List<StepAC>> partTwoMap = partTwoList.stream().collect(
                Collectors.groupingBy(StepAC :: getSkuCode));
        Map<String, List<StepAC>> partThreeMap = partThreeList.stream().collect(
                Collectors.groupingBy(StepAC :: getSkuCode));
        for (StepBC stepBC : partOneList) {
            String sizeCode = stepBC.getSizeCode();
            Integer sumSalQty = partTwoMap.get(sizeCode).stream().mapToInt(StepAC::getUnits).sum();
            stepBC.setSumSalQty(sumSalQty);
            Integer sumFrist4wksSalQty = partThreeMap.get(sizeCode).stream().mapToInt(StepAC::getUnits).sum();
            stepBC.setSumFrist4wksSalQty(sumFrist4wksSalQty);
            if (stepBC.getStartInv() == null || stepBC.getStartInv() == 0) {
                stepBC.setStartInv(stepBC.getSumFrist4wksSalQty());
            }
            if (stepBC.getStartInv() == 0) {
                stepBC.setStartInv(1);
            }
        }
    }


    private List<StepAC> handlePart2List (Map<String, List<StepAC>> groupByMap, Map<String, List<String>> minDateMap,
                                          String key, List<StepAC> part1List) {
        List<StepAC> part2NewList = new ArrayList<>();
        if (!groupByMap.isEmpty()) {
            List<StepAC> stepACSet = groupByMap.get(key);
            if (minDateMap.containsKey(key)) {
                List<String> minDateList = minDateMap.get(key);
                if (minDateList != null && !minDateList.isEmpty()) {
                    for (String str : minDateList) {
                        if (stepACSet != null && !stepACSet.isEmpty()) {
                            for (int i = 0; i < stepACSet.size(); i++) {
                                StepAC stepAC = stepACSet.get(i);
                                if (str.equals(stepAC.getSize())) {
                                    stepACSet.remove(stepAC);
                                    i--;
                                    continue;
                                }
                            }
                        }
                    }
                }
            }
            if (!stepACSet.isEmpty()) {
                for (StepAC stepAC : stepACSet) {
                    for (StepAC stepAC1 : part1List) {
                        if (stepAC.getSize().equals(stepAC1.getSize())) {
                            stepAC1.setRecId(1);
                            part2NewList.add(stepAC1);
                        }
                    }
                }
            }
        }
        return part2NewList;
    }
}
