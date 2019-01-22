package com.cc5.btt.dao;

import java.util.List;

public interface BaseDao<T> {

    int getNum(int userId);

    int insert(int userId, List<T> beanList);

    int delete(int userId);

}
