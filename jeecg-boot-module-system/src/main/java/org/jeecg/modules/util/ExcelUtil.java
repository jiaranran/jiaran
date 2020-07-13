package org.jeecg.modules.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public class ExcelUtil {
    public static Workbook readExcel(MultipartFile file){
        String fileName = file.getOriginalFilename().equals("") ? file.getName() : file.getOriginalFilename();

        Workbook wb = null;

        /*MultipartFile转换File*/
        File f = null;
        if(file.equals("")||file.getSize()<=0){
            file = null;
        }else {
            try {
                // 获得文件名称
                // 获得后缀
                String extName = fileName.substring(fileName.indexOf("."));
                // 根据后缀名称判断excel的版本

                if (ExcelVersion.V2003.getSuffix().equals(extName)) {
                    wb = new HSSFWorkbook(file.getInputStream());

                } else if (ExcelVersion.V2007.getSuffix().equals(extName)) {
                    wb = new XSSFWorkbook(file.getInputStream());

                } else {
                    // 无效后缀名称，这里之能保证excel的后缀名称，不能保证文件类型正确，不过没关系，在创建Workbook的时候会校验文件格式
                    throw new IllegalArgumentException("Invalid excel version");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    return wb;
    }
    /**
     * author
     * <p>
     * create 2019-01-21 16:00
     * <p>
     * desc      Excel版本枚举
     **/
    public enum ExcelVersion {
        /**
         * 虽然V2007版本支持最大支持1048575 * 16383 ，
         * V2003版支持65535*255
         * 但是在实际应用中如果使用如此庞大的对象集合会导致内存溢出，
         * 因此这里限制最大为10000*100，如果还要加大建议先通过单元测试进行性能测试。
         * 1000*100 全部导出预计时间为27s左右
         */
        V2003(".xls", 10000, 100), V2007(".xlsx", 100, 100);

        private String suffix;

        private int maxRow;

        private int maxColumn;

        ExcelVersion(String suffix, int maxRow, int maxColumn) {
            this.suffix = suffix;
            this.maxRow = maxRow;
            this.maxColumn = maxColumn;
        }

        public String getSuffix() {
            return this.suffix;
        }

        public int getMaxRow() {
            return maxRow;
        }

        public void setMaxRow(int maxRow) {
            this.maxRow = maxRow;
        }

        public int getMaxColumn() {
            return maxColumn;
        }

        public void setMaxColumn(int maxColumn) {
            this.maxColumn = maxColumn;
        }

        public void setSuffix(String suffix) {
            this.suffix = suffix;
        }
    }
}
