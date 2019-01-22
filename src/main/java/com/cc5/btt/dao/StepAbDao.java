package com.cc5.btt.dao;

import com.cc5.btt.entity.StepAB;

import java.util.List;

public interface StepAbDao extends BaseDao<StepAB> {

    List<String> getPosProdList(int userId);

    List<StepAB> getbeanList(int userId, String posProd);

}
