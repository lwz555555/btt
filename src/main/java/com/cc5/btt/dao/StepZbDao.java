package com.cc5.btt.dao;

import com.cc5.btt.entity.StepZA;

import java.util.List;
import java.util.Map;

public interface StepZbDao extends BaseDao{

    Map<String, List<StepZA>> getMapByPosId (int posId, int userId);
}
