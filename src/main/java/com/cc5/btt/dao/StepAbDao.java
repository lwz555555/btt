package com.cc5.btt.dao;

import com.cc5.btt.entity.StepAB;

import java.util.List;
import java.util.Map;

public interface StepAbDao extends BaseDao<StepAB> {

    List<String> getPosProdList(int userId);

    Map<String, List<StepAB>> getMapList(int userId);

}
