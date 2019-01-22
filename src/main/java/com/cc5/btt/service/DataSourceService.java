package com.cc5.btt.service;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface DataSourceService<StepAB> {

    Map<String, String> insert(int userId, InputStream is) throws SQLException;

    List<Integer> getPosIdList(int userId);

    List<StepAB> getStepAbList(int userId, String posIds);

}
