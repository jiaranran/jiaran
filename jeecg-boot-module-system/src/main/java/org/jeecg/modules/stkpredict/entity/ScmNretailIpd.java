package org.jeecg.modules.stkpredict.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 销量统计按天
 * @Author: jeecg-boot
 * @Date:   2020-03-11
 * @Version: V1.0
 */
@Data
@TableName("scm_nretail_ipd")
@Document(collection="scm_nretail_ipd")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="scm_nretail_ipd对象", description="销量统计按天")
public class ScmNretailIpd implements Serializable {
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
	/**日期按天(yyyy-MM-dd)*/
	@Excel(name = "日期按天(yyyy-MM-dd)", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "日期按天(yyyy-MM-dd)")
	private Date ds;
	/**销量*/
	@Excel(name = "销量", width = 15)
    @ApiModelProperty(value = "销量")
	private Double cnt;

}
