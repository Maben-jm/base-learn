package com.maben.base_learn.excel.poi;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.junit.Test;

import java.io.FileOutputStream;

/**
 * 测试POI的写功能
 */
public class MT001 {

    private final static String PATH = "/Users/maben/Downloads/";

    /**
     * 测试针对03EXCEL的写入操作
     */
    @Test
    public void testWrite03() {
        //        1.创建一个工作簿
        final Workbook workbook = new HSSFWorkbook();
        //        2.创建一个工作表
        final Sheet sheet = workbook.createSheet();
        for (int i = 0; i < 10; i++) {
            //        3.创建一行
            final Row row = sheet.createRow(i);
            for (int j = 0; j < 10; j++) {
                //        4.创建一个单元格
                final Cell cell = row.createCell(j);
                if (j % 3 == 0) {
                    cell.setCellValue(new DateTime().toString("yyyy-MM-dd HH:mm:ss"));
                } else {
                    cell.setCellValue("I-" + i + "-J-" + j);
                }
            }
        }
        //        5.使用输出流将工作簿输出到一张表中
        try (final FileOutputStream outputStream = new FileOutputStream(PATH + new DateTime().toString("yyyy-MM-dd HH:mm:ss") + ".xls");) {
            workbook.write(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 测试针对07EXCEL的写入操作
     */
    @Test
    public void testWrite07() {
        //        1.创建一个工作簿
        final Workbook workbook = new XSSFWorkbook();
        //        2.创建一个工作表
        final Sheet sheet = workbook.createSheet();
        for (int i = 0; i < 20; i++) {
            //        3.创建一行
            final Row row = sheet.createRow(i);
            for (int j = 0; j < 7; j++) {
                //        4.创建一个单元格
                final Cell cell = row.createCell(j);
                if (j % 2 == 0) {
                    cell.setCellValue(new DateTime().toString("yyyy-MM-dd"));
                } else {
                    cell.setCellValue("I-" + i + "-J-" + j);
                }
            }
        }
        //        5.使用输出流将工作簿输出到一张表中
        try (final FileOutputStream outputStream = new FileOutputStream(PATH + new DateTime().toString("yyyy-MM-dd HH:mm:ss") + ".xlsx");) {
            workbook.write(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 测试针对03EXCEL的大数据写书操作测试
     *  大概2s左右
     */
    @Test
    public void testWrite03BigData() {
        final long start = System.currentTimeMillis();
        //        1.创建一个工作簿
        final Workbook workbook = new HSSFWorkbook();
        //        2.创建一个工作表
        final Sheet sheet = workbook.createSheet();
        for (int i = 0; i < 65536; i++) {
            //        3.创建一行
            final Row row = sheet.createRow(i);
            for (int j = 0; j < 9; j++) {
                //        4.创建一个单元格
                final Cell cell = row.createCell(j);
                if (j % 5 == 0) {
                    cell.setCellValue(new DateTime().toString("yyyy-MM-dd HH:mm:ss"));
                } else {
                    cell.setCellValue("I-" + i + "-J-" + j);
                }
            }
        }
        //        5.使用输出流将工作簿输出到一张表中
        try (final FileOutputStream outputStream = new FileOutputStream(PATH + new DateTime().toString("yyyy-MM-dd HH:mm:ss") + ".xls");) {
            workbook.write(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final long end = System.currentTimeMillis();
        System.out.println("执行时间：" + (end - start));
    }

    /**
     * 测试针对07EXCEL的大数据量写入操作
     *  大概10s左右
     */
    @Test
    public void testWrite07BigData() {
        final long start = System.currentTimeMillis();
        //        1.创建一个工作簿
        final Workbook workbook = new XSSFWorkbook();
        //        2.创建一个工作表
        final Sheet sheet = workbook.createSheet();
        for (int i = 0; i < 65537; i++) {
            //        3.创建一行
            final Row row = sheet.createRow(i);
            for (int j = 0; j < 9; j++) {
                //        4.创建一个单元格
                final Cell cell = row.createCell(j);
                if (j % 4 == 0) {
                    cell.setCellValue(new DateTime().toString("yyyy-MM-dd"));
                } else {
                    cell.setCellValue("I-" + i + "-J-" + j);
                }
            }
        }
        //        5.使用输出流将工作簿输出到一张表中
        try (final FileOutputStream outputStream = new FileOutputStream(PATH + new DateTime().toString("yyyy-MM-dd HH:mm:ss") + ".xlsx");) {
            workbook.write(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final long end = System.currentTimeMillis();
        System.out.println("使用时间：" + (end - start));
    }

    /**
     * 测试针对07EXCEL的大数据量写入操作s
     *  大概3.5s左右
     */
    @Test
    public void testWrite07BigDataS() {
        final long start = System.currentTimeMillis();
        //        1.创建一个工作簿
        final Workbook workbook = new SXSSFWorkbook();
        //        2.创建一个工作表
        final Sheet sheet = workbook.createSheet();
        for (int i = 0; i < 65536; i++) {
            //        3.创建一行
            final Row row = sheet.createRow(i);
            for (int j = 0; j < 8; j++) {
                //        4.创建一个单元格
                final Cell cell = row.createCell(j);
                if (j % 6 == 0) {
                    cell.setCellValue(new DateTime().toString("yyyy-MM-dd"));
                } else {
                    cell.setCellValue("I-" + i + "-J-" + j);
                }
            }
        }
        //        5.使用输出流将工作簿输出到一张表中
        try (final FileOutputStream outputStream = new FileOutputStream(PATH + new DateTime().toString("yyyy-MM-dd HH:mm:ss") + "s.xlsx");) {
            workbook.write(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //        6.需要清除临时文件
        ((SXSSFWorkbook) workbook).dispose();
        final long end = System.currentTimeMillis();
        System.out.println("使用时间：" + (end - start));
    }
}
