package com.cc5.btt.service;

import com.cc5.btt.entity.StepZC;

import java.sql.SQLException;

public interface StepZcService extends BaseService<StepZC> {

    int processStepZc(int userId) throws SQLException;
}
