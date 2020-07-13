package org.jeecg.modules.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import freemarker.log.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * 处理流线程
 *
 * @version 1.0
 * @since JDK1.8
 * @author weijs
 * @date 2018年9月28日 下午1:31:59
 */
public class StreamThread extends Thread {

    private Logger logger = Logger.getLogger(String.valueOf(getClass()));
    // 输入流
    private InputStream inputStream;
    // 流类型
    private String streamType;
    // 是否运行完成
    private volatile boolean inFinish = false;
    // 需要的返回结果数据
    private Map<String, JSONArray> resultMap;

    /**
     * 构造器
     *
     * @param inputStream 输入流
     * @param streamType 流类型
     */
    public StreamThread(InputStream inputStream, String streamType) {
        this.inputStream = inputStream;
        this.streamType = streamType;
        this.inFinish = false;
        this.resultMap = new HashMap<>();
    }

    /**
     * 重写run()方法
     */
    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(isr);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (streamType.equals("Error")) {
                    logger.info("python_log_err : " + line);
                } else {
                    logger.info("python_log_out : " + line);
                    if (line.contains("[{")) {// Python返回的json数据
                        logger.debug("---------最想要的结果：" + line);
                        if (line.contains("back_result:")) {
                            String back = line.substring(line.indexOf("[{"), line.length());
                            JSONArray fromObject = JSONObject.parseArray(back);
                            resultMap.put("back", fromObject);
                        }
                        if (line.contains("filter_result:")) {
                            String string = line.substring(line.indexOf("[{"), line.length());
                            JSONArray fromObject = JSONObject.parseArray(string);
                            resultMap.put("filter", fromObject);
                        }
                        if (line.contains("buy_result:")) {
                            String string = line.substring(line.indexOf("[{"), line.length());
                            JSONArray fromObject = JSONObject.parseArray(string);
                            resultMap.put("buy", fromObject);
                        }
                    }
                }
            }
            isr.close();
            bufferedReader.close();
        } catch (IOException e) {
            logger.error("Exception:" + e);
            e.printStackTrace();
        } finally {
            this.inFinish = true;
            synchronized (this) {
                notify();
            }
        }
    }

    /**
     *
     * 返回结果
     *
     * @return
     *
     * @author weijs
     * @date 2018年9月28日 下午1:37:42
     */
    public Map<String, JSONArray> getContent() {
        if (!this.inFinish) {
            synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException ignore) {
                    ignore.printStackTrace();
                }
            }
        }
        return this.resultMap;
    }

}


