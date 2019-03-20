package com.cc5.btt.dao;

import com.cc5.btt.entity.StepZC;

import java.util.List;
import java.util.Map;

public interface StepZcDao extends BaseDao<StepZC> {

    Map<Integer, List<String>> getCaData(int userId);

    List<StepZC> getProdInfoMasterForModellingData(boolean groupByStyleCd);
}
