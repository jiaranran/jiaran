package org.jeecg.modules.util;


import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


/**
 * java操作excel工具类
 *
 * @author yangxu
 * @date 2019-07-17 10:14
 */
public class ExcelUtils {
    public static Set<String> provinces = new HashSet<String>(Arrays.asList(new String[]{"河北", "山西", "辽宁", "吉林", "黑龙江", "江苏", "浙江", "安徽", "福建",
            "江西", "山东", "河南", "湖北", "湖南", "广东", "海南", "四川", "贵州", "云南", "陕西", "甘肃", "青海", "香港", "北京", "天津",
            "上海", "重庆", "广西", "内蒙", "西藏", "宁夏", "新疆", "内蒙古"}));

    public static void main(String[] args) throws Exception {

        Workbook workbook = readExcel("C:\\download\\ruleConfig.xlsx");

    }

    /**
     * 检查excel文件是否符合规范
     * 检查excel文件，excel文件规范： 1、文件不得为空 2、首行不得为空 3、首行没有合并单元格
     * 4、首行必须为日期 5、首列必须为省份 6、内容部分不得出现空值
     *
     * @param file excel文件
     * @return boolean  文件是否符合上述规范
     * @exception/throws
     * @author yangxu
     * @date 2019-07-17 10:32
     */
    public static void check(MultipartFile file) throws Exception {
        //Workbook workbook = readExcel(fileName);
        Workbook workbook = ExcelUtil.readExcel(file);
        int sheetNum = workbook.getNumberOfSheets();
        for (int i = 0; i < sheetNum; i++) {
            System.out.println("================检测sheet" + i + "===================");
            Sheet sheet = workbook.getSheetAt(i);
            if (isRowEmpty(sheet.getRow(0))) {
                throw new RuntimeException("sheet" + i + "第一行为空");
            }
            if (isFirstRowContainsMergecell(sheet)) {
                throw new RuntimeException("sheet" + i + "首行为合并单元格，格式错误");
            }
            if (!isFirstCellEmpty(sheet)) {
                throw new RuntimeException("sheet" + i + "格式有误");
            }
            if (!isFirstRowDate(sheet)) {
                throw new RuntimeException("sheet" + i + "第一行不是日期");
            }
            if (!isFirstColumnProvince(sheet)) {
                throw new RuntimeException("sheet" + i + "第一列不是省份");
            }
            if (getEmptyCellNum(sheet) > 0) {
                throw new RuntimeException("sheet" + i + "存在空值");
            }
            System.out.println("sheet" + i + "符合格式要求\n");
        }
    }

    /**
     * 判断excel第一个单元格是否为空
     * 第一行为日期，第一列为省份，那交叉的第一个单元格必定为空，如果不是，说明文件有问题
     *
     * @param sheet excel中sheet
     * @return boolean  是否为空
     * @exception/throws
     * @author yangxu
     * @date 2019-07-17 10:37
     */
    public static boolean isFirstCellEmpty(Sheet sheet) {
        Row row = sheet.getRow(0);
        Cell cell = row.getCell(0);
        int cellType = cell.getCellType();
        String value = cell.getStringCellValue();

        return StringUtils.isBlank(value);
    }

