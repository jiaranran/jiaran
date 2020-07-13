package org.jeecg.modules.job.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

public class ScmNretailResult {
    /**id*/
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private Integer id;
    /**job_uuid*/
    @Excel(name = "job_uuid", width = 15)
    @ApiModelProperty(value = "job_uuid")
    private String job_uuid;

    /**输入文件名称*/
    @Excel(name = "输入文件名称", width = 15)
    @ApiModelProperty(value = "输入文件名称")
    private String input_filename;

    @Excel(name = "执行结果", width = 15)
    @ApiModelProperty(value = "执行结果")
    private String result;

}
