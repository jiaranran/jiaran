package org.jeecg.modules.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jeecg.modules.predict.entity.ScmNretail;
import org.jeecg.modules.time.entity.ScmNretailTime;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author hewangtong
 */
@Slf4j
public class ReadExcel {
    // 总行数
    private int totalRows = 0;
    // 总条数
    private int totalCells = 0;
    // 错误信息接收器
    private String errorMsg;

    // 构造方法
    public ReadExcel() {
    }

    // 获取总行数
    public int getTotalRows() {
        return totalRows;
    }

    // 获取总列数
    public int getTotalCells() {
        return totalCells;
    }

    // 获取错误信息
    public String getErrorInfo() {
        return errorMsg;
    }
    public List<ScmNretail> getExcelInfo(MultipartFile mFile, Integer weekStart) {
        String fileName = mFile.getOriginalFilename().equals("") ? mFile.getName() : mFile.getOriginalFilename();// 获取文件名
//        List<Map<String, Object>> userList = new LinkedList<Map<String, Object>>();
        try {
            boolean isExcel2003 = true;// 根据文件名判断文件是2003版本还是2007版本
            if (isExcel2007(fileName)) {
                isExcel2003 = false;
            }
            return createExcel(mFile.getInputStream(), isExcel2003, weekStart);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ScmNretail> getExcelInfo(File file, int weekStart) {
        String fileName = file.getName();// 获取文件名
//        List<Map<String, Object>> userList = new LinkedList<Map<String, Object>>();
        try {
            boolean isExcel2003 = true;// 根据文件名判断文件是2003版本还是2007版本
            if (isExcel2007(fileName)) {
                isExcel2003 = false;
            }
            return createExcel(new FileInputStream(file), isExcel2003, fileName,weekStart);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 读EXCEL文件，获取信息集合
     *
     * @param file
     * @return
     */
    public List getExcelInfo(File file) {
        String fileName = file.getName();// 获取文件名
//        List<Map<String, Object>> userList = new LinkedList<Map<String, Object>>();
        try {
            boolean isExcel2003 = true;// 根据文件名判断文件是2003版本还是2007版本
            if (isExcel2007(fileName)) {
                isExcel2003 = false;
            }
            return createExcel(new FileInputStream(file), isExcel2003);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ScmNretailTime> getExcelInfo2(File file) {
        String fileName = file.getName();// 获取文件名
//        List<Map<String, Object>> userList = new LinkedList<Map<String, Object>>();
        try {
            boolean isExcel2003 = true;// 根据文件名判断文件是2003版本还是2007版本
            if (isExcel2007(fileName)) {
                isExcel2003 = false;
            }
            return createExcel2(new FileInputStream(file), isExcel2003);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 根据excel里面的内容读取客户信息
     *
     * @param is          输入流
     * @param isExcel2003 excel是2003还是2007版本
     * @return
     * @throws IOException
     */
    public List createExcel(InputStream is, boolean isExcel2003) {
        try {
            Workbook wb = null;
            if (isExcel2003) {// 当excel是2003时,创建excel2003
                wb = new HSSFWorkbook(is);
            } else {// 当excel是2007时,创建excel2007
                wb = new XSSFWorkbook(is);
            }
//            return readExcelValue(wb);// 读取Excel里面客户的信息
            return readExcel(wb,null,0,2);// 读取Excel里面客户的信息
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<ScmNretail> createExcel(InputStream is, boolean isExcel2003, Integer weekStart) {
        try {
            Workbook wb = null;
            if (isExcel2003) {// 当excel是2003时,创建excel2003
                wb = new HSSFWorkbook(is);
            } else {// 当excel是2007时,创建excel2007
                wb = new XSSFWorkbook(is);
            }
//            return readExcelValue(wb);// 读取Excel里面客户的信息
            return readExcel(wb,"",weekStart,1);// 读取Excel里面客户的信息
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<ScmNretailTime> createExcel2(InputStream is, boolean isExcel2003) {
        try {
            Workbook wb = null;
            if (isExcel2003) {// 当excel是2003时,创建excel2003
                wb = new HSSFWorkbook(is);
            } else {// 当excel是2007时,创建excel2007
                wb = new XSSFWorkbook(is);
            }
            return readExcel2(wb);// 读取Excel里面客户的信息
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<ScmNretail> createExcel(InputStream is, boolean isExcel2003, String fileName, int weekStart) {
        try {
            Workbook wb = null;
            if (isExcel2003) {// 当excel是2003时,创建excel2003
                wb = new HSSFWorkbook(is);
            } else {// 当excel是2007时,创建excel2007
                wb = new XSSFWorkbook(is);
            }
            return readExcel(wb, fileName,weekStart,1);// 读取Excel里面客户的信息
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private List<ScmNretail> readExcel(Workbook wb, String fileName, int weekStart, int num){
        ArrayList scmNretails = new ArrayList<>();
        int m=0;
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            // 得到第一个shell
            Sheet sheet = wb.getSheetAt(i);
            // 原始数据总行数
            int rowNum = sheet.getPhysicalNumberOfRows()-1;
            if(rowNum==0){
                log.error("文件内容为空");
                return null;
            }
            log.info("原始数据总行数:" + rowNum);
            String sheetName = sheet.getSheetName();
            Row row0 = sheet.getRow(0);

            String buyg = row0.getCell(0).getStringCellValue();
            if(!buyg.equals("采购组")){
                log.error("表头第一列不是采购组！");
                return null;
            }
            String prov = row0.getCell(1).getStringCellValue();
            if(!prov.equals("省编码")){
                log.error("表头第二列不是省编码！");
                return null;
            }
            String matnr = row0.getCell(2).getStringCellValue();
            if(!matnr.equals("物料")){
                log.error("表头第三列不是物料！");
                return null;
            }
            String matnrdes = row0.getCell(3).getStringCellValue();
            if(!matnrdes.equals("物料描述")){
                log.error("表头第四列不是物料描述！");
                return null;
            }
            String ds = row0.getCell(4).getStringCellValue();
            if(!ds.equals("创建日期")){
                log.error("表头第五列不是创建日期！");
                return null;
            }
            String cnt = row0.getCell(5).getStringCellValue();
            if(!cnt.equals("总和_订单数量")){
                log.error("表头第六列不是总和_订单数量！");
                return null;
            }
            for(int j=1;j<=sheet.getLastRowNum();j++){
                Row row = sheet.getRow(j);
                ScmNretail scmNretail = new ScmNretail();
                for (int k=0;k<row.getLastCellNum();k++){
                    Cell cell = row.getCell(k);
                    String value=null;
                    String head = row0.getCell(k).getStringCellValue();
                    if(cell!=null && cell.getCellType() != XSSFCell.CELL_TYPE_BLANK){
                        if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                            value = replaceBlank(cell.getStringCellValue());
                        }else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                            value = String.valueOf(cell.getNumericCellValue());
                        }
                    }else {
                        if(!head.equals("物料描述")){
                            m++;
                        }

                       /* log.error("---------第"+j+"行有空值");
                        return null;*/
                    }


                    if(value!=null){
                        if(head.equals("采购组")){
                            int i1 = value.indexOf('.');
                            if(i1!=-1){
                                value=replaceBlank(value.substring(0, i1));
                            }
                            if(value.length()!=3){
                                m++;
                                log.info("---------第"+j+"行采购组格式错误");
//                                break;
                                return null;
                            }
                            scmNretail.setBuyg(value) ;
                        }else if(head.equals("省编码")){
                            int i1 = value.indexOf('.');
                            if(i1!=-1){
                                value=replaceBlank(value.substring(0, i1));
                            }
                            if(value.length()!=6){
                                m++;
                                log.info("---------第"+j+"行省份格式错误");
//                                break;
                                return null;
                            }
                            scmNretail.setProv(value);
                        }else if(head.equals("物料")){
                            if(value.length()!=18&&value.length()!=16){
                                m++;
                                log.info("---------第"+j+"行物料格式错误");
//                                break;
                                return null;
                            }
                            scmNretail.setMatnr(value);
                        }else if(head.equals("物料描述")){
                            scmNretail.setMatnrDesc(value);
                        }else if(head.equals("创建日期")){
                            int i1 = value.indexOf('.');
                            if(i1!=-1){
                                BigDecimal bigDecimal = new BigDecimal(value);
                                value = bigDecimal.toPlainString();
                            }
                            if(value.length()!=8){
                                m++;
                                log.info("---------第"+j+"行日期格式错误");
//                                break;
                                return null;
                            }
                            Date date ;
                            if(num==1){
                                 date = DateUtils.dateToWeek(value,fileName,weekStart);
                            }else {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                                 date = null;
                                try {
                                    date = sdf.parse(value);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }

                            scmNretail.setDs(date);
                        }else if(head.equals("总和_订单数量")){
                            double cnt2 = cell.getNumericCellValue();
                            if(cnt2<0){
                                cnt2=0;
                            }
                            scmNretail.setCnt(cnt2);
                        }
                    }
                }
                if(scmNretail.getBuyg()!=null&&scmNretail.getProv()!=null&&scmNretail.getMatnr()!=null&&scmNretail.getDs()!=null){
                    scmNretails.add(scmNretail);
                }
            }
            log.info("---------共去除"+m+"行-----------");
        }
        return scmNretails;
    }
    private List<ScmNretailTime> readExcel2(Workbook wb){
        ArrayList<ScmNretailTime> scmNretails = new ArrayList<ScmNretailTime>();
        int m=0;
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            // 得到第一个shell
            Sheet sheet = wb.getSheetAt(i);
            // 原始数据总行数
            int rowNum = sheet.getPhysicalNumberOfRows()-1;
            if(rowNum==0){
                log.error("文件内容为空");
                return null;
            }
            log.info("原始数据总行数:" + rowNum);
            String sheetName = sheet.getSheetName();
            Row row0 = sheet.getRow(0);

            String prov = row0.getCell(0).getStringCellValue();
            if(!prov.equals("工厂")){
                log.error("表头第一列不是工厂！");
                return null;
            }
            String matnr = row0.getCell(1).getStringCellValue();
            if(!matnr.equals("物料")){
                log.error("表头第二列不是物料！");
                return null;
            }
            String orCreDate = row0.getCell(2).getStringCellValue();
            if(!orCreDate.equals("订单创建日期（采购订单创建日期）")){
                log.error("表头第三列不是订单创建日期！");
                return null;
            }
            String sendDate = row0.getCell(3).getStringCellValue();
            if(!sendDate.equals("实际发货日期（过账日期）")){
                log.error("表头第四列不是实际发货日期！");
                return null;
            }

            for(int j=1;j<=sheet.getLastRowNum();j++){
                Row row = sheet.getRow(j);
                ScmNretailTime scmNretailTime = new ScmNretailTime();
                for (int k=0;k<row.getLastCellNum();k++){
                    Cell cell = row.getCell(k);
                    String value=null;
                    String head = row0.getCell(k).getStringCellValue();
                    if(cell!=null && cell.getCellType() != XSSFCell.CELL_TYPE_BLANK){
                        if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                            value = replaceBlank(cell.getStringCellValue());
                        }else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                            value = String.valueOf(cell.getNumericCellValue());
                        }
                    }else {
                        m++;
                       /* log.error("---------第"+j+"行有空值");
                        return null;*/
                    }

                    if(value!=null){
                         if(head.equals("工厂")){
                            if(value.length()!=4){
                                m++;
                                log.info("---------第"+j+"行省份格式错误");
//                                break;
                                return null;
                            }
                            scmNretailTime.setProv(value);
                        }else if(head.equals("物料")){
                            if(value.length()!=18&&value.length()!=16){
                                m++;
                                log.info("---------第"+j+"行物料格式错误");
//                                break;
                                return null;
                            }
                            scmNretailTime.setMatnr(value);
                        }else if(head.equals("订单创建日期（采购订单创建日期）")){
                            if(value.length()!=8){
                                m++;
                                log.info("---------第"+j+"行订单创建日期格式错误");
//                                break;
                                return null;
                            }
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                            Date date = null;
                            try {
                                date = sdf.parse(value);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            scmNretailTime.setOrderCreateDate(date);
                        }else if(head.equals("实际发货日期（过账日期）")){
                             if(value.length()!=8){
                                 m++;
                                 log.info("---------第"+j+"行实际发货日期格式错误");
//                                break;
                                 return null;
                             }
                             SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                             Date date = null;
                             try {
                                 date = sdf.parse(value);
                             } catch (ParseException e) {
                                 e.printStackTrace();
                             }
                             scmNretailTime.setRealSendDate(date);
                         }
                    }
                }
                if(scmNretailTime.getProv()!=null&&scmNretailTime.getMatnr()!=null&&scmNretailTime.getOrderCreateDate()!=null&&scmNretailTime.getRealSendDate()!=null){
                    scmNretails.add(scmNretailTime);
                }
            }
            log.info("---------共去除"+m+"行-----------");
        }
        return scmNretails;
    }
    public static ScmNretail formate(ScmNretail scmNretail){
        int i = scmNretail.getProv().indexOf('.');
        if(i !=-1){
            String pro = scmNretail.getProv().substring(0, i);
            scmNretail.setProv(pro);
        }
        int j = scmNretail.getBuyg().indexOf('.');
        if(j !=-1){
            String buyg = scmNretail.getBuyg().substring(0, j);
            scmNretail.setBuyg(buyg);
        }

        return scmNretail;
    }
    /*
     * 去除数据的空格、回车、换行符、制表符
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            //空格\t、回车\n、换行符\r、制表符\t
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }


    public static String parseLast(String value) {
        int indexOf = value.indexOf(".0");
        return indexOf != -1 && indexOf == value.length() - 2 ? value.substring(0, value.length() - 2) : value;
    }

    public static String parseScientificNotation(String value) {
        BigDecimal bd = new BigDecimal(value);
        return bd.toPlainString();
    }

    public static Object getCellValue(Cell cell, FormulaEvaluator evaluator) {
        Object value = null;
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                value = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                value = cell.getNumericCellValue();
                break;
            case Cell.CELL_TYPE_FORMULA:
                CellValue cellValue = evaluator.evaluate(cell);
                boolean isNumeric = cellValue.getCellType() == Cell.CELL_TYPE_NUMERIC;
                value = (isNumeric) ? cellValue.getNumberValue() : cellValue.getStringValue();
                if (isNumeric && value.toString().equals("0.0")) {
                    value = cell.getNumericCellValue();
                }
                break;
            case Cell.CELL_TYPE_BLANK:
                break;
            default:
                break;
        }
        return value;
    }

    /**
     * 验证EXCEL文件
     *
     * @param filePath
     * @return
     */
    public boolean validateExcel(String filePath) {
        if (filePath == null || !(isExcel2003(filePath) || isExcel2007(filePath))) {
            errorMsg = "文件名不是excel格式";
            return false;
        }
        return true;
    }

    // @描述：是否是2003的excel，返回true是2003
    public static boolean isExcel2003(String filePath) {
        return filePath.matches("^.+\\.(?i)(xls)$");
    }

    // @描述：是否是2007的excel，返回true是2007
    public static boolean isExcel2007(String filePath) {
        return filePath.matches("^.+\\.(?i)(xlsx)$");
    }

}
