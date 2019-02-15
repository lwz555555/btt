package com.cc5.btt.service.impl;

import com.cc5.btt.dao.StepAcDao;
import com.cc5.btt.dao.StepBaDao;
import com.cc5.btt.entity.StepAC;
import com.cc5.btt.entity.StepBA;
import com.cc5.btt.service.StepBaService;
import com.cc5.btt.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class StepBaServiceImpl implements StepBaService {

    @Autowired
    private StepAcDao stepAcDao;
    @Autowired
    private StepBaDao stepBaDao;

    @Override
    public int runBaStep(int userId) {
        Map<String, List<StepAC>> stepBaPart1Map = stepAcDao.getStepAc(userId);
        Map<String, List<StepAC>> groupByMap = stepAcDao.getGroupByCodeSize(userId);
        Map<String, List<String>> minDateMap = new HashMap<>();
        List<StepBA> stepBAList = new ArrayList<>();
        if (!stepBaPart1Map.isEmpty()) {
            for (Map.Entry<String, List<StepAC>> entry : stepBaPart1Map.entrySet()) {
                String key = entry.getKey();
                if ("136307_325213-802".equals(key)) {
                    int a = 3;
                }
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
                List<StepAC> stepBaPart2List = handlePart2List (groupByMap, minDateMap, key);
                if (stepBaPart2List == null) {
                    return 0;
                }
                part1List.addAll(stepBaPart2List);
                //分组
                Map<String, List<StepAC>> groupMap = part1List.stream().collect(
                        Collectors.groupingBy(StepAC :: getSize));
                List<Integer> sortList = new ArrayList<>();
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
                    for (int j = 0; j < list.size(); j++) {
                        StepAC stepAC = list.get(j);
                        if (stepAC.getRecId() == null) {
                            list.remove(j);
                            j--;
                            continue;
                        }
                        if (1 == stepAC.getRecId()) {
                            break;
                        }
                    }
                    sortList.add(list.size());
                }
                Collections.sort(sortList);
                for (int i = 0; i < sortList.get(sortList.size() - 1); i++) {
                    if (i > 90) {
                        break;
                    }
                    for (String str : keyList) {
                        StepBA stepBA = new StepBA();
                        stepBA.setRecId(i+1);
                        stepBA.setFileName(key);
                        stepBA.setPosId(posId);
                        stepBA.setUserId(userId);
                        stepBA.setName(key.substring(key.length()-10) + "____" + str);
                        try {
                            stepBA.setValue(groupMap.get(str).get(i).getUnits());
                        } catch (Exception e) {

                        }
                        stepBAList.add(stepBA);
                    }
                }
            }
        }
        try {
            stepBaDao.delete(userId);
            int ret = stepBaDao.insert(userId, stepBAList);
            if (ret > 0) {
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    private List<StepAC> handlePart2List (Map<String, List<StepAC>> groupByMap, Map<String, List<String>> minDateMap,
                                          String key) {
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
                                } else {
                                    stepAC.setMaxDate(stepAC.getDate());
                                    String minDate = DateUtil.getFixedDate(stepAC.getDate(), -7);
                                    if (minDate == null) return null;
                                    stepAC.setMinDate(minDate);
                                }
                            }
                        }
                    }
                }
            } else {
                if (stepACSet != null && !stepACSet.isEmpty()) {
                    for (int i = 0; i < stepACSet.size(); i++) {
                        StepAC stepAC = stepACSet.get(i);
                        stepAC.setMaxDate(stepAC.getDate());
                        String minDate = DateUtil.getFixedDate(stepAC.getDate(), -7);
                        if (minDate == null) return null;
                        stepAC.setMinDate(minDate);
                    }
                }
            }

            for (StepAC stepAC : stepACSet) {
                int day = -7;
                for (int i = 1; i < 8; i++) {
                    StepAC stepAC1 = new StepAC();
                    stepAC1.setSkuCode(stepAC.getSkuCode());
                    stepAC1.setMinDate(stepAC.getMinDate());
                    stepAC1.setMaxDate(stepAC.getMaxDate());
                    String date = DateUtil.getFixedDate(stepAC.getMaxDate(), day);
                    if (date == null) return null;
                    String week = DateUtil.getWeek(date);
                    stepAC1.setDayInWeek(week);
                    if ("Monday".equals(week)) {
                        stepAC1.setRecId(1);
                    }
                    stepAC1.setDate(date);
                    stepAC1.setProdCd(stepAC.getProdCd());
                    stepAC1.setPosId(stepAC.getPosId());
                    stepAC1.setFileName(stepAC.getFileName());
                    stepAC1.setUnits(0);
                    stepAC1.setSize(stepAC.getSize());
                    part2NewList.add(stepAC1);
                    day = day + 1;
                }
                String week = DateUtil.getWeek(stepAC.getDate());
                if ("Monday".equals(week)) {
                    stepAC.setRecId(1);
                }
                stepAC.setUnits(1);
                part2NewList.add(stepAC);
            }
        }
        return part2NewList;
    }
}
