package com.cc5.btt.dao;

import com.cc5.btt.entity.StepBD;

import java.util.List;
import java.util.Map;

public interface StepCaDao extends BaseDao{

    Map<Integer, List<StepBD>> getBdResult1(int userId);

    Map<Integer, Map<Integer, Map<String, Integer>>> getBdResult2(int userId);

}
