package com.cc5.btt.service.impl;

import com.cc5.btt.constants.BTTConstants;
import com.cc5.btt.dao.StepZaDao;
import com.cc5.btt.entity.CoreSize;
import com.cc5.btt.entity.ProdInfoMasterForModelling;
import com.cc5.btt.entity.StepCB;
import com.cc5.btt.entity.StepZA;
import com.cc5.btt.service.StepZaService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("stepZaService")
public class StepZaServiceImpl implements StepZaService {

    private static final Logger log = Logger.getLogger(StepAbServiceImpl.class);

    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Resource(name = "stepZaDao")
    private StepZaDao stepZaDao;

    @Override
    public int processStepZa(int userId) throws SQLException {
        List<StepZA> beanList = new ArrayList<>();
        Map<Integer, List<StepZA>> unionMapList = new HashMap<>();
        log.info(df.format(new Date()) + "-----Start Step ZA : joinExtendsimAndCb.");
        Map<Integer, List<StepZA>> extendsimAndCb = joinExtendsimAndCb(userId);
        log.info(df.format(new Date()) + "-----Start Step ZA : joinProdInfoMasterForModelling.");
        Map<Integer, Map<String, List<StepZA>>> joinProdInfo = joinProdInfoMasterForModelling(extendsimAndCb);
        log.info(df.format(new Date()) + "-----Start Step ZA : getCoreSize.");
        Map<String, List<CoreSize>> coreSizeMap = stepZaDao.getCoreSize();
        List<CoreSize> appCoreSizeList = coreSizeMap.get(BTTConstants.app);
        List<CoreSize> appNoneCoreSizeList = coreSizeMap.get(BTTConstants.others);
        log.info(df.format(new Date()) + "-----Start Step ZA : union.");
        for (Map.Entry<Integer, Map<String, List<StepZA>>> mapEntry : joinProdInfo.entrySet()){
            List<StepZA> unionList = new ArrayList<>();
            List<StepZA> appList = mapEntry.getValue().get(BTTConstants.app);
            List<StepZA> alcs = leftCoreSize(appList, appCoreSizeList);         //#2
            List<StepZA> ajcs = joinCoreSize(appList, appCoreSizeList);         //#1
            List<StepZA> ftwList = mapEntry.getValue().get(BTTConstants.ftw);
            List<StepZA> flcs = leftCoreSize(ftwList, appNoneCoreSizeList);     //#3
            List<StepZA> fjcs = joinCoreSize(ftwList, appNoneCoreSizeList);     //#4
            unionList = addAll(addAll(addAll(addAll(addAll(unionList, ajcs), alcs), fjcs), flcs), mapEntry.getValue().get(BTTConstants.others));
            unionMapList.put(mapEntry.getKey(), lastWashForList(unionList));
            beanList.addAll(unionList);
        }
        log.info(df.format(new Date()) + "-----Start Step ZA : insert.");
        if (insert(userId,beanList).get("result").equals("success")){
            return 1;
        }
        return 0;
    }

    @Override
    public Map<String, String> insert(int userId, List<StepZA> beanList) throws SQLException {
        Map<String, String> map = new HashMap<>();
        int num = stepZaDao.getNum(userId);
        if (num == 0 || num == stepZaDao.delete(userId)){
            //验证插入条数与源数据是否相等
            if (beanList.size() != stepZaDao.insert(userId, beanList)){
                log.error("Insert StepZA: Number of insert is not equals data rows.");
                throw new SQLException();
            }
            map.put("result", "success");
        }else {
            log.error("Delete StepZA: Number of delete is not equals former records.");
            throw new SQLException();
        }
        return map;
    }

