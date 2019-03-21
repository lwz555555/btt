package com.cc5.btt.service.impl;

import com.cc5.btt.dao.StepZcDao;
import com.cc5.btt.entity.StepZC;
import com.cc5.btt.service.StepZcService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("stepZcService")
public class StepZcServiceImpl implements StepZcService {

    private static final Logger log = Logger.getLogger(StepZcServiceImpl.class);

    @Resource(name = "stepZcDao")
    private StepZcDao stepZcDao;

    @Override
    public int processStepZc(int userId) throws SQLException {
        log.info("-----Step ZC : get step ca data.-----");
        Map<Integer, List<String>> names = stepZcDao.getCaData(userId);
        //Unique MATL_NBR
        log.info("-----Step ZC : get unique matlNbr from prodInfoMasterForModelling data.-----");
        List<StepZC> prodInfoMasterForModelling1 = stepZcDao.getProdInfoMasterForModellingData(false);
        Map<Integer, List<String>> leftMatlNbr = new HashMap<>();
        Map<Integer, List<StepZC>> joinMatlNbr = new HashMap<>();
        log.info("-----Step ZC : left adn join by matlNbr.-----");
        leftJoinMatlNbr(names, prodInfoMasterForModelling1, leftMatlNbr, joinMatlNbr);
        //Unique STYLE_CD
        log.info("-----Step ZC : get unique styleCd from prodInfoMasterForModelling data.-----");
        List<StepZC> prodInfoMasterForModelling2 = stepZcDao.getProdInfoMasterForModellingData(true);
        log.info("-----Step ZC : join by styleCd.-----");
        Map<Integer, List<StepZC>> joinStyleCd = joinByStyleCd(leftMatlNbr, prodInfoMasterForModelling2);
        log.info("-----Step ZC : insert to database.-----");
        Map<String, String> result = insert(userId, union(joinStyleCd, joinMatlNbr));
        log.info("-----Step ZC : return result.-----");
        if (result.get("result").equals("success")){
            return 1;
        }
        return 0;
    }

    @Override
    public Map<String, String> insert(int userId, List<StepZC> beanList) throws SQLException {
        Map<String, String> map = new HashMap<>();
        int num = stepZcDao.getNum(userId);
        if (num == 0 || num == stepZcDao.delete(userId)){
            //验证插入条数与源数据是否相等
            if (beanList.size() != stepZcDao.insert(userId, beanList)){
                log.error("Insert StepZC: Number of insert is not equals data rows.");
                throw new SQLException();
            }
            map.put("result", "success");
        }else {
            log.error("Delete StepZC: Number of delete is not equals former records.");
            throw new SQLException();
        }
        return map;
    }

    private void leftJoinMatlNbr(Map<Integer, List<String>> names,
                                 List<StepZC> prodInfoMasterForModelling,
                                 Map<Integer, List<String>> leftMatlNbr,
                                 Map<Integer, List<StepZC>> joinMatlNbr){
        for (Integer posId : names.keySet()){
            List<String> nameList = new ArrayList<>();
            nameList.addAll(names.get(posId));
            List<StepZC> zcList = new ArrayList<>();
            for (String name : names.get(posId)){
                for (StepZC zc : prodInfoMasterForModelling){
                    if (name == null || name.length() <= 0){
                        if (zc.getName() == null || zc.getName().length() <= 0){
                            if (nameList.contains(null)){
                                nameList.remove(null);
                            }
                        }
                    }else {
                        if (name.equals(zc.getName())){
                            nameList.remove(name);
                            StepZC newZc = new StepZC();
                            newZc.setPosId(posId);
                            newZc.setName(zc.getName());
                            newZc.setProdEngnDesc(zc.getProdEngnDesc());
                            newZc.setItemCategory(zc.getItemCategory());
                            newZc.setRegMsrp(zc.getRegMsrp());
                            zcList.add(newZc);
                        }
                    }
                }
            }
            leftMatlNbr.put(posId, nameList);
            joinMatlNbr.put(posId, zcList);
        }
    }

    private Map<Integer, List<StepZC>> joinByStyleCd(Map<Integer, List<String>> leftMatlNbr,
                                                   List<StepZC> prodInfoMasterForModelling2){
        Map<Integer, List<StepZC>> result = new HashMap<>();
        for (Map.Entry<Integer, List<String>> entry : leftMatlNbr.entrySet()){
            List<StepZC> list = new ArrayList<>();
            for (StepZC zc : prodInfoMasterForModelling2){
                for (String str : entry.getValue()){
                    if (zc.getStyleCd().equals(StringUtils.left(str, 6))){
                        zc.setPosId(entry.getKey());
                        list.add(zc);
                    }
                }
            }
            result.put(entry.getKey(), list);
        }
        return result;
    }

    private List<StepZC> union(Map<Integer, List<StepZC>> joinStyleCd,
                                             Map<Integer, List<StepZC>> joinMatlNbr){
        List<StepZC> result = new ArrayList<>();
        for (int posId : joinStyleCd.keySet()){
            if (!joinStyleCd.get(posId).isEmpty()){
                result.addAll(joinStyleCd.get(posId));
            }
            if (!joinMatlNbr.get(posId).isEmpty()){
                result.addAll(joinMatlNbr.get(posId));
            }
        }
        return result;
    }
}
