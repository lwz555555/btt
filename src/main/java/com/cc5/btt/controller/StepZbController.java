package com.cc5.btt.controller;


import com.cc5.btt.bean.ResponseBean;
import com.cc5.btt.service.StepZbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
@RequestMapping("/stepzb")
public class StepZbController {

    @Autowired
    private StepZbService stepZbService;

    @RequestMapping(value = "/runStepZB", method = {RequestMethod.POST})
    public ResponseBean runZaStep (int userId) {
        int ret = stepZbService.runZaStep(userId);
        if (ret == 1) {
            return new ResponseBean(true, "成功", "操作成功");
        } else {
            return new ResponseBean(false, "失败", "请稍后再试。");
        }
    }

}
