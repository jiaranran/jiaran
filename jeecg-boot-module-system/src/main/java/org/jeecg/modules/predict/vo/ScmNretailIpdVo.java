package org.jeecg.modules.predict.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class ScmNretailIpdVo {
    /**物料码*/
    @Excel(name = "物料码", width = 15)
    private String matnr;
    /**省份*/
    @Excel(name = "省份", width = 15)
    private String prov;
    /**日期按周(yyyyww)*//*
    @Excel(name = "日期按周(yyyyww)", width = 15)
    private String ds;*/
    /**日期按周(yyyy-MM-dd)*/
    @Excel(name = "日期按周(yyyy-MM-dd)", width = 15)
    private Date ds;
    /**销量*/
    @Excel(name = "销量", width = 15)
    private java.math.BigDecimal cnt;
}
