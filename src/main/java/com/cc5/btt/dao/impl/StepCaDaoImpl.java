package com.cc5.btt.dao.impl;

import com.cc5.btt.dao.StepCaDao;
import com.cc5.btt.entity.StepBD;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.*;

@Repository("stepCaDao")
public class StepCaDaoImpl implements StepCaDao {

    private static final Logger log = Logger.getLogger(StepAbDaoImpl.class);

    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public int getNum(int userId) {
        return 0;
    }

    @Override
    public int insert(int userId, List beanList) {
        return 0;
    }

    @Override
    public int delete(int userId) {
        return 0;
    }

    @Override
    public Map<Integer, List<StepBD>> getBdResult1(int userId) {
        String sql = "SELECT size_code, start_inv, sum_qty, " +
                "first_4weeks_sale_qty, prod_cd, pos_id " +
                "FROM btt.dim_step_bd WHERE user_id = :userId " +
                "ORDER BY pos_id, size_code";
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("userId", userId);
        Map<Integer, List<StepBD>> map = new HashMap<>();
        return namedParameterJdbcTemplate.query(sql, sqlParameterSource, rs -> {
            while (rs.next()){
                int posId = rs.getInt("pos_id");
                StepBD bd = new StepBD();
                bd.setUserId(userId);
                bd.setSizeCode(rs.getString("size_code").replaceAll("[-\\.]","_"));
                bd.setStartInv(rs.getInt("start_inv"));
                bd.setSumQty(rs.getInt("sum_qty"));
                bd.setFirst4WeeksSaleQty(rs.getInt("first_4weeks_sale_qty"));
                bd.setProdCd(rs.getString("prod_cd"));
                bd.setPosId(posId);
                if (map.containsKey(posId)){
                    map.get(posId).add(bd);
                }else {
                    List<StepBD> list = new ArrayList<>();
                    list.add(bd);
                    map.put(posId, list);
                }
            }
            return map;
        });
    }

    @Override
    public Map<Integer, Map<Integer, Map<String, Integer>>> getBdResult2(int userId) {
        String sql = "SELECT size_code, start_inv, sum_qty, pos_id " +
                "FROM btt.dim_step_bd WHERE user_id = :userId " +
                "ORDER BY pos_id, size_code";
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("userId", userId);
        Map<Integer, Map<Integer, Map<String, Integer>>> result = new HashMap<>();
        return namedParameterJdbcTemplate.query(sql, sqlParameterSource, rs -> {
            while (rs.next()){
                int posId = rs.getInt("pos_id");
                String sizeCode = rs.getString("size_code");
                int startInv = rs.getInt("start_inv");
                int sumQty = rs.getInt("sum_qty");
                if (result.containsKey(posId)){
                    Map<Integer, Map<String, Integer>> map = result.get(posId);
                    map.get(-2).put(sizeCode, sumQty);
                    map.get(-1).put(sizeCode, startInv);
                    map.get(0).put(sizeCode, 0);
                }else {
                    Map<Integer, Map<String, Integer>> map = new HashMap<>(3);
                    Map<String, Integer> map1 = new LinkedHashMap<>();
                    map1.put(sizeCode, sumQty);
                    Map<String, Integer> map2 = new LinkedHashMap<>();
                    map2.put(sizeCode, startInv);
                    Map<String, Integer> map3 = new LinkedHashMap<>();
                    map3.put(sizeCode, 0);
                    map.put(-2, map1);
                    map.put(-1, map2);
                    map.put(0, map3);
                    result.put(posId, map);
                }
            }
            return result;
        });
    }

}
