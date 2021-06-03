package com.cwl.demo;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;


public class testsetCellValue {
    public static void main(String[] args) throws Exception {
        writeIntoExcel("D:\\桌面\\网络信息安全实验报告\\hh.xlsx");
    }

    public static void writeIntoExcel(String filePath ) throws Exception {
        File file = new File(filePath);
        if (!file.exists()){
            throw new Exception("文件不存在!");
        }
        InputStream in = new FileInputStream(file);

        // 读取整个Excel
        XSSFWorkbook sheets = new XSSFWorkbook(in);

        // 获取第一个表单Sheet
        XSSFSheet sheetAt = sheets.getSheetAt(0);

        sheetAt.getRow(1).createCell(6).setCellValue("是");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        sheets.write(fileOutputStream);

    }
}
