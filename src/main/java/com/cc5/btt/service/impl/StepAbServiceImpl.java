package com.cc5.btt.service.impl;

import com.cc5.btt.entity.StepAB;
import com.cc5.btt.service.BaseService;
import org.apache.log4j.Logger;

import java.util.List;

public class StepAbServiceImpl implements BaseService<StepAB> {

    private static final Logger log = Logger.getLogger(StepAbServiceImpl.class);

    @Override
    public int add(int userId, List<StepAB> beanList) {
        return 0;
    }

    @Override
    public int delete(int userId) {
        return 0;
    }

    @Override
    public List<StepAB> getList(int userId) {
        return null;
    }
}
