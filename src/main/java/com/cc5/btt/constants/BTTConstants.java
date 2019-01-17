package com.cc5.btt.constants;

import java.util.HashMap;
import java.util.Map;

public class BTTConstants {

    public static final Map<String, String> stepAbError = new HashMap<>();

    public static final Map<String, String> stepAbHeaders = new HashMap<>(7);

    static {
        stepAbError.put("headerError", "必须包含字段:POS ID、Prod Cd、Size、Units、Sales、Inv_Qty、Date。");

        stepAbHeaders.put("POS ID", "posId");
        stepAbHeaders.put("Prod Cd", "prodCd");
        stepAbHeaders.put("Size", "size");
        stepAbHeaders.put("Units", "units");
        stepAbHeaders.put("Sales", "sales");
        stepAbHeaders.put("Inv_Qty", "invQty");
        stepAbHeaders.put("Date", "date");
    }
}
