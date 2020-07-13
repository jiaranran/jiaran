package org.jeecg.modules.result.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csvreader.CsvReader;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.jeecg.modules.input.entity.ScmNretailIpwResult;
import org.jeecg.modules.input.service.impl.ScmNretailIpwResultServiceImpl;
import org.jeecg.modules.job.entity.ScmNretailJob;
import org.jeecg.modules.job.service.ScmNretailJobService;
import org.jeecg.modules.result.entity.ScmNretailResult;
import org.jeecg.modules.result.mapper.ScmNretailResultMapper;
import org.jeecg.modules.result.service.IScmNretailResultService;
import org.jeecg.modules.result.vo.ScmNretailQuotaVo;
import org.jeecg.modules.result.vo.ScmNretailResultVo;
import org.jeecg.modules.util.FileUtil;
import org.jeecg.modules.util.PythonCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.jeecg.common.util.DateUtils.str2Date;

/**
 * @Description: 预测结果
 * @Author: jeecg-boot
 * @Date:   2020-03-16
 * @Version: V1.0
 */
@Slf4j
@Service
@DS("predict")
public class ScmNretailResultServiceImpl extends ServiceImpl<ScmNretailResultMapper, ScmNretailResult> implements IScmNretailResultService {
    @Autowired
    ScmNretailResultMapper scmNretailResultMapper;
    @Autowired
    ScmNretailJobService scmNretailJobService;
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
            scmNretailResultMapper.truncate();
        }else {
            truncateMo();
        }

    }

    @Override
    @Async("asyncServiceExecutor")
    public Future<Boolean> predict(String uuid,String readPath, String readfileName, String outFilepath,String logFilepath, String pythonCommand, String beforeCommant,int startnum) {
        ScmNretailIpwResult result = new ScmNretailIpwResult();
        result.setJobUuid(uuid);
        result.setInputFilename(readfileName);
        long start = System.currentTimeMillis();
        String database="mysql";
        Boolean b = pc.predict(readPath, readfileName, outFilepath,logFilepath, pythonCommand, beforeCommant, startnum);
        ScmNretailJob job = new ScmNretailJob();
//        lock.lock();
        try {
            if(b){
                job.setUuid(uuid);
                job.setJobinfo(++presuccess_num +"个预测脚本执行成功，"+prefail_num +"个预测脚本执行失败");
                scmNretailJobService.update(job,database);
                int index = readfileName.lastIndexOf(".");
                String prov = readfileName.substring(index - 6, index);
                File file = new File(outFilepath+prov+File.separator+readfileName);
                if(!file.exists()){
                    job.setUuid(uuid);
                    job.setJobinfo(success_num +"个解析结果文件成功，"+ ++fail_num +"个解析结果文件失败");
                    scmNretailJobService.update(job,database);
                    log.info(outFilepath+prov+File.separator+readfileName+"结果文件不存在");
                    result.setResult("结果文件不存在");
                    long end = System.currentTimeMillis();
                    result.setExecTime((end-start)/1000);
                    scmNretailIpwResultService.save(result);
//                    lock.unlock();
                    return new AsyncResult<>(false);
                }
                FileUtil.checkCsv(file);
                ArrayList<ScmNretailResult> list = new ArrayList<>();
                CsvReader csvReader = new CsvReader(outFilepath+prov+File.separator+readfileName, ',', Charset.forName("UTF-8"));
                // 如果你的文件没有表头，这行不用执行
                // 这行不要是为了从表头的下一行读，也就是过滤表头
                csvReader.readHeaders();
                while (csvReader.readRecord()) {
                    ScmNretailResult scmNretailResult = new ScmNretailResult();
                    scmNretailResult.setMatnr(csvReader.get("matnr"));
                    scmNretailResult.setProv(csvReader.get("prov"));
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date ds = str2Date(csvReader.get("ds"), simpleDateFormat);
                    scmNretailResult.setDs(ds);
                    scmNretailResult.setPred(Double.parseDouble(csvReader.get("pre_cnt1")));
                    if(csvReader.get("eval")!=null&&csvReader.get("eval")!=""){
                        scmNretailResult.setEval(csvReader.get("eval"));
//                        scmNretailResult.setEval(Double.parseDouble(csvReader.get("eval")));
                    }
                    list.add(scmNretailResult);
                }
                save(list,database);
                //将实际值填充到结果表
                update(database);
                log.info(readfileName+"解析结果文件成功");

                job.setUuid(uuid);
                job.setJobinfo(++success_num +"个解析结果文件成功，"+fail_num+"个解析结果文件失败");
                scmNretailJobService.update(job,database);
                long end = System.currentTimeMillis();
                result.setExecTime((end-start)/1000);
                result.setResult("解析结果文件成功");
                scmNretailIpwResultService.save(result);
//                lock.unlock();
                return new AsyncResult<>(true);
            }else {
                job.setUuid(uuid);
                job.setJobinfo(presuccess_num +"个预测脚本执行成功，"+ ++prefail_num +"个预测脚本执行失败");
                scmNretailJobService.update(job,database);
                long end = System.currentTimeMillis();
                result.setExecTime((end-start)/1000);
                result.setResult("预测脚本执行失败");
                scmNretailIpwResultService.save(result);
//                lock.unlock();
                return new AsyncResult<>(false);
            }

        } catch (Exception e) {
            e.printStackTrace();
            job.setUuid(uuid);
            job.setJobinfo(success_num +"个解析结果文件成功，"+ ++fail_num +"个解析结果文件失败");
            scmNretailJobService.update(job,database);
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
    public void save(ArrayList<ScmNretailResult> list, String database) {
        for (ScmNretailResult res :list) {
            HashMap<String, Object> map = new HashMap<>();
            if(res!=null){
                map.put("matnr",res.getMatnr());
                map.put("prov",res.getProv());
                map.put("ds",res.getDs());
            }

            if(database.equals("mysql")){
                List<ScmNretailResult> scmNretailResults = scmNretailResultMapper.selectByMap(map);
                if(scmNretailResults!=null&&scmNretailResults.size()>0){
                    scmNretailResultMapper.deleteById(scmNretailResults.get(0).getId());
                    System.out.println(res.getMatnr()+res.getProv()+"删除成功");
                }
            }else {
                deleteMo(res.getMatnr(),res.getProv(),res.getDs());
            }
        }
        if(database.equals("mysql")){
            super.saveBatch(list);
        }else {
            saveVo(list);
        }

    }
    @Override
    @Async("asyncServiceExecutor")
    public Boolean save2(ArrayList<Future> list, ScmNretailJob job, String database) {
        Boolean b=false;
        ArrayList<ScmNretailResult> allList = new ArrayList<>();
        for (Future<List<ScmNretailResult>> future:list) {
            List<ScmNretailResult> results = null;
            try {
                results = future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (results!=null){
                allList.addAll(results);
            }
        }
        if(allList!=null){
            try(SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH,false);) {
                //由于数据库对于插入字段的限制，在这里对批量插入的数据进行分批处理
                long start = System.currentTimeMillis();
                for (int i = 0; i < allList.size() - 1;i++) {
                    scmNretailResultMapper.insert(allList.get(i));
                    if(i%10000==9999){
                        sqlSession.commit();
                        sqlSession.clearCache();
                    }
                }
                sqlSession.commit();
                sqlSession.clearCache();
                b=true;
                System.out.println("批处理执行时间"+(System.currentTimeMillis()-start)/1000+"秒");
                job.setState("sucess");
                job.setJobinfo("结果文件入库成功");
                scmNretailJobService.update(job,database);
            } catch (Exception e) {
                e.printStackTrace();
                job.setState("fail");
                job.setJobinfo("结果文件入库失败");
                scmNretailJobService.update(job,database);
                return b;
            }
        }
        return b;
    }
    private void deleteMo(String matnr,String prov,Date ds) {
        Query query = new Query();
        query.addCriteria(Criteria.where("matnr").is(matnr).and("prov").is(prov).and("ds").is(ds));
        predictMongoTemplate.remove(query, ScmNretailResult.class,"scm_nretail_ipw");
    }
    @Override
    public List<ScmNretailResultVo> resultLists(String database, ScmNretailResult scmResult) {
        List<ScmNretailResultVo> list = null;
        if(database.equals("mysql")){
            list = scmNretailResultMapper.list(scmResult);
            if(list==null){
                return null;
            }
        }
        return list;
    }
    @Override
    public List<ScmNretailResultVo> resultLists2(String database, ScmNretailResult scmResult) {
        List<ScmNretailResult> list = null;
        if(database.equals("mysql")){
             list = scmNretailResultMapper.list2(scmResult);
             if(list==null){
                 return null;
             }
        }
        ArrayList<ScmNretailResultVo> scmNretailResultVos = new ArrayList<>();
        for (ScmNretailResult result :list) {
            if(result!=null){
                String matnr = result.getMatnr();
                matnr = matnr.substring(0, 17) + "X";
                String prov = result.getProv();
                Date ds = result.getDs();
                Map<String, Object> map = new HashMap<>();
                map.put("matnr",matnr);
                map.put("prov",prov);
                map.put("ds",ds);
                List<ScmNretailResult> result1;
                ScmNretailResult scmNretailResult;
                if(database.equals("mysql")){
                    result1 = scmNretailResultMapper.getResult(map);
                    if(result1.size()==0){
                        scmNretailResult=null;
                    }else {
                        scmNretailResult = result1.get(0);
                    }
                }else {
                    scmNretailResult = getResultMo(matnr,prov,ds);
                }
                ScmNretailResultVo scmNretailResultVo = new ScmNretailResultVo();
                scmNretailResultVo.setMatnr(result.getMatnr());
                scmNretailResultVo.setProv(prov);
                scmNretailResultVo.setDs(ds);
                scmNretailResultVo.setCnt(result.getCnt());
                scmNretailResultVo.setPred(result.getPred());
                scmNretailResultVo.setEval(result.getEval());
                if(scmNretailResult!=null){
                    scmNretailResultVo.setProcess_pred(scmNretailResult.getPred());
                    scmNretailResultVo.setProcess_eval(scmNretailResult.getEval());
                }else {
                    scmNretailResultVo.setProcess_pred(null);
                    scmNretailResultVo.setProcess_eval(null);
                }
                scmNretailResultVos.add(scmNretailResultVo);
            }

        }
        return scmNretailResultVos;
    }

    private ScmNretailResult getResultMo(String matnr, String prov, Date ds) {
        Query query = new Query();
        query.addCriteria(Criteria.where("matnr").is(matnr).and("prov").is(prov).and("ds").is(ds));
        ScmNretailResult result = predictMongoTemplate.findOne(query, ScmNretailResult.class, "scm_nretail_ipw");
        return result;
    }

    @Override
    public boolean update(String database) {
        if(database.equals("mysql")){
            return scmNretailResultMapper.update();
        }else {
            return updateMo();
        }

    }

    @Override
    public List<ScmNretailQuotaVo> countByEval(String database) {
        List<ScmNretailQuotaVo> scmNretailQuotaVos = new ArrayList<>();
        if(database.equals("mysql")){
             scmNretailQuotaVos = scmNretailResultMapper.countByEval();
        }else {
            ScmNretailQuotaVo scmNretailQuotaVo = countByEvalMo();
            scmNretailQuotaVos.add(scmNretailQuotaVo);
        }
        return scmNretailQuotaVos;
    }

    @Override
    public List<Map<String,Object>> evalList() {
        return scmNretailResultMapper.evalList();
    }

    @Override
    public List<Map<String,Object>> evalpmList( String matnr, String prov,String database) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> newList = new ArrayList<Map<String, Object>>();
        if(database.equals("mysql")){
            HashMap<String, String> map = new HashMap<>();
            map.put("matnr",matnr);
            map.put("prov",prov);
            list = scmNretailResultMapper.evalpmList(map);
            for (Map m :list) {
                Object ds = m.get("ds");
                Object eval = m.get("eval");
                m.put("ds", ds);
                m.put("eval",eval);
                newList.add(m);
            }
            return newList;
        }else {
            List<ScmNretailResult> scmNretailResults = evalpmListMo(matnr,prov);
            for (ScmNretailResult scmNretailResult :scmNretailResults) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("ds",scmNretailResult.getDs());
                map.put("eval",scmNretailResult.getEval());
                list.add(map);
            }
            return list;
        }
    }
    public  List<ScmNretailResult> evalpmListMo(String matnr, String prov){
        Sort sort = new Sort(Sort.Direction.ASC, "ds");
        Aggregation agg = Aggregation.newAggregation(
                // 第一步：挑选所需的字段，类似select *，*所代表的字段内容
                Aggregation.project("prov", "matnr","ds", "eval"),
                // 第二步：sql where 语句筛选符合条件的记录
                Aggregation.match(Criteria.where("matnr").is(matnr).and("prov").is(prov)),
                // 第三步：分组条件，设置分组字段
                Aggregation.group("prov", "matnr","ds").first("eval").as("eval"),
                Aggregation.sort(sort),
                // 第四步：重新挑选字段
                Aggregation.project("ds", "eval")
        );
        AggregationResults<ScmNretailResult> scm_nretail_ipw = predictMongoTemplate.aggregate(agg, "scm_nretail_ipw", ScmNretailResult.class);
        List<ScmNretailResult> scmNretailResults = scm_nretail_ipw.getMappedResults();
        return scmNretailResults;
    }
    public ScmNretailQuotaVo countByEvalMo() {
        int all = predictMongoTemplate.findAll( ScmNretailQuotaVo.class, "scm_nretail_ipw").size();
        Query query = new Query();
        query.addCriteria(Criteria.where("eval").gte(0.6));
        int low = predictMongoTemplate.find(query, ScmNretailQuotaVo.class, "scm_nretail_ipw").size();
        Query query2 = new Query();
        query2.addCriteria(Criteria.where("eval").gte(0.7));
        int mid = predictMongoTemplate.find(query2, ScmNretailQuotaVo.class, "scm_nretail_ipw").size();
        Query query3 = new Query();
        query3.addCriteria(Criteria.where("eval").gte(0.8));
        int high = predictMongoTemplate.find(query3, ScmNretailQuotaVo.class, "scm_nretail_ipw").size();
        ScmNretailQuotaVo scmNretailQuotaVo = new ScmNretailQuotaVo();
        double lowpercent  =   new BigDecimal((double)low/all).setScale(4,   BigDecimal.ROUND_HALF_UP).doubleValue();
        double midpercent   =   new BigDecimal((double)mid/all).setScale(4,   BigDecimal.ROUND_HALF_UP).doubleValue();
        double highpercent  =   new BigDecimal((double)high/all).setScale(4,   BigDecimal.ROUND_HALF_UP).doubleValue();
        scmNretailQuotaVo.setLowPercent(lowpercent);
        scmNretailQuotaVo.setMidPercent(midpercent);
        scmNretailQuotaVo.setHighPercent(highpercent);
        return scmNretailQuotaVo;
    }
    public void truncateMo() {
        predictMongoTemplate.dropCollection("scm_nretail_ipw");
    }
    public void saveVo(ArrayList<ScmNretailResult> list) {
        predictMongoTemplate.insert(list,"scm_nretail_ipw");
    }

    public List<ScmNretailResult> resultListsMo() {
        Aggregation agg = Aggregation.newAggregation(
                // 第一步：挑选所需的字段，类似select *，*所代表的字段内容
                Aggregation.project("prov", "matnr","ds", "cnt","pred","eval").and("matnr").project("substr", 17,-1).as("matnr_last"),
                // 第二步：sql where 语句筛选符合条件的记录
                Aggregation.match(Criteria.where("matnr_last").ne("X")),
                Aggregation.project("prov", "matnr","ds", "cnt","pred","eval")

        );
        AggregationResults<ScmNretailResult> scm_nretail_ipw = predictMongoTemplate.aggregate(agg, "scm_nretail_ipw", ScmNretailResult.class);
        List<ScmNretailResult> scmNretailResults = scm_nretail_ipw.getMappedResults();
        return scmNretailResults;
    }
    public boolean updateMo() {
//        SELECT a.* from predict.scm_nretail_ipw AS a	WHERE a.cnt IS NULL
//        SELECT b.cnt from input.scm_nretail_ipw AS a	WHERE b.prov="" and b.matnr="" and b.ds=""
        Query query = new Query();
        query.addCriteria(Criteria.where("cnt").is(null));
        List<ScmNretailResult> nretailResults = predictMongoTemplate.find(query, ScmNretailResult.class, "scm_nretail_ipw");
        if(nretailResults!=null){
            for (ScmNretailResult result:nretailResults) {
                Query q = new Query();
                q.addCriteria(Criteria.where("prov").is(result.getProv()).and("matnr").is(result.getMatnr()).and("ds").is(result.getDs()));
                ScmNretailResult scmNretailResult = secondaryMongoTemplate.findOne(q, ScmNretailResult.class, "scm_nretail_ipw");
                Update update = new Update();
                if(scmNretailResult!=null){
                    update.set("cnt",scmNretailResult.getCnt());
                }else{
                    update.set("cnt",null);
                }
                UpdateResult updateResult = predictMongoTemplate.updateFirst(q, update, ScmNretailResult.class, "scm_nretail_ipw");
                long modifiedCount = updateResult.getModifiedCount();
                System.out.println(modifiedCount);
            }
        }
        return true;
    }

}
