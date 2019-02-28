package com.cc5.btt.service.impl;

import com.cc5.btt.dao.StepBdDao;
import com.cc5.btt.entity.StepBD;
import com.cc5.btt.service.StepBdService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("stepBdService")
public class StepBdServiceImpl implements StepBdService {

    private static final Logger log = Logger.getLogger(StepAbServiceImpl.class);

    @Resource(name = "stepBdDao")
    private StepBdDao stepBdDao;

    @Override
    public List<StepBD> getStepBdResult(int userId) {
        Map<Integer, List<Map<String, Object>>> listMap = stepBdDao.getStep10(userId);
        List<StepBD> bdList = new ArrayList<>();
        List<StepBD> rList = new ArrayList<>();
        for(List<Map<String, Object>> mapList : listMap.values()){
            rList.addAll(getRList(userId, mapList));
        }
        List<StepBD> lList = stepBdDao.getLList(userId);
        for(StepBD rBd : rList){
            int posId = rBd.getPosId();
            String prodCd = rBd.getProdCd();
            for(StepBD lBd : lList){
                if(posId == lBd.getPosId() && prodCd.equals(lBd.getProdCd())){
                    bdList.add(lBd);
                }
            }
        }
        return bdList;

    }

    @Override
    public Map<String, String> insert(int userId, List<StepBD> beanList) throws SQLException {
        Map<String, String> map = new HashMap<>();
        int num = stepBdDao.getNum(userId);     //查询当前用户的数据量
        if (num == 0 || num == stepBdDao.delete(userId)){
            //验证插入条数与源数据是否相等
            if (beanList.size() != stepBdDao.insert(userId, beanList)){
                log.error("Insert StepBD: Number of insert is not equals data rows.");
                throw new SQLException();
            }
            map.put("result", "success");
        }else {
            log.error("Delete StepBD: Number of delete is not equals former records.");
            throw new SQLException();
        }
        return map;
    }

    /**
     * 获取BD步骤中最后Join之前右表数据
     * @param userId
     * @param list
     * @return
     */
    private List<StepBD> getRList(int userId, List<Map<String, Object>> list){
        int size = list.size();
        int count = (int) Math.ceil(size/100d) - 1;
        List<Integer> field = new ArrayList<>(130);
        for (int i = 1; i <= 130; i++){
            field.add(i * count);
        }
        List<StepBD> bdList = new ArrayList<>();
        for(int i = 0; i < size; i++){
            int recordId = (int)list.get(i).get("recordId");
            if(field.contains(recordId)){
                Map<String, Object> map = list.get(i);
                StepBD bd = new StepBD();
                bd.setUserId(userId);
                bd.setPosId((int)map.get("posId"));
                bd.setRecordId((int)map.get("recordId"));
                bd.setProdCd(String.valueOf(map.get("prodCd")));
                bd.setSumQty((int)map.get("sumSumQty"));
                bdList.add(bd);
            }
        }
        return bdList;
    }
}
