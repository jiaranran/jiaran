package org.jeecg.modules.job.service;

import org.jeecg.modules.job.entity.ScmNretailJob;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

/**
 * @Description: job
 * @Author: jeecg-boot
 * @Date:   2020-03-20
 * @Version: V1.0
 */
public interface ScmNretailJobService extends IService<ScmNretailJob> {

    public Boolean update(ScmNretailJob job, String database);

    public void updateTime(String uuid, int l, String database);

    public List<Map<String, Object>> jobList(ScmNretailJob job, String database);

    public void add(String fileName, String uuid, String database);

    void updateState(ScmNretailJob job, String database, ArrayList<Future<Boolean>> list);

    void updateEndTime(String uuid, Date date, String database);
}
