package org.jeecg.modules.stkjob.mapper;

import java.util.List;

import io.lettuce.core.dynamic.annotation.Param;
import org.jeecg.modules.stkjob.entity.ScmNretailJobStk;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 安全库存任务
 * @Author: jeecg-boot
 * @Date:   2020-07-06
 * @Version: V1.0
 */
public interface ScmNretailJobStkMapper extends BaseMapper<ScmNretailJobStk> {
    public int insert(ScmNretailJobStk scmNretailJob);
    public Boolean update(ScmNretailJobStk scmNretailJob);
    public List<ScmNretailJobStk> jobList(ScmNretailJobStk scmNretailJob);
    public void updateTime(@Param("uuid") String uuid, @Param("time") int time);
}
