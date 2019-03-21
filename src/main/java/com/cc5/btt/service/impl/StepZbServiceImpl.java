package com.cc5.btt.service.impl;

import com.cc5.btt.dao.StepZbDao;
import com.cc5.btt.entity.StepZA;
import com.cc5.btt.service.StepZbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class StepZbServiceImpl implements StepZbService {

    @Autowired
    private StepZbDao stepZbDao;


    @Override
    public int runZaStep(int userId) {


        return 0;
    }



    private List<StepZA> getStepZbPart1 (int posId, int userId) {
        Map<String, List<StepZA>> listMap = stepZbDao.getMapByPosId (posId, userId);
        return null;
    }
}
