package com.cc5.btt.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;



public class ExcelWriter {
	
    public static final Map<Class<?>, Map<String, Method>> CACHE = new ConcurrentHashMap<>();


    public static Workbook writeStepACMap (Map<Integer, Map<String, List<String>>> dataMap,
                                      Map<Integer, List<String>> headerMap) {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        for (Map.Entry<Integer, List<String>> entry : headerMap.entrySet()) {
            int podId = entry.getKey();
            SXSSFSheet sheet = workbook.createSheet(podId+"");
            SXSSFRow header = sheet.createRow(0);
            int i = 0;
            for (String str : entry.getValue()) {
                header.createCell(i++).setCellValue(str);
            }
            i = 1;
            Map<String, List<String>> map = dataMap.get(podId);
            for (Map.Entry<String, List<String>> entry1 : map.entrySet()) {
                SXSSFRow body = sheet.createRow(i++);
                int j = 0;
                body.createCell(j).setCellValue(entry1.getKey());
                j++;
                if (entry1.getValue() != null) {
                    for (String string : entry1.getValue()) {
                        body.createCell(j++).setCellValue(string == null ? "" : string);
                    }
                }
            }
        }
        return workbook;
    }



    public static Workbook writeMap(List<Map<String, String>> rows, Map<String, String> headerMap) {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        SXSSFSheet sheet = workbook.createSheet();
        SXSSFRow header = sheet.createRow(0);
        int i = 0;
        for (String head : headerMap.keySet()) {
            header.createCell(i++).setCellValue(head);
        }
        i = 1;
        for (Map<String, String> row : rows) {
            SXSSFRow body = sheet.createRow(i++);
            int j = 0;
            for (String key : headerMap.values()) {
                body.createCell(j++).setCellValue(row.get(key) == null ? "" : row.get(key));
            }
        }
        return workbook;
    }

    public static Workbook writeMap(List<Map<Integer, String>> rows){
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        SXSSFSheet sheet = workbook.createSheet();
        int i = 0;
        for (Map<Integer, String> row : rows) {
            SXSSFRow body = sheet.createRow(i++);
            for (int j = 0; j < row.size(); j++) {
                body.createCell(j).setCellValue(row.get(j) == null ? "" : row.get(j));
            }
        }
        return workbook;
    }



    private static void writeTitle(String[] title, SXSSFSheet sheet, int j, CellStyle cellStyle) {
        SXSSFRow titleRow = getRow(j, sheet);
        for (int i = 0; i < title.length; i++) {
            if (i + 1 == title.length) {
                break;
            }
            SXSSFCell cell = getCell(i+1,  titleRow);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(title[i]);
        }
//        Stream.of(title)
//                .forEach(s -> {
//                    Cell cell = getCell(titleRow, s.);
//                    cell.setCellValue(s);
//                });
    }

    private static void writeVerticalTitle(String[] title, SXSSFSheet sheet, CellStyle cellStyle) {
        int line = 3;
        for (int i = 0; i < title.length; i++) {
            if (i == 0) {
                line = 3;
            }
            if (i == 1) {
                line = 5;
            }
            SXSSFRow row = getRow(line, sheet);
            SXSSFCell cell = getCell(0, row);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(title[i]);
        }
    }

    private static void writeData(SXSSFSheet xssfSheet,
                                  String[] fwData, String[] appData, CellStyle cellStyle) {
        SXSSFRow row = getRow(3, xssfSheet);
        for (int i = 0; i < fwData.length; i++) {
            int index = i + 1;
            if (index == fwData.length) {
                break;
            }
            SXSSFCell cell = getCell(index, row);
            cell.setCellStyle(cellStyle);
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(Integer.valueOf(fwData[i]));
        }
        SXSSFRow row1 = getRow(5, xssfSheet);
        for (int j = 0; j < appData.length; j++) {
            int index = j + 1;
            if (index == appData.length) {
                 break;
            }
            SXSSFCell cell = getCell(index, row1);
            cell.setCellStyle(cellStyle);
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(Integer.valueOf(appData[j]));
        }
    }

    private static SXSSFCell getCell(int index, SXSSFRow row) {
        SXSSFCell cell = row.getCell(index);
        if (cell == null) cell = row.createCell(index);
        return cell;
    }

