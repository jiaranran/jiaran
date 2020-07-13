package org.jeecg.modules.stkjob.entity;

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
 * @Description: 安全库存任务
 * @Author: jeecg-boot
 * @Date:   2020-07-06
 * @Version: V1.0
 */
@Data
@TableName("scm_nretail_job_stk")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="scm_nretail_job_stk对象", description="安全库存任务")
public class ScmNretailJobStk {
    
	/**id*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
	private Integer id;
	/**uuid*/
	@Excel(name = "uuid", width = 15)
    @ApiModelProperty(value = "uuid")
	private String uuid;
	/**销量文件名称*/
	@Excel(name = "销量文件名称", width = 15)
    @ApiModelProperty(value = "销量文件名称")
	private String cntFilename;
	/**提前期文件名称*/
	@Excel(name = "提前期文件名称", width = 15)
    @ApiModelProperty(value = "提前期文件名称")
	private String timeFilename;
	/**任务创建时间*/
	@Excel(name = "任务创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "任务创建时间")
	private Date createtime;
	/**running执行中，success执行成功，fail执行失败*/
	@Excel(name = "running执行中，success执行成功，fail执行失败", width = 15)
    @ApiModelProperty(value = "running执行中，success执行成功，fail执行失败")
	private String state;
	/**任务信息*/
	@Excel(name = "任务信息", width = 15)
    @ApiModelProperty(value = "任务信息")
	private String jobinfo;
	/**任务结束时间*/
	@Excel(name = "任务结束时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "任务结束时间")
	private Date endtime;
}
