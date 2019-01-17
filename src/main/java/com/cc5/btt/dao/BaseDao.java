package com.cc5.btt.dao;

import java.util.List;

public interface BaseDao<T> {

    int add(int userId, List<T> beanList);

    int delete(int userId);

    List<T> getList(int userId);
}