    private Map<Integer, List<StepZA>> joinExtendsimAndCb(int userId){
        Map<Integer, List<StepZA>> result = new HashMap<>();
        Map<Integer, List<StepZA>> extendsimMapList = stepZaDao.getExtendsimSource(userId);
        Map<Integer, List<StepCB>> cbMapList = stepZaDao.getCbSource(userId);
        for (Map.Entry<Integer, List<StepZA>> extendsimEntry : extendsimMapList.entrySet()){
            if (cbMapList.containsKey(extendsimEntry.getKey())){
                List<StepZA> zaList = new ArrayList<>();
                for (StepZA extendsim : extendsimEntry.getValue()){
                    for (StepCB cb : cbMapList.get(extendsimEntry.getKey())){
                        if (extendsim.getDemandFct().equals(cb.getField())){
                            extendsim.setProdCd(StringUtils.replace(StringUtils.substringBefore(cb.getProdCd(), "____"), "_", "-"));
                            extendsim.setProdCd2(StringUtils.replace(StringUtils.substringAfter(cb.getProdCd(), "____"),"_","."));
                            zaList.add(extendsim);
                        }
                    }
                }
                if (zaList.size() > 0){
                    result.put(extendsimEntry.getKey(), zaList);
                }
            }
        }
        return result;
    }

    //this takes time 186 seconds in test
    private Map<Integer, Map<String, List<StepZA>>> joinProdInfoMasterForModelling(Map<Integer, List<StepZA>> joinExtendsimAndCb){
        Map<Integer, Map<String, List<StepZA>>> result = new HashMap<>();
        List<ProdInfoMasterForModelling> pimfmList = stepZaDao.getProdInfoMasterForModelling();
        for (Map.Entry<Integer, List<StepZA>> extendsimAndCb : joinExtendsimAndCb.entrySet()){
            for (StepZA za : extendsimAndCb.getValue()){
                for (ProdInfoMasterForModelling pimfm : pimfmList){
                    if (za.getProdCd().equals(pimfm.getMatlNbr())){
                        za.setProdEngnDesc(pimfm.getProdEngnDesc());
                        za.setGblCatSumLongDesc(pimfm.getGblCatSumLongDesc());
                        za.setGender(pimfm.getGender());
                        za.setItemCategory(pimfm.getItemCategory());
                        if (result.containsKey(extendsimAndCb.getKey())){
                            if (za.getProdEngnDesc().equals(BTTConstants.app)){
                                if (result.get(extendsimAndCb.getKey()).containsKey(BTTConstants.app)){
                                    result.get(extendsimAndCb.getKey()).get(BTTConstants.app).add(za);
                                }else {
                                    List<StepZA> zaList = new ArrayList<>();
                                    zaList.add(za);
                                    result.get(extendsimAndCb.getKey()).put(BTTConstants.app, zaList);
                                }
                            }else if (za.getProdEngnDesc().equals(BTTConstants.ftw)){
                                if (result.get(extendsimAndCb.getKey()).containsKey(BTTConstants.ftw)){
                                    result.get(extendsimAndCb.getKey()).get(BTTConstants.ftw).add(za);
                                }else {
                                    List<StepZA> zaList = new ArrayList<>();
                                    zaList.add(za);
                                    result.get(extendsimAndCb.getKey()).put(BTTConstants.ftw, zaList);
                                }
                            }else {
                                za.setCoreSizeVal("N");
                                if (result.get(extendsimAndCb.getKey()).containsKey(BTTConstants.others)){
                                    result.get(extendsimAndCb.getKey()).get(BTTConstants.others).add(za);
                                }else {
                                    List<StepZA> zaList = new ArrayList<>();
                                    zaList.add(za);
                                    result.get(extendsimAndCb.getKey()).put(BTTConstants.others, zaList);
                                }
                            }
                        }else {
                            List<StepZA> zaList = new ArrayList<>();
                            zaList.add(za);
                            Map<String, List<StepZA>> map = new HashMap<>(3);
                            if (za.getProdEngnDesc().equals(BTTConstants.app)){
                                map.put(BTTConstants.app, zaList);
                            }else if (za.getProdEngnDesc().equals(BTTConstants.ftw)){
                                map.put(BTTConstants.ftw, zaList);
                            }else {
                                zaList.get(zaList.size() - 1).setCoreSizeVal("N");
                                map.put(BTTConstants.others, zaList);
                            }
                            result.put(extendsimAndCb.getKey(), map);
                        }
                    }
                }
            }
        }
        return result;
    }

