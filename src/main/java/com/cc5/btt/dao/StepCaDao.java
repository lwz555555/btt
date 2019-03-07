package com.cc5.btt.dao;

import com.cc5.btt.entity.StepBA;
import com.cc5.btt.entity.StepBD;
import com.cc5.btt.entity.StepCA;

import java.util.List;
import java.util.Map;

public interface StepCaDao extends BaseDao<StepCA>{

    Map<Integer, List<StepBD>> getBdResult1(int userId);

    Map<Integer, Map<Integer, Map<String, Integer>>> getBdResult2(int userId);

    Map<Integer, Map<String, List<StepBA>>> getGroupMap (int userId);

    Map<Integer, List<String>> getGroupNameMap (int userId);

    Map<Integer, List<String>> getHeaderMap (int userId);

    Map<Integer, Map<String, List<String>>> getExcelData (int userId);


}
