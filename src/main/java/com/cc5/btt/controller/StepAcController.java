package com.cc5.btt.controller;

import com.cc5.btt.bean.ResponseBean;
import com.cc5.btt.entity.StepAB;
import com.cc5.btt.service.StepAbService;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @RequestMapping("/getStepAcIn")
    public ResponseBean getStepAcIn(int userId){
        Map<String, List<StepAB>> stepAcInMap = stepAbService.getMap(userId);
        if (stepAcInMap.isEmpty()){
            return new ResponseBean(false, "获取失败", "请稍后再试。");
        }else {
            return new ResponseBean(true, "获取成功:"+stepAcInMap.size(), stepAcInMap);
        }
    }
}
