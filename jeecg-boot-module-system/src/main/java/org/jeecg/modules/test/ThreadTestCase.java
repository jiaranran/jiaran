package org.jeecg.modules.test;

import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.executor.service.AsyncService;
import org.jeecg.modules.util.PythonCommand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 单元测试
 *
 * @author LarryKoo (larrykoo@126.com)
 * @date 2018/08/03 14:21
 * @slogon 站在巨人的肩膀上
 * @since 1.0.0
 */
@Slf4j
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class ThreadTestCase {

    @Autowired
    private AsyncService asyncService;
    @Autowired
    private PythonCommand pythonCommand;

    @Test
    public void testRunTaskTwo() throws ExecutionException, InterruptedException, BrokenBarrierException {
        log.info("================= run_task_two =======================");

        log.info("开始请求任务2");
        Stopwatch sw = Stopwatch.createStarted();
        //调用service层的任务
        CyclicBarrier cb = new CyclicBarrier(2);//注意：10个子线程 + 1个主线程
        asyncService.executeAsync(cb);
        cb.await();
        Future<String> feature = asyncService.doTaskTwo(1);
        while (true) {
            if (feature.isDone()) {
                log.info("完成请求任务2，返回结果：{}， 耗时：{}", feature.get(), sw.stop());
                break;
            }
        }

    }

    @Test
    public void testRunTaskTwoPlus() throws ExecutionException, InterruptedException, TimeoutException, IOException {
        log.info("================= run_task_two_plus =======================");

        log.info("开始请求任务2");
        List<Future<Map<String, Object>>> list = new ArrayList();
        for(int i=0;i<10;i++){
           /* Future<Map<String, Object>> future = pythonCommand.predict("D:\\\\Desktop\\\\office\\\\out\\\\", i+"scm_nretail_ipw_002127096701003000_440000.csv", "D:\\\\Desktop\\\\office\\\\result\\\\", "/opt/anaconda3/bin/python /root/cmdc/bin/python/main_online_go.py", "/root/cmdc/bin/python");

            pythonCommand.readresult(future);*/
        }
//        pythonCommand.readresult(list);
        Thread.sleep(100000);
        /*for (Future feature:list) {
            Stopwatch sw = Stopwatch.createStarted();
            Object o = feature.get(100, TimeUnit.SECONDS);
            log.info("完成请求任务2，返回结果：{}， 耗时：{}", String.valueOf(o), sw.stop());
        }*/
    }
}
