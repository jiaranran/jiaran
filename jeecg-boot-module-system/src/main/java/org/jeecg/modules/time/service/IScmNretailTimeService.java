package org.jeecg.modules.time.service;

import org.jeecg.modules.time.entity.ScmNretailTime;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 提前期
 * @Author: jeecg-boot
 * @Date:   2020-06-22
 * @Version: V1.0
 */
public interface IScmNretailTimeService extends IService<ScmNretailTime> {
    public void save(List<ScmNretailTime> scmTimes, String database);
    public void truncate(String database);

    List<ScmNretailTime> getAll(String database);
}
