package com.cc5.btt.service;

import com.cc5.btt.entity.StepBD;

import java.util.List;

public interface StepBdService extends BaseService<StepBD> {

    List<StepBD> getStepBdResult(int userId);

}
