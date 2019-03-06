package com.cc5.btt.dao;

import com.cc5.btt.entity.StepCB;

import java.util.Map;

public interface StepCbDao extends BaseDao<StepCB> {

    Map<Integer, Map<String, Map<Integer, String>>> getCaResult(int userId);
}
