package org.jeecg.modules.predict.vo;

import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;
@Document(collection="scm_nretail")
public class ScmNretailVo {
    /**物料码*/
    @Excel(name = "物料码", width = 15)
    private String matnr;
    /**省份*/
    @Excel(name = "省份", width = 15)
    private String prov;
    /**日期按周(yyyy-MM-dd)*/
    @Excel(name = "日期按周(yyyy-MM-dd)", width = 15)
    private Date ds;

    public String getMatnr() {
        return matnr;
    }

    public void setMatnr(String matnr) {
        this.matnr = matnr;
    }

    public String getProv() {
        return prov;
    }

    public void setProv(String prov) {
        this.prov = prov;
    }

    public Date getDs() {
        return ds;
    }

    public void setDs(Date ds) {
        this.ds = ds;
    }

    public BigDecimal getCnt() {
        return cnt;
    }

    public void setCnt(BigDecimal cnt) {
        this.cnt = cnt;
    }

    /**销量*/
    @Excel(name = "销量", width = 15)
    private BigDecimal cnt;
}
