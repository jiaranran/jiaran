package org.jeecg.modules.executor.controller;

import org.jeecg.modules.executor.service.AsyncService;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;


@RestController
@RequestMapping(value = "/executor", method = RequestMethod.GET)
public class ExecutorController {
 
    private static final Logger logger = LoggerFactory.getLogger(ExecutorController.class);

    @Autowired
    private AsyncService asyncService;

    @RequestMapping("/ececutor")

    public String submit() throws BrokenBarrierException, InterruptedException {

    logger.info("start submit");


    //调用service层的任务

    CyclicBarrier cb = new CyclicBarrier(2);//注意：10个子线程 + 1个主线程
    asyncService.executeAsync(cb);


    logger.info("end submit");

    return "success";

    }

}