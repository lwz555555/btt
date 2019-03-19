package com.cc5.btt.controller;

import com.cc5.btt.bean.ResponseBean;
import com.cc5.btt.service.StepZaService;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.sql.SQLException;

@RestController
@RequestMapping("/stepza")
public class StepZaController {

    private static final Logger log = Logger.getLogger(StepBaController.class);

    @Resource(name = "stepZaService")
    private StepZaService stepZaService;

    @RequestMapping(value = "/runStepZA", method = {RequestMethod.POST})
    public ResponseBean runStepCb(int userId){
        try {
            if (stepZaService.processStepZa(userId) == 1){
                return new ResponseBean(true, "操作成功", null);
            }
            return new ResponseBean(false, "操作失败。", "请稍后再试！");
        } catch (SQLException e) {
            e.fillInStackTrace();
            log.error("Step ZA failed!");
            return new ResponseBean(false, "操作失败。", "请稍后再试！");
        }
    }
}
