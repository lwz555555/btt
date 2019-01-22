package com.cc5.btt.service.impl;

import com.cc5.btt.dao.StepAbDao;
import com.cc5.btt.entity.StepAB;
import com.cc5.btt.service.StepAbService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.*;

@Service("stepAbService")
public class StepAbServiceImpl implements StepAbService {

    private static final Logger log = Logger.getLogger(StepAbServiceImpl.class);

    @Resource(name = "stepAbDao")
    private StepAbDao stepAbDao;

    //生成 Step AB结果
    @Override
    public Map<String, String> insert(int userId, List<StepAB> beanList) throws SQLException {
        Map<String, String> map = new HashMap<>();
        int num = stepAbDao.getNum(userId);     //查询当前用户的数据量
        if (num == 0 || num == stepAbDao.delete(userId)){
            //验证插入条数与源数据是否相等
            if (beanList.size() != stepAbDao.insert(userId, beanList)){
                log.error("Insert StepAB: Number of insert is not equals data rows.");
                throw new SQLException();
            }
            map.put("result", "success");
        }else {
            log.error("Delete StepAB: Number of delete is not equals former records.");
            throw new SQLException();
        }
        return map;
    }

    //根据 pos_prod 分组（为StepAC输入做准备）
    @Override
    public Map<String, List<StepAB>> getMap(int userId) {
        List<String> posProdList = stepAbDao.getPosProdList(userId);
        Map<String, List<StepAB>> map = new LinkedHashMap<>();
        for(int i = 0; i < posProdList.size(); i++){
            List<StepAB> beanList = stepAbDao.getbeanList(userId, posProdList.get(i));
            map.put(posProdList.get(i), beanList);
        }
        return map;
    }

}
