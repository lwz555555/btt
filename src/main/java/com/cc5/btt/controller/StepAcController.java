package com.cc5.btt.controller;

import com.cc5.btt.bean.ResponseBean;
import com.cc5.btt.entity.StepAB;
import com.cc5.btt.entity.StepAC;
import com.cc5.btt.service.StepAbService;
import com.cc5.btt.service.StepAcService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stepac")
public class StepAcController {

    private static final Logger log = Logger.getLogger(StepAbController.class);

    @Resource(name = "stepAbService")
    StepAbService stepAbService;

    @Autowired
    StepAcService stepAcService;

    @RequestMapping("/getStepAcIn")
    public ResponseBean getStepAcIn(int userId){
        Map<String, List<StepAB>> stepAcInMap = stepAbService.getMap(userId);
        if (stepAcInMap.isEmpty()){
            return new ResponseBean(false, "获取失败", "请稍后再试。");
        }else {
            return new ResponseBean(true, "获取成功:"+stepAcInMap.size(), stepAcInMap);
        }
    }

    @RequestMapping(value = "/runStepAc", method = {RequestMethod.POST})
    public ResponseBean runAcStep (int userId) {
        int ret = stepAcService.runAcStep(userId);
        if (ret == 1) {
            return new ResponseBean(true, "成功", null);
        } else {
            return new ResponseBean(false, "失败", "请稍后再试。");
        }
    }

    @RequestMapping(value = "/getStepAc", method = {RequestMethod.GET})
    public ResponseBean getStepAc (int userId) {
        Map<String, List<StepAC>> result = stepAcService.getStepAc(userId);
        if (result != null) {
            return new ResponseBean(true, "成功", result);
        } else {
            return new ResponseBean(false, "失败", "请稍后再试。");
        }
    }
}
