package com.cc5.btt.dao.impl;

import com.cc5.btt.dao.StepBdDao;
import com.cc5.btt.entity.StepBD;
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

@Repository("stepBdDao")
public class StepBdDaoImpl  implements StepBdDao {

    private static final Logger log = Logger.getLogger(StepAbDaoImpl.class);

    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public int getNum(int userId) {
        String sql = "SELECT COUNT(1) FROM btt.dim_step_bd WHERE user_id = :userId";
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

    /**
     * 把BD步骤的结果插入数据库
     * @param userId 用户id
     * @param beanList StepBD对象列表
     * @return
     */
    @Override
    public int insert(int userId, List<StepBD> beanList) {
        String sql = "INSERT INTO btt.dim_step_bd(user_id, size_code, start_inv, sum_qty, " +
                    "first_4weeks_sale_qty, prod_cd, pos_id, update_time) " +
                "VALUES(:userId, :sizeCode, :startInv, :sumQty, " +
                    ":first4WeeksSaleQty, :prodCd, :posId, now())";
        Map<String, Object>[] namedParameters = new HashMap[beanList.size()];
        for (int i = 0; i < beanList.size(); i++) {
            StepBD bd = beanList.get(i);
            Map<String, Object> map = new HashMap<>();
            map.put("userId", userId);
            map.put("sizeCode", bd.getSizeCode());
            map.put("startInv", bd.getStartInv());
            map.put("sumQty", bd.getSumQty());
            map.put("first4WeeksSaleQty", bd.getFirst4WeeksSaleQty());
            map.put("prodCd", bd.getProdCd());
            map.put("posId", bd.getPosId());
            namedParameters[i] = map;
        }
        return namedParameterJdbcTemplate.batchUpdate(sql, namedParameters).length;
    }

    /**
     * 删除用户的老数据
     * @param userId 用户id
     * @return
     */
    @Override
    public int delete(int userId) {
        String sql = "DELETE FROM btt.dim_step_bd WHERE user_id = :userId";
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("userId", userId);
        return namedParameterJdbcTemplate.update(sql, sqlParameterSource);
    }

    /**
     * 获取BD步骤中最后Join之前左表数据
     * @param userId 用户id
     * @return List<StepBD>
     */
    @Override
    public Map<Integer, List<StepBD>> getLList(int userId) {
        String sql = "SELECT size_code, start_inv, sum_sal_qty sum_qty, " +
                "first4wks_sal_qty, LEFT(size_code,10) prod_cd, pos_id " +
                "FROM btt.dim_step_bc WHERE user_id = :userId";
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("userId", userId);
//        List<StepBD> bdList = new ArrayList<>();


        Map<Integer, List<StepBD>> map = new HashMap<>();


        return namedParameterJdbcTemplate.query(sql, sqlParameterSource, rs -> {
            List<Integer> posIdList = new ArrayList<>();

            while (rs.next()) {
                boolean had = true;
                Integer posId = rs.getInt("pos_id");
                if (!posIdList.contains(posId)){
                    had = false;
                    posIdList.add(posId);
                }
                String prodCd = rs.getString("prod_cd");
                StepBD bd = new StepBD();
                bd.setUserId(userId);
                bd.setSizeCode(rs.getString("size_code"));
                bd.setStartInv(rs.getInt("start_inv"));
                bd.setSumQty(rs.getInt("sum_qty"));
                bd.setFirst4WeeksSaleQty(rs.getInt("first4wks_sal_qty"));
                bd.setProdCd(prodCd);
                bd.setPosId(posId);

                if (had){
                    map.get(posId).add(bd);
                }else {
                    List<StepBD> bdList = new ArrayList<>();
                    bdList.add(bd);
                    map.put(posId, bdList);
                }

//                bdList.add(bd);
            }
//            return bdList;
            return map;
        });
    }

    /**
     * 得到BD步骤中的小步骤10(Record ID 55)的结果
     * @param userId 用户id
     * @return
     */
    @Override
    public Map<Integer, List<Map<String, Object>>> getStep10(int userId) {
        String sql = "SELECT pos_id, prod_cd, SUM(sum_sal_qty) sum_sum_qty FROM " +
                        "(SELECT pos_id, LEFT(size_code,10) prod_cd, start_inv, " +
                            "sum_sal_qty, first4wks_sal_qty " +
                        "FROM btt.dim_step_bc WHERE user_id = :userId " +
                            "AND sum_sal_qty > 0) a " +
                    "GROUP BY pos_id, prod_cd ORDER BY pos_id, sum_sum_qty DESC, prod_cd";
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("userId", userId);
        Map<Integer, List<Map<String, Object>>> result = new HashMap<>();
        return namedParameterJdbcTemplate.query(sql, sqlParameterSource, rs -> {
            while (rs.next()){
                int posId = rs.getInt("pos_id");
                Map<String, Object> row = new HashMap<>(4);
                row.put("posId", posId);
                row.put("prodCd", rs.getString("prod_cd"));
                row.put("sumSumQty", rs.getInt("sum_sum_qty"));
                if (result.containsKey(posId)){
                    row.put("recordId", result.get(posId).size() + 1);
                    result.get(posId).add(row);
                }else {
                    row.put("recordId", 1);
                    List<Map<String, Object>> list = new ArrayList<>();
                    list.add(row);
                    result.put(posId, list);
                }
            }
            return result;
        });
    }
}
