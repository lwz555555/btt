package com.cc5.btt.dao.impl;

import com.cc5.btt.dao.StepBcDao;
import com.cc5.btt.entity.StepBC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class StepBcDaoImpl implements StepBcDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public int getNum(int userId) {
        return 0;
    }

    @Override
    public int insert(int userId, List<StepBC> beanList) {
        String sql = "INSERT INTO btt.dim_step_bc (user_id, pos_id, size_code, start_inv, sum_sal_qty, " +
                "first4wks_sal_qty, update_time) " +
                "VALUES(:userId, :posId, :sizeCode, :startInv, :sumSalQty, " +
                ":sumFirst4wksSalQty, now())";
        Map<String, Object>[] namedParameters = new HashMap[beanList.size()];
        for (int i = 0; i < beanList.size(); i++) {
            StepBC bean = beanList.get(i);
            Map<String, Object> map = new HashMap<>();
            map.put("userId", userId);
            map.put("posId", bean.getPosId());
            map.put("sizeCode", bean.getSizeCode());
            map.put("startInv", bean.getStartInv());
            map.put("sumSalQty", bean.getSumSalQty());
            map.put("sumFirst4wksSalQty", bean.getSumFirst4wksSalQty());
            namedParameters[i] = map;
        }
        return namedParameterJdbcTemplate.batchUpdate(sql, namedParameters).length;
    }

    @Override
    public int delete(int userId) {
        String sql = "DELETE FROM btt.dim_step_bc WHERE user_id = :userId";
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("userId", userId);
        return namedParameterJdbcTemplate.update(sql, sqlParameterSource);
    }
}
