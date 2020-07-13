package org.jeecg.modules.executor.service.impl;

import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.executor.service.AsyncService;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Future;


@Service
@EnableAsync
@Slf4j
public class AsyncServiceImpl implements AsyncService {
 

    private static final Logger logger = LoggerFactory.getLogger(AsyncServiceImpl.class);


    @Override
    @Async("asyncServiceExecutor")

    public void executeAsync(CyclicBarrier cb) throws BrokenBarrierException, InterruptedException {

    logger.info("start executeAsync");

    try{

    Thread.sleep(2000);


    }catch(Exception e){

    e.printStackTrace();

    }

    logger.info("end executeAsync");
    cb.await();
    }

    /**
     * 有返回结果，通过 AsyncResult 异步获取
     *
     * @param data
     * @return
     */
    @Override
    @Async("asyncServiceExecutor")
    public Future<String> doTaskTwo(int data) {
        try {
            log.info("开始做任务"+data);
            Stopwatch sw = Stopwatch.createStarted();

            Thread.sleep(50000);


            log.info(data+"完成任务， 耗时：{}", sw.stop());

            return new AsyncResult<>(String.valueOf(data));
        } catch (InterruptedException  e) {
            e.printStackTrace();
        }
        return null;
    }

}
