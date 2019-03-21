package com.cc5.btt.dao.impl;

import com.cc5.btt.dao.StepZbDao;
import com.cc5.btt.entity.StepZA;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class StepZbDaoImpl implements StepZbDao {
    @Override
    public int getNum(int userId) {
        return 0;
    }

    @Override
    public int insert(int userId, List beanList) {
        return 0;
    }

    @Override
    public int delete(int userId) {
        return 0;
    }

    @Override
    public Map<String, List<StepZA>> getMapByPosId(int posId, int userId) {
        return null;
    }
}
