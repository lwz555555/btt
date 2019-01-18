package com.cc5.btt.constants;

import java.util.ArrayList;
import java.util.List;

public class BTTConstants {

    public static final List<String> stepAbHeaders = new ArrayList<>(7);

    static {
        stepAbHeaders.add("POS ID");
        stepAbHeaders.add("Prod Cd");
        stepAbHeaders.add("Size");
        stepAbHeaders.add("Units");
        stepAbHeaders.add("Sales");
        stepAbHeaders.add("Inv_Qty");
        stepAbHeaders.add("Date");
    }
}
