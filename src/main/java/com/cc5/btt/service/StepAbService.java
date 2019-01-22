package com.cc5.btt.service;

import com.cc5.btt.entity.StepAB;

import java.util.List;
import java.util.Map;

public interface StepAbService extends BaseService<StepAB> {

    Map<String, List<StepAB>> getMap(int userId);

}
