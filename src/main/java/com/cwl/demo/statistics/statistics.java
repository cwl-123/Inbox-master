package com.cwl.demo.statistics;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class statistics {
    //统计哪些人没交作业

    //班级名单文件路径
    final static String CLASS_LIST_PATH="D:\\桌面\\网络信息安全实验报告\\班级名单.xlsx";

    //实际提交文件夹路径
    final static String ACTUAL_COMMIT_FOLDER_PATH="D:\\桌面\\网络信息安全实验报告\\第一次实验";

    //缺交名单路径
    final static String MISSING_LIST_PATH="D:\\桌面\\网络信息安全实验报告\\第一次实验\\缺交名单.txt";

    //第n次作业统计excel
    final static String HOMEWORK_STATISTICS_EXCEL_PATH="D:\\桌面\\网络信息安全实验报告\\第一次实验\\第一次作业统计.xlsx";

    public static void main(String[] args) throws Exception {
        new statistics();
    }

    public statistics() throws Exception {
        //读班级名单excel，获取名单
        ArrayList<String> Namelist= readExpecialExcel(CLASS_LIST_PATH,2);

        //获取实际提交名单
        ArrayList<String> fileNameList = new ArrayList<>();
        getAllFileName(ACTUAL_COMMIT_FOLDER_PATH,fileNameList);
        System.out.println(fileNameList);

        //获取并写入缺交名单.txt
        Namelist.removeAll(fileNameList);
        String write_in = "共计"+Namelist.size()+"人未提交，名单如下："+Namelist.toString();
        writeFile(MISSING_LIST_PATH,write_in);

        //修改作业统计excel
        writeIntoExcel(HOMEWORK_STATISTICS_EXCEL_PATH,fileNameList);
    }

    /**
     * 获取某个文件夹下的所有文件夹名称！
     *
     * @param fileNameList 存放文件名称的list
     * @param path 文件夹的路径
     * @return
     */
    public static void getAllFileName(String path, ArrayList<String> fileNameList) {
        File file = new File(path);
        File[] tempList = file.listFiles();

        for (int i = 0; i < tempList.length; i++) {
            if(tempList[i].isDirectory()){
            fileNameList.add(tempList[i].getName());}
        }
    }

    /**
     * 读取excel内容
     * <p>
     * 用户模式下：
     * 弊端：对于少量的数据可以，单数对于大量的数据，会造成内存占据过大，有时候会造成内存溢出
     * 建议修改成事件模式
     */
    public static List<Map<String, String>> readExcel(String filePath) throws Exception {
        File file = new File(filePath);
        if (!file.exists()){
            throw new Exception("文件不存在!");
        }
        InputStream in = new FileInputStream(file);

        // 读取整个Excel
        XSSFWorkbook sheets = new XSSFWorkbook(in);
        // 获取第一个表单Sheet
        XSSFSheet sheetAt = sheets.getSheetAt(0);
        ArrayList<Map<String, String>> list = new ArrayList<>();

        //默认第一行为标题行，i = 0
        XSSFRow titleRow = sheetAt.getRow(0);
        // 循环获取每一行数据
        for (int i = 1; i < sheetAt.getPhysicalNumberOfRows(); i++) {
            XSSFRow row = sheetAt.getRow(i);
            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            // 读取每一格内容
            for (int index = 0; index < row.getPhysicalNumberOfCells(); index++) {
                XSSFCell titleCell = titleRow.getCell(index);
                XSSFCell cell = row.getCell(index);
                cell.setCellType(CellType.STRING);
                if (cell.getStringCellValue().equals("")) {
                    continue;
                }
                map.put(getString(titleCell), getString(cell));
            }
            if (map.isEmpty()) {
                continue;
            }
            list.add(map);
        }
        return list;
    }

    /**
     * 把单元格的内容转为字符串
     *
     * @param xssfCell 单元格
     * @return String
     */
    public static String getString(XSSFCell xssfCell) {
        if (xssfCell == null) {
            return "";
        }
        if (xssfCell.getCellTypeEnum() == CellType.NUMERIC) {
            return String.valueOf(xssfCell.getNumericCellValue());
        } else if (xssfCell.getCellTypeEnum() == CellType.BOOLEAN) {
            return String.valueOf(xssfCell.getBooleanCellValue());
        } else {
            return xssfCell.getStringCellValue();
        }
    }

    /**
     * 读取excel某列的内容到ArrayList中
     *
     * @param filePath 文件路径
     * @param column   第几列
     * @return String
     */
    public static ArrayList<String> readExpecialExcel(String filePath,int column) throws Exception {
        File file = new File(filePath);
        if (!file.exists()){
            throw new Exception("文件不存在!");
        }
        InputStream in = new FileInputStream(file);

        // 读取整个Excel
        XSSFWorkbook sheets = new XSSFWorkbook(in);
        // 获取第一个表单Sheet
        XSSFSheet sheetAt = sheets.getSheetAt(0);

        //默认第一行为标题行，i = 0
        XSSFRow titleRow = sheetAt.getRow(0);

        //结果存在cellValue里
        ArrayList<String> cellValue = new ArrayList<>();

        // 循环获取每一行数据
        for (int i = 1; i < sheetAt.getPhysicalNumberOfRows(); i++) {
            XSSFRow row = sheetAt.getRow(i);
            XSSFCell cell = row.getCell(column);
            cell.setCellType(CellType.STRING);
            if (cell.getStringCellValue().equals("")) {
                continue;
            }
            cellValue.add(getString(cell));
        }
        return cellValue;
    }

    /**
     * 把内容写入TXT文件
     */
    public static void writeFile(String filePath,String content) {
        try {
            File writeName = new File(filePath); // 绝对路径，如果没有则要建立一个新的output.txt文件
            writeName.createNewFile(); // 创建新文件,有同名的文件的话直接覆盖
            try (FileWriter writer = new FileWriter(writeName);
                 BufferedWriter out = new BufferedWriter(writer)
            ) {
                out.write(content);
                out.flush(); // 把缓存区内容压入文件
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 在excel中写入:是否提交作业一行
     */
    public static void writeIntoExcel(String filePath,ArrayList<String> fileNameList ) throws Exception {
        File file = new File(filePath);
        if (!file.exists()){
            throw new Exception("文件不存在!");
        }
        InputStream in = new FileInputStream(file);

        // 读取整个Excel
        XSSFWorkbook sheets = new XSSFWorkbook(in);

        // 获取第一个表单Sheet
        XSSFSheet sheetAt = sheets.getSheetAt(0);

        //默认第一行为标题行，i = 0
        XSSFRow titleRow = sheetAt.getRow(0);

        titleRow.getCell(4).setCellValue("hhh");
        // 循环获取每一行数据
        for (int i = 1; i < sheetAt.getPhysicalNumberOfRows(); i++) {
            XSSFRow row = sheetAt.getRow(i);
            XSSFCell cell = row.getCell(2);
            if(fileNameList.contains(cell.toString())){
                System.out.println("hh");
                row.createCell(6).setCellValue("是");
            }
            else{
                row.createCell(6).setCellValue("否");
            }
            //将excel写入
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            sheets.write(fileOutputStream);
        }
    }
}

