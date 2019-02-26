package com.cc5.btt.service.impl;

import com.cc5.btt.dao.StepAcDao;
import com.cc5.btt.dao.StepBcDao;
import com.cc5.btt.entity.StepAC;
import com.cc5.btt.entity.StepBC;
import com.cc5.btt.service.StepBcService;
import com.cc5.btt.util.DateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StepBcServiceImpl implements StepBcService {

    @Autowired
    private StepAcDao stepAcDao;
    @Autowired
    private StepBcDao stepBcDao;

    @Override
    public int runBcStep(int userId) {
        List<StepBC> stepBCList = new ArrayList<>();
        Map<String, List<StepAC>> stepBaPart1Map = stepAcDao.getNullStepAc(userId);
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
                        if (stepAC.getUnits() < 0) {
                            stepAC.setUnits(0);
                        }
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
                            if (o1Time == o2Time && o1.getRecId() == 1) {
                                return 1;
                            } else if (o1Time == o2Time && o2.getRecId() == 1) {
                                return -1;
                            } else if (o1Time == o2Time && o2.getRecId() == null
                                && o1.getRecId() == null) {
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
                        if (stepAC.getRecId() != null && 1 == stepAC.getRecId()) {
                            b = true;
                            continue;
                        }
                        if (stepAC.getRecId() == null) {
                            a++;
                            stepAC.setRecId(a);
                        }
                    }
                }
                sortSize (keyList);
                for (String str : keyList) {
                    sortList.addAll(groupMap.get(str));
                }
                List<StepBC> partOneList = new ArrayList<>();
                List<StepAC> partTwoList = new ArrayList<>();
                List<StepAC> partThreeList = new ArrayList<>();
                Map<String, Map<String, String>> recordMap = new HashMap<>();
                for (int i = 0; i < sortList.size(); i++) {
                    StepAC stepAC = sortList.get(i);
                    Integer recId = stepAC.getRecId();
                    String sizeCode = stepAC.getSkuCode();
                    if (i > 0 && i < sortList.size()) {
                        stepAC.setInitialInv(sortList.get(i-1).getInvQty());
                    }
                    Integer initialInv = stepAC.getInitialInv();
                    String initialInvString = "";
                    if (initialInv == null) {
                        initialInvString = "null";
                    } else {
                        initialInvString = initialInv + "";
                    }
                    if (recId != null && recId == 1) {
                       if (!recordMap.containsKey(sizeCode)) {
                           Map<String, String> map = new HashMap<>();
                           map.put(initialInvString, initialInvString);
                           recordMap.put(sizeCode, map);
                           StepBC stepBC = new StepBC();
                           stepBC.setPosId(posId);
                           stepBC.setSizeCode(stepAC.getSkuCode());
                           stepBC.setUserId(userId);
                           stepBC.setStartInv(stepAC.getInitialInv());
                           partOneList.add(stepBC);
                        } else if (recordMap.containsKey(sizeCode)
                               && !recordMap.get(sizeCode).containsKey(initialInvString)) {
                           Map<String, String> map = recordMap.get(sizeCode);
                           map.put(initialInvString, initialInvString);
                           StepBC stepBC = new StepBC();
                           stepBC.setPosId(posId);
                           stepBC.setSizeCode(stepAC.getSkuCode());
                           stepBC.setUserId(userId);
                           stepBC.setStartInv(stepAC.getInitialInv());
                           partOneList.add(stepBC);
                       }
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
            try {
                stepBcDao.delete(userId);
                int ret = stepBcDao.insert(userId, stepBCList);
                if (ret > 0) {
                    return 1;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }


    private void sortSize (List<String> keyList) {
        if (!keyList.isEmpty()) {
            Collections.sort(keyList, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    char char1 = o1.charAt(0);
                    char char2 = o2.charAt(0);
                    if (char1 > char2) {
                        return 1;
                    }
                    if (char1 == char2) {
                        return 0;
                    }
                    return -1;
                }
            });
        }
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
            stepBC.setSumFirst4wksSalQty(sumFrist4wksSalQty);
            if (stepBC.getStartInv() == null || stepBC.getStartInv() == 0) {
                stepBC.setStartInv(stepBC.getSumFirst4wksSalQty());
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
            if (stepACSet != null && !stepACSet.isEmpty()) {
                for (StepAC stepAC : stepACSet) {
                    for (StepAC stepAC1 : part1List) {
                        if (stepAC.getSize().equals(stepAC1.getSize())) {
                            StepAC copyStepAC = new StepAC();
                            BeanUtils.copyProperties(stepAC1, copyStepAC);
                            copyStepAC.setRecId(1);
                            part2NewList.add(copyStepAC);
                        }
                    }
                }
            }
        }
        return part2NewList;
    }
}
