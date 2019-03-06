package com.cc5.btt.util;

import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

public final class ResponseUtil {
	
    public static void write(HttpServletResponse response, Object o) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        response.addHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = response.getWriter();
        out.println(o.toString());
        out.flush();
        out.close();
    }

    public static void writeWorkbook(Workbook workbook, String fileName, HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Pragma", "public");
        response.setHeader("Cache-Control", "max-age=30");
        response.setHeader("Content-Disposition", "attachment; filename="
                + URLEncoder.encode(fileName, "UTF-8").replace("+",	"%20"));
        try {
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
        	throw e;
		}finally {
            if (null != workbook) {
                workbook.close();
            }
        }
    }

    public static void writeExcel(HttpServletRequest request, HttpServletResponse response,
                                  String fileName, String name) throws Exception {
        String path = request.getServletContext().getRealPath("/");
        File file = new File(path + File.separator + fileName);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Pragma", "public");
        response.setHeader("Cache-Control", "max-age=30");
        response.setHeader("Content-Disposition", "attachment; filename="
                + URLEncoder.encode(name, "UTF-8").replace("+",	"%20"));
        byte[] buffer = new byte[4096];
        try (BufferedOutputStream output = new BufferedOutputStream(response.getOutputStream());
             BufferedInputStream input = new BufferedInputStream(new FileInputStream(file))) {
            int n;
            while ((n = input.read(buffer, 0, 4096)) != -1) {
                output.write(buffer, 0, n);
            }
            output.flush();
            response.flushBuffer();
        } catch (Exception e) {
            throw e;
        }
    }

}
