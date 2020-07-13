package org.jeecg.modules.predict.job;

import org.jeecg.modules.util.FileUtil;
import org.jeecg.modules.util.ResouceUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Map;
@EnableScheduling
@Component
public class ScmNretailJob  {
    @Autowired
    ResourceLoader resourceLoader;
//0 /1 * * ?
    @Scheduled(cron = "0 0 10 30 * ?")
    public void execute() throws JobExecutionException {
        Map<String,String> resource =  new ResouceUtil().getResource(resourceLoader,"classpath:jeecg\\dataPath.properties");
        String sourceFilePath = resource.get("sourceFilePath");
//        sourceFilePath= "D:\\Desktop\\office\\lingshou\\test";
        try {
            System.out.println("清理原始数据");
            FileUtil.deletefile(sourceFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
