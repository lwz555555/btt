package com.cc5.btt.controller;


import com.cc5.btt.bean.ResponseBean;
import com.cc5.btt.service.StepCaService;
import com.cc5.btt.util.ResponseUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/stepca")
public class StepCaController {

    @Resource(name = "stepCaService")
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

    @RequestMapping(value = "/download", method = {RequestMethod.POST})
    public void downloadStepAc(HttpServletResponse response, int userId){
        try {
            ResponseUtil.writeWorkbook(stepCaService.prepareDownload(userId),
                    "StepAC_Data.xlsx", response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
