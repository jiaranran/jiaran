package org.jeecg.modules.stkresult.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.csvreader.CsvReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.jeecg.modules.input.entity.ScmNretailIpwResult;
import org.jeecg.modules.input.service.impl.ScmNretailIpwResultServiceImpl;
import org.jeecg.modules.stkjob.entity.ScmNretailJobStk;
import org.jeecg.modules.stkjob.service.IScmNretailJobStkService;
import org.jeecg.modules.stkresult.entity.ScmNretailIpdStk;
import org.jeecg.modules.stkresult.mapper.ScmNretailIpdStkMapper;
import org.jeecg.modules.stkresult.service.IScmNretailIpdStkService;
import org.jeecg.modules.util.FileUtil;
import org.jeecg.modules.util.PythonCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description: 安全库存结果
 * @Author: jeecg-boot
 * @Date:   2020-07-01
 * @Version: V1.0
 */
@Slf4j
@Service
@DS("predict")
public class ScmNretailIpdStkServiceImpl extends ServiceImpl<ScmNretailIpdStkMapper, ScmNretailIpdStk> implements IScmNretailIpdStkService {
    @Autowired
    ScmNretailIpdStkMapper scmNretailIpdStkMapper;
    @Autowired
    IScmNretailJobStkService scmNretailJobStkService;
    @Autowired
    ScmNretailIpwResultServiceImpl scmNretailIpwResultService;
    @Autowired
    private SqlSessionFactory sqlSessionFactory;
    //    @Autowired
//    @Qualifier(value = "secondaryMongoTemplate")
    private MongoTemplate secondaryMongoTemplate;
    //    @Autowired
//    @Qualifier(value = "predictMongoTemplate")
    private MongoTemplate predictMongoTemplate;
    @Autowired
    private PythonCommand pc;
    public static int success_num;
    public static int fail_num;
    public static int presuccess_num;
    public static int prefail_num;
    private static Lock lock = new ReentrantLock();
    @Override
    public void truncate(String database) {
        if(database.equals("mysql")){
            scmNretailIpdStkMapper.truncate();
        }

    }

