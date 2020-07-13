package org.jeecg.modules.input.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.input.entity.ScmNretailIpwResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 输入文件预测结果
 * @Author: jeecg-boot
 * @Date:   2020-06-11
 * @Version: V1.0
 */
public interface ScmNretailIpwResultMapper extends BaseMapper<ScmNretailIpwResult> {

    void truncate();
}
