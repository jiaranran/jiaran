package org.jeecg.modules.job.mapper;

import io.lettuce.core.dynamic.annotation.Param;
import org.jeecg.modules.job.entity.ScmNretailJob;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Description: job
 * @Author: jeecg-boot
 * @Date:   2020-03-20
 * @Version: V1.0
 */
public interface ScmNretailJobMapper extends BaseMapper<ScmNretailJob> {
    public int insert(ScmNretailJob scmNretailJob);

    public Boolean update(ScmNretailJob scmNretailJob);


    public List<ScmNretailJob> jobList(ScmNretailJob scmNretailJob);

    public void updateTime(@Param("uuid") String uuid, @Param("time") int time);
}
