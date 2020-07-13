package org.jeecg.modules.job.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.SpringContextUtils;
import org.jeecg.modules.job.entity.ScmNretailJob;
import org.jeecg.modules.job.service.ScmNretailJobService;
import org.jeecg.modules.predict.controller.ScmNretailController;
import org.jeecg.modules.util.FileUtil;
import org.jeecg.modules.util.ResouceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
* @Description: job
* @Author: jeecg-boot
* @Date:   2020-03-20
* @Version: V1.0
*/
@Slf4j
@Api(tags="job")
@RestController
@RequestMapping("/job/scmNretailJob")
public class ScmNretailJobController {
   @Autowired
   private ScmNretailJobService scmNretailJobService;
    @Autowired
    ResourceLoader resourceLoader;
   @GetMapping(value = "/stopTask")
   public Result<?> remove (){
       ThreadPoolTaskExecutor asyncServiceExecutor = (ThreadPoolTaskExecutor) SpringContextUtils.getBean("asyncServiceExecutor");
       ThreadPoolExecutor threadPoolExecutor = asyncServiceExecutor.getThreadPoolExecutor();
       System.out.println("最大线程个数"+threadPoolExecutor.getMaximumPoolSize());
       System.out.println("核心线程个数"+threadPoolExecutor.getCorePoolSize());
       System.out.println("活动线程个数"+threadPoolExecutor.getActiveCount());
       BlockingQueue<Runnable> queue = threadPoolExecutor.getQueue();
       System.out.println("队列线程个数"+queue.size());
       System.out.println("总任务个数"+threadPoolExecutor.getTaskCount());
       System.out.println("已完成线程个数"+threadPoolExecutor.getCompletedTaskCount());
       int size = queue.size();
       System.out.println(queue.size());
       for (int i = 0; i < size; i++) {
           queue.remove();
       }

//		threadPoolExecutor.shutdownNow();
       Runtime runtime = Runtime.getRuntime();
       String command="ps -ef | grep main_online_go.py | grep -v grep | cut -c 9-15 | xargs kill -s 9";
       log.info("exec__command : " + command);
       String []cmdArray = new String[]{ "/bin/sh", "-c", command};
       try {
           // 异常日志
           Process process = runtime.exec(cmdArray);
           InputStreamReader isrerr = new InputStreamReader(process.getErrorStream());
           BufferedReader brerr = new BufferedReader(isrerr);
           String lineerr = null;
           while((lineerr = brerr.readLine()) != null) {
               log.info("python_log_err : " + lineerr);
           }
           // 正常日志
           InputStreamReader isr = new InputStreamReader(process.getInputStream());
           BufferedReader br = new BufferedReader(isr);
           String line = null;
           while((line = br.readLine()) != null) {
               log.info("python_log_out : " + line);
           }
           OutputStream outputStream = process.getOutputStream();
           int i = process.waitFor();
           System.out.println("-----------------------"+i);
           System.out.println("队列线程个数"+queue.size());
           System.out.println("活动线程个数"+threadPoolExecutor.getActiveCount());
       } catch (Exception e) {
           e.printStackTrace();
           return Result.error("任务停止失败");
       }
       return Result.ok("任务停止成功");
   }
    @RequestMapping(value = "/startTask", method = RequestMethod.POST)
    public Result<?> startTask(@RequestParam(name = "weekStart", required = false) Integer  weekStart, @RequestParam(name = "fileName", required = false) String fileName, @RequestParam(name = "startnum", required = false) Integer startnum) {
        Map<String, String> resource = new ResouceUtil().getResource(resourceLoader, "classpath:jeecg\\dataPath.properties");
        String sourceFilePath = resource.get("sourceFilePath");
        Map<String, String> resource2 = new ResouceUtil().getResource(resourceLoader, "classpath:jeecg\\jeecg_database.properties");
        String database = resource2.get("database");
        //创建任务
        String uuid = createTask(fileName,database);
        log.info("------------------"+uuid);
        if (uuid == null) {return null;}
        ScmNretailJob job = new ScmNretailJob();
        job.setUuid(uuid);
       //	文件名校验
        Boolean fileCheck = fileCheck(uuid,sourceFilePath, fileName, database);
        if (!fileCheck) {return jobList( database, job);}

        ScmNretailController scmNretailController = SpringContextUtils.getBean(ScmNretailController.class);
        scmNretailController.startTask( fileName, uuid, weekStart, startnum);
        return jobList( database, job);
    }
    @RequestMapping(value = "/jobList", method = RequestMethod.GET)
    public Result<?> jobList(@RequestParam(name = "database", defaultValue = "mysql") String database, ScmNretailJob job) {
        List<Map<String, Object>> list = scmNretailJobService.jobList(job,database);
        return Result.ok(list);
    }
    public String createTask(String fileName, String database){
        if(fileName==null||fileName==""){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            fileName = "scm_nretail"+sdf.format(new Date())+".xlsx";
        }
        UUID randomUUID = UUID.randomUUID();
        String uuid = randomUUID.toString();
        scmNretailJobService.add(fileName,uuid,database);
        return uuid;
    }
    public Boolean fileCheck(String uuid,String sourceFilePath,String fileName, String database){
        ScmNretailJob job = new ScmNretailJob();
        if(fileName==null||fileName==""){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//			fileName="scm_nretail"+DateUtils.formatDate(new Date());
            fileName = "scm_nretail_"+sdf.format(new Date())+".xlsx";
        }
        File file = new File(sourceFilePath+fileName);

        try {
            FileUtil.check(fileName);
            if(!file.exists()){
                log.error(sourceFilePath+fileName+"文件不存在");
                job.setUuid(uuid);
                job.setState("fail");
                job.setJobinfo(sourceFilePath+fileName+"文件不存在");
                scmNretailJobService.update(job,database);
                return false;
            }
            return true;
        } catch (Exception e) {
            job.setUuid(uuid);
            job.setState("fail");
            job.setJobinfo(fileName+"文件必须为Excel格式");
            scmNretailJobService.update(job,database);
            e.printStackTrace();
            return false;
        }
    }

}
