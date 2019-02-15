package com.cc5.btt.dao.impl;

import com.cc5.btt.dao.StepBaDao;
import com.cc5.btt.entity.StepBA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class StepBaDaoImpl implements StepBaDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    @Override
    public int getNum(int userId) {
        return 0;
    }

    @Override
    public int insert(int userId, List<StepBA> beanList) {
        String sql = "INSERT INTO btt.dim_step_ba (user_id, pos_id, rec_id, `name`, `value`, " +
                "file_name, update_time) " +
                "VALUES(:userId, :posId, :recId, :name, :value, " +
                ":fileName, now())";
        Map<String, Object>[] namedParameters = new HashMap[beanList.size()];
        for (int i = 0; i < beanList.size(); i++) {
            StepBA bean = beanList.get(i);
            Map<String, Object> map = new HashMap<>();
            map.put("userId", userId);
            map.put("posId", bean.getPosId());
            map.put("recId", bean.getRecId());
            map.put("name", bean.getName());
            map.put("value", bean.getValue());
            map.put("fileName", bean.getFileName());
            namedParameters[i] = map;
        }
        return namedParameterJdbcTemplate.batchUpdate(sql, namedParameters).length;
    }

    @Override
    public int delete(int userId) {
        String sql = "DELETE FROM btt.dim_step_ba WHERE user_id = :userId";
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("userId", userId);
        return namedParameterJdbcTemplate.update(sql, sqlParameterSource);
    }
}
