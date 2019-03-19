package com.cc5.btt.dao.impl;

import com.cc5.btt.constants.BTTConstants;
import com.cc5.btt.dao.StepZaDao;
import com.cc5.btt.entity.CoreSize;
import com.cc5.btt.entity.ProdInfoMasterForModelling;
import com.cc5.btt.entity.StepCB;
import com.cc5.btt.entity.StepZA;
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

@Repository("stepZaDao")
public class StepZaDaoImpl implements StepZaDao {

    private static final Logger log = Logger.getLogger(StepAbDaoImpl.class);

    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Map<Integer, List<StepCB>> getCbSource(int userId) {
        //实际需求中"record"字段可以不用查询
        String sql = "SELECT field, prod_cd rigth_prod_cd, record, pos_id " +
                "FROM btt.dim_step_cb WHERE user_id = :userId ORDER BY prod_cd";
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("userId", userId);
        Map<Integer, List<StepCB>> result = new HashMap<>();
        return namedParameterJdbcTemplate.query(sql, sqlParameterSource, rs -> {
            while (rs.next()){
                StepCB cb = new StepCB();
                cb.setField(rs.getString("field"));
                cb.setProdCd(rs.getString("rigth_prod_cd"));
                cb.setRecord(rs.getInt("record"));
                int posId = rs.getInt("pos_id");
                cb.setPosId(posId);
                if (result.containsKey(posId)){
                    result.get(posId).add(cb);
                }else {
                    List<StepCB> cbList = new ArrayList<>();
                    cbList.add(cb);
                    result.put(posId, cbList);
                }
            }
            return result;
        });
    }

    @Override
    public Map<Integer, List<StepZA>> getExtendsimSource(int userId) {
        String sql = "SELECT pos_id, scenario_name, freq_fct, " +
                        "prod_cd demand_fct, target_days_fct, total_demand_rsp, " +
                        "initial_inventory_rsp, day_rsp, inventory_rsp, dc_check_rsp, " +
                        "instock_record_rsp, actual_sales_rsp, last_seven_days_sales, " +
                        "accumulated_sales, replen_qty_rsp, ordering_cost_rsp, " +
                        "transporting_cost_rsp, holding_cost_rsp " +
                    "FROM btt.dim_extendsim WHERE user_id = :userId";
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("userId", userId);
        Map<Integer, List<StepZA>> result = new HashMap<>();
        return namedParameterJdbcTemplate.query(sql, sqlParameterSource, rs -> {
            while (rs.next()){
                StepZA za = new StepZA();
                int posId = rs.getInt("pos_id");
                za.setPosId(posId);
                za.setScenarioName(rs.getString("scenario_name"));
                za.setFreqFct(rs.getInt("freq_fct"));
                za.setDemandFct(rs.getString("demand_fct"));
                za.setTargetDaysFct(rs.getInt("target_days_fct"));
                za.setTotalDemandRsp(rs.getInt("total_demand_rsp"));
                za.setInitialInventoryRsp(rs.getInt("initial_inventory_rsp"));
                za.setDayRsp(rs.getInt("day_rsp"));
                za.setInventoryRsp(rs.getInt("inventory_rsp"));
                za.setDcCheckRsp(rs.getInt("dc_check_rsp"));
                za.setInstockRecordRsp(rs.getInt("instock_record_rsp"));
                za.setActualSalesRsp(rs.getInt("actual_sales_rsp"));
                za.setLast7daysSalesRsp(rs.getInt("last_seven_days_sales"));
                za.setAccumulatedSalesRsp(rs.getInt("accumulated_sales"));
                za.setReplenQtyRsp(rs.getInt("replen_qty_rsp"));
                za.setOrderingCostRsp(rs.getString("ordering_cost_rsp"));
                za.setTransportingCostRsp(rs.getString("transporting_cost_rsp"));
                za.setHoldingCostRsp(rs.getString("holding_cost_rsp"));
                if (result.containsKey(posId)){
                    result.get(posId).add(za);
                }else {
                    List<StepZA> zaList = new ArrayList<>();
                    zaList.add(za);
                    result.put(posId, zaList);
                }
            }
            return result;
        });
    }