    @Override
    @Async("asyncServiceExecutor")
    public Future<Boolean> predict(Double dependRate, String uuid, String cntreadFilePath,String timereadFilePath, String readfileName, String outFilepath, String logFilepath, String pythonCommand, String beforeCommant) {
        String database="mysql";
        ScmNretailJobStk job = new ScmNretailJobStk();
        int index = readfileName.lastIndexOf(".");
        String prov = readfileName.substring(index - 6, index-2);
        File file;
        if(dependRate==null){dependRate=0.93;}

        ScmNretailIpwResult result = new ScmNretailIpwResult();
        result.setJobUuid(uuid);
        result.setInputFilename(readfileName);
        long start = System.currentTimeMillis();
        Boolean b = pc.predict(dependRate,cntreadFilePath,timereadFilePath, readfileName, outFilepath,logFilepath, pythonCommand, beforeCommant);
//        lock.lock();
        try {
            if(b){
                job.setUuid(uuid);
                job.setJobinfo(++presuccess_num +"个安全库存脚本执行成功，"+prefail_num +"个安全库存脚本执行失败");
                scmNretailJobStkService.update(job,database);
                prov = readfileName.substring(index - 6, index);
                file = new File(outFilepath+prov+File.separator+readfileName);
                if(!file.exists()){
                    job.setUuid(uuid);
                    job.setJobinfo(success_num +"个解析结果文件成功，"+ ++fail_num +"个解析结果文件失败");
                    scmNretailJobStkService.update(job,database);
                    log.info(outFilepath+prov+File.separator+readfileName+"结果文件不存在");
                    result.setResult("结果文件不存在");
                    long end = System.currentTimeMillis();
                    result.setExecTime((end-start)/1000);
                    scmNretailIpwResultService.save(result);
//                    lock.unlock();
                    return new AsyncResult<>(false);
                }
                FileUtil.checkCsv(file);
                ArrayList<ScmNretailIpdStk> list = new ArrayList<>();
                CsvReader csvReader = new CsvReader(outFilepath+prov+File.separator+readfileName, ',', Charset.forName("UTF-8"));
                // 如果你的文件没有表头，这行不用执行
                // 这行不要是为了从表头的下一行读，也就是过滤表头
                csvReader.readHeaders();
                while (csvReader.readRecord()) {
                    ScmNretailIpdStk scmNretailIpdStk = new ScmNretailIpdStk();
                    scmNretailIpdStk.setMatnr(csvReader.get("matnr"));
                    scmNretailIpdStk.setProv(csvReader.get("prov"));
                    if(csvReader.get("平均需求量")==null||csvReader.get("平均需求量").isEmpty()){
                        scmNretailIpdStk.setDemandAvg(null);
                    }else {
                        scmNretailIpdStk.setDemandAvg(BigDecimal.valueOf(Double.parseDouble(csvReader.get("平均需求量"))));

                    }
                    if(csvReader.get("需求量的标准差")==null||csvReader.get("需求量的标准差").isEmpty()){
                        scmNretailIpdStk.setDemandMse(null);
                    }else {
                        scmNretailIpdStk.setDemandMse(BigDecimal.valueOf(Double.parseDouble(csvReader.get("需求量的标准差"))));

                    }
                    if(csvReader.get("提前期的平均值")==null||csvReader.get("提前期的平均值").isEmpty()){
                        scmNretailIpdStk.setTimeAvg(null);
                    }else {
                        scmNretailIpdStk.setTimeAvg(BigDecimal.valueOf(Double.parseDouble(csvReader.get("提前期的平均值"))));

                    }
                    if(csvReader.get("提前期的标准差")==null||csvReader.get("提前期的标准差").isEmpty()){
                        scmNretailIpdStk.setTimeMse(null);
                    }else {
                        scmNretailIpdStk.setTimeMse(BigDecimal.valueOf(Double.parseDouble(csvReader.get("提前期的标准差"))));

                    }
                    if(csvReader.get("对应z值或者B值")==null||csvReader.get("对应z值或者B值").isEmpty()){
                        scmNretailIpdStk.setZbValue(null);
                    }else {
                        scmNretailIpdStk.setZbValue(BigDecimal.valueOf(Double.parseDouble(csvReader.get("对应z值或者B值"))));

                    }
                    if(csvReader.get("安全库存")==null||csvReader.get("安全库存").isEmpty()){
                        scmNretailIpdStk.setSafeStock(null);
                    }else {
                        scmNretailIpdStk.setSafeStock(BigDecimal.valueOf(Double.parseDouble(csvReader.get("安全库存"))));

                    }if(csvReader.get("是否超限")==null||csvReader.get("是否超限").isEmpty()){
                        scmNretailIpdStk.setOutLimit(null);
                    }else {
                        String outlimit = csvReader.get("是否超限");
                        if(outlimit.equals("0")){
                            scmNretailIpdStk.setOutLimit("否");
                        }else {
                            scmNretailIpdStk.setOutLimit("是");
                        }
                    }
                    scmNretailIpdStk.setDistribution(csvReader.get("符合的分布"));

                    list.add(scmNretailIpdStk);
                }
                save(list,database);
                //将实际值填充到结果表
                log.info(readfileName+"解析结果文件成功");

                job.setUuid(uuid);
                job.setJobinfo(++success_num +"个解析结果文件成功，"+fail_num+"个解析结果文件失败");
                scmNretailJobStkService.update(job,database);
                long end = System.currentTimeMillis();
                result.setExecTime((end-start)/1000);
                result.setResult("解析结果文件成功");
                scmNretailIpwResultService.save(result);
//                lock.unlock();
                return new AsyncResult<>(true);
            }else {
                log.error(presuccess_num +"个安全库存脚本执行成功，"+ ++prefail_num +"个安全库存脚本执行失败");
                job.setUuid(uuid);
                job.setJobinfo(presuccess_num +"个安全库存脚本执行成功，"+ prefail_num +"个安全库存脚本执行失败");
                scmNretailJobStkService.update(job,database);
                long end = System.currentTimeMillis();
                result.setExecTime((end-start)/1000);
                result.setResult("安全库存脚本执行失败");
                scmNretailIpwResultService.save(result);
//                lock.unlock();
                return new AsyncResult<>(false);
            }

        } catch (Exception e) {
            e.printStackTrace();
            job.setUuid(uuid);
            job.setJobinfo(success_num +"个解析结果文件成功，"+ ++fail_num +"个解析结果文件失败");
            scmNretailJobStkService.update(job,database);
            long end = System.currentTimeMillis();
            result.setExecTime((end-start)/1000);
            result.setResult("解析结果文件失败");
            scmNretailIpwResultService.save(result);
            log.error(readfileName+"解析结果文件失败");
//            lock.unlock();
            return new AsyncResult<>(false);
        }
    }

    @Override
    @Transactional(timeout = 1200)
    public void save(ArrayList<ScmNretailIpdStk> list, String database) {
        for (ScmNretailIpdStk res :list) {
            HashMap<String, Object> map = new HashMap<>();
            if(res!=null){
                map.put("matnr",res.getMatnr());
                map.put("prov",res.getProv());
            }

            if(database.equals("mysql")){
                List<ScmNretailIpdStk> scmNretailResults = scmNretailIpdStkMapper.selectByMap(map);
                if(scmNretailResults!=null&&scmNretailResults.size()>0){
                    scmNretailIpdStkMapper.deleteById(scmNretailResults.get(0).getId());
                    System.out.println(res.getMatnr()+res.getProv()+"删除成功");
                }
            }
        }
        if(database.equals("mysql")){
            super.saveBatch(list);
        }

    }

    @Override
    public List<ScmNretailIpdStk> resultLists(String database, ScmNretailIpdStk scmResult) {
        List<ScmNretailIpdStk> list = null;
        if(database.equals("mysql")){
            list = scmNretailIpdStkMapper.list(scmResult);
            if(list==null){
                return null;
            }
        }
        return list;
    }

}
