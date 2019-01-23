package com.cc5.btt.dao.impl;

import com.cc5.btt.dao.StepAcDao;
import com.cc5.btt.entity.StepAB;
import com.cc5.btt.entity.StepAC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.*;


@Repository
public class StepAcDaoImpl implements StepAcDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public int getNum(int userId) {
        return 0;
    }

    @Override
    public int insert(int userId, List<StepAC> beanList) {
        String sql = "INSERT INTO btt.dim_step_ac (user_id, pos_id, prod_cd, `size`, units, sales, inv_qty, " +
                "`date`, sku_code, file_name, update_time) " +
                "VALUES(:userId, :posId, :prodCd, :size, :units, " +
                ":sales, :invQty, :date, :skuCode, :fileName, now())";
        Map<String, Object>[] namedParameters = new HashMap[beanList.size()];
        for (int i = 0; i < beanList.size(); i++) {
            StepAC bean = beanList.get(i);
            Map<String, Object> map = new HashMap<>();
            map.put("userId", userId);
            map.put("posId", bean.getPosId());
            map.put("prodCd", bean.getProdCd());
            map.put("size", bean.getSize());
            map.put("units", bean.getUnits());
            map.put("sales", bean.getSales());
            map.put("invQty", bean.getInvQty());
            map.put("date", bean.getDate());
            map.put("skuCode", bean.getSkuCode());
            map.put("fileName", bean.getFileName());
            namedParameters[i] = map;
        }
        return namedParameterJdbcTemplate.batchUpdate(sql, namedParameters).length;
    }

    @Override
    public int delete(int userId) {
        String sql = "DELETE FROM btt.dim_step_ac WHERE user_id = :userId";
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("userId", userId);
        return namedParameterJdbcTemplate.update(sql, sqlParameterSource);
    }

    @Override
    public Map<String, List<StepAC>> getStepAc(int userId) {
        String sql = "SELECT user_id, pos_id, prod_cd, `size`, units, sales, inv_qty, `date`, sku_code, file_name " +
                "FROM btt.dim_step_ac WHERE user_id = :userId";
        Map<String, List<StepAC>> result = new LinkedHashMap<>();
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("userId", userId);
        return namedParameterJdbcTemplate.query(sql, sqlParameterSource, rs -> {
            while (rs.next()) {
                StepAC bean = new StepAC();
                bean.setUserId(rs.getInt("user_id"));
                bean.setPosId(rs.getInt("pos_id"));
                String prodCd = rs.getString("prod_cd");
                bean.setProdCd(prodCd);
                String size = rs.getString("size");
                bean.setSize(size);
                bean.setUnits(rs.getInt("units"));
                bean.setSales(rs.getInt("sales"));
                bean.setInvQty(rs.getInt("inv_qty"));
                bean.setDate(rs.getString("date"));
                bean.setSkuCode(rs.getString("sku_code"));
                String key = rs.getString("file_name");
                if (result.containsKey(key)) {
                    List<StepAC> list = result.get(key);
                    list.add(bean);
                } else {
                    List<StepAC> list = new ArrayList<>();
                    list.add(bean);
                    result.put(key, list);
                }
            }
            return result;
        });
    }
}