    @Override
    public List<ProdInfoMasterForModelling> getProdInfoMasterForModelling() {
        //The result by unique field "matl_nbr".
        String sql = "SELECT matl_nbr, prod_engn_desc, gbl_cat_sum_long_desc, " +
                "gender, item_category FROM btt.fct_modelling_prodinfomaster " +
                "GROUP BY matl_nbr";
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        List<ProdInfoMasterForModelling> result = new ArrayList<>();
        return namedParameterJdbcTemplate.query(sql, sqlParameterSource, rs -> {
            while (rs.next()){
                ProdInfoMasterForModelling pimfm = new ProdInfoMasterForModelling();
                pimfm.setMatlNbr(rs.getString("matl_nbr"));
                pimfm.setProdEngnDesc(rs.getString("prod_engn_desc"));
                pimfm.setGblCatSumLongDesc(rs.getString("gbl_cat_sum_long_desc"));
                pimfm.setGender(rs.getString("gender"));
                pimfm.setItemCategory(rs.getString("item_category"));
                result.add(pimfm);
            }
            return result;
        });
    }

    @Override
    public Map<String, List<CoreSize>> getCoreSize() {
        String sql = "SELECT bu, gender, category, region, core_size, core_size_flag " +
                "FROM btt.fct_core_size GROUP BY bu, gender, category, core_size";
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        Map<String, List<CoreSize>> result = new HashMap<>(2);
        return namedParameterJdbcTemplate.query(sql, sqlParameterSource, rs -> {
            while (rs.next()){
                if (StringUtils.isBlank(rs.getString("category"))){
                    continue;
                }else {
                    String pe = rs.getString("bu");
                    CoreSize coreSize = new CoreSize();
                    coreSize.setPe(pe);
                    coreSize.setGender(rs.getString("gender"));
                    coreSize.setCategory(rs.getString("category"));
                    coreSize.setRegion(rs.getString("region"));
                    coreSize.setCoreSize(rs.getString("core_size"));
                    coreSize.setCoreSizeFlag(rs.getInt("core_size_flag"));
                    if (pe.equals(BTTConstants.app)){
                        if (result.containsKey(BTTConstants.app)){
                            result.get(BTTConstants.app).add(coreSize);
                        }else {
                            List<CoreSize> coreSizeList = new ArrayList<>();
                            coreSizeList.add(coreSize);
                            result.put(BTTConstants.app, coreSizeList);
                        }
                    }else {
                        if (result.containsKey(BTTConstants.others)){
                            result.get(BTTConstants.others).add(coreSize);
                        }else {
                            List<CoreSize> coreSizeList = new ArrayList<>();
                            coreSizeList.add(coreSize);
                            result.put(BTTConstants.others, coreSizeList);
                        }
                    }
                }
            }
            return result;
        });
    }

