package org.jeecg.modules.stkjob.service.impl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.result.mapper.ScmNretailResultMapper;
import org.jeecg.modules.stkjob.entity.ScmNretailJobStk;
import org.jeecg.modules.stkjob.mapper.ScmNretailJobStkMapper;
import org.jeecg.modules.stkjob.service.IScmNretailJobStkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.*;
import java.util.concurrent.Future;

/**
 * @Description: 安全库存任务
 * @Author: jeecg-boot
 * @Date:   2020-07-06
 * @Version: V1.0
 */
@Slf4j
@Service
public class ScmNretailJobStkServiceImpl extends ServiceImpl<ScmNretailJobStkMapper, ScmNretailJobStk> implements IScmNretailJobStkService {
    @Autowired
    ScmNretailJobStkMapper scmNretailJobStkMapper;
    @Autowired
    ScmNretailResultMapper scmNretailResultMapper;

    @Override
    public Boolean update(ScmNretailJobStk job, String database) {
        return scmNretailJobStkMapper.update(job);
    }

    @Override
    public void updateTime(String uuid, int time, String database) {
        if(database.equals("mysql")){
            scmNretailJobStkMapper.updateTime(uuid,time);
        }
    }

    public List<Map<String, Object>> jobList(ScmNretailJobStk job, String database){
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        List<ScmNretailJobStk> scmNretailJobs=null;
        if(database.equals("mysql")){
            scmNretailJobs = scmNretailJobStkMapper.jobList(job);
        }

        if(scmNretailJobs!=null){
            for (ScmNretailJobStk scmNretailJob:scmNretailJobs) {
                HashMap map = new HashMap();
                map.put("id",scmNretailJob.getId());
                map.put("cntfilename",scmNretailJob.getCntFilename());
                map.put("timefilename",scmNretailJob.getTimeFilename());
                map.put("state",scmNretailJob.getState());
                map.put("jobinfo",scmNretailJob.getJobinfo());
                map.put("creatime",scmNretailJob.getCreatetime());
                map.put("endtime",scmNretailJob.getEndtime());
//                map.put("time",scmNretailJob.getTime());
                list.add(map);
            }
        }

        return list;
    }

    @Override
    public void add(String cntfileName,String timefileName, String uuid, String database) {
        ScmNretailJobStk scmNretailJob = new ScmNretailJobStk();
        scmNretailJob.setUuid(uuid);
        scmNretailJob.setCntFilename(cntfileName);
        scmNretailJob.setTimeFilename(timefileName);
        scmNretailJob.setCreatetime(new Date());
        scmNretailJob.setState("running");
        scmNretailJob.setJobinfo("任务执行中");
//        scmNretailJob.setTime(0);
        if(database.equals("mysql")){
            scmNretailJobStkMapper.insert(scmNretailJob);
        }
    }

    @Override
//    @Async("asyncServiceExecutor")
    public void updateState( ScmNretailJobStk job, String database, ArrayList<Future<Boolean>> list) {
        Boolean b=true;
        try {
//            latch.await();
            for (Future<Boolean> future:list) {
                if (!future.get()){
                    b=false;
                }
            }
            if(b){
//                scmNretailResultMapper.update();
                job.setState("success");
                job.setJobinfo("安全库存任务成功");
                job.setEndtime(new Date());
                scmNretailJobStkMapper.update(job);
                log.info("安全库存任务成功");
            }else {
                job.setState("fail");
                job.setJobinfo("安全库存任务失败");
                job.setEndtime(new Date());
                scmNretailJobStkMapper.update(job);
                log.info("安全库存任务失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("安全库存任务失败");
            job.setState("fail");
            job.setJobinfo("安全库存任务失败");
            job.setEndtime(new Date());
            scmNretailJobStkMapper.update(job);
        }
    }

    @Override
    public void updateEndTime(String uuid, Date date, String database) {
        ScmNretailJobStk job = new ScmNretailJobStk();
        job.setUuid(uuid);
        job.setEndtime(new Date());
        scmNretailJobStkMapper.update(job);
    }

  
}
