package com.cc5.btt.service;

import org.apache.poi.ss.usermodel.Workbook;

public interface StepCaService {

    int runCaStep (int userId);

    Workbook prepareDownload (int userId);
}
