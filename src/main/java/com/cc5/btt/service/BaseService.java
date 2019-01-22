package com.cc5.btt.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface BaseService<T> {

    Map<String, String> insert(int userId, List<T> beanList) throws SQLException;

}
