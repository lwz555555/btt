package com.cc5.btt.controller;

import com.cc5.btt.bean.ResponseBean;
import com.cc5.btt.service.StepBaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class StepBaController {

    @Autowired
    private StepBaService stepBaService;


    @RequestMapping(value = "/runStepAc", method = {RequestMethod.POST})
    public ResponseBean runBaStep (int userId) {
        int ret = stepBaService.runBaStep(userId);
        if (ret == 1) {
            return new ResponseBean(true, "成功", null);
        } else {
            return new ResponseBean(false, "失败", "请稍后再试。");
        }
    }
}
