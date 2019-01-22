package com.cc5.btt.dao.impl;

import com.cc5.btt.dao.DataSourceDao;
import com.cc5.btt.entity.StepAB;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("dataSourceDao")
public class DataSourceDaoImpl implements DataSourceDao {

    private static final Logger log = Logger.getLogger(StepAbDaoImpl.class);

    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public int getNum(int userId) {
        String sql = "SELECT COUNT(1) FROM btt.dim_data_source WHERE user_id = :userId";
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
    public int insert(int userId, List<StepAB> beanList) {
        String sql = "INSERT INTO btt.dim_data_source(user_id, pos_id, prod_cd, `size`, units, sales, inv_qty, `date`, update_time) " +
                "VALUES(:userId, :posId, :prodCd, :size, :units, :sales, :invQty, :date, now())";
        Map<String, Object>[] namedParameters = new HashMap[beanList.size()];
        for (int i = 0; i < beanList.size(); i++) {
            StepAB bean = beanList.get(i);
            Map<String, Object> map = new HashMap<>();
            map.put("userId", userId);
            map.put("posId", bean.getPosId());
            map.put("prodCd", bean.getProdCd());
            map.put("size", bean.getSize());
            map.put("units", bean.getUnits());
            map.put("sales", bean.getSales());
            map.put("invQty", bean.getInvQty());
            map.put("date", bean.getDate());
            namedParameters[i] = map;
        }
        return namedParameterJdbcTemplate.batchUpdate(sql, namedParameters).length;
    }

    @Override
    public int delete(int userId) {
        String sql = "DELETE FROM btt.dim_data_source WHERE user_id = :userId";
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("userId", userId);
        return namedParameterJdbcTemplate.update(sql, sqlParameterSource);
    }


    @Override
    public List<Integer> getPosIdList(int userId) {
        String sql = "SELECT distinct pos_id FROM btt.dim_data_source WHERE user_id = :userId";
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("userId", userId);
        List<Integer> posIdList = new ArrayList<>();
        return namedParameterJdbcTemplate.query(sql, sqlParameterSource, rs -> {
            while (rs.next()) {
                posIdList.add(rs.getInt("pos_id"));
            }
            return posIdList;
        });
    }

    @Override
    public List<StepAB> getStepAbData(int userId, String posIds) {
        String sql = "SELECT user_id, pos_id, prod_cd, `size`, units, sales, inv_qty, `date` " +
                "FROM btt.dim_data_source WHERE user_id = :userId AND pos_id in" + posIds;
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("userId", userId);
        List<StepAB> beanList = new ArrayList<>();
        return namedParameterJdbcTemplate.query(sql, sqlParameterSource, rs -> {
            while (rs.next()) {
                StepAB bean = new StepAB();
                bean.setUserId(rs.getInt("user_id"));
                bean.setPosId(rs.getInt("pos_id"));
                bean.setProdCd(rs.getString("prod_cd"));
                bean.setSize(rs.getString("size"));
                bean.setUnits(rs.getInt("units"));
                bean.setSales(rs.getInt("sales"));
                bean.setInvQty(rs.getInt("inv_qty"));
                bean.setDate(rs.getString("date"));
                bean.setPosProd(bean.getPosId() + "_" + bean.getProdCd());
                beanList.add(bean);
            }
            return beanList;
        });
    }
}
