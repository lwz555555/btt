package com.cc5.btt.controller;

import com.cc5.btt.bean.ResponseBean;
import com.cc5.btt.service.StepBdService;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.Map;

@RestController
@RequestMapping("/stepbd")
public class StepBdController {

    private static final Logger log = Logger.getLogger(StepBaController.class);

    @Resource(name = "stepBdService")
    private StepBdService stepBdService;

    @RequestMapping(value = "/runStepBD", method = {RequestMethod.POST})
    public ResponseBean runStepBd(int userId){
        Map<String, String> result;
        try {
            result = stepBdService.insert(userId, stepBdService.getStepBdResult(userId));
        } catch (SQLException e) {
            log.error("Step BD failed!");
            return new ResponseBean(false, "操作失败。", "请稍后再试！");
        }
        if (result.get("result").equals("success")){
            return new ResponseBean(true, "操作成功", null);
        }else {
            return new ResponseBean(false, "操作失败。", "请稍后再试！");
        }
    }
}
