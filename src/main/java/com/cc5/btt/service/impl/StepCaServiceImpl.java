package com.cc5.btt.service.impl;

import com.cc5.btt.constants.BTTConstants;
import com.cc5.btt.dao.StepCaDao;
import com.cc5.btt.entity.StepBA;
import com.cc5.btt.entity.StepBD;
import com.cc5.btt.entity.StepCA;
import com.cc5.btt.service.StepCaService;
import com.cc5.btt.util.ExcelWriter;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service("stepCaService")
public class StepCaServiceImpl implements StepCaService {

    private static final Logger log = Logger.getLogger(StepCaServiceImpl.class);

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
        List<StepCA> dataList = new ArrayList<>();
        Map<Integer, Map<Integer, Map<String, List<StepBA>>>> sortMap = sortData (step1Map);
        handleData (sortMap, userId, dataList);
        List<StepCA> newDataList = getIntersection(dataList);
        stepCaDao.delete(userId);
        int ret = stepCaDao.insert(userId, newDataList);
        if (ret > 0) {
            return 1;
        }
        return 0;
    }

    @Override
    public Workbook prepareDownload(int userId) {
        Map<Integer, List<String>> headerMap = stepCaDao.getHeaderMap(userId);
        Map<Integer, Map<String, List<String>>> dataMap = stepCaDao.getExcelData(userId);
        if (!dataMap.isEmpty()) {
            for (Map.Entry<Integer, Map<String, List<String>>> entry : dataMap.entrySet()) {
                int posId = entry.getKey();
                Map<String, List<String>> map = entry.getValue();
                Map<String, List<String>> newMap = new LinkedHashMap<>();
                for (String str : BTTConstants.stepCAHeaders) {
                    if (map.get(str) != null) {
                        newMap.put(str, map.get(str));
                    }
                }
                dataMap.put(posId, newMap);
            }
        }
        if (!headerMap.isEmpty()) {
            return ExcelWriter.writeStepACMap(dataMap, headerMap);
        }
        return null;
    }


    /**
     * 取交集
     * @param dataList
     */
    private List<StepCA> getIntersection (List<StepCA> dataList) {
        List<StepCA> newDataList = new ArrayList<>();
        if (dataList != null) {
           for (StepCA stepCA : dataList) {
               for (String str : BTTConstants.stepCAHeaders) {
                   if (stepCA.getName().equals(str)) {
                       newDataList.add(stepCA);
                   }
               }
           }
        }
        return newDataList;
    }



    /**
     * 处理数据
     * @param sortMap
     * @param userId
     * @param dataList
     */
    private void handleData (Map<Integer, Map<Integer, Map<String, List<StepBA>>>> sortMap,
                             int userId, List<StepCA> dataList) {
        for (Map.Entry<Integer, Map<Integer, Map<String, List<StepBA>>>> entry : sortMap.entrySet()) {
            int posId = entry.getKey();
            Map<Integer, Map<String, List<StepBA>>> recordMap = entry.getValue();
            int size = recordMap.size();
            if (size < 422) {
                int recordId = 0;
                int listSize = 0;
                for (Map.Entry<Integer, Map<String, List<StepBA>>> entry1 : recordMap.entrySet()) {
                    recordId = entry1.getKey();
                    Map<String, List<StepBA>> listMap = entry1.getValue();
                    for (Map.Entry<String, List<StepBA>> entry2 : listMap.entrySet()) {
                        String sizeCode = entry2.getKey();
                        List<StepBA> list = entry2.getValue();
                        listSize = list.size();
                        List<StepCA> bagainList = createFristList(recordId, sizeCode, userId, posId, 0);
                        dataList.addAll(bagainList);
                        for (StepBA stepBA : list) {
                            StepCA stepCA = new StepCA();
                            stepCA.setUserId(userId);
                            stepCA.setPosId(posId);
                            stepCA.setRecordId(recordId);
                            stepCA.setName(stepBA.getRecId()+"");
                            if (stepBA.getValue() != null) {
                                stepCA.setValue(stepBA.getValue()+"");
                            }
                            dataList.add(stepCA);
                        }
                    }
                }
                addNullRow (recordId+1, null, userId, posId, dataList, listSize);
            } else if (size >= 422 && size < 842) {
                int recordId = 0;
                int listSize = 0;
                for (Map.Entry<Integer, Map<String, List<StepBA>>> entry1 : recordMap.entrySet()) {
                    recordId = entry1.getKey();
                    if (recordId == 421) {
                        addNullRow (recordId, null, userId, posId, dataList, listSize);
                    }
                    Map<String, List<StepBA>> listMap = entry1.getValue();
                    for (Map.Entry<String, List<StepBA>> entry2 : listMap.entrySet()) {
                        String sizeCode = entry2.getKey();
                        List<StepBA> list = entry2.getValue();
                        listSize = list.size();
                        List<StepCA> bagainList = createFristList(recordId, sizeCode, userId, posId, 0);
                        dataList.addAll(bagainList);
                        for (StepBA stepBA : list) {
                            StepCA stepCA = new StepCA();
                            stepCA.setUserId(userId);
                            stepCA.setPosId(posId);
                            if (recordId >= 421) {
                                stepCA.setRecordId(recordId - 420);
                            } else {
                                stepCA.setRecordId(recordId);
                            }
                            stepCA.setName(stepBA.getRecId()+"");
                            if (stepBA.getValue() != null) {
                                stepCA.setValue(stepBA.getValue()+"");
                            }
                            dataList.add(stepCA);
                        }
                    }
                }
                addNullRow ((recordId-420)+1, null, userId, posId, dataList, listSize);
            } else if (size >= 842) {
                int recordId = 0;
                int listSize = 0;
                for (Map.Entry<Integer, Map<String, List<StepBA>>> entry1 : recordMap.entrySet()) {
                    recordId = entry1.getKey();
                    if (recordId == 421) {
                        addNullRow (recordId, null, userId, posId, dataList, listSize);
                    }
                    if (recordId == 841) {
                        addNullRow (recordId-420, null, userId, posId, dataList, listSize);
                    }
                    Map<String, List<StepBA>> listMap = entry1.getValue();
                    for (Map.Entry<String, List<StepBA>> entry2 : listMap.entrySet()) {
                        String sizeCode = entry2.getKey();
                        List<StepBA> list = entry2.getValue();
                        listSize = list.size();
                        List<StepCA> bagainList = createFristList(recordId, sizeCode, userId, posId, 0);
                        dataList.addAll(bagainList);
                        for (StepBA stepBA : list) {
                            StepCA stepCA = new StepCA();
                            stepCA.setUserId(userId);
                            stepCA.setPosId(posId);
                            if (recordId >= 421 && recordId < 841) {
                                stepCA.setRecordId(recordId - 420);
                            } else if (recordId >= 841) {
                                stepCA.setRecordId(recordId - 840);
                            } else {
                                stepCA.setRecordId(recordId);
                            }
                            stepCA.setName(stepBA.getRecId()+"");
                            if (stepBA.getValue() != null) {
                                stepCA.setValue(stepBA.getValue()+"");
                            }
                            dataList.add(stepCA);
                        }
                    }
                }
            }
        }
    }


    /**
     * 填加空行
     * @param recordId
     * @param sizeCode
     * @param userId
     * @param posId
     * @param dataList
     * @param listSize
     */
    private void addNullRow (int recordId, String sizeCode,
                             int userId, int posId, List<StepCA> dataList,
                             int listSize) {
        List<StepCA> bagainList = createFristList(recordId, sizeCode, userId, posId, 1);
        dataList.addAll(bagainList);
        int a = -2;
        for (int i = 0; i < listSize; i++) {
            StepCA stepCA = new StepCA();
            stepCA.setUserId(userId);
            stepCA.setPosId(posId);
            stepCA.setRecordId(recordId);
            stepCA.setName(a + "");
            stepCA.setValue(null);
            dataList.add(stepCA);
            a++;
        }
    }



    private List<StepCA> createFristList (int recordId, String sizeCode, int userId, int posId,
                                          int type) {
        List<StepCA> list = new ArrayList<>();
        StepCA stepCA1 = new StepCA();
        stepCA1.setUserId(userId);
        stepCA1.setName("Field");
        stepCA1.setPosId(posId);
        stepCA1.setRecordId(recordId);
        if (type == 0) {
            stepCA1.setValue(recordId + "");
        } else if (type == 1) {
            stepCA1.setValue(null);
        }
        StepCA stepCA2 = new StepCA();
        stepCA2.setUserId(userId);
        stepCA2.setName("Name");
        stepCA2.setPosId(posId);
        stepCA2.setRecordId(recordId);
        stepCA2.setValue(sizeCode);
        list.add(stepCA1);
        list.add(stepCA2);
        return list;

    }


    /**
     * 给分组之后的map再加一个序列
     * @param step1Map
     * @return
     */
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

    /**
     * 按name分组
     * @param list
     * @return
     */
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
