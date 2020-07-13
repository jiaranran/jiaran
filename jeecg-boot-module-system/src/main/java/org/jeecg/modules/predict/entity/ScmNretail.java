package org.jeecg.modules.predict.entity;

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
 * @Description: 原始数据
 * @Author: jeecg-boot
 * @Date:   2020-03-14
 * @Version: V1.0
 */
@Data
@TableName("scm_nretail")
@Document(collection="scm_nretail")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="scm_nretail对象", description="原始数据")
public class ScmNretail {
	/**id*/
	@TableId(type = IdType.AUTO)
	@ApiModelProperty(value = "id")
	private Integer id;
	/**采购组*/
	@Excel(name = "采购组", width = 15)
    @ApiModelProperty(value = "采购组")
	private String buyg;
	/**省编码*/
	@Excel(name = "省编码", width = 15)
	@ApiModelProperty(value = "省编码")
	private String prov;
	/**物料*/
	@Excel(name = "物料", width = 15)
    @ApiModelProperty(value = "物料")
	private String matnr;
	/**物料描述*/
	@Excel(name = "物料描述", width = 20)
    @ApiModelProperty(value = "物料描述")
	private String matnrDesc;

	/**实际发货日期*/
	@Excel(name = "实际发货日期", width = 15)
    @ApiModelProperty(value = "实际发货日期")
	private Date ds;
	/**总和_实际交货数量*/
	@Excel(name = "总和_实际交货数量", width = 15)
    @ApiModelProperty(value = "总和_实际交货数量")
	private Double cnt;
}
