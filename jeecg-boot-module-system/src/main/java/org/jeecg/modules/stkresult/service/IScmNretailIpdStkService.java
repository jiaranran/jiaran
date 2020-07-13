package org.jeecg.modules.stkresult.service;

import org.jeecg.modules.job.entity.ScmNretailJob;
import org.jeecg.modules.stkresult.entity.ScmNretailIpdStk;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * @Description: 安全库存结果
 * @Author: jeecg-boot
 * @Date:   2020-07-01
 * @Version: V1.0
 */
public interface IScmNretailIpdStkService extends IService<ScmNretailIpdStk> {
    public void truncate(String database);
    public void save(ArrayList<ScmNretailIpdStk> list, String database);
    public List<ScmNretailIpdStk> resultLists(String database, ScmNretailIpdStk scmResult);
    public Future<Boolean> predict(Double dependRate, String uuid, String cntreadPath, String timereadPath, String readfileName, String outFilepath, String logFilepath, String pythonCommand, String beforeCommant) ;
}
