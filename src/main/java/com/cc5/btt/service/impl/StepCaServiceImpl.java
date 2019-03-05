package com.cc5.btt.service.impl;

import com.cc5.btt.dao.StepCaDao;
import com.cc5.btt.entity.StepAC;
import com.cc5.btt.entity.StepBA;
import com.cc5.btt.entity.StepBD;
import com.cc5.btt.service.StepCaService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service("stepCaService")
public class StepCaServiceImpl implements StepCaService {

    private static final Logger log = Logger.getLogger(StepAbServiceImpl.class);

    @Resource(name = "stepCaDao")
    private StepCaDao stepCaDao;

    @Override
    public int runCaStep(int userId) {
        //加载BB步骤的数据，BB步骤比较简单，整合到了BA步骤
        Map<Integer, Map<String, List<StepBA>>> groupMap = stepCaDao.getGroupMap(userId);
        Map<Integer, List<String>> groupNameMap = stepCaDao.getGroupNameMap(userId);
        Map<Integer, List<StepBA>> step1Map = new HashMap<>();
        for (Map.Entry<Integer, Map<String, List<StepBA>>> entry : groupMap.entrySet()) {
            List<StepBA> step1List = new ArrayList<>();
            int posId = entry.getKey();
            Map<String, List<StepBA>> entryMap = entry.getValue();
            for (Map.Entry<String, List<StepBA>> entry1 : entryMap.entrySet()) {
                Set<String> nameSet = new HashSet<>(groupNameMap.get(posId));
                List<StepBA> list = entry1.getValue();
                int recId = list.get(0).getRecId();
                step1List.addAll(list);
                for (StepBA stepBA : list) {
                    String name = stepBA.getName();
                    if (nameSet != null) {
                        nameSet.remove(name);
                    }
                }
                if (!nameSet.isEmpty()) {
                    for (String str : nameSet) {
                        StepBA stepBA = new StepBA();
                        stepBA.setRecId(recId);
                        stepBA.setPosId(posId);
                        stepBA.setUserId(userId);
                        stepBA.setName(str);
                        step1List.add(stepBA);
                    }
                }
            }
            step1Map.put(posId, step1List);
        }
        getIntersection(step1Map, userId);
        getUnion (step1Map, userId);
        Map<Integer, Map<Integer, Map<String, List<StepBA>>>> sortMap = sortData (step1Map);
        return 0;
    }

    private Map<Integer, Map<Integer, Map<String, List<StepBA>>>> sortData (
            Map<Integer, List<StepBA>> step1Map) {
        Map<Integer, Map<Integer, Map<String, List<StepBA>>>> returnMap = new LinkedHashMap<>();
        for (Map.Entry<Integer, List<StepBA>> entry : step1Map.entrySet()) {
            Map<Integer, Map<String, List<StepBA>>> newMap = new LinkedHashMap<>();
            int posId = entry.getKey();
            List<StepBA> list = entry.getValue();
            Map<String, List<StepBA>> groupMap = groupByName(list);
            int i = 1;
            for (Map.Entry<String, List<StepBA>> entry1 : groupMap.entrySet()) {
                Map<String, List<StepBA>> stringListMap = new LinkedHashMap<>();
                stringListMap.put(entry1.getKey(), entry1.getValue());
                newMap.put(i, stringListMap);
                i++;
            }
            returnMap.put(posId, newMap);
        }
        return returnMap;
    }


    private Map<String, List<StepBA>> groupByName (List<StepBA> list) {
        Map<String, List<StepBA>> sortMap = new LinkedHashMap<>();
        for (StepBA stepBA : list) {
            if (sortMap.containsKey(stepBA.getName())) {
                List<StepBA> newList = sortMap.get(stepBA.getName());
                newList.add(stepBA);
            } else {
                List<StepBA> newList = new ArrayList<>();
                newList.add(stepBA);
                sortMap.put(stepBA.getName(), newList);
            }
        }
        return sortMap;
    }


    /**
     * 取并集
     * @param step1Map
     * @param userId
     */
    private void getUnion (Map<Integer, List<StepBA>> step1Map, int userId) {
        Map<Integer, Map<Integer, Map<String, Integer>>> result2 = stepCaDao.getBdResult2(userId);
        if (!result2.isEmpty()) {
            for (Map.Entry<Integer, Map<Integer, Map<String, Integer>>> entry : result2.entrySet()) {
                int posId = entry.getKey();
                List<StepBA> stepBAS = new ArrayList<>();
                Map<Integer, Map<String, Integer>> result2Map = entry.getValue();
                if (!result2Map.isEmpty()) {
                    for (Map.Entry<Integer, Map<String, Integer>> entry1 : result2Map.entrySet()) {
                        int recId = entry1.getKey();
                        Map<String, Integer> integerMap = entry1.getValue();
                        if (!integerMap.isEmpty()) {
                            for (Map.Entry<String, Integer> entry2 : integerMap.entrySet()) {
                                StepBA stepBA = new StepBA();
                                stepBA.setRecId(recId);
                                stepBA.setName(entry2.getKey());
                                stepBA.setPosId(posId);
                                stepBA.setValue(entry2.getValue());
                                stepBA.setUserId(userId);
                                stepBAS.add(stepBA);
                            }
                        }
                    }
                }
                step1Map.get(posId).addAll(stepBAS);
            }
        }
    }



    /**
     * 取交集
     * @param step1Map
     * @param userId
     */
    private void getIntersection (Map<Integer, List<StepBA>> step1Map, int userId) {
        Map<Integer, List<StepBD>> result1 = stepCaDao.getBdResult1(userId);
        if (!result1.isEmpty()) {
            for (Map.Entry<Integer, List<StepBA>> entry : step1Map.entrySet()) {
                Set<String> recordSet = new HashSet<>();
                int posId = entry.getKey();
                List<StepBA> newList = new ArrayList<>();
                List<StepBA> list1 = entry.getValue();
                List<StepBD> list2 = result1.get(posId);
                if (list2 != null) {
                    for (StepBD stepBD : list2) {
                        for (StepBA stepBA : list1) {
                            if (stepBA.getName().equals(stepBD.getSizeCode())) {
                                int recId = stepBA.getRecId();
                                String name = stepBA.getName();
                                String key = posId + "_" + recId + "_" + name;
                                if (!recordSet.contains(key)) {
                                    newList.add(stepBA);
                                    recordSet.add(key);
                                }
                            }
                        }
                    }
                }
                step1Map.put(posId, newList);
            }
        }
    }
}
