package org.jeecg.modules.predict.vo;

import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.Date;
@Data
public class ScmNretailDsVo {
    /**物料码*/
    @Excel(name = "物料码", width = 15)
    private String matnr;
    /**省份*/
    @Excel(name = "省份", width = 15)
    private String prov;
    /**最大日期(yyyy-MM-dd)*/
    @Excel(name = "日期按周(yyyy-MM-dd)", width = 15)
    private Date maxDs;
    /**最小日期(yyyy-MM-dd)*/
    @Excel(name = "日期按周(yyyy-MM-dd)", width = 15)
    private Date minDs;

}
