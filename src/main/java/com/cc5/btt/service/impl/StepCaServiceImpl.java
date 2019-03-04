package com.cc5.btt.service.impl;

import com.cc5.btt.dao.StepCaDao;
import com.cc5.btt.entity.StepBA;
import com.cc5.btt.service.StepCaService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service("stepCaService")
public class StepCaServiceImpl implements StepCaService {

    private static final Logger log = Logger.getLogger(StepAbServiceImpl.class);

    @Resource(name = "stepCaDao")
    private StepCaDao stepCaDao;

    @Override
    public int runCaStep(int userId) {
        //加载BB步骤的数据，BB步骤比较简单，整合到了BA步骤
        Map<Integer, Map<String, List<StepBA>>> groupMap = stepCaDao.getGroupMap(userId);
        Map<Integer, List<String>> groupNameMap = stepCaDao.getGroupNameMap(userId);
        List<StepBA> step1List = new ArrayList<>();
        for (Map.Entry<Integer, Map<String, List<StepBA>>> entry : groupMap.entrySet()) {
            int posId = entry.getKey();
            Map<String, List<StepBA>> entryMap = entry.getValue();
            for (Map.Entry<String, List<StepBA>> entry1 : entryMap.entrySet()) {
                Set<String> nameSet = new HashSet<>(groupNameMap.get(posId));
                int recId = Integer.valueOf(entry1.getKey().substring(0, 1));
                List<StepBA> list = entry1.getValue();
                step1List.addAll(list);
                for (StepBA stepBA : list) {
                    String name = stepBA.getName();
                    if (nameSet != null) {
                        nameSet.remove(name);
                    }
                }
                if (!nameSet.isEmpty()) {
                    for (String str : nameSet) {
                        StepBA stepBA = new StepBA();
                        stepBA.setRecId(recId);
                        stepBA.setPosId(posId);
                        stepBA.setUserId(userId);
                        stepBA.setName(str);
                        step1List.add(stepBA);
                    }
                }
            }
        }
        return 0;
    }
}
