package com.cc5.btt.service;

import java.util.List;

public interface BaseService<T> {

    int add(int userId, List<T> beanList);

    int delete(int userId);

    List<T> getList(int userId);
}
