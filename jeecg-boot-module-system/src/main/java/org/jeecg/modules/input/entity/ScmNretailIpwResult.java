package org.jeecg.modules.input.entity;

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
 * @Description: 输入文件预测结果
 * @Author: jeecg-boot
 * @Date:   2020-06-11
 * @Version: V1.0
 */
@Data
@TableName("scm_nretail_result")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="scm_nretail_result对象", description="输入文件预测结果")
public class ScmNretailIpwResult {
    
	/**id主键*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id主键")
	private Integer id;
	/**uuid*/
	@Excel(name = "uuid", width = 15)
    @ApiModelProperty(value = "uuid")
	private String jobUuid;
	/**输入文件名称*/
	@Excel(name = "输入文件名称", width = 15)
    @ApiModelProperty(value = "输入文件名称")
	private String inputFilename;
	/**文件预测结果*/
	@Excel(name = "文件预测结果", width = 15)
    @ApiModelProperty(value = "文件预测结果")
	private String result;
	/**运行时间*/
	@ApiModelProperty(value = "运行时间")
	private long execTime;

}
