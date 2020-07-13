package org.jeecg.modules.scm.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.jeecg.modules.job.entity.ScmNretailJob;
import org.jeecg.modules.job.mapper.ScmNretailJobMapper;
import org.jeecg.modules.predict.entity.ScmNretailIpw;
import org.jeecg.modules.scm.mapper.ScmNretailIpwMapper;
import org.jeecg.modules.scm.service.ScmNretailIpwService;
import org.jeecg.modules.predict.vo.ScmNretailIpdVo;
import org.jeecg.modules.scm.vo.ScmNretailpiVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @Description: 销量统计按天
 * @Author: jeecg-boot
 * @Date:   2020-03-11
 * @Version: V1.0
 */
@Service
@DS("input")
public class ScmNretailIpwServiceImpl extends ServiceImpl<ScmNretailIpwMapper, ScmNretailIpw> implements ScmNretailIpwService {
    @Autowired
    ScmNretailIpwMapper scmNretailIpwMapper;
    @Autowired
    ScmNretailJobMapper scmNretailJobMapper;
    @Autowired
//    @Qualifier(value = "secondaryMongoTemplate")
    private MongoTemplate secondaryMongoTemplate;
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Override
    public void truncate( String database){
        if(database.equals("mysql")){
            scmNretailIpwMapper.truncate();
        }else {
            truncateVo();
        }
    }

    public boolean saveIpds(List<ScmNretailIpw> lists, String database){

        if(database.equals("mysql")){
            try(SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH,false);) {
                //由于数据库对于插入字段的限制，在这里对批量插入的数据进行分批处理
                long start = System.currentTimeMillis();
                for (int i = 0; i <= lists.size() - 1;i++) {
                    scmNretailIpwMapper.saveOne(lists.get(i));
                    if(i%10000==9999){
                        sqlSession.commit();
                        sqlSession.clearCache();
                    }
                }
                sqlSession.commit();
                sqlSession.clearCache();
                System.out.println("批处理执行时间"+(System.currentTimeMillis()-start)/1000+"秒");
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
//            return scmNretailIpwMapper.save(lists);
        }else {
            return saveIpdsVo(lists);
        }
    }
    @Transactional
    @Override
    public boolean  save(String uuid,List<ScmNretailIpw> scmNretailIpdList, String database){
        ScmNretailJob job = new ScmNretailJob();
        try {
            truncate(database);
            return saveIpds(scmNretailIpdList,database);
        }catch (Exception e){
            job.setUuid(uuid);
            job.setState("fail");
            job.setJobinfo("汇总数据入库失败");
            job.setEndtime(new Date());
            scmNretailJobMapper.update(job);
            return false;
        }

    }
    @Override
    public List<ScmNretailIpw>  getAll(String database){
        if(database.equals("mysql")){
            return scmNretailIpwMapper.getAll();
        }else {
            return getAllMo();
        }
    }
    @Override
    public List<ScmNretailpiVo> countByGpw(String database) {
        if(database.equals("mysql")){
            return scmNretailIpwMapper.countByGpw();
        }else {
            return countByGpwVo();
        }
    }

    @Override
    public void saveOne(ScmNretailIpw scmNretailIpw) {
        scmNretailIpwMapper.saveOne(scmNretailIpw);
    }

    public boolean saveIpdsVo(List<ScmNretailIpw> lists){
        secondaryMongoTemplate.insert(lists,"scm_nretail_ipw");
        return true;
    }
    public void truncateVo(){
        secondaryMongoTemplate.dropCollection("scm_nretail_ipw");
    }


    public List<ScmNretailIpw>  getAllMo(){
//        SELECT matnr, prov,ds ,cnt FROM scm_nretail_ipw ORDER  BY  matnr,prov,ds
        Sort sort = new Sort(Sort.Direction.ASC, "matnr").and(new Sort(Sort.Direction.ASC, "prov")).and(new Sort(Sort.Direction.ASC, "ds"));
        Query query = new Query();
        query.with(sort);
        List<ScmNretailIpw> scmNretailIpws = secondaryMongoTemplate.find(query,ScmNretailIpw.class, "scm_nretail_ipw");
        return scmNretailIpws;
    }
    public List<ScmNretailpiVo> countByGpwVo() {
//SELECT prov,matnr  FROM scm_nretail_ipw GROUP BY prov, matnr
        Aggregation agg = Aggregation.newAggregation(
                // 第一步：挑选所需的字段，类似select *，*所代表的字段内容
                Aggregation.project("prov", "matnr"),
                // 第二步：sql where 语句筛选符合条件的记录
                // 第三步：分组条件，设置分组字段
                Aggregation.group("prov", "matnr"),
                         // 增加publishDate为分组后输出的字段
                // 第四步：重新挑选字段
                Aggregation.project("prov", "matnr")
        );
        AggregationResults<ScmNretailpiVo> scmNretailpiVos = secondaryMongoTemplate.aggregate(agg, "scm_nretail_ipw", ScmNretailpiVo.class);
        return scmNretailpiVos.getMappedResults();
    }
}
