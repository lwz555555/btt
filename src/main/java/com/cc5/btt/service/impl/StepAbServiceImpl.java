package com.cc5.btt.service.impl;

import com.cc5.btt.constants.BTTConstants;
import com.cc5.btt.dao.BaseDao;
import com.cc5.btt.entity.StepAB;
import com.cc5.btt.service.BaseService;
import com.cc5.btt.util.ExcelReader;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("stepAbService")
public class StepAbServiceImpl implements BaseService<StepAB> {

    private static final Logger log = Logger.getLogger(StepAbServiceImpl.class);

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Resource(name = "stepAbDao")
    private BaseDao<StepAB> stepAbDao;

    @Override
    //上传StepAB
    public Map<String, String> upload(int userId, InputStream is) {
        Map<String, String> map = new HashMap<>();
        ExcelReader er = new ExcelReader();
        try {
            er.processAllSheets(is);
        } catch (Exception e) {
            log.error("Excel Read Fail : " + e.fillInStackTrace());
            map.put("result","error");
            map.put("error","Excel文件读取错误");
            return map;
        }
        //获取表头
        Collection<String> headers = er.getHeaderMap().values();
        //验证表头
        Map<String, String> checkHeaderMap = new HashMap<>(2);
        if (checkHeaders(checkHeaderMap,headers).get("headerCheck").equals("true")){
            List<Map<String, String>> rows = er.getRows();
            //验证数据
            Map<String, String> checkDataMap = new HashMap<>(1);
            List<StepAB> strpAbList = checkData(checkDataMap,rows);
            if (strpAbList.size() == 0){
                map.put("result","error");
                map.put("error",checkDataMap.get("dataMessage"));
            }else {
                stepAbDao.upload(userId, strpAbList);
            }
        }else {
            map.put("result","error");
            map.put("error",checkHeaderMap.get("headerMessage"));
        }
        return map;
    }

    @Override
    public int delete(int userId) {
        return 0;
    }

    @Override
    public List<StepAB> getList(int userId) {
        return null;
    }

    //验证表头
    private Map<String, String> checkHeaders(Map<String, String> checkHeaderMap, Collection<String> headers){
        StringBuffer sb = new StringBuffer("字段：");
        boolean pass = true;
        for (String userHeader : headers){
            for (String definedHeader : BTTConstants.stepAbHeaders){
                if (userHeader.equalsIgnoreCase(definedHeader)){
                    continue;
                }else {
                    pass = false;
                    sb.append(definedHeader + ",");
                }
            }
        }
        if (pass){
            checkHeaderMap.put("headerCheck", "true");
        }else {
            checkHeaderMap.put("headerCheck", "false");
            checkHeaderMap.put("headerMessage", sb.append("是必需的。").toString());
        }
        return checkHeaderMap;
    }

    //验证数据
    private List<StepAB> checkData(Map<String, String> checkDataMap, List<Map<String, String>> rows){
        List<StepAB> beanList = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        StringBuffer sbColumn = new StringBuffer("字段：");
        boolean valid = true;
        for (int i = 0; i < rows.size(); i++){
            StepAB bean = new StepAB();
            boolean pass = true;
            for(Map.Entry<String, String> entry : rows.get(i).entrySet()){
                if (entry.getKey().equalsIgnoreCase(BTTConstants.stepAbHeaders.get(0))){
                    try {
                        bean.setPosId(Integer.parseInt(entry.getValue()));
                    } catch (NumberFormatException e) {
                        log.error("StepAB posId format error :" + e.fillInStackTrace());
                        pass = false;
                        sbColumn.append(entry.getKey() + "、");
                    }
                }
                if (entry.getKey().equalsIgnoreCase(BTTConstants.stepAbHeaders.get(1))){
                    bean.setProdCd(entry.getValue());
                }
                if (entry.getKey().equalsIgnoreCase(BTTConstants.stepAbHeaders.get(2))){
                    bean.setSize(entry.getValue());
                }
                if (entry.getKey().equalsIgnoreCase(BTTConstants.stepAbHeaders.get(3))){
                    try {
                        bean.setUnits(Integer.parseInt(entry.getValue()));
                    } catch (NumberFormatException e) {
                        log.error("StepAB units format error :" + e.fillInStackTrace());
                        pass = false;
                        sbColumn.append(entry.getKey() + "、");
                    }
                }
                if (entry.getKey().equalsIgnoreCase(BTTConstants.stepAbHeaders.get(4))){
                    try {
                        bean.setSales(Integer.parseInt(entry.getValue()));
                    } catch (NumberFormatException e) {
                        log.error("StepAB sales format error :" + e.fillInStackTrace());
                        pass = false;
                        sbColumn.append(entry.getKey() + "、");
                    }
                }
                if (entry.getKey().equalsIgnoreCase(BTTConstants.stepAbHeaders.get(5))){
                    try {
                        bean.setInvQty(Integer.parseInt(entry.getValue()));
                    } catch (NumberFormatException e) {
                        log.error("StepAB invQty format error :" + e.fillInStackTrace());
                        pass = false;
                        sbColumn.append(entry.getKey() + "、");
                    }
                }
                if (entry.getKey().equalsIgnoreCase(BTTConstants.stepAbHeaders.get(6))){
                    try {
                        bean.setDate(sdf.parse(entry.getValue()));
                    } catch (ParseException e) {
                        log.error("StepAB date conversion error :" + e.fillInStackTrace());
                        pass = false;
                        sbColumn.append(entry.getKey() + "、");
                    }
                }
            }
            if (pass){
                beanList.add(bean);
            }else {
                valid = false;
                sb.append("第" + (i+2) + "行");
                sb.append(StringUtils.removeEnd(sbColumn.toString(), "、"));
                sb.append("数据格式不正确。");
                break;
            }
        }
        if (valid){
            return beanList;
        }else {
            checkDataMap.put("dataMessage",sb.toString());
            return new ArrayList<>(0);
        }
    }
}