    /**
     * 判断第一列是否为省公司，或者其代码
     *
     * @param sheet excel中的sheet
     * @return boolean
     * @exception/throws
     * @author yangxu
     * @date 2019-07-17 10:43
     */
    public static boolean isFirstColumnProvince(Sheet sheet) {
        System.out.println("判断第一列是否为省公司，或者其代码");
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            Row row = sheet.getRow(i);
            Cell cell = row.getCell(0);
            if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
                String cellValue = cell.getRichStringCellValue().toString();
                if (!provinces.contains(cellValue)) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断一行是否为空行
     *
     * @param row excel中的一行
     * @return boolean
     * @exception/throws
     * @author yangxu
     * @date 2019-07-17 10:45
     */
    public static boolean isRowEmpty(Row row) {
        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != XSSFCell.CELL_TYPE_BLANK) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断第一行是否包含合并单元格
     * 有可能第一行会是标题，那不符合规范
     *
     * @param sheet excel中的sheet
     * @return boolean
     * @exception/throws
     * @author yangxu
     * @date 2019-07-17 10:46
     */
    public static boolean isFirstRowContainsMergecell(Sheet sheet) {
        System.out.println("判断第一行是否有合并单元格");
        Row row1 = sheet.getRow(0);
        for (int i = 0; i < row1.getPhysicalNumberOfCells(); i++) {
            if (isMergedRegion(sheet, 0, i)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断单元格是否为合并单元格
     *
     * @param sheet  excel中的sheet
     * @param row    行号
     * @param column 列号
     * @return boolean
     * @exception/throws
     * @author yangxu
     * @date 2019-07-17 10:48
     */
    private static boolean isMergedRegion(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if (row >= firstRow && row <= lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 判断第一行是否为日期
     * 之前已经将excel中的日期类型转换为字符串类型，使用正则表达式来匹配第一行字符是否为日期类型
     *
     * @param sheet
     * @return boolean
     * @exception/throws
     * @author yangxu
     * @date 2019-07-17 10:51
     */
    public static boolean isFirstRowDate(Sheet sheet) {
        // Excel正常日期获取到的cell type 是 CELL_TYPE_NUMERIC 格式的
        // 所以直接判断是不是 CELL_TYPE_NUMERIC 就行
        System.out.println("判断第一行是否为日期");
        int index = 0;
        Row row1 = sheet.getRow(index);

        for (int i = 1; i < row1.getPhysicalNumberOfCells(); i++) {
            Cell cell = row1.getCell(i);
            if (cell.getCellType() != Cell.CELL_TYPE_NUMERIC)
                return false;
        }

        return true;
    }

    /**
     * 判断文件是否为空
     *
     * @param
     * @return
     * @exception/throws
     * @author yangxu
     * @date 2019-07-17 10:54
     */
    public boolean isFileEmpty(Sheet sheet) {
        return sheet.getPhysicalNumberOfRows() == 0;
    }

    /**
     * 判断某行是否存在空值，空值的数量
     *
     * @param sheet
     * @return int  空值的数量
     * @exception/throws
     * @author yangxu
     * @date 2019-07-17 10:55
     */
    public static int getEmptyCellNum(Sheet sheet) {
        int count = 0;
        int rows = sheet.getPhysicalNumberOfRows();
        Row row1 = sheet.getRow(0);
        int columns = row1.getPhysicalNumberOfCells();

        int[][] isEmpty = new int[rows][columns];

        for (int i = 1; i < rows; i++) {
            Row row = sheet.getRow(i);
            for (int j = 1; j < columns; j++) {
                Cell cell = row.getCell(j);
                if (cell != null && cell.getCellType() == XSSFCell.CELL_TYPE_BLANK) {
                    isEmpty[i][j] = 1;
                    ++count;
                }
            }
        }
        System.out.print("空值数量为： " + count);
        return count;
    }


    /**
     * 从excel中获取workbook对象
     *
     * @param fileName excel的全路径名
     * @return org.apache.poi.ss.usermodel.Workbook  workbook对象
     * @exception/throws
     * @author yangxu
     * @date 2019-07-17 10:56
     */
    public static Workbook readExcel(String fileName) {
        Workbook wb = null;
        if (fileName == null) {
            return null;
        }
        String extString = fileName.substring(fileName.lastIndexOf("."));
        InputStream is = null;
        try {
            is = new FileInputStream(fileName);
            if (".xls".equals(extString)) {
                return wb = new HSSFWorkbook(is);
            } else if (".xlsx".equals(extString)) {
                return wb = new XSSFWorkbook(is);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wb;
    }

    /**
     * 获取sheet中的行数
     *
     * @param sheet
     * @return int  一个sheet中的行数
     * @exception/throws
     * @author yangxu
     * @date 2019-07-17 10:57
     */
    public static int getRowCount(Sheet sheet) {
        if (sheet.getPhysicalNumberOfRows() == 0) {
            return 0;
        }
        return sheet.getLastRowNum() + 1;

    }


    /**
     * 将excel文件的一行的日期类型统一成为"1996-1"形式的字符
     * 可检测的类型有所有的日期类型，和以下几种格式的字符日期
     * 2019-01    2019-1  2019/1  2019.1  2019年3月  2019-01-01    2019-1-1  2019/1/1  2019.1.1  2019年3月1日
     * 如果形式不在此列，报格式异常，请重新修改格式，上传
     *
     * @param fileName excel文件路径名
     * @return void
     * @exception/throws
     * @author yangxu
     * @date 2019-07-17 11:00
     */
    public static void firstRowDateTransfer(String fileName) {

        Workbook workbook = readExcel(fileName);
        if (workbook instanceof XSSFWorkbook) {
            XSSFWorkbook wb = (XSSFWorkbook) workbook;
            int sheetNum = workbook.getNumberOfSheets();

            for (int i = 0; i < sheetNum; i++) {
                XSSFSheet sheet = wb.getSheetAt(i);
                XSSFRow row = null;
                if (isFirstRowContainsMergecell(sheet)) {
                    throw new RuntimeException("sheet" + i + "首行有合并单元格，格式错误");
                }
                row = sheet.getRow(0);
                for (int j = 1; j < row.getPhysicalNumberOfCells(); j++) {
                    XSSFCell cell = row.getCell(j);
                    if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
                        String dateStr = "";
                        if (cell.getNumericCellValue() < 3000) {
                            dateStr = String.valueOf((int) cell.getNumericCellValue());
                        } else {
                            Date date = DateUtil.getJavaDate(cell.getNumericCellValue());
                            int year = date.getYear() + 1900;
                            int mon = date.getMonth() + 1;
                            dateStr = year + "-" + mon;
                        }
                        //System.out.println(dateStr);
                        cell.setCellType(XSSFCell.CELL_TYPE_STRING);
                        cell.setCellValue(dateStr);
                    } else if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
                        String str = cell.getRichStringCellValue().toString();
                        //System.out.println(str);
                        cell.setCellValue(getDateFromString(str));
                    }
                }
            }
        }
        try {
            File file = new File(fileName);
            FileOutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将字符形式的日期 转换成规范形式的字符日期
     *
     * @param str 转换后的字符型日期
     * @return java.lang.String
     * @exception/throws
     * @author yangxu
     * @date 2019-07-17 11:03
     */
    private static String getDateFromString(String str) {
        int len = str.length();
        String year = "";
        String mon = "";
        if (!str.startsWith("20") && !str.startsWith("19")) {
            throw new RuntimeException("格式有误,第一行不是规范的日期类型");
        }
        if (len <= 5) // 2019年
        {
            year = str.substring(0, 4);
            return year;
        } else if (len <= 7)//2019-01    2019-1  2019/1  2019.1  2019年3月
        {
            year = str.substring(0, 4);
            if (str.substring(5).startsWith("0")) {
                mon = str.substring(6);
            } else {
                mon = str.substring(5);
            }

        } else if (len >= 8) { // 2019-01-01    2019-1-1  2019/1/1  2019.1.1  2019年3月1日
            year = str.substring(0, 4);
            String mon_day = str.substring(5);
            if (mon_day.startsWith("0")) {
                mon = mon_day.substring(1, 2);
            } else {
                if (mon_day.charAt(1) >= '0' && mon_day.charAt(1) <= '9') {
                    mon = mon_day.substring(0, 2);
                } else {
                    mon = mon_day.substring(0, 1);
                }
            }
        }
        return year + "-" + mon;
    }


}
