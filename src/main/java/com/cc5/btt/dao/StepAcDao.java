package com.cc5.btt.dao;

import com.cc5.btt.entity.StepAC;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface StepAcDao extends BaseDao<StepAC>{

    Map<String, List<StepAC>> getStepAc (int userId);

    Map<String, List<StepAC>> getGroupByCodeSize (int userId);

    Map<String, List<StepAC>> getNullStepAc (int userId);


}
