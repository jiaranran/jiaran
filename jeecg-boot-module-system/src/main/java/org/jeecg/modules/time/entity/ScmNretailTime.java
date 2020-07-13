package org.jeecg.modules.time.entity;

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
 * @Description: 提前期
 * @Author: jeecg-boot
 * @Date:   2020-06-22
 * @Version: V1.0
 */
@Data
@TableName("scm_nretail_time")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="scm_nretail_time对象", description="提前期")
public class ScmNretailTime {
    
	/**id主键*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id主键")
	private Integer id;
	/**物料*/
	@Excel(name = "物料", width = 15)
    @ApiModelProperty(value = "物料")
	private String matnr;
	/**省份*/
	@Excel(name = "省份", width = 15)
    @ApiModelProperty(value = "省份")
	private String prov;
	/**订单创建日期*/
	@Excel(name = "订单创建日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "订单创建日期")
	private Date orderCreateDate;
	/**实际发货日期*/
	@Excel(name = "实际发货日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "实际发货日期")
	private Date realSendDate;
}
