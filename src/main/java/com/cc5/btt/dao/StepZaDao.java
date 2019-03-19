package com.cc5.btt.dao;

import com.cc5.btt.entity.CoreSize;
import com.cc5.btt.entity.ProdInfoMasterForModelling;
import com.cc5.btt.entity.StepCB;
import com.cc5.btt.entity.StepZA;

import java.util.List;
import java.util.Map;

public interface StepZaDao extends BaseDao<StepZA> {

    Map<Integer, List<StepCB>> getCbSource(int userId);

    Map<Integer, List<StepZA>> getExtendsimSource(int userId);

    List<ProdInfoMasterForModelling> getProdInfoMasterForModelling();

    Map<String, List<CoreSize>> getCoreSize();
}