    @Override
    public int getNum(int userId) {
        String sql = "SELECT COUNT(1) FROM btt.dim_step_za WHERE user_id = :userId";
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
    public int insert(int userId, List<StepZA> beanList) {
        String sql = "INSERT INTO btt.dim_step_za(user_id, pos_id, field_1, prod_cd, `size`, " +
                "scneario_name, freq_fct, demand_fct, target_days_fct, total_demand_rsp, " +
                "initial_inventory_rsp, day_rsp, inventory_rsp, dc_check_rsp, instock_record_rsp, " +
                "actual_sales_rsp, last_7days_sales_rsp, accumulated_sales_rsp, replen_qty_rsp, " +
                "ordering_cost_rsp, transporting_cost_rsp, holding_cost_rsp, prod_engn_desc, " +
                "core_size_val, dc_stock, acc_replen, dc_check_new, acc_4weeks_sales, woh_inv, " +
                "update_time) " +
                "VALUES(:userId, :posId, :field1, :prodCd, :size, " +
                ":scenarioName, :freqFct, :demandFct, :targetDaysFct, :totalDemandRsp, " +
                ":initialInventoryRsp, :dayRsp, :inventoryRsp, :dcCheckRsp, :instockRecordRsp, " +
                ":actualSalesRsp, :last7daysSalesRsp, :accumulatedSalesRsp, :replenQtyRsp, " +
                ":orderingCostRsp, :transportingCostRsp, :holdingCostRsp, :prodEngnDesc, " +
                ":coreSizeVal, :dcStock, :accReplen, :dcCheckNew, :acc4weeksSales, :wohInv, " +
                "now())";
        Map<String, Object>[] namedParameters = new HashMap[beanList.size()];
        for (int i = 0; i < beanList.size(); i++) {
            StepZA za = beanList.get(i);
            Map<String, Object> map = new HashMap<>();
            map.put("userId", userId);
            map.put("posId", za.getPosId());
            map.put("field1", za.getField1());
            map.put("prodCd", za.getProdCd());
            map.put("size", za.getSize());
            map.put("scenarioName", za.getScenarioName());
            map.put("freqFct", za.getFreqFct() == null ? 0 : za.getFreqFct());
            map.put("demandFct", za.getDemandFct());
            map.put("targetDaysFct", za.getTargetDaysFct() == null ? 0 : za.getTargetDaysFct());
            map.put("totalDemandRsp", za.getTotalDemandRsp() == null ? 0 : za.getTotalDemandRsp());
            map.put("initialInventoryRsp", za.getInitialInventoryRsp() == null ? 0 : za.getInitialInventoryRsp());
            map.put("dayRsp", za.getDayRsp() == null ? 0 : za.getDayRsp());
            map.put("inventoryRsp", za.getInventoryRsp() == null ? 0 : za.getInventoryRsp());
            map.put("dcCheckRsp", za.getDcCheckRsp() == null ? 0 : za.getDcCheckRsp());
            map.put("instockRecordRsp", za.getInstockRecordRsp() == null ? 0 : za.getInstockRecordRsp());
            map.put("actualSalesRsp", za.getActualSalesRsp() == null ? 0 : za.getActualSalesRsp());
            map.put("last7daysSalesRsp", za.getLast7daysSalesRsp() == null ? 0 : za.getLast7daysSalesRsp());
            map.put("accumulatedSalesRsp", za.getAccumulatedSalesRsp() == null ? 0 : za.getAccumulatedSalesRsp());
            map.put("replenQtyRsp", za.getReplenQtyRsp() == null ? 0 : za.getReplenQtyRsp());
            map.put("orderingCostRsp", za.getOrderingCostRsp());
            map.put("transportingCostRsp", za.getTransportingCostRsp());
            map.put("holdingCostRsp", za.getHoldingCostRsp());
            map.put("prodEngnDesc", za.getProdEngnDesc());
            map.put("coreSizeVal", za.getCoreSizeVal());
            map.put("dcStock", za.getDcStock() == null ? 0 : za.getDcStock());
            map.put("accReplen", za.getAccReplen() == null ? 0 : za.getAccReplen());
            map.put("dcCheckNew", za.getDcCheckNew() == null ? 0 : za.getDcCheckNew());
            map.put("acc4weeksSales", za.getAcc4weeksSales() == null ? 0 : za.getAcc4weeksSales());
            map.put("wohInv", za.getWohInv() == null ? 0 : za.getWohInv());
            namedParameters[i] = map;
        }
        return namedParameterJdbcTemplate.batchUpdate(sql, namedParameters).length;
    }

    @Override
    public int delete(int userId) {
        String sql = "DELETE FROM btt.dim_step_za WHERE user_id = :userId";
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("userId", userId);
        return namedParameterJdbcTemplate.update(sql, sqlParameterSource);
    }
}
