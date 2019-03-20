package com.cc5.btt.controller;

import com.cc5.btt.bean.ResponseBean;
import com.cc5.btt.service.StepZcService;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.sql.SQLException;

@RestController
@RequestMapping("/stepzc")
public class StepZcController {

    private static final Logger log = Logger.getLogger(StepZcController.class);

    @Resource(name = "stepZcService")
    private StepZcService stepZcService;

    @RequestMapping(value = "/runStepZC", method = {RequestMethod.POST})
    public ResponseBean runStepCb(int userId){
        try {
            if (stepZcService.processStepZc(userId) == 1){
                return new ResponseBean(true, "操作成功", null);
            }
            return new ResponseBean(false, "操作失败。", "请稍后再试！");
        } catch (SQLException e) {
            e.fillInStackTrace();
            log.error("Step ZC failed!");
            return new ResponseBean(false, "操作失败。", "请稍后再试！");
        }
    }
}
