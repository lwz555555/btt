package com.cc5.btt.service.impl;

import com.cc5.btt.dao.StepCaDao;
import com.cc5.btt.service.StepCaService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("stepCaService")
public class StepCaServiceImpl implements StepCaService {

    private static final Logger log = Logger.getLogger(StepAbServiceImpl.class);

    @Resource(name = "stepCaDao")
    private StepCaDao stepCaDao;

    @Override
    public int runCaStep(int userId) {
        return 0;
    }
}
