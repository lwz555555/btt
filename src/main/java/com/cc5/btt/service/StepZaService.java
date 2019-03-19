package com.cc5.btt.service;

import com.cc5.btt.entity.StepZA;

import java.sql.SQLException;

public interface StepZaService extends BaseService<StepZA> {

    int processStepZa(int userId) throws SQLException;

}
