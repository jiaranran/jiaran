package org.jeecg.modules.result.service;

import org.jeecg.modules.job.entity.ScmNretailJob;
import org.jeecg.modules.result.entity.ScmNretailResult;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.result.vo.ScmNretailQuotaVo;
import org.jeecg.modules.result.vo.ScmNretailResultVo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

/**
 * @Description: 预测结果
 * @Author: jeecg-boot
 * @Date:   2020-03-16
 * @Version: V1.0
 */
public interface IScmNretailResultService extends IService<ScmNretailResult> {

    public void truncate(String database);
    public void save(ArrayList<ScmNretailResult> list, String database);
    public Boolean save2(ArrayList<Future> list, ScmNretailJob job, String database);
    public List<ScmNretailResultVo> resultLists(String database, ScmNretailResult result);
    public List<ScmNretailResultVo> resultLists2(String database, ScmNretailResult result);
    public boolean update(String database);
    public List<ScmNretailQuotaVo> countByEval(String database);
    public List<Map<String,Object>> evalList();
    Future<Boolean> predict(String uuid, String readPath, String readfileName, String outFilepath, String logFilepath, String pythonCommand, String beforeCommant, int startnum);
    public List<Map<String,Object>> evalpmList(String matnr, String prov, String database);
}