    private List<StepZA> leftCoreSize(List<StepZA> zaList, List<CoreSize> coreSizeList){
        List<StepZA> result = new ArrayList<>();
        for (StepZA za : zaList){
            boolean match = false;
            for (CoreSize cs : coreSizeList){
                if (za.getProdEngnDesc().equals(cs.getPe())
                        && za.getGender().equals(cs.getGender())
                        && za.getGblCatSumLongDesc().equals(cs.getCategory())
                        && za.getProdCd2().equals(cs.getCoreSize())){
                    match = true;
                    break;
                }
            }
            if (!match){
                za.setSize(za.getProdCd2());
                za.setCoreSizeVal("N");
                result.add(lastWashForBean(za));
            }
        }
        return result;
    }

    private List<StepZA> joinCoreSize(List<StepZA> appList, List<CoreSize> coreSizeList){
        List<StepZA> result = new ArrayList<>();
        for (StepZA za : appList){
            for (CoreSize cs : coreSizeList){
                if (za.getProdEngnDesc().equals(cs.getPe())
                        && za.getGender().equals(cs.getGender())
                        && za.getGblCatSumLongDesc().equals(cs.getCategory())
                        && za.getProdCd2().equals(cs.getCoreSize())){
                    za.setRegion(cs.getRegion());
                    za.setCoreSize(cs.getCoreSize());
                    za.setCoreSizeFlag(cs.getCoreSizeFlag());
                    za.setSize(za.getProdCd2());
                    za.setCoreSizeVal("Y");
                    result.add(lastWashForBean(za));
                    break;
                }
            }
        }
        return result;
    }

    private List<StepZA> addAll(List<StepZA> unionList, List<StepZA> zaList){
        if (zaList != null && zaList.size() > 0){
            unionList.addAll(zaList);
        }
        return unionList;
    }

    private StepZA lastWashForBean(StepZA za){
        //value inventoryRsp
        if (za.getDayRsp() <= 49 && za.getTotalDemandRsp() == za.getInitialInventoryRsp()){
            za.setInventoryRsp(za.getInventoryRsp() + 1);
        }
        //value instockRecordRsp
        if (za.getInventoryRsp() == 0){
            za.setInstockRecordRsp(0);
        }else {
            za.setInstockRecordRsp(1);
        }
        //value dcStock
        za.setDcStock((int)Math.round(za.getInitialInventoryRsp() / 0.6 * 0.4));
        //value dcCheckNew
        if (za.getAccReplen() == null){
            za.setAccReplen(0);
        }
        if (za.getDcStock() == null){
            za.setDcStock(0);
        }
        if (za.getAccReplen() > za.getDcStock()){
            za.setDcCheckNew(0);
        }else {
            za.setDcCheckNew(1);
        }
        return za;
    }

    private List<StepZA> lastWashForList(List<StepZA> zaList){
        for (int i = 0; i < zaList.size(); i++){
            //value accReplen
            if (zaList.get(i).getDayRsp() != 0){
                if (i > 0){
                    zaList.get(i).setAccReplen(zaList.get(i).getReplenQtyRsp() + zaList.get(i - 1).getAccReplen());
                }else {
                    zaList.get(i).setAccReplen(zaList.get(i).getReplenQtyRsp());
                }
            }else {
                zaList.get(i).setAccReplen(zaList.get(i).getReplenQtyRsp());
            }
            //value acc4weeksSales
            if (i > 27){
                zaList.get(i).setAcc4weeksSales(zaList.get(i).getAccumulatedSalesRsp() - zaList.get(i - 28).getAccumulatedSalesRsp());
            }else {
                zaList.get(i).setAcc4weeksSales(zaList.get(i).getAccumulatedSalesRsp());
            }
            //revalue acc4weeksSales
            int dayRsp = zaList.get(i).getDayRsp();
            if (dayRsp != 28 && dayRsp != 35 && dayRsp != 42 && dayRsp != 49){
                //in Alteryx the value is null
                zaList.get(i).setAcc4weeksSales(0);
            }
        }
        return zaList;
    }
}
