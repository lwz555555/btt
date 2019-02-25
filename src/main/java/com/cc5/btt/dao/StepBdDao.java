package com.cc5.btt.dao;

import com.cc5.btt.entity.StepBD;

import java.util.List;
import java.util.Map;

public interface StepBdDao extends BaseDao<StepBD> {

    List<StepBD> getBdResult(int userId, List<String> prodCdList);

    List<Map<String, Object>> getStep10(int userId);
}
