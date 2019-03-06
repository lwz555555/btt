package com.cc5.btt.service;

import com.cc5.btt.entity.StepCB;

import java.sql.SQLException;

public interface StepCbService extends BaseService<StepCB> {

    int runStepCb(int userId) throws SQLException;
}
