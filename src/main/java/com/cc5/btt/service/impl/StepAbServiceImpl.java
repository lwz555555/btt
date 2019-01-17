package com.cc5.btt.service.impl;

import com.cc5.btt.constants.BTTConstants;
import com.cc5.btt.entity.StepAB;
import com.cc5.btt.service.BaseService;
import com.cc5.btt.util.ExcelReader;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;

@Service("stepAbService")
public class StepAbServiceImpl implements BaseService<StepAB> {

    private static final Logger log = Logger.getLogger(StepAbServiceImpl.class);



    @Override
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
        Collection<String> headers = er.getHeaderMap().values();
        if (validateHeaders(headers)){
            List<Map<String, String>> rows = er.getRows();
            if (validateData(rows)){

            }
        }else {
            map.put("result","error");
            map.put("error",BTTConstants.stepAbError.get("headerError"));
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

    private boolean validateHeaders(Collection<String> headers){
        if (headers.containsAll(BTTConstants.stepAbHeaders.keySet())){
            return true;
        }else {
            return false;
        }
    }

    private boolean validateData(List<Map<String, String>> rows){
        List<StepAB> beanList = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++){
            for(Map.Entry<String, String> entry : rows.get(i).entrySet()){
                StepAB bean = new StepAB();
                for (String str : BTTConstants.stepAbHeaders.keySet()){
                    if (entry.getKey().equalsIgnoreCase(str)){
                        Class c =  bean.getClass();
                        try {
                            Field field = c.getDeclaredField(BTTConstants.stepAbHeaders.get(str));
                            field.setAccessible(true);
                            field.set(bean, entry.getValue());
                        } catch (NoSuchFieldException e) {
                            log.error(e.fillInStackTrace());
                        } catch (IllegalAccessException e) {
                            log.error(e.fillInStackTrace());
                        }
                    }
                }
            }
        }
        return true;
    }
}
