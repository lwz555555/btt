package com.cc5.btt.controller;

import com.cc5.btt.bean.ResponseBean;
import com.cc5.btt.entity.StepAB;
import com.cc5.btt.service.DataSourceService;
import com.cc5.btt.service.StepAbService;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stepab")
public class StepAbController {

    private static final Logger log = Logger.getLogger(StepAbController.class);

    @Resource(name = "dataSourceService")
    DataSourceService dataSourceService;

    @Resource(name = "stepAbService")
    StepAbService stepAbService;

    //上传源文件
    @RequestMapping("/upload")
    public ResponseBean upload(int userId, MultipartFile file){
        InputStream is = null;
        try {
            is = file.getInputStream();
        } catch (IOException e) {
            log.error("Get InputStream Fail : " + e.fillInStackTrace());
        }
        Map<String, String> result = null;
        try {
            result = dataSourceService.insert(userId, is);
        } catch (SQLException e) {
            log.error("Step AB data upload failed!");
            return new ResponseBean(false, "上传失败。", "请稍后再试！");
        }
        if (result.get("result").equals("success")){
            List<Integer> posIdList = dataSourceService.getPosIdList(userId);
            return new ResponseBean(true, "上传成功", posIdList);
        }else {
            return new ResponseBean(false, "上传失败。", result.get("error"));
        }
    }

    //根据用户选择 pos id，生成 Step AB 结果
    @RequestMapping("/filter")
    public ResponseBean insert(int userId, String posIds){
        List<StepAB> beanList = dataSourceService.getStepAbList(userId, posIds);
        Map<String, String> result = null;
        try {
            result = stepAbService.insert(userId, beanList);
        } catch (SQLException e) {
            log.error("Step AB data insert failed!");
            return new ResponseBean(false, "根据pos_id筛选数据失败。", "请稍后再试！");
        }
        if (result.get("result").equals("success")){
            return new ResponseBean(true, "根据pos_id筛选数据成功。", null);
        }else {
            return new ResponseBean(false, "根据pos_id筛选数据失败。", "请稍后再试！");
        }
    }
}
