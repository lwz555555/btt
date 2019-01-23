package com.cc5.btt.service;

import com.cc5.btt.entity.StepAC;

import java.util.List;
import java.util.Map;

public interface StepAcService {

    int runAcStep (int userId);

    Map<String, List<StepAC>> getStepAc (int userId);
}
