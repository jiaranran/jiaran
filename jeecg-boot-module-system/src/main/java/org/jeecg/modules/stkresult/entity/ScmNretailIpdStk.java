package org.jeecg.modules.stkresult.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 安全库存结果
 * @Author: jeecg-boot
 * @Date:   2020-07-01
 * @Version: V1.0
 */
@Data
@TableName("scm_nretail_ipd_stk")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="scm_nretail_ipd_stk对象", description="安全库存结果")
public class ScmNretailIpdStk {
    
	/**id主键*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id主键")
	private Integer id;
	/**物料码*/
	@Excel(name = "物料码", width = 15)
    @ApiModelProperty(value = "物料码")
	private String matnr;
	/**省份*/
	@Excel(name = "省份", width = 15)
    @ApiModelProperty(value = "省份")
	private String prov;
	/**平均需求量*/
	@Excel(name = "平均需求量", width = 15)
    @ApiModelProperty(value = "平均需求量")
	private java.math.BigDecimal demandAvg;
	/**需求量标准差*/
	@Excel(name = "需求量标准差", width = 15)
    @ApiModelProperty(value = "需求量标准差")
	private java.math.BigDecimal demandMse;
	/**提前期平均值*/
	@Excel(name = "提前期平均值", width = 15)
    @ApiModelProperty(value = "提前期平均值")
	private java.math.BigDecimal timeAvg;
	/**提前期标准差*/
	@Excel(name = "提前期标准差", width = 15)
    @ApiModelProperty(value = "提前期标准差")
	private java.math.BigDecimal timeMse;
	/**符合的分布*/
	@Excel(name = "符合的分布", width = 15)
    @ApiModelProperty(value = "符合的分布")
	private String distribution;
	/**z值或b值*/
	@Excel(name = "z值或b值", width = 15)
    @ApiModelProperty(value = "z值或b值")
	private java.math.BigDecimal zbValue;
	/**安全库存值*/
	@Excel(name = "安全库存值", width = 15)
    @ApiModelProperty(value = "安全库存值")
	private java.math.BigDecimal safeStock;
	/**是否超限*/
	@Excel(name = "是否超限", width = 15)
    @ApiModelProperty(value = "是否超限")
	private String outLimit;
}
