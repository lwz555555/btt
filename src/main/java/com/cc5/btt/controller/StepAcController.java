package com.cc5.btt.controller;

import com.cc5.btt.service.StepAbService;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/stepac")
public class StepAcController {

    private static final Logger log = Logger.getLogger(StepAbController.class);

    @Resource(name = "stepAbService")
    StepAbService stepAbService;

}
