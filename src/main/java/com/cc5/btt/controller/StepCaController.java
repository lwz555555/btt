package com.cc5.btt.controller;


import com.cc5.btt.bean.ResponseBean;
import com.cc5.btt.service.StepCaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stepca")
public class StepCaController {

    @Autowired
    private StepCaService stepCaService;


    @RequestMapping(value = "/runStepCa", method = {RequestMethod.POST})
    public ResponseBean runBaStep (int userId) {
        int ret = stepCaService.runCaStep(userId);
        if (ret == 1) {
            return new ResponseBean(true, "成功", "操作成功");
        } else {
            return new ResponseBean(false, "失败", "请稍后再试。");
        }
    }

}
