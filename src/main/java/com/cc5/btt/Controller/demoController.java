package com.cc5.btt.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class demoController {

    @RequestMapping("/get")
    public String demo () {
        return "Hello World!";
    }

}
