package com.cc5.btt.dao.impl;

import com.cc5.btt.dao.StepZcDao;
import com.cc5.btt.entity.StepZC;
import org.apache.commons.lang.StringUtils;
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

@Repository("stepZcDao")
public class StepZcDaoImpl implements StepZcDao {

    private static final Logger log = Logger.getLogger(StepZcDaoImpl.class);

    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Map<Integer, List<String>> getCaData(int userId) {
        String sql = "SELECT DISTINCT(REPLACE(LEFT(`value`,10),\"_\",\"-\")) `value`, pos_id " +
                "FROM dim_step_ca WHERE user_id = :userId AND " +
                "`name`=\"Name\" GROUP BY pos_id, `value` ";
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("userId", userId);
        Map<Integer, List<String>> result = new HashMap<>();
        return namedParameterJdbcTemplate.query(sql, sqlParameterSource, rs -> {
            while (rs.next()){
                int posId = rs.getInt("pos_id");
                String value = StringUtils.isBlank(rs.getString("value")) ? "FileName" : rs.getString("value");
                if (result.containsKey(posId)){
                    result.get(posId).add(value);
                }else {
                    List<String> list = new ArrayList<>();
                    list.add(value);
                    result.put(posId, list);
                }
            }
            return result;
        });
    }

    @Override
    public List<StepZC> getProdInfoMasterForModellingData(boolean groupByStyleCd) {
        String sql = "SELECT matl_nbr, style_cd, prod_engn_desc, item_category, " +
                "reg_msrp FROM fct_modelling_prodinfomaster GROUP BY matl_nbr";
        if (groupByStyleCd){
            sql = "SELECT * FROM (" + sql + ") t GROUP BY style_cd";
        }
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        List<StepZC> result = new ArrayList<>();
        return namedParameterJdbcTemplate.query(sql, sqlParameterSource, rs -> {
            while (rs.next()){
                StepZC zc = new StepZC();
                zc.setName(rs.getString("matl_nbr"));
                if (groupByStyleCd){
                    zc.setStyleCd(rs.getString("style_cd"));
                }
                zc.setProdEngnDesc(rs.getString("prod_engn_desc"));
                zc.setItemCategory(rs.getString("item_category"));
                zc.setRegMsrp(rs.getInt("reg_msrp"));
                result.add(zc);
            }
            return result;
        });
    }

    @Override
    public int getNum(int userId) {
        String sql = "SELECT COUNT(1) FROM btt.dim_step_zc WHERE user_id = :userId";
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
    public int insert(int userId, List<StepZC> beanList) {
        String sql = "INSERT INTO btt.dim_step_zc(user_id, pos_id, name, " +
                "prod_engn_desc, item_category, reg_msrp, update_time) " +
                "VALUES(:userId, :posId, :name, :prodEngnDesc, :itemCategory, " +
                ":regMsrp, now())";
        Map<String, Object>[] namedParameters = new HashMap[beanList.size()];
        for (int i = 0; i < beanList.size(); i++) {
            StepZC zc = beanList.get(i);
            Map<String, Object> map = new HashMap<>();
            map.put("userId", userId);
            map.put("posId", zc.getPosId());
            map.put("name", zc.getName());
            map.put("prodEngnDesc", zc.getProdEngnDesc());
            map.put("itemCategory", zc.getItemCategory());
            map.put("regMsrp", zc.getRegMsrp());
            namedParameters[i] = map;
        }
        return namedParameterJdbcTemplate.batchUpdate(sql, namedParameters).length;
    }

    @Override
    public int delete(int userId) {
        String sql = "DELETE FROM btt.dim_step_zc WHERE user_id = :userId";
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("userId", userId);
        return namedParameterJdbcTemplate.update(sql, sqlParameterSource);
    }
}
