package com.cc5.btt.dao;

import java.util.List;

public interface BaseDao<T> {

    int getNum(int userId);

    int upload(int userId, List<T> rows);

    int delete(int userId);

    List<T> getList(int userId);
}
