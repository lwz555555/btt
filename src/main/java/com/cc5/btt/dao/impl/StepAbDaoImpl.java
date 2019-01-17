package com.cc5.btt.dao.impl;

import com.cc5.btt.dao.BaseDao;
import com.cc5.btt.entity.StepAB;
import org.apache.log4j.Logger;

import java.util.List;

public class StepAbDaoImpl implements BaseDao<StepAB> {

    private static final Logger log = Logger.getLogger(StepAbDaoImpl.class);

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
