# 简介

> 这里主要讲解的API是Apache的POI和阿里的easyExcel

# POI

## 官网

https://poi.apache.org/

## 简介

HSSF - 提供读写Microsoft Excel格式档案的功能；（俗称03版，最多放65535行）

XSSF - 提供读写Microsoft Excel OOXML格式档案的功能；（促成07版，行数无限制）

HSLF - 提供读写Microsoft PowerPoint格式档案的功能；

HDGF - 提供读写Microsoft Visio格式档案的功能；

## maven依赖

```xml
<dependency>
  <groupId>org.apache.poi</groupId>
  <artifactId>poi</artifactId>
  <version>3.17</version>
</dependency>
<dependency>
  <groupId>org.apache.poi</groupId>
  <artifactId>poi-ooxml</artifactId>
  <version>3.17</version>
</dependency>
<dependency>
  <groupId>joda-time</groupId>
  <artifactId>joda-time</artifactId>
  <version>2.10.1</version>
</dependency>
```

## 基础写操作

### HSSF（03）学习

```java
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
```

### XSSF（07）学习

```java
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
```

## 大数量写入学习

### HSSF （03）学习

#### 缺点

> 最多只能写入65535行，超出的话会抛异常（java.lang.IllegalArgumentException）

#### 优点

> 在添加过程中，先写入缓存，最后一次性写入磁盘，IO操作少，速度快

#### 代码展示

```java
		/**
     * 测试针对03EXCEL的大数据写书操作测试
     * 大概2s左右
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
        System.out.println("执行时间："+(end-start));
    }
```

### XSSF（07）学习

#### 缺点

> 写数据时速度非常慢，非常耗内存；当数据量非常大的时候（比方说100万条），也会发生内存溢出

#### 优点

> 可以写入较大的数据量

#### 代码展示

```java
    /**
     * 测试针对07EXCEL的大数据量写入操作
     * 大概13s左右
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
```

### SXSSF(XSSF升级版)

#### 优点

> 可以写非常大的数据量，写入速度比XSSF更快，占用更少内存

#### 缺点

> 过程中会产生临时文件，需要定时清理临时文件；
>
> 默认在内存中存100条（可以自定义），如果超过了，就把最前面的数据写入临时文件

#### 代码展示

```java
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
```

## Excel读操作

### 常用代码

```java
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

```

### 注意事项

    在公式操作时：
     *      =SUM(E2*1,E3*1) 这种能计算出来
     *      =SUM(G4:G16)  这种是计算不出来的
# easyExcel

## 官网

https://github.com/alibaba/easyexcel

## 简介

Java解析、生成Excel比较有名的框架有Apache poi、jxl。但他们都存在一个严重的问题就是<font color='red'>非常的耗内存</font>，poi有一套SAX模式的API可以一定程度的解决一些内存溢出的问题，但POI还是有一些缺陷，比如07版Excel解压缩以及解压后存储都是在内存中完成的，内存消耗依然很大。

easyexcel重写了poi对07版Excel的解析，能够原本一个3M的excel用POI sax依然需要100M左右内存降低到几M，并且再大的excel不会出现内存溢出，03版依赖POI的sax模式。在上层做了模型转换的封装，让使用者更加简单方便

## maven依赖

```xml
<!--json-->
<dependency>
  <groupId>com.alibaba</groupId>
  <artifactId>fastjson</artifactId>
  <version>1.2.70</version>
</dependency>
<!--easyExcel-->
<dependency>
  <groupId>com.alibaba</groupId>
  <artifactId>easyexcel</artifactId>
  <version>2.2.0-beta2</version>
</dependency>
<!-- logback -->
<dependency>
  <groupId>ch.qos.logback</groupId>
  <artifactId>logback-classic</artifactId>
  <version>1.2.3</version>
</dependency>
```

## 简单写

### PO

```java
package com.maben.base_learn.excel.easyexcel.write;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class DemoData {
    @ExcelProperty("字符串标题")
    private String string;
    @ExcelProperty("日期标题")
    private Date date;
    @ExcelProperty("数字标题")
    private Double doubleData;
    /**
     * 忽略这个字段
     */
    @ExcelIgnore
    private String ignore;
}
```

### 测试类

