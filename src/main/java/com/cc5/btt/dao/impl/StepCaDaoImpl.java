package com.cc5.btt.dao.impl;

import com.cc5.btt.dao.StepCaDao;
import com.cc5.btt.entity.StepBA;
import com.cc5.btt.entity.StepBD;
import com.cc5.btt.entity.StepCA;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.*;

@Repository("stepCaDao")
public class StepCaDaoImpl implements StepCaDao {

    private static final Logger log = Logger.getLogger(StepCaDaoImpl.class);

    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public int getNum(int userId) {
        return 0;
    }

    @Override
    public int insert(int userId, List<StepCA> beanList) {
        String sql = "INSERT INTO btt.dim_step_ca(user_id, record_id, `name`, `value`, " +
                "pos_id, update_time) " +
                "VALUES(:userId, :recordId, :name, :value, " +
                ":posId, now())";
        Map<String, Object>[] namedParameters = new HashMap[beanList.size()];
        for (int i = 0; i < beanList.size(); i++) {
            StepCA stepCA = beanList.get(i);
            Map<String, Object> map = new HashMap<>();
            map.put("userId", userId);
            map.put("recordId", stepCA.getRecordId());
            map.put("name", stepCA.getName());
            map.put("value", stepCA.getValue());
            map.put("posId", stepCA.getPosId());
            namedParameters[i] = map;
        }
        return namedParameterJdbcTemplate.batchUpdate(sql, namedParameters).length;
    }

    @Override
    public int delete(int userId) {
        String sql = "DELETE FROM btt.dim_step_ca WHERE user_id = :userId";
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("userId", userId);
        return namedParameterJdbcTemplate.update(sql, sqlParameterSource);
    }

    @Override
    public Map<Integer, List<StepBD>> getBdResult1(int userId) {
        String sql = "SELECT size_code, start_inv, sum_qty, " +
                "first_4weeks_sale_qty, prod_cd, pos_id " +
                "FROM btt.dim_step_bd WHERE user_id = :userId " +
                "ORDER BY LEFT(size_code, 10), RIGHT(size_code, LENGTH(size_code) - 14)";
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
                String sizeCode = rs.getString("size_code").replaceAll("[-\\.]","_");
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


    @Override
    public Map<Integer, Map<String, List<StepBA>>> getGroupMap(int userId) {
        String sql = "SELECT user_id, pos_id, rec_id, `name`, `value`, " +
                "file_name FROM dim_step_ba WHERE user_id = :userId";
        Map<Integer, Map<String, List<StepBA>>> result = new HashMap<>();
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("userId", userId);
        return namedParameterJdbcTemplate.query(sql, sqlParameterSource, rs -> {
            while (rs.next()) {
                StepBA stepBA = new StepBA();
                int posId = rs.getInt("pos_id");
                int recId = rs.getInt("rec_id");
                String key = recId + "_" + posId;
                stepBA.setPosId(posId);
                stepBA.setRecId(recId);
                stepBA.setUserId(rs.getInt("user_id"));
                String name = rs.getString("name");
                name = name.replaceAll("[-\\.]", "_");
                stepBA.setName(name);
                stepBA.setFileName(rs.getString("file_name"));
                String value = rs.getString("value");
                if (value != null) {
                    int valueInt = rs.getInt("value");
                    if (valueInt < 0) {
                        stepBA.setValue(0);
                    } else {
                        stepBA.setValue(valueInt);
                    }
                }
                if (result.containsKey(posId)) {
                    Map<String, List<StepBA>> listMap = result.get(posId);
                    if (listMap.containsKey(key)) {
                        List<StepBA> list = listMap.get(key);
                        list.add(stepBA);
                    } else {
                        List<StepBA> list = new ArrayList<>();
                        list.add(stepBA);
                        listMap.put(key, list);
                    }
                } else {
                    Map<String, List<StepBA>> listMap = new HashMap<>();
                    List<StepBA> list = new ArrayList<>();
                    list.add(stepBA);
                    listMap.put(key, list);
                    result.put(posId, listMap);
                }
            }
            return result;
        });
    }

    @Override
    public Map<Integer, List<String>> getGroupNameMap(int userId) {
        String sql = "SELECT pos_id, `name` " +
                "FROM dim_step_ba WHERE user_id = :userId GROUP BY `name`, pos_id";
        Map<Integer, List<String>> result = new HashMap<>();
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("userId", userId);
        return namedParameterJdbcTemplate.query(sql, sqlParameterSource, rs -> {
            while (rs.next()) {
                int posId = rs.getInt("pos_id");
                String name = rs.getString("name");
                name = name.replaceAll("[-\\.]", "_");
                if (result.containsKey(posId)) {
                    List<String> list = result.get(posId);
                    list.add(name);
                } else {
                    List<String> list = new ArrayList<>();
                    list.add(name);
                    result.put(posId, list);
                }
            }
            return result;
        });
    }

    @Override
    public Map<Integer, List<String>> getHeaderMap(int userId) {
        String sql = "SELECT pos_id, record_id FROM dim_step_ca WHERE user_id = :userId" +
                " GROUP BY record_id, pos_id ORDER BY record_id";
        Map<Integer, List<String>> result = new HashMap<>();
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("userId", userId);
        return namedParameterJdbcTemplate.query(sql, sqlParameterSource, rs -> {
            while (rs.next()) {
                int posId = rs.getInt("pos_id");
                int recordId = rs.getInt("record_id");
                if (result.containsKey(posId)) {
                    List<String> list = result.get(posId);
                    list.add(recordId+"");
                } else {
                    List<String> list = new ArrayList<>();
                    list.add("RecordID");
                    list.add(recordId+"");
                    result.put(posId, list);
                }
            }
            return result;
        });
    }

    @Override
    public Map<Integer, Map<String, List<String>>> getExcelData(int userId) {
        String sql = "SELECT `name`, `value`, " +
                "pos_id FROM btt.dim_step_ca WHERE user_id = :userId ORDER BY record_id";
        Map<Integer, Map<String, List<String>>> result = new HashMap<>();
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("userId", userId);
        return namedParameterJdbcTemplate.query(sql, sqlParameterSource, rs -> {
            while (rs.next()) {
                String name = rs.getString("name");
                String value = rs.getString("value");
                int posId = rs.getInt("pos_id");
                if (result.containsKey(posId)) {
                    Map<String, List<String>> map = result.get(posId);
                    if (map.containsKey(name)) {
                        List<String> list = map.get(name);
                        list.add(value);
                    } else {
                        List<String> list = new ArrayList<>();
                        list.add(value);
                        map.put(name, list);
                    }
                } else {
                    Map<String, List<String>> map = new LinkedHashMap<>();
                    List<String> list = new ArrayList<>();
                    list.add(value);
                    map.put(name, list);
                    result.put(posId, map);
                }
            }
            return result;
        });
    }
}
