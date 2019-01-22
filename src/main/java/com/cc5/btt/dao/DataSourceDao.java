package com.cc5.btt.dao;

import com.cc5.btt.entity.StepAB;

import java.util.List;

public interface DataSourceDao extends BaseDao<StepAB> {

    List<Integer> getPosIdList(int userId);

    List<StepAB> getStepAbData(int userId, String posIds);
}
