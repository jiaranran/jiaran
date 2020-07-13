package org.jeecg.modules.job.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.job.entity.ScmNretailJob;
import org.jeecg.modules.job.mapper.ScmNretailJobMapper;
import org.jeecg.modules.job.service.ScmNretailJobService;
import org.jeecg.modules.result.entity.ScmNretailResult;
import org.jeecg.modules.result.mapper.ScmNretailResultMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

/**
 * @Description: job
 * @Author: jeecg-boot
 * @Date:   2020-03-20
 * @Version: V1.0
 */
@Slf4j
@Service
public class ScmNretailJobServiceImpl extends ServiceImpl<ScmNretailJobMapper, ScmNretailJob> implements ScmNretailJobService {

//    @Autowired
//    @Qualifier(value = "primaryMongoTemplate")
    private MongoTemplate primaryMongoTemplate;
    @Autowired
    ScmNretailJobMapper scmNretailJobMapper;
    @Autowired
    ScmNretailResultMapper scmNretailResultMapper;

    @Override
    public Boolean update(ScmNretailJob job, String database) {
        if(database.equals("mysql")){
           return scmNretailJobMapper.update(job);
        }else {
            String state = job.getState();
            String uuid = job.getUuid();
            updateMo(uuid,state);
            return true;
        }

    }

    @Override
    public void updateTime(String uuid, int time, String database) {
        if(database.equals("mysql")){
            scmNretailJobMapper.updateTime(uuid,time);
        }else {
            updateTimeMo(uuid,time);
        }

    }

    public List<Map<String, Object>> jobList(ScmNretailJob job,String database){
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        List<ScmNretailJob> scmNretailJobs=null;
        if(database.equals("mysql")){
            scmNretailJobs = scmNretailJobMapper.jobList(job);
        }else {
            Query query = new Query();
            query.with(new Sort(Sort.Direction.DESC,"id"));
            scmNretailJobs = primaryMongoTemplate.find(query,ScmNretailJob.class, "scm_nretail_job");
        }

        if(scmNretailJobs!=null){
            for (ScmNretailJob scmNretailJob:scmNretailJobs) {
                HashMap map = new HashMap();
                map.put("id",scmNretailJob.getId());
                map.put("filename",scmNretailJob.getFilename());
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
    public void add(String fileName, String uuid, String database) {
        ScmNretailJob scmNretailJob = new ScmNretailJob();
        scmNretailJob.setUuid(uuid);
        scmNretailJob.setFilename(fileName);
        scmNretailJob.setCreatetime(new Date());
        scmNretailJob.setState("running");
        scmNretailJob.setJobinfo("任务执行中");
//        scmNretailJob.setTime(0);
        if(database.equals("mysql")){
            scmNretailJobMapper.insert(scmNretailJob);
        }else {
            int id=0;
            Query query = new Query();
            query.with(new Sort(Sort.Direction.DESC, "id"));
            ScmNretailJob job = primaryMongoTemplate.findOne(query, ScmNretailJob.class, "scm_nretail_job");
            if(job!=null){
                 id = job.getId();
            }
            scmNretailJob.setId(++id);
            primaryMongoTemplate.insert(scmNretailJob,"scm_nretail_job");
        }

    }

    @Override
//    @Async("asyncServiceExecutor")
    public void updateState( ScmNretailJob job, String database, ArrayList<Future<Boolean>> list) {
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
                job.setJobinfo("预测任务成功");
                job.setEndtime(new Date());
                scmNretailJobMapper.update(job);
                log.info("预测任务成功");
            }else {
                job.setState("fail");
                job.setJobinfo("预测任务失败");
                job.setEndtime(new Date());
                scmNretailJobMapper.update(job);
                log.info("预测任务失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("预测任务失败");
            job.setState("fail");
            job.setJobinfo("预测任务失败");
            job.setEndtime(new Date());
            scmNretailJobMapper.update(job);
        }
    }

    @Override
    public void updateEndTime(String uuid, Date date, String database) {
        ScmNretailJob job = new ScmNretailJob();
        job.setUuid(uuid);
        job.setEndtime(new Date());
        scmNretailJobMapper.update(job);
    }

    public void updateMo(String uuid, String state) {
//        update raw.scm_nretail_job set state=#{state} where uuid=#{uuid}
        Query query = new Query();
        query.addCriteria(Criteria.where("uuid").is(uuid));
        Update update = new Update();
        update.set("state",state);
        primaryMongoTemplate.updateFirst(query,update,"scm_nretail_job");
    }
    public void updateTimeMo(String uuid, int time) {
//        update raw.scm_nretail_job set time=#{time} where uuid=#{uuid}
        Query query = new Query();
        query.addCriteria(Criteria.where("uuid").is(uuid));
        Update update = new Update();
        update.set("time",time);
        primaryMongoTemplate.updateMulti(query,update,"scm_nretail_job");
    }

}
