package org.jeecg.modules.time.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.time.entity.ScmNretailTime;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 提前期
 * @Author: jeecg-boot
 * @Date:   2020-06-22
 * @Version: V1.0
 */
public interface ScmNretailTimeMapper extends BaseMapper<ScmNretailTime> {

    void truncate();

    List<ScmNretailTime> getAll();
}
