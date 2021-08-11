package com.maben.base_learn.excel.poi;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.junit.Test;

import java.io.FileInputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;

/**
 * 测试POI针对Excel的读操作
 */
public class MT002 {

    private final static String PATH = "/Users/maben/Downloads/";

    /**
     * 读取Excel2013版本
     */
    @Test
    public void testRead03() {
        //1.获取流数据
        try (final FileInputStream inputStream = new FileInputStream(PATH + "e03.xls");) {
            //2.创建一个工作簿
            final Workbook workbook = new HSSFWorkbook(inputStream);
            handleExcel(workbook);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取excel
     * @param workbook workbook
     */
    private void handleExcel(Workbook workbook) {
        if (Objects.isNull(workbook)) {
            return;
        }
        final Sheet sheetAt0 = workbook.getSheetAt(0);
        final Iterator<Row> rowIterator = sheetAt0.rowIterator();
        while (rowIterator.hasNext()) {
            final Row next = rowIterator.next();
            final int cellSize = next.getPhysicalNumberOfCells();
            for (int i = 0; i < cellSize; i++) {
                final Cell cell = next.getCell(i);
                final String str = handleCellType(cell,workbook);
                System.out.print(str + " ");
            }
            System.out.println();
        }
    }

    /**
     * 判断cell的类型
     *  注意：
     *      =SUM(E2*1,E3*1) 这种能计算出来
     *      =SUM(G4:G16)  这种是计算不出来的
     * @param cell 单元格类
     * @param workbook workbook
     */
    private String handleCellType(Cell cell, Workbook workbook) {
        if (Objects.isNull(cell)) {
            return "";
        }

        switch (cell.getCellTypeEnum()) {
            case STRING:
                //字符串
                return cell.getStringCellValue();
            case BOOLEAN:
                //布尔类型
                return String.valueOf(cell.getBooleanCellValue());
            case BLANK:
                //空
                return "";
            case NUMERIC:
                //数字（分日期、普通数字）
                if (HSSFDateUtil.isCellDateFormatted(cell)){
                    //日期数字
                    final Date date = cell.getDateCellValue();
                    return new DateTime(date).toString("yyyy-MM-dd HH:mm:ss");
                }else {
                    //纯数字,防止数字太长操作
                    cell.setCellType(CellType.STRING);
                    return cell.toString();
                }
            case FORMULA:
                final String cellFormula = cell.getCellFormula();
                System.out.print("公式："+cellFormula);
                FormulaEvaluator eval=null;
                if(workbook instanceof HSSFWorkbook) {
                    eval = new HSSFFormulaEvaluator((HSSFWorkbook) workbook);
                }else if(workbook instanceof XSSFWorkbook) {
                    eval = new XSSFFormulaEvaluator((XSSFWorkbook) workbook);
                }
                final CellValue evaluate = eval.evaluate(cell);
                return evaluate.getNumberValue()+"";
            case ERROR:
                //数据类型错误
                return "";
            default:
                return "";
        }
    }

    /**
     * 读取Excel2017版本
     */
    @Test
    public void testRead07() {
        //1.获取流数据
        try (final FileInputStream inputStream = new FileInputStream(PATH + "e07.xlsx");) {
            //2.创建一个工作簿
            final Workbook workbook = new XSSFWorkbook(inputStream);
            handleExcel(workbook);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