    private static SXSSFRow getRow(int line, SXSSFSheet sheet) {
        SXSSFRow row = sheet.getRow(line);
        if (row == null) row = sheet.createRow(line);
        return row;
    }


    private static void handleDate (List<Map<Integer, String>> fwrOw, SXSSFSheet sheet1) {
        Set<String> monthSet = new HashSet<>();
        int i = 1;
        int k = 0;
        int a = 0;
        if (fwrOw != null) {
            for (Map<Integer, String> row : fwrOw) {
                SXSSFRow body = sheet1.createRow(i++);
                String month = row.get(0);
                if (!monthSet.contains(month)) {
                    monthSet.add(month);
                }
                if (monthSet == null || monthSet.size() <= 4) {
                    k = 2;
                    a = 31;
                }
                if (monthSet.size() >= 5 && monthSet.size() <= 7) {
                    k = 5;
                    a = 32;
                }
                if (monthSet.size() >= 8 && monthSet.size() <= 10) {
                    k = 8;
                    a = 33;
                }
                if (monthSet.size() >= 11 && monthSet.size() <= 13) {
                    k = 11;
                    a = 34;
                }
                if (monthSet.size() >= 14 && monthSet.size() <= 16) {
                    k = 14;
                    a = 35;
                }
                if (monthSet.size() >= 17 && monthSet.size() <= 19) {
                    k = 17;
                    a = 36;
                }
                if (monthSet.size() >= 20 && monthSet.size() <= 22) {
                    k = 20;
                    a = 37;
                }
                if (monthSet.size() >= 23 && monthSet.size() <= 25) {
                    k = 23;
                    a = 38;
                }
                if (monthSet.size() >= 26 && monthSet.size() <= 27) {
                    k = 26;
                    a = 39;
                }
                for (int j = 0; j < row.size(); j++) {

                    if (j <= 1) {
                        body.createCell(j).setCellValue(row.get(j) == null ? "" : row.get(j));
                    }
                    body.createCell(29).setCellValue(row.get(11) == null ? "" : row.get(11));
                    body.createCell(30).setCellValue(row.get(12) == null ? "" : row.get(12));
                    if (j >= 2 && j <= 10) {
                        if (k == 30) {
                            continue;
                        }
                        body.createCell(k).setCellValue(row.get(j) == null ? "" : row.get(j));
                        k++;
                    }
                    if (j >= 13 && j <= 15) {
                        body.createCell(a).setCellValue(row.get(j) == null ? "" : row.get(j));
                        a++;
                    }
                }
            }
        }
    }





    /**
     * Filling data by using java reflect.
     *
     * @param rows      data need to be filled in excel
     * @param headerMap a title map with excel title and bean field
     * @param clazz     bean class to get filed
     * @param <T>       generic type
     * @return an excel workbook filled with MIQ data
     * @throws Exception some excel processing exceptions
     */
    public static <T> Workbook writeBean(List<T> rows, Map<String, String> headerMap, Class<T> clazz) throws Exception {
        Map<String, Method> methodsMap = CACHE.computeIfAbsent(clazz, k -> new HashMap<>());
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        SXSSFSheet sheet = workbook.createSheet();
        SXSSFRow header = sheet.createRow(0);
        int i = 0;
        for (String head : headerMap.keySet()) {
            header.createCell(i++).setCellValue(head);
        }
        i = 1;
        for (T row : rows) {
            SXSSFRow body = sheet.createRow(i++);
            int j = 0;
            for (String key : headerMap.values()) {
                Method method = methodsMap.get(key);
                if (null == method) {
                    method = clazz.getMethod("get" + Character.toUpperCase(key.charAt(0)) + key.substring(1));
                    methodsMap.put(key, method);
                }
                Object value = method.invoke(row);
                if (value instanceof Double) {
                    SXSSFCell cell = body.createCell(j++);
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue((Double) value);
                } else if (value instanceof Integer) {
                    SXSSFCell cell = body.createCell(j++);
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue((Integer) value);
                } else {
                    SXSSFCell cell = body.createCell(j++);
                    cell.setCellType(CellType.STRING);
                    cell.setCellValue(value == null ? "" : String.valueOf(value));
                }
            }
        }
        return workbook;
    }

}
