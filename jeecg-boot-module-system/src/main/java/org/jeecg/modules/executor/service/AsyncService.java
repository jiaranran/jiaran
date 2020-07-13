package org.jeecg.modules.executor.service;


import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Future;

public interface AsyncService {
 
/**
      * 执行异步任务
      */

        void executeAsync(CyclicBarrier cb) throws BrokenBarrierException, InterruptedException;
        public Future<String> doTaskTwo(int data);

}