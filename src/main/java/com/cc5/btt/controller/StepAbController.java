package com.cc5.btt.controller;

import com.cc5.btt.bean.ResponseBean;
import com.cc5.btt.entity.StepAB;
import com.cc5.btt.service.BaseService;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@RestController
@RequestMapping("/stepab")
public class StepAbController {

    private static final Logger log = Logger.getLogger(StepAbController.class);

    @Resource(name = "stepAbService")
    BaseService<StepAB> stepAbService;

    //上传StepAB源文件
    public ResponseBean upload(int userId, MultipartFile file){
        InputStream is = null;
        try {
            is = file.getInputStream();
        } catch (IOException e) {
            log.error("Get InputStream Fail : " + e.fillInStackTrace());
        }
        Map<String, String> result = stepAbService.upload(userId, is);
        if (result.get("result").equals("success")){
            return new ResponseBean(true, "上传成功", null);
        }else {
            return new ResponseBean(false, "上传失败。", result.get("error"));
        }
    }

}
