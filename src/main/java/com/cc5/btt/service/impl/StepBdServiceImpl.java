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
        List<Map<String, Object>> mapList = stepBdDao.getStep10(userId);
        int size = mapList.size();
        int count = (int) Math.ceil(size/100 - 1);
        List<Integer> field = new ArrayList<>(130);
        for (int i = 1; i <= 130; i++){
            field.add(i * count);
        }
        List<String> prodCdList = new ArrayList<>();
        for(int i = 0; i < size; i++){
            int recordId = (int)mapList.get(i).get("recordId");
            if(field.contains(recordId)){
                String prodCd = String.valueOf(mapList.get(i).get("prodCd"));
                prodCdList.add(prodCd);
            }
        }
        return stepBdDao.getBdResult(userId, prodCdList);
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
}
