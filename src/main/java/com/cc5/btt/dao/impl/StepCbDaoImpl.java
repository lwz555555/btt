package com.cc5.btt.dao.impl;

import com.cc5.btt.dao.StepCbDao;
import com.cc5.btt.entity.StepCB;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository("stepCbDao")
public class StepCbDaoImpl implements StepCbDao {

    private static final Logger log = Logger.getLogger(StepCbDaoImpl.class);

    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    @Override
    public Map<Integer, Map<String, Map<Integer, String>>> getCaResult(int userId) {
        String sql = "SELECT record_id `name`, `name` record_id, `value`, pos_id " +
                "FROM btt.dim_step_ca WHERE user_id = :userId " +
                "AND `name` = :recordId1 OR `name` = :recordId2 " +
                "ORDER BY pos_id, record_id, `name`";
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("recordId1", "Name")
                .addValue("recordId2", "Field");
        Map<Integer, Map<String, Map<Integer, String>>> result = new HashMap<>();
        return namedParameterJdbcTemplate.query(sql, sqlParameterSource, rs -> {
            while (rs.next()){
                int posId = rs.getInt("pos_id");
                String recordId = rs.getString("record_id");
                int name = rs.getInt("name");
                String value = rs.getString("value");
                if (result.containsKey(posId)){
                    Map<String, Map<Integer, String>> map = result.get(posId);
                    if (map.containsKey(recordId)){
                        map.get(recordId).put(name, value);
                    }else {
                        Map<Integer, String> kv = new HashMap<>();
                        kv.put(name, value);
                        map.put(recordId, kv);
                    }
                }else {
                    Map<String, Map<Integer, String>> map = new LinkedHashMap<>();
                    Map<Integer, String> kv = new HashMap<>();
                    kv.put(name, value);
                    map.put(recordId, kv);
                    result.put(posId, map);
                }
            }
            return result;
        });
    }

    @Override
    public int getNum(int userId) {
        String sql = "SELECT COUNT(1) FROM btt.dim_step_cb WHERE user_id = :userId";
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("userId", userId);
        return namedParameterJdbcTemplate.query(sql, sqlParameterSource, rs -> {
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return 0;
            }
        });
    }

    @Override
    public int insert(int userId, List<StepCB> beanList) {
        String sql = "INSERT INTO btt.dim_step_cb(user_id, pos_id, record, " +
                        "field, prod_cd, tab_name, update_time) " +
                    "VALUES(:userId, :posId, :record, :field, :prodCd, :tabName, now())";
        Map<String, Object>[] namedParameters = new HashMap[beanList.size()];
        for (int i = 0; i < beanList.size(); i++) {
            StepCB bd = beanList.get(i);
            Map<String, Object> map = new HashMap<>();
            map.put("userId", userId);
            map.put("posId", bd.getPosId());
            map.put("record", bd.getRecord());
            map.put("field", bd.getField());
            map.put("prodCd", bd.getProdCd());
            map.put("tabName", bd.getTabName());
            namedParameters[i] = map;
        }
        return namedParameterJdbcTemplate.batchUpdate(sql, namedParameters).length;
    }

    @Override
    public int delete(int userId) {
        String sql = "DELETE FROM btt.dim_step_cb WHERE user_id = :userId";
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("userId", userId);
        return namedParameterJdbcTemplate.update(sql, sqlParameterSource);
    }
}
