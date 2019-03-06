package com.cc5.btt.service.impl;

import com.cc5.btt.dao.StepCbDao;
import com.cc5.btt.entity.StepCB;
import com.cc5.btt.service.StepCbService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.*;

@Service("stepCbService")
public class StepCbServiceImpl implements StepCbService {

    private static final Logger log = Logger.getLogger(StepAbServiceImpl.class);

    @Resource(name = "stepCbDao")
    private StepCbDao stepCbDao;

    @Override
    public Map<String, String> insert(int userId, List<StepCB> beanList) throws SQLException {
        Map<String, String> map = new HashMap<>();
        int num = stepCbDao.getNum(userId);
        if (num == 0 || num == stepCbDao.delete(userId)){
            //验证插入条数与源数据是否相等
            if (beanList.size() != stepCbDao.insert(userId, beanList)){
                log.error("Insert StepCB: Number of insert is not equals data rows.");
                throw new SQLException();
            }
            map.put("result", "success");
        }else {
            log.error("Delete StepCB: Number of delete is not equals former records.");
            throw new SQLException();
        }
        return map;
    }

    @Override
    public int runStepCb(int userId) throws SQLException {
        Map<Integer, Map<String, Map<Integer, String>>> caResult = stepCbDao.getCaResult(userId);
        Map<String, String> map = insert(userId, getCbResult(caResult));
        if (map.get("result").equals("success")){
            return 1;
        }
        return 0;
    }

    /**
     * CB步骤
     * @param caResult
     * @return
     */
    private List<StepCB> getCbResult(Map<Integer, Map<String, Map<Integer, String>>> caResult){
        List<StepCB> cbResult = new ArrayList<>();
        for (Map.Entry<Integer, Map<String, Map<Integer, String>>> entry : caResult.entrySet()) {
            int posId = entry.getKey();
            Map<String, Map<Integer, String>> mapMap = entry.getValue();
            Map<Integer, StepCB> cbMap = new LinkedHashMap<>();
            List<StepCB> list = new ArrayList<>();
            boolean processed = false;
            if (mapMap.containsKey("Field")){
                Map<Integer, String> map = mapMap.get("Field");
                for (Integer i : map.keySet()){
                    if (StringUtils.isBlank(map.get(i))){
                        continue;
                    }
                    StepCB cb = new StepCB();
                    cb.setName(i);
                    if (i < 421){
                        cb.setTabName("b1");
                    }else if (i < 842){
                        cb.setTabName("b2");
                    }else {
                        cb.setTabName("b3");
                    }
                    cb.setPosId(posId);
                    cb.setRecord(Integer.parseInt(map.get(i)));
                    cb.setField("Field " + map.get(i));
                    cbMap.put(i, cb);
                }
                processed = true;
            }
            if (processed && mapMap.containsKey("Name")){
                Map<Integer, String> map = mapMap.get("Name");
                List<Integer> nameList = new ArrayList<>(cbMap.keySet());
                Collections.sort(nameList);
                for (int i : nameList){
                    String prodCd = StringUtils.left(map.get(i), 10);
                    prodCd = StringUtils.replace(prodCd,"_","-");
                    prodCd = prodCd + StringUtils.substring(map.get(i),10);
                    StepCB cb = cbMap.get(i);
                    cb.setProdCd(prodCd);
                    list.add(cb);
                }
            }
            cbResult.addAll(list);
        }
        return cbResult;
    }

}
