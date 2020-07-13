package org.jeecg.modules.job.entity;

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
 * @Description: job
 * @Author: jeecg-boot
 * @Date:   2020-03-20
 * @Version: V1.0
 */
@Data
@TableName("scm_nretail_job")
@Document(collection="scm_nretail_job")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="scm_nretail_job对象", description="job")
public class ScmNretailJob {
    
	/**id*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
	private Integer id;
	/**UUID*/
	@Excel(name = "UUID", width = 15)
	@ApiModelProperty(value = "UUID")
	private String uuid;

	/**文件名称*/
	@Excel(name = "文件名称", width = 15)
    @ApiModelProperty(value = "文件名称")
	private String filename;
	/**0执行中，1执行成功，2执行失败*/
	@Excel(name = "执行状态", width = 15)
    @ApiModelProperty(value = "running执行中，success执行成功，fail执行失败")
	private String state;
	/**文件上传时间*/
	@Excel(name = "任务创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "任务创建时间")
	private Date createtime;
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "任务结束时间")
	private Date endtime;
	/**运行时间*/
	@Excel(name = "运行时间", width = 15)
    @ApiModelProperty(value = "运行时间")
	private Integer time;
	@ApiModelProperty(value = "任务信息")
	private String jobinfo;
}
