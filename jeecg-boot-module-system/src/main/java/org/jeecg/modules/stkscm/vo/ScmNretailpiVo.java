package org.jeecg.modules.stkscm.vo;

import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

@Data
public class ScmNretailpiVo {
    /**物料码*/
    @Excel(name = "物料码", width = 15)
    private String matnr;
    /**省份*/
    @Excel(name = "省份", width = 15)
    private String prov;

}