```java
package com.maben.base_learn.excel.easyexcel.write;

import com.alibaba.excel.EasyExcel;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 测试easyExcel
 */
public class MT001 {

    private final static String PATH = "/Users/maben/Downloads/";

    /**
     * 最简单的写
     * <p>1. 创建excel对应的实体对象 参照{@link DemoData}
     * <p>2. 直接写即可
     */
    @Test
    public void simpleWrite() {
        // 写法1
        String fileName = PATH + "simpleWrite" + System.currentTimeMillis() + ".xlsx";
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        // 如果这里想使用03 则 传入excelType参数即可
        EasyExcel.write(fileName, DemoData.class).sheet("模板").doWrite(data());
    }

    /**
     * 获取数据
     * @return 返回数据
     */
    private List<DemoData> data() {
        List<DemoData> list = new ArrayList<DemoData>();
        for (int i = 0; i < 10; i++) {
            DemoData data = new DemoData();
            data.setString("字符串" + i);
            data.setDate(new Date());
            data.setDoubleData(0.56);
            list.add(data);
        }
        return list;
    }

}
```

## 简单读

### PO

```java
package com.maben.base_learn.excel.easyexcel.read;

import lombok.Data;

import java.util.Date;

@Data
public class DemoData {
    private String string;
    private Date date;
    private Double doubleData;
}
```

### DAO

```java
package com.maben.base_learn.excel.easyexcel.read;

import java.util.List;

/**
 * 假设这个是你的DAO存储。当然还要这个类让spring管理，当然你不用需要存储，也不需要这个类。
 **/
public class DemoDAO {
    public void save(List<DemoData> list) {
        // 如果是mybatis,尽量别直接调用多次insert,自己写一个mapper里面新增一个方法batchInsert,所有数据一次性插入
    }
}
```

### 监听类

```java
package com.maben.base_learn.excel.easyexcel.read;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
@Slf4j(topic = "m.DemoDataListener")
// 有个很重要的点 DemoDataListener 不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
public class DemoDataListener extends AnalysisEventListener<DemoData> {
    /**
     * 每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 5;
    List<DemoData> list = new ArrayList<DemoData>();
    /**
     * 假设这个是一个DAO，当然有业务逻辑这个也可以是一个service。当然如果不用存储这个对象没用。
     */
    private DemoDAO demoDAO;
    public DemoDataListener() {
        // 这里是demo，所以随便new一个。实际使用如果到了spring,请使用下面的有参构造函数
        demoDAO = new DemoDAO();
    }
    /**
     * 如果使用了spring,请使用这个构造方法。每次创建Listener的时候需要把spring管理的类传进来
     *
     * @param demoDAO
     */
    public DemoDataListener(DemoDAO demoDAO) {
        this.demoDAO = demoDAO;
    }
    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data
     *            one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     */
    @Override
    public void invoke(DemoData data, AnalysisContext context) {
        log.info("解析到一条数据:{}", JSON.toJSONString(data));
        list.add(data);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (list.size() >= BATCH_COUNT) {
            saveData();
            // 存储完成清理 list
            list.clear();
        }
    }
    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
        log.info("所有数据解析完成！");
    }
    /**
     * 加上存储数据库
     */
    private void saveData() {
        log.info("{}条数据，开始存储数据库！", list.size());
        demoDAO.save(list);
        log.info("存储数据库成功！");
    }
}
```

### 测试类

```java
package com.maben.base_learn.excel.easyexcel.read;

import com.alibaba.excel.EasyExcel;
import org.junit.Test;

public class MT001 {
    private final static String PATH = "/Users/maben/Downloads/";
    /**
     * 最简单的读
     * <p>1. 创建excel对应的实体对象 参照{@link DemoData}
     * <p>2. 由于默认一行行的读取excel，所以需要创建excel一行一行的回调监听器，参照{@link DemoDataListener}
     * <p>3. 直接读即可
     */
    @Test
    public void simpleRead() {
        // 有个很重要的点 DemoDataListener 不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
        // 写法1：
        String fileName = PATH + "simpleWrite1628699798443.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        EasyExcel.read(fileName, DemoData.class, new DemoDataListener()).sheet().doRead();
    }
}
```

