package com.cc5.btt.service.impl;

import com.cc5.btt.dao.StepAcDao;
import com.cc5.btt.entity.StepAC;
import com.cc5.btt.service.StepBaService;
import com.cc5.btt.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class StepBaServiceImpl implements StepBaService {

    @Autowired
    private StepAcDao stepAcDao;

    @Override
    public int runBaStep(int userId) {
        Map<String, List<StepAC>> stringListMap = stepAcDao.getStepAc(userId);
        List<StepAC> mondayList = new ArrayList<>();
        if (!stringListMap.isEmpty()) {
            for (Map.Entry<String, List<StepAC>> entry : stringListMap.entrySet()) {
                if (entry.getValue() != null) {
                    for (StepAC stepAC : entry.getValue()) {
                        String week = DateUtil.getWeek(stepAC.getDate());
                        if (week == null) {
                            return 0;
                        }
                        stepAC.setDayInWeek(week);
                        if ("Monday".equals(week)) {
                            mondayList.add(stepAC);
                        }
                    }
                }

            }
            Map<String, List<StepAC>> groupByMap = null;
            if (!mondayList.isEmpty()) {
                groupByMap = mondayList.stream()
                        .collect(Collectors.groupingBy(StepAC::getSize));
            }
            List<StepAC> minDateList = new ArrayList<>();
            if (!groupByMap.isEmpty()) {
                for (Map.Entry<String, List<StepAC>> entry : groupByMap.entrySet()) {
                    List<StepAC> mapList = entry.getValue();
                    Collections.sort(mapList, new Comparator<StepAC>() {
                        @Override
                        public int compare(StepAC o1, StepAC o2) {
                            Long o1Time = DateUtil.getLongTime(o1.getDate());
                            Long o2Time = DateUtil.getLongTime(o2.getDate());
                            if (o1Time != null && o2Time != null) {
                                if (o1Time > o2Time) {
                                    return 1;
                                }
                                if (o1Time == o2Time) {
                                    return 0;
                                }
                            }
                            return -1;
                        }
                    });
                    minDateList.add(mapList.get(0));
                }
            }


        }
        return 0;
    }
}
