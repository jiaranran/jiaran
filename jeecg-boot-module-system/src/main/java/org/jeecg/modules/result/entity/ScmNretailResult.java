package org.jeecg.modules.result.entity;

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
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 预测结果
 * @Author: jeecg-boot
 * @Date:   2020-03-16
 * @Version: V1.0
 */
@Data
@TableName("scm_nretail_ipw")
@Document(collection="scm_nretail_ipw")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="scm_nretail_ipw对象", description="预测结果")
public class ScmNretailResult {
	/**id*/
	@TableId(type = IdType.AUTO)
	@ApiModelProperty(value = "id")
	private Integer id;
	/**物料码*/
	@Excel(name = "物料码", width = 15)
    @ApiModelProperty(value = "物料码")
	private String matnr;
	/**省份*/
	@Excel(name = "省份", width = 15)
    @ApiModelProperty(value = "省份")
	private String prov;
	/**日期按周(yyyymmdd)*/
	@Excel(name = "日期按周(yyyymmdd)", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "日期按周(yyyymmdd)")
	private Date ds;
	/**实际销量*/
	@Excel(name = "实际销量", width = 15)
	@ApiModelProperty(value = "实际销量")
	private Double cnt;
	/**预测销量*/
	@Excel(name = "预测销量", width = 15)
    @ApiModelProperty(value = "预测销量")
	private Double pred;
	/**评估值*/
	@Excel(name = "评估值", width = 15)
	@ApiModelProperty(value = "评估值")
	private String eval;
}
