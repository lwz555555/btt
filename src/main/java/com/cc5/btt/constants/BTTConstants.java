package com.cc5.btt.constants;

import java.util.ArrayList;
import java.util.List;

public class BTTConstants {

    public static final List<String> stepAbHeaders = new ArrayList<>(7);

    static {
        stepAbHeaders.add("POS ID");
        stepAbHeaders.add("PROD CD");
        stepAbHeaders.add("SIZE");
        stepAbHeaders.add("UNITS");
        stepAbHeaders.add("SALES");
        stepAbHeaders.add("INV_QTY");
        stepAbHeaders.add("DATE");
    }
}
