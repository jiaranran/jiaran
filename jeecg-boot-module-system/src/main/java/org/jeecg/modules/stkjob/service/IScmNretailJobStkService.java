package org.jeecg.modules.stkjob.service;
import org.jeecg.modules.stkjob.entity.ScmNretailJobStk;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * @Description: 安全库存任务
 * @Author: jeecg-boot
 * @Date:   2020-07-06
 * @Version: V1.0
 */
public interface IScmNretailJobStkService extends IService<ScmNretailJobStk> {
    public Boolean update(ScmNretailJobStk job, String database);
    public void updateTime(String uuid, int l, String database);
    public List<Map<String, Object>> jobList(ScmNretailJobStk job, String database);
    public void add(String cntfileName, String timefileName, String uuid, String database);
    void updateState(ScmNretailJobStk job, String database, ArrayList<Future<Boolean>> list);
    void updateEndTime(String uuid, Date date, String database);
}
