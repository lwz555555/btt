package com.cc5.btt.controller;


import com.cc5.btt.bean.ResponseBean;
import com.cc5.btt.service.StepBcService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stepbc")
public class StepBcController {

    private static final Logger log = Logger.getLogger(StepBcController.class);

    @Autowired
    private StepBcService stepBcService;


    @RequestMapping(value = "/runStepBc", method = {RequestMethod.POST})
    public ResponseBean runBaStep (int userId) {
        int ret = stepBcService.runBcStep(userId);
        if (ret == 1) {
            return new ResponseBean(true, "成功", "操作成功");
        } else {
            return new ResponseBean(false, "失败", "请稍后再试。");
        }
    }

}
