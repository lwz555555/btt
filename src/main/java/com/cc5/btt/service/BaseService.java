package com.cc5.btt.service;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface BaseService<T> {

    Map<String, String> upload(int userId, InputStream is) throws SQLException;

    int delete(int userId);

    List<T> getList(int userId);
}
